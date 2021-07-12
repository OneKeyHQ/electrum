import logging
from typing import Iterable, List

import peewee

from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.coin import codes
from electrum_gui.common.coin.data import CoinInfo
from electrum_gui.common.conf import chains as chains_conf
from electrum_gui.common.price.data import YieldedPrice
from electrum_gui.common.price.interfaces import PriceChannelInterface

logger = logging.getLogger("app.price")

API_HOST = "https://api.coingecko.com"


class Coingecko(PriceChannelInterface):
    def __init__(self):
        self.restful = RestfulRequest(API_HOST)

    def fetch_btc_to_fiats(self) -> Iterable[YieldedPrice]:
        resp = self.restful.get("/api/v3/exchange_rates")
        rates = resp.get("rates") or {}

        rates = ((unit, rate) for unit, rate in rates.items() if rate and rate.get("type") == "fiat")
        rates = (YieldedPrice(coin_code=codes.BTC, unit=unit, price=rate.get("value") or 0) for unit, rate in rates)
        yield from rates

    def fetch_cgk_ids_to_currency(self, cgk_ids: List[str], currency: str) -> Iterable[YieldedPrice]:
        if not cgk_ids:
            return

        resp = (
            self.restful.get(
                "/api/v3/coins/markets",
                params={
                    "ids": ",".join(cgk_ids),
                    "vs_currency": currency,
                },
            )
            or ()
        )
        for rate in resp:
            if not rate:
                continue
            coingecko_id = rate.get("id")
            coin_codes = chains_conf.get_coin_codes_by_coingecko_id(coingecko_id)
            for coin_code in coin_codes:
                yield YieldedPrice(coin_code=coin_code, unit=currency, price=rate.get("current_price") or 0)

    def fetch_erc20_to_currency(self, erc20_coins: List[CoinInfo], currency: str) -> Iterable[YieldedPrice]:
        if not erc20_coins:
            return

        mapping = {i.token_address.lower(): i for i in erc20_coins}

        for batch_addresses in peewee.chunked(mapping.keys(), 100):
            resp = self.restful.get(
                "/api/v3/simple/token_price/ethereum",
                params={
                    "contract_addresses": ",".join(batch_addresses),
                    "vs_currencies": currency,
                },
            )
            rates = ((address.lower(), rate) for address, rate in resp.items() if address.lower() in mapping and rate)
            rates = (
                YieldedPrice(
                    coin_code=mapping[address].code,
                    unit=currency,
                    price=rate.get(currency) or 0,
                )
                for address, rate in rates
            )
            yield from rates

    def pricing(self, coins: Iterable[CoinInfo]) -> Iterable[YieldedPrice]:
        try:
            yield from self.fetch_btc_to_fiats()
        except Exception as e:
            logger.exception("Error in fetching fiat rate of btc.", e)

        cgk_ids = chains_conf.get_coingecko_ids()
        if cgk_ids:
            try:
                yield from self.fetch_cgk_ids_to_currency(cgk_ids, currency=codes.BTC)
            except Exception as e:
                logger.exception("Error in fetching btc rate of config cgk ids.", e)

        erc20_coins = [coin for coin in coins if coin.token_address and coin.chain_code == codes.ETH]
        if erc20_coins:
            try:
                yield from self.fetch_erc20_to_currency(erc20_coins, currency=codes.BTC)
            except Exception as e:
                logger.exception("Error in fetching btc rate of erc20.", e)

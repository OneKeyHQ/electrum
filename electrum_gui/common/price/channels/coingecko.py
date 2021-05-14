import logging
from typing import Iterable, List

import peewee

from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.coin import codes
from electrum_gui.common.coin.data import CoinInfo
from electrum_gui.common.conf import settings
from electrum_gui.common.price.data import YieldedPrice
from electrum_gui.common.price.interfaces import PriceChannelInterface

logger = logging.getLogger("app.price")


class Coingecko(PriceChannelInterface):
    COINGECKO_ID_TO_CODES = None

    def __init__(self, url: str):
        self.restful = RestfulRequest(url)

    def fetch_btc_to_fiats(self) -> Iterable[YieldedPrice]:
        resp = self.restful.get("/api/v3/exchange_rates")
        rates = resp.get("rates") or {}

        rates = ((unit, rate) for unit, rate in rates.items() if rate and rate.get("type") == "fiat")
        rates = (YieldedPrice(coin_code=codes.BTC, unit=unit, price=rate.get("value") or 0) for unit, rate in rates)
        yield from rates

    def fetch_cgk_ids_to_currency(self, cgk_ids: List[str], currency: str) -> Iterable[YieldedPrice]:
        if not cgk_ids:
            return

        if self.COINGECKO_ID_TO_CODES is None:
            self.COINGECKO_ID_TO_CODES = {cgk_id: code for code, cgk_id in settings.COINGECKO_IDS.items()}

        mapping = self.COINGECKO_ID_TO_CODES

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

        rates = (rate for rate in resp if rate and rate.get("id") in mapping)
        rates = (
            YieldedPrice(coin_code=mapping[rate.get("id")], unit=currency, price=rate.get("current_price") or 0)
            for rate in rates
        )
        yield from rates

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
        coin_dict = {settings.PRICING_COIN_MAPPING.get(i.code, i.code): i for i in coins}

        if codes.BTC in coin_dict:
            try:
                yield from self.fetch_btc_to_fiats()
            except Exception as e:
                logger.exception("Error in fetching fiat rate of btc.", e)

        cgk_ids = [cgk_id for code, cgk_id in settings.COINGECKO_IDS.items() if code in coin_dict]
        if cgk_ids:
            try:
                yield from self.fetch_cgk_ids_to_currency(cgk_ids, currency=codes.BTC)
            except Exception as e:
                logger.exception("Error in fetching btc rate of config cgk ids.", e)

        erc20_coins = [coin for coin in coin_dict.values() if coin.token_address and coin.chain_code == codes.ETH]
        if erc20_coins:
            try:
                yield from self.fetch_erc20_to_currency(erc20_coins, currency=codes.BTC)
            except Exception as e:
                logger.exception("Error in fetching btc rate of erc20.", e)

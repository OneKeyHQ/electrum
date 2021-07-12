import decimal
import itertools
import operator
from typing import Iterable, List, Tuple

import eth_abi

from electrum_gui.common.coin import data as coin_data
from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.conf import chains as chains_conf
from electrum_gui.common.price import data, interfaces
from electrum_gui.common.provider import manager as provider_manager
from electrum_gui.common.provider.chains.eth.clients import geth as geth_client

BATCH_SIZE = 50

# Refer to https://uniswap.org/docs/v2/smart-contracts/router02


def _get_eth_call_data(coin: coin_data.CoinInfo, paths: Tuple[Tuple]) -> List[str]:
    # >>> eth_utils.keccak("getAmountsOut(uint256,address[])".encode())[:4].hex()
    # 'd06ca61f'
    return [
        "0xd06ca61f"
        + eth_abi.encode_single("(uint256,address[])", (10 ** coin.decimals, [coin.token_address, *path])).hex()
        for path in paths
    ]


def _extract_price_from_resp(resp: str) -> int:
    # We want the last number only.
    return eth_abi.decode_single("uint256", bytes.fromhex(resp[2:])[-32:])


def _obtain_prices_from_dex(
    chain_code: str,
    router_address: str,
    base_divisor: int,
    coins: List[Tuple[coin_data.CoinInfo, int]],
    call_data: List[str],
) -> Iterable[data.YieldedPrice]:
    client = provider_manager.get_client_by_chain(chain_code, instance_required=geth_client.Geth)

    resp_iterator = iter(client.call_contract(router_address, call_data))
    for coin, paths_count in coins:
        price = (
            decimal.Decimal(
                max(
                    _extract_price_from_resp(resp) if resp is not None else 0
                    for resp in itertools.islice(resp_iterator, paths_count)
                )
            )
            / base_divisor
        )
        if price > 0:
            yield data.YieldedPrice(coin_code=coin.code, unit=chain_code, price=price)

    yield from tuple()


class Uniswap(interfaces.PriceChannelInterface):
    def pricing(self, coins: Iterable[coin_data.CoinInfo]) -> Iterable[data.YieldedPrice]:
        coins = sorted(coins, key=operator.attrgetter("chain_code"))
        for chain_code, coins_on_chain in itertools.groupby(coins, operator.attrgetter("chain_code")):
            uniswap_config = chains_conf.get_uniswap_configs(chain_code)
            if uniswap_config is None:
                continue

            router_address = uniswap_config["router_address"]
            base_token_address = uniswap_config["base_token_address"]
            media_token_addresses = tuple(uniswap_config["media_token_addresses"])
            paths_for_media_tokens = ((base_token_address,),)  # Direct exchange
            paths_for_normal_tokens = paths_for_media_tokens + tuple(
                itertools.product(media_token_addresses, [base_token_address])
            )

            base_coin = coin_manager.get_coin_info(chain_code)
            base_divisor = 10 ** base_coin.decimals

            total_paths_count = 0
            coins_in_one_request = []
            data_in_one_request = []
            for coin in coins_on_chain:
                if coin.token_address is None:  # Not a token
                    continue
                elif coin.token_address == base_token_address:  # It's a base token, i.e., a WETH/WBNB/WHT...
                    yield data.YieldedPrice(coin_code=coin.code, unit=base_coin.code, price=decimal.Decimal("1"))
                    continue
                elif coin.token_address in media_token_addresses:  # A media token
                    paths = paths_for_media_tokens
                else:
                    paths = paths_for_normal_tokens

                paths_count_of_this_coin = len(paths)
                coins_in_one_request.append((coin, paths_count_of_this_coin))
                data_in_one_request.extend(_get_eth_call_data(coin, paths))

                total_paths_count += paths_count_of_this_coin
                if total_paths_count >= BATCH_SIZE:
                    yield from _obtain_prices_from_dex(
                        chain_code, router_address, base_divisor, coins_in_one_request, data_in_one_request
                    )
                    total_paths_count = 0
                    coins_in_one_request = []
                    data_in_one_request = []

            if data_in_one_request:
                yield from _obtain_prices_from_dex(
                    chain_code, router_address, base_divisor, coins_in_one_request, data_in_one_request
                )
            else:
                yield from tuple()

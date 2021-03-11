from typing import List, Optional, Tuple

from electrum_gui.common.coin import daos, exceptions, registry
from electrum_gui.common.coin.data import ChainInfo, CoinInfo
from electrum_gui.common.conf import settings


def get_chain_info(chain_code: str) -> ChainInfo:
    """
    Get chain info by chain_code
    :param chain_code: chain_code
    :return: ChainInfo
    :raise ChainNotFound: if nothing found by chain_code
    """
    if chain_code not in registry.chain_dict:
        raise exceptions.ChainNotFound(chain_code)

    return registry.chain_dict[chain_code]


def is_chain_enabled(chain_code: str) -> bool:
    """
    Check if a specific chain is enabled
    :param chain_code: chain_code
    :return: enabled or not
    """
    return chain_code in settings.ENABLED_CHAIN_COINS


def get_coin_info(coin_code: str, nullable: bool = False) -> Optional[CoinInfo]:
    """
    Get coin info by coin_code
    :param coin_code:  coin_code
    :param nullable: return None if coin not found
    :return: CoinInfo
    :raise CoinNotFound: raise if coin not found and no-nullable
    """
    coin = registry.coin_dict.get(coin_code) or daos.get_coin_info(coin_code)
    if not coin and not nullable:
        raise exceptions.CoinNotFound(coin_code)

    return coin


def get_all_chains() -> List[ChainInfo]:
    """
    Get all chains info
    :return: list of ChainInfo
    """
    return list(registry.chain_dict.values())


def get_all_coins() -> List[CoinInfo]:
    """
    Get all coins info
    :return: list of CoinInfo
    """
    coins = list(registry.coin_dict.values())
    coins.extend(daos.get_all_coins())

    coins = _deduplicate_coins(coins)
    return coins


def get_coins_by_chain(chain_code: str) -> List[CoinInfo]:
    """
    Get coins by specific chain_code
    :param chain_code: chain_code
    :return: list of CoinInfo
    """
    coins = [i for i in registry.coin_dict.values() if i.chain_code == chain_code]
    coins.extend(daos.get_coins_by_chain(chain_code))

    coins = _deduplicate_coins(coins)
    return coins


def get_related_coins(coin_code: str) -> Tuple[CoinInfo, CoinInfo, CoinInfo]:
    """
    Get tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code) at the same time
    :param coin_code: coin code
    :return: tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code)
    """
    coin_info = get_coin_info(coin_code)
    chain_info = get_chain_info(coin_info.chain_code)

    return get_coin_info(coin_info.chain_code), coin_info, get_coin_info(chain_info.fee_code)


def add_coin(
    chain_code: str,
    token_address: str,
    symbol: str,
    decimals: int,
    name: str = None,
    icon: str = None,
) -> str:
    coin_code = _generate_coin_code(chain_code, token_address, symbol)

    if coin_code in registry.coin_dict:
        return coin_code

    coin = daos.get_coin_info(coin_code)

    if coin is None:
        daos.add_coin(
            CoinInfo(
                code=coin_code,
                chain_code=chain_code,
                token_address=token_address,
                symbol=symbol,
                decimals=decimals,
                name=name or symbol,
                icon=icon,
            )
        )
    else:
        daos.update_coin_info(coin_code, name=name, icon=icon)

    return coin_code


def _generate_coin_code(chain_code: str, token_address: str, symbol: str) -> str:
    slice_length = 6
    address_length = len(token_address)  # length of ERC20 token_address is 42
    token_address = token_address.lower()

    coin_code_base_str = f"{chain_code}_{symbol}".lower()
    candidate = coin_code_base_str

    while slice_length <= address_length:
        coin = get_coin_info(candidate, nullable=True)

        if not coin or (coin and coin.token_address and coin.token_address.lower() == token_address):
            break

        candidate = f"{coin_code_base_str}_{token_address[:slice_length]}"
        slice_length += 2

    return candidate


def _deduplicate_coins(coins: List[CoinInfo]) -> List[CoinInfo]:
    codes = set()
    deduplicate_coins = []

    for i in coins:
        if i.code in codes:
            continue

        codes.add(i.code)
        deduplicate_coins.append(i)

    return deduplicate_coins

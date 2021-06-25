from typing import List, Optional, Tuple

from electrum_gui.common.coin import daos, data, exceptions, loader
from electrum_gui.common.conf import settings
from electrum_gui.common.provider import manager as provider_manager


@loader.refresh_chains
def get_chain_info(chain_code: str) -> data.ChainInfo:
    """
    Get chain info by chain_code
    :param chain_code: chain_code
    :return: data.ChainInfo
    :raise ChainNotFound: if nothing found by chain_code
    """
    if chain_code not in loader.CHAINS_DICT:
        raise exceptions.ChainNotFound(chain_code)

    return loader.CHAINS_DICT[chain_code]


@loader.refresh_chains
def get_chains_by_affinity(chain_affinity: str) -> List[data.ChainInfo]:
    """
    Get chains by affinity
    :param chain_affinity: chain_affinity used to search the chains
    :return: list of data.ChainInfo with the specified chain_affinity
    """
    return [chain_info for chain_info in loader.CHAINS_DICT.values() if chain_info.chain_affinity == chain_affinity]


@loader.refresh_coins
def get_coin_info(coin_code: str, nullable: bool = False) -> Optional[data.CoinInfo]:
    """
    Get coin info by coin_code
    :param coin_code:  coin_code
    :param nullable: return None if coin not found
    :return: data.CoinInfo
    :raise CoinNotFound: raise if coin not found and no-nullable
    """
    coin = loader.COINS_DICT.get(coin_code) or daos.get_coin_info(coin_code)
    if not coin and not nullable:
        raise exceptions.CoinNotFound(coin_code)

    return coin


@loader.refresh_coins
def query_coins_by_codes(coin_codes: List[str]) -> List[data.CoinInfo]:
    """
    Query coins by codes
    :param coin_codes: list of coin codes
    :return: list of data.CoinInfo found
    """
    coin_codes = set(coin_codes)
    coins = list(i for i in loader.COINS_DICT.values() if i.code in coin_codes)
    coins.extend(daos.query_coins_by_codes(coin_codes))

    coins = _deduplicate_coins(coins)
    return coins


@loader.refresh_chains
def get_all_chains() -> List[data.ChainInfo]:
    """
    Get all chains info
    :return: list of data.ChainInfo
    """
    return list(loader.CHAINS_DICT.values())


@loader.refresh_coins
def get_all_coins() -> List[data.CoinInfo]:
    """
    Get all coins info
    :return: list of data.CoinInfo
    """
    coins = list(loader.COINS_DICT.values())
    coins.extend(daos.get_all_coins())

    coins = _deduplicate_coins(coins)
    return coins


@loader.refresh_coins
def get_coins_by_chain(chain_code: str) -> List[data.CoinInfo]:
    """
    Get coins by specific chain_code
    :param chain_code: chain_code
    :return: list of data.CoinInfo
    """
    coins = [i for i in loader.COINS_DICT.values() if i.chain_code == chain_code]
    coins.extend(daos.get_coins_by_chain(chain_code))

    coins = _deduplicate_coins(coins)
    return coins


def get_related_coins(coin_code: str) -> Tuple[data.CoinInfo, data.CoinInfo, data.CoinInfo]:
    """
    Get tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code) at the same time
    :param coin_code: coin code
    :return: tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code)
    """
    coin_info = get_coin_info(coin_code)
    chain_info = get_chain_info(coin_info.chain_code)

    chain_coin = coin_info if chain_info.chain_code == coin_code else get_coin_info(coin_info.chain_code)
    fee_coin = coin_info if chain_info.fee_code == coin_code else get_coin_info(chain_info.fee_code)

    return chain_coin, coin_info, fee_coin


@loader.refresh_coins
def add_coin(
    chain_code: str,
    token_address: str,
    symbol: str,
    decimals: int,
    name: str = None,
    icon: str = None,
) -> str:
    coin_code = _generate_coin_code(chain_code, token_address, symbol)

    if coin_code in loader.COINS_DICT:
        return coin_code

    coin = daos.get_coin_info(coin_code)

    if coin is None:
        daos.add_coin(
            data.CoinInfo(
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
    address_length = len(token_address)  # length of ERC20 token_address is 42
    slice_length = min(address_length, 6)
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


def _deduplicate_coins(coins: List[data.CoinInfo]) -> List[data.CoinInfo]:
    codes = set()
    deduplicate_coins = []

    for i in coins:
        if i.code in codes:
            continue

        codes.add(i.code)
        deduplicate_coins.append(i)

    return deduplicate_coins


@loader.refresh_coins
def query_coins_by_token_addresses(chain_code: str, token_addresses: List[str]) -> List[data.CoinInfo]:
    coins = [i for i in loader.COINS_DICT.values() if i.chain_code == chain_code and i.token_address in token_addresses]
    coins.extend(daos.query_coins_by_token_addresses(chain_code, token_addresses))
    coins = _deduplicate_coins(coins)
    return coins


def get_coin_by_token_address(
    chain_code: str, token_address: str, add_if_missing: bool = False
) -> Optional[data.CoinInfo]:
    coin = None
    coins = query_coins_by_token_addresses(chain_code, [token_address])

    if coins:
        coin = coins[0]
    elif add_if_missing:
        symbol, name, decimals = provider_manager.get_token_info_by_address(chain_code, token_address)
        coin_code = add_coin(chain_code, token_address, symbol, decimals, name)
        coin = get_coin_info(coin_code)
    else:
        raise exceptions.CoinNotFoundByTokenAddress(token_address)

    return coin


def legacy_coin_to_chain_code(legacy_coin: str) -> str:
    # Return chain code for legacy coin
    prefix = "t" if settings.IS_DEV else ""
    return f"{prefix}{legacy_coin}"


def chain_code_to_legacy_coin(chain_code: str) -> str:
    # Return legacy coin for chain code
    if settings.IS_DEV and chain_code.startswith("t"):
        return chain_code[1:]
    else:
        return chain_code

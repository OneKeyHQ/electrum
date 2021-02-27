from typing import List, Tuple

from electrum_gui.common.coin import exceptions, registry
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


def get_coin_info(coin_code: str) -> CoinInfo:
    """
    Get coin info by coin_code
    :param coin_code:  coin_code
    :return: CoinInfo
    :raise CoinNotFound: if nothing found by coin_code
    """
    if coin_code not in registry.coin_dict:
        raise exceptions.CoinNotFound(coin_code)

    return registry.coin_dict[coin_code]


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
    return list(registry.coin_dict.values())


def get_coins_by_chain(chain_code: str) -> List[CoinInfo]:
    """
    Get coins by specific chain_code
    :param chain_code: chain_code
    :return: list of CoinInfo
    """
    return [i for i in get_all_coins() if i.chain_code == chain_code]


def get_related_coins(coin_code: str) -> Tuple[CoinInfo, CoinInfo, CoinInfo]:
    """
    Get tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code) at the same time
    :param coin_code: coin code
    :return: tuple of (coin info of chain_code, coin info of coin_code, coin info of fee_code)
    """
    coin_info = get_coin_info(coin_code)
    chain_info = get_chain_info(coin_info.chain_code)

    return get_coin_info(coin_info.chain_code), coin_info, get_coin_info(chain_info.fee_code)

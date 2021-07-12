import functools

from electrum_gui.common.coin import data
from electrum_gui.common.conf import chains as chains_conf
from electrum_gui.common.secret import data as secret_data

CHAINS_DICT = {}
COINS_DICT = {}


def refresh_coins(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        global COINS_DICT
        added_coins = chains_conf.get_added_coins(set(COINS_DICT.keys()))
        if added_coins:
            for coin in added_coins:
                coininfo = data.CoinInfo(**coin)
                COINS_DICT.setdefault(coininfo.code, coininfo)

        return func(*args, **kwargs)

    return wrapper


def refresh_chains(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        global CHAINS_DICT
        added_chains = chains_conf.get_added_chains(set(CHAINS_DICT.keys()))
        if added_chains:
            for chain in added_chains:
                chain.update(
                    {
                        "chain_model": data.ChainModel[chain["chain_model"].upper()],
                        "curve": secret_data.CurveEnum[chain["curve"].upper()],
                    }
                )
                chaininfo = data.ChainInfo(**chain)
                CHAINS_DICT.setdefault(chaininfo.chain_code, chaininfo)
        return func(*args, **kwargs)

    return wrapper

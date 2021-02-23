import json
import os
from typing import Dict

from electrum_gui.common.coin.data import ChainInfo, CoinInfo


def _load_chains(chains_json_name: str) -> Dict[str, ChainInfo]:
    raw_chains = json.loads(open(chains_json_name).read())
    ret = {}

    for config in raw_chains:
        chain_info = ChainInfo(**config)
        ret[chain_info.chain_code] = chain_info

    return ret


def _load_coins(coins_json_name: str) -> Dict[str, CoinInfo]:
    raw_coins_dict = json.loads(open(coins_json_name).read())
    ret = {}

    for chain_code, coins in raw_coins_dict.items():
        for coin in coins:
            coin_info = CoinInfo(chain_code=chain_code, **coin)
            ret[coin_info.code] = coin_info

    return ret


_base_pth = os.path.dirname(__file__)

mainnet_chains = _load_chains(os.path.join(_base_pth, "./configs/chains.json"))
testnet_chains = _load_chains(os.path.join(_base_pth, "./configs/testnet_chains.json"))

mainnet_coins = _load_coins(os.path.join(_base_pth, "./configs/coins.json"))
testnet_coins = _load_coins(os.path.join(_base_pth, "./configs/testnet_coins.json"))

chain_dict = {**mainnet_chains, **testnet_chains}
coin_dict = {**mainnet_coins, **testnet_coins}

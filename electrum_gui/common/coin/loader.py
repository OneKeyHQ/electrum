import json
import os
from typing import Dict, List, Tuple

from electrum_gui.common.coin.data import ChainInfo, ChainModel, CoinInfo


def _load_config(json_name: str) -> Tuple[Dict[str, ChainInfo], Dict[str, CoinInfo]]:
    configs: List[dict] = json.loads(open(json_name).read())
    chains, coins = {}, {}

    for chain_config in configs:
        coins_config = chain_config.pop("coins")
        chain_config["chain_model"] = ChainModel[chain_config["chain_model"].upper()]
        chain_info = ChainInfo(**chain_config)
        chains[chain_info.chain_code] = chain_info
        coins.update({i["code"]: CoinInfo(chain_code=chain_info.chain_code, **i) for i in coins_config})

    return chains, coins


_BASE_PATH = os.path.dirname(__file__)

_MAINNET_CHAINS, _MAINNET_COINS = _load_config(os.path.join(_BASE_PATH, "./configs/chains.json"))
_TESTNET_CHAINS, _TESTNET_COINS = _load_config(os.path.join(_BASE_PATH, "./configs/testnet_chains.json"))

CHAINS_DICT = {**_MAINNET_CHAINS, **_TESTNET_CHAINS}
COINS_DICT = {**_MAINNET_COINS, **_TESTNET_COINS}

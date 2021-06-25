import collections
import hashlib
import io
import json
import os
import zipfile
from typing import Dict, List, Optional

from electrum_gui.common.conf import settings, utils

PRICE = {
    "coingecko_mappings": collections.defaultdict(list),
    "uniswap_configs": {},
}

CHAIN_IMPLS = {
    "bitcoin": {
        "chain_model": "utxo",
        "curve": "secp256k1",
        "chain_affinity": "btc",
        "bip44_coin_type": 0,
        "bip44_auto_increment_level": "ACCOUNT",
        "default_address_encoding": "P2WPKH-P2SH",
        "bip44_purpose_options": {
            "P2PKH": 44,
            "P2WPKH-P2SH": 49,
            "P2WPKH": 84,
        },
    },
    "evm": {
        "chain_model": "account",
        "curve": "secp256k1",
        "chain_affinity": "eth",
        "bip44_coin_type": 60,
        "fee_price_decimals_for_legibility": 9,
    },
    "stc": {
        "chain_model": "account",
        "curve": "ed25519",
        "chain_affinity": "stc",
        "bip44_coin_type": 101010,
        "bip44_target_level": "ADDRESS_INDEX",
        "bip44_auto_increment_level": "ADDRESS_INDEX",
        "bip44_last_hardened_level": "ADDRESS_INDEX",
        "default_address_encoding": "HEX",
    },
    "sol": {
        "chain_model": "account",
        "curve": "ed25519",
        "chain_affinity": "sol",
        "bip44_coin_type": 501,
        "bip44_target_level": "CHANGE",
        "bip44_auto_increment_level": "ACCOUNT",
        "bip44_last_hardened_level": "CHANGE",
    },
}

CHAINS = {}
TOKENS = {}

_CONF_FILENAME = "chain_configs.dat"
_LOCAL_FILE = os.path.join(os.path.dirname(__file__), "data", _CONF_FILENAME)
_DATA_FILE = os.path.join(settings.DATA_DIR, "app_configs", _CONF_FILENAME)
_FILE_MD5SUM = None


def _read_data_file(filename: str) -> Optional[bytes]:
    if not os.path.exists(filename):
        return
    with open(filename, "rb") as f:
        tmp_data = f.read()
    if not tmp_data or len(tmp_data) <= 512:
        return

    signature, zipfile_content = tmp_data[:512], tmp_data[512:]
    if utils.verify(signature, zipfile_content):
        global _FILE_MD5SUM
        _FILE_MD5SUM = _FILE_MD5SUM or hashlib.md5(tmp_data).hexdigest()  # nosec B303
        return zipfile_content


def _load_data(refresh=False):
    zipfile_content = None

    if refresh:
        pass  # TODO: download new file

    if zipfile_content is None and _FILE_MD5SUM is not None:
        return  # Not refreshed, or failed to refresh, and local file is already loaded.
    else:
        zipfile_content = _read_data_file(_DATA_FILE) or _read_data_file(_LOCAL_FILE)
        if zipfile_content is None:  # SHOULD NOT happen
            return

    global PRICE, CHAINS, TOKENS
    with zipfile.ZipFile(io.BytesIO(zipfile_content)) as chains_zip:
        for filename in chains_zip.namelist():
            chain, conf_type = filename.split("/")

            # Release package only supports btc, dev package only supports tbtc.
            if chain == "btc" and settings.IS_DEV:
                continue
            if chain == "tbtc" and not settings.IS_DEV:
                continue

            if conf_type == "tokens":
                TOKENS[chain] = json.loads(chains_zip.read(filename))
            elif conf_type == "settings":
                chain_settings = json.loads(chains_zip.read(filename))

                if chain_settings["impl"] not in CHAIN_IMPLS:
                    continue
                if chain_settings.get("staging", False) and not settings.IS_DEV:
                    continue

                CHAINS[chain] = chain_settings
                # Extract price configs
                for price_config in chain_settings.get("prices", []):
                    if price_config["channel"] == "coingecko":
                        for coin_code, coingecko_id in price_config["code_mappings"].items():
                            PRICE["coingecko_mappings"][coingecko_id].append(coin_code)
                    elif price_config["channel"] == "uniswap":
                        PRICE["uniswap_configs"][chain] = price_config

    # Cleanup useless tokens
    TOKENS = {chain: tokens for chain, tokens in TOKENS.items() if chain in CHAINS}


def list_chain_settings(refresh: bool = False) -> List[Dict]:
    _load_data(refresh=refresh)
    return list(CHAINS.values())

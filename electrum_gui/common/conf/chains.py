import collections
import hashlib
import io
import json
import os
import zipfile
from typing import Dict, List, Optional, Set

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
        "bip44_target_level": "ADDRESS_INDEX",
        "bip44_auto_increment_level": "ACCOUNT",
        "bip44_last_hardened_level": "ACCOUNT",
        "default_address_encoding": "P2WPKH-P2SH",
        "bip44_purpose_options": {
            "P2PKH": 44,
            "P2WPKH-P2SH": 49,
            "P2WPKH": 84,
        },
        "fee_price_decimals_for_legibility": 0,
    },
    "evm": {
        "chain_model": "account",
        "curve": "secp256k1",
        "chain_affinity": "eth",
        "bip44_coin_type": 60,
        "bip44_target_level": "ADDRESS_INDEX",
        "bip44_auto_increment_level": "ADDRESS_INDEX",
        "bip44_last_hardened_level": "ACCOUNT",
        "default_address_encoding": None,
        "bip44_purpose_options": {},
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
        "bip44_purpose_options": {},
        "fee_price_decimals_for_legibility": 0,
    },
    "sol": {
        "chain_model": "account",
        "curve": "ed25519",
        "chain_affinity": "sol",
        "bip44_coin_type": 501,
        "bip44_target_level": "CHANGE",
        "bip44_auto_increment_level": "ACCOUNT",
        "bip44_last_hardened_level": "CHANGE",
        "default_address_encoding": None,
        "bip44_purpose_options": {},
        "fee_price_decimals_for_legibility": 0,
    },
}

CHAINS = {
    # Below are basic example of btc/tbtc/eth/teth
    #
    # "btc": {
    #     # Properties required by app
    #     "impl": "bitcoin",
    #     "chain_id": None,
    #     "code": "btc",
    #     "name": "Bitcoin",
    #     "shortname": "BTC",
    #     "testnet_of": None,
    #     "fee_coin": "btc",
    #     "bip44_coin_type": 0,
    #     "hardware_supported": True,
    #     "tokens_supported": False,
    #     "explorers": [
    #         {
    #             "name": "https://btc.com/",
    #             "address": "https://btc.com/{address}",
    #             "transaction": "https://btc.com/{transaction}",
    #             "block": "https://btc.com/{block}",
    #         },
    #     ],
    #     # Properties required by python lib
    #     "qr_code_prefix": "btc",
    #     "chain_model": "utxo",
    #     "curve": "secp256k1",
    #     "chain_affinity": "btc",
    #     "bip44_target_level": "ADDRESS_INDEX",
    #     "bip44_auto_increment_level": "ACCOUNT",
    #     "bip44_last_hardened_level": "ADDRESS_INDEX",
    #     "default_address_encoding": "P2WPKH-P2SH",
    #     "bip44_purpose_options": {
    #         "P2PKH": 44,
    #         "P2WPKH-P2SH": 49,
    #         "P2WPKH": 84,
    #     },
    #     "fee_price_decimals_for_legibility": 0,
    #     # Coins
    #     "coins": [
    #         {
    #             "code": "btc",
    #             "symbol": "BTC",
    #             "decimals": 8,
    #         },
    #     ],
    # },
    # "tbtc": {
    #     # Properties required by app
    #     "impl": "bitcoin",
    #     "chain_id": None,
    #     "code": "tbtc",
    #     "name": "Bitcoin TestNet3",
    #     "shortname": "TBTC",
    #     "testnet_of": "btc",
    #     "fee_coin": "tbtc",
    #     "bip44_coin_type": 1,
    #     "hardware_supported": True,
    #     "tokens_supported": False,
    #     "explorers": [
    #         {
    #             "name": "https://live.blockcypher.com/",
    #             "address": "https://live.blockcypher.com/btc-testnet/address/{address}",
    #             "transaction": "https://live.blockcypher.com/btc-testnet/tx/{transaction}",
    #             "block": "https://https://live.blockcypher.com/btc-testnet/block/{block}",
    #         },
    #     ],
    #     # Properties required by python lib
    #     "qr_code_prefix": "btc",
    #     "chain_model": "utxo",
    #     "curve": "secp256k1",
    #     "chain_affinity": "btc",
    #     "bip44_target_level": "ADDRESS_INDEX",
    #     "bip44_auto_increment_level": "ACCOUNT",
    #     "bip44_last_hardened_level": "ADDRESS_INDEX",
    #     "default_address_encoding": "P2WPKH-P2SH",
    #     "bip44_purpose_options": {
    #         "P2PKH": 44,
    #         "P2WPKH-P2SH": 49,
    #         "P2WPKH": 84,
    #     },
    #     "fee_price_decimals_for_legibility": 0,
    #     # Coins
    #     "coins": [
    #         {
    #             "code": "tbtc",
    #             "symbol": "TBTC",
    #             "decimals": 8,
    #         },
    #     ],
    # },
    # "eth": {
    #     # Properties required by app
    #     "impl": "evm",
    #     "chain_id": "1",
    #     "code": "eth",
    #     "name": "Ethereum",
    #     "shortname": "ETH",
    #     "testnet_of": None,
    #     "fee_coin": "eth",
    #     "bip44_coin_type": 60,
    #     "hardware_supported": True,
    #     "tokens_supported": True,
    #     "explorers": [
    #         {
    #             "name": "https://cn.etherscan.com/",
    #             "address": "https://cn.etherscan.com/address/{address}",
    #             "transaction": "https://cn.etherscan.com/tx/{transaction}",
    #             "block": "https://cn.etherscan.com/block/{block}",
    #         },
    #     ],
    #     # Properties required by python lib
    #     "qr_code_prefix": "eth",
    #     "chain_model": "account",
    #     "curve": "secp256k1",
    #     "chain_affinity": "eth",
    #     "bip44_target_level": "ADDRESS_INDEX",
    #     "bip44_auto_increment_level": "ADDRESS_INDEX",
    #     "bip44_last_hardened_level": "ACCOUNT",
    #     "default_address_encoding": None,
    #     "bip44_purpose_options": {},
    #     "fee_price_decimals_for_legibility": 9,
    #     "clients": [
    #         {
    #             "class": "Geth",
    #             "url": "https://rpc.blkdb.cn/eth",
    #         },
    #         {
    #             "class": "Geth",
    #             "url": "https://eth1.onekey.so/rpc",
    #         },
    #     ],
    #     "prices": {
    #         [
    #             {
    #                 "channel": "coingecko",
    #                 "code_mappings": {"eth": "ethereum"},
    #             },
    #             {
    #                 "channel": "uniswap",
    #                 "router_address": "0x7a250d5630b4cf539739df2c5dacb4c659f2488d",
    #                 "base_token_address": "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2",
    #                 "media_token_addresses": [
    #                     "0x2260fac5e5542a773aa44fbcfedf7c193bc2c599",
    #                 ],
    #             },
    #         ],
    #     },
    #     # Coins
    #     "coins": [
    #         {
    #             "code": "eth",
    #             "symbol": "ETH",
    #             "decimals": 18,
    #         },
    #     ],
    # },
    # "teth": {
    #     # Properties required by app
    #     "impl": "evm",
    #     "chain_id": "3",
    #     "code": "teth",
    #     "name": "Ethereum ropsten Test Network",
    #     "shortname": "Ropsten",
    #     "testnet_of": "eth",
    #     "fee_coin": "teth",
    #     "bip44_coin_type": 60,
    #     "hardware_supported": True,
    #     "tokens_supported": True,
    #     "explorers": [
    #         {
    #             "name": "https://teth.bitaps.com/",
    #             "address": "https://teth.bitaps.com/{address}",
    #             "transaction": "https://teth.bitaps.com/{transaction}",
    #             "block": "https://teth.bitaps.com/{block}",
    #         },
    #     ],
    #     # Properties required by python lib
    #     "qr_code_prefix": "eth",
    #     "chain_model": "account",
    #     "curve": "secp256k1",
    #     "chain_affinity": "eth",
    #     "bip44_target_level": "ADDRESS_INDEX",
    #     "bip44_auto_increment_level": "ADDRESS_INDEX",
    #     "bip44_last_hardened_level": "ACCOUNT",
    #     "default_address_encoding": None,
    #     "bip44_purpose_options": {},
    #     "fee_price_decimals_for_legibility": 9,
    #     "clients": [
    #         {
    #             "class": "Geth",
    #             "url": "https://ropsten.infura.io/v3/f001ce716b6e4a33a557f74df6fe8eff",
    #         },
    #     ],
    #     # Coins
    #     "coins": [
    #         {
    #             "code": "teth",
    #             "symbol": "TETH",
    #             "decimals": 18,
    #         },
    #     ],
    # },
}
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

                impl = CHAIN_IMPLS.get(chain_settings["impl"])
                if impl is None:
                    continue
                if chain_settings.get("staging", False) and not settings.IS_DEV:
                    continue

                # Fill with implementation defaults
                for k, v in impl.items():
                    chain_settings.setdefault(k, v)
                # Fill with defaults
                chain_settings.setdefault("chain_id", None)
                chain_settings.setdefault("qr_code_prefix", chain)
                chain_settings.setdefault("testnet_of", None)
                chain_settings.setdefault("explorers", [])

                if chain == "tbtc":  # Set tbtc chain and coin as btc
                    chain = "btc"
                    chain_settings["code"] = "btc"
                    chain_settings["fee_coin"] = "btc"
                    chain_settings["coins"][0]["code"] = "btc"

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


def get_evm_chains_to_recover() -> List[str]:
    # See issue #1751
    if settings.IS_DEV:
        return [chain_code for chain_code in ("teth", "tbsc", "theco", "tokt") if chain_code in CHAINS]
    else:
        return [chain_code for chain_code in ("eth", "bsc", "heco", "okt") if chain_code in CHAINS]


def get_coingecko_ids() -> List[str]:
    _load_data()
    return list(PRICE["coingecko_mappings"].keys())


def get_coin_codes_by_coingecko_id(cgk_id: str) -> List[str]:
    _load_data()
    return PRICE["coingecko_mappings"].get(cgk_id) or []


def get_uniswap_configs(chain_code: str) -> Optional[Dict]:
    _load_data()
    return PRICE["uniswap_configs"].get(chain_code)


def get_client_configs(chain_code: str) -> List[Dict[str, str]]:
    _load_data()
    return CHAINS.get(chain_code, {}).get("clients") or []


def get_added_coins(existing_coin_codes: Set[str]) -> List[Dict[str, str]]:
    ret = []

    _load_data()
    for chain_code, chain_settings in CHAINS.items():
        for coin_info in chain_settings.get("coins", []):
            coin_code = coin_info["code"]
            if coin_code in existing_coin_codes:
                continue
            ret.append(
                {
                    "code": coin_code,
                    "chain_code": chain_code,
                    "name": coin_info["symbol"],  # This doesn't matter.
                    "symbol": coin_info["symbol"],
                    "decimals": coin_info["decimals"],
                }
            )

    return ret


def get_added_chains(existing_chain_codes: Set[str]) -> List[Dict]:
    ret = []

    _load_data()
    for chain_code, chain_settings in CHAINS.items():
        if chain_code in existing_chain_codes:
            continue

        added_chain = {
            "chain_code": chain_code,
            "fee_code": chain_settings["fee_coin"],
            "name": chain_settings["name"],
            "chain_model": chain_settings["chain_model"],
            "curve": chain_settings["curve"],
            "chain_affinity": chain_settings["chain_affinity"],
            "qr_code_prefix": chain_settings["qr_code_prefix"],
            "bip44_coin_type": chain_settings["bip44_coin_type"],
            "bip44_target_level": chain_settings["bip44_target_level"],
            "bip44_auto_increment_level": chain_settings["bip44_auto_increment_level"],
            "bip44_last_hardened_level": chain_settings["bip44_last_hardened_level"],
            "default_address_encoding": chain_settings["default_address_encoding"],
            "chain_id": chain_settings["chain_id"],
            "bip44_purpose_options": chain_settings["bip44_purpose_options"],
            "fee_price_decimals_for_legibility": chain_settings["fee_price_decimals_for_legibility"],
        }
        ret.append(added_chain)

    return ret


try:
    # Add chains from local settings
    CHAINS.update(settings.CHAINS)
except AttributeError:
    pass

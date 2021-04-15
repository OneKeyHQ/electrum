from typing import List

import electrum.bip32
from electrum_gui.common.basic.functional.require import require


def encode_base58_check(value: bytes) -> str:
    return electrum.bip32.EncodeBase58Check(value)


def decode_base58_check(value: str) -> bytes:
    return electrum.bip32.DecodeBase58Check(value)


def hmac_oneshot(key: bytes, msg: bytes, digest) -> bytes:
    return electrum.bip32.hmac_oneshot(key, msg, digest)


def decode_bip32_path(path: str) -> List[int]:
    return electrum.bip32.convert_bip32_path_to_list_of_uint32(path)


def encode_bip32_path(path_as_ints: List[int]) -> str:
    return electrum.bip32.convert_bip32_intpath_to_strpath(path_as_ints)


def hash_160(digest: bytes) -> bytes:
    return electrum.bip32.hash_160(digest)


def merge_bip32_paths(*paths: str) -> str:
    nodes = ["m"]
    paths = (i for i in paths if i)
    for path in paths:
        sub_nodes = path.split("/")
        sub_nodes = (i for i in sub_nodes if i and i.lower() != "m")
        nodes.extend(sub_nodes)

    return "/".join(nodes)


def diff_bip32_paths(src: str, dst: str) -> str:
    require(src.startswith("m") and dst.startswith("m") and dst.startswith(src))
    return "m" + dst[len(src) :]

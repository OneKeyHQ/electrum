from typing import List

import electrum.bip32


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

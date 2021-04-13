import hashlib
import struct
from abc import ABC
from typing import Tuple

import ecdsa

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret import exceptions, utils
from electrum_gui.common.secret.interfaces import BIP32Interface


def _ckd_prv(
    curve: ecdsa.curves.Curve,
    parent_prvkey: bytes,
    compressed_parent_pubkey: bytes,
    parent_chaincode: bytes,
    is_hardened_child: bool,
    child_index: int,
) -> Tuple[bytes, bytes]:
    child_number = child_index.to_bytes(length=4, byteorder="big")

    if is_hardened_child:
        data = bytes([0]) + parent_prvkey + child_number
    else:
        compressed_parent_pubkey = compressed_parent_pubkey
        data = compressed_parent_pubkey + child_number

    i_64 = utils.hmac_oneshot(parent_chaincode, data, hashlib.sha512)
    i_left = ecdsa.util.string_to_number(i_64[:32])
    child_prvkey = (i_left + ecdsa.util.string_to_number(parent_prvkey)) % curve.order

    if i_left >= curve.order or child_prvkey == 0:
        raise exceptions.InvalidECPointException()  # refer to https://github.com/trezor/trezor-crypto/blob/master/bip32.c, leave it raise

    child_prvkey = int.to_bytes(child_prvkey, length=32, byteorder='big', signed=False)
    child_chain_code = i_64[32:]
    return child_prvkey, child_chain_code


def _ckd_pub(
    curve: ecdsa.curves.Curve, parent_pubkey: bytes, parent_chaincode: bytes, child_index: int
) -> Tuple[bytes, bytes]:
    child_number = child_index.to_bytes(length=4, byteorder="big")
    i_64 = utils.hmac_oneshot(parent_chaincode, parent_pubkey + child_number, hashlib.sha512)
    the_point = (
        ecdsa.SigningKey.from_string(i_64[:32], curve=curve, hashfunc=None).verifying_key.pubkey.point
        + ecdsa.VerifyingKey.from_string(parent_pubkey, curve=curve, hashfunc=None).pubkey.point
    )
    if the_point == ecdsa.ellipticcurve.INFINITY:
        raise exceptions.InvalidECPointException()  # refer to https://github.com/trezor/trezor-crypto/blob/master/bip32.c, leave it raise

    pubkey = ecdsa.VerifyingKey.from_public_point(the_point, curve=curve, hashfunc=None).to_string("compressed")
    child_chain_code = i_64[32:]
    return pubkey, child_chain_code


def extract_hd_wif_data(data: bytes):
    depth = ord(data[4:5])
    parent_fingerprint, child_index = struct.unpack(">4sL", data[5:13])
    chaincode = data[13:45]
    is_private = data[45:46] == b'\0'
    key_data = data[45:]

    return depth, parent_fingerprint, child_index, chaincode, is_private, key_data


class BaseBIP32ECDSA(BIP32Interface, ABC):
    @classmethod
    def from_master_seed(cls, master_seed: bytes) -> "BIP32Interface":
        require(cls.bip32_salt is not None)
        require(cls.key_class is not None)

        curve: ecdsa.curves.Curve = getattr(cls.key_class, "curve")
        require(curve is not None)

        while True:
            i_64 = utils.hmac_oneshot(key=cls.bip32_salt, msg=master_seed, digest=hashlib.sha512)
            prvkey, chain_code = i_64[:32], i_64[32:]
            prvkey_as_number = ecdsa.util.string_to_number(prvkey)
            if prvkey_as_number != 0 and prvkey_as_number < curve.order:
                break
            else:
                master_seed = i_64

        return cls(prvkey=prvkey, chain_code=chain_code)

    @classmethod
    def deserialize(cls, data: bytes) -> "BIP32Interface":
        depth, parent_fingerprint, child_index, chaincode, is_private, key_data = extract_hd_wif_data(data)
        if is_private:
            prvkey = key_data[1:]
            pubkey = None
        else:
            prvkey = None
            pubkey = key_data

        return cls(
            prvkey=prvkey,
            pubkey=pubkey,
            chain_code=chaincode,
            depth=depth,
            parent_fingerprint=parent_fingerprint,
            child_index=child_index,
        )

    def _derive(self, child_index: int, is_hardened: bool, as_private: bool) -> "BIP32Interface":
        curve = getattr(self.key_class, "curve")
        require(curve is not None)

        depth = self.depth + 1
        parent_fingerprint = self.fingerprint

        prvkey = None
        pubkey = None
        if as_private:
            prvkey, child_chain_code = _ckd_prv(
                curve, self._prvkey, self._pubkey, self.chain_code, is_hardened, child_index
            )
        else:
            pubkey, child_chain_code = _ckd_pub(curve, self._pubkey, self.chain_code, child_index)

        return self.__class__(
            prvkey=prvkey,
            pubkey=pubkey,
            chain_code=child_chain_code,
            depth=depth,
            parent_fingerprint=parent_fingerprint,
            child_index=child_index,
        )

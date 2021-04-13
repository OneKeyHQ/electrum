from typing import Type

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret import bip32, keys
from electrum_gui.common.secret.data import CurveEnum
from electrum_gui.common.secret.interfaces import BIP32Interface, KeyInterface

KEY_CLASS_MAPPING = {
    CurveEnum.SECP256K1: keys.ECDSASecp256k1,
    CurveEnum.SECP256R1: keys.ECDSASecp256r1,
    CurveEnum.ED25519: keys.ED25519,
}


def key_class_on_curve(curve: CurveEnum) -> Type[KeyInterface]:
    require(curve in KEY_CLASS_MAPPING, f"{curve} unsupported")
    return KEY_CLASS_MAPPING[curve]


BIP32_CLASS_MAPPING = {
    CurveEnum.SECP256K1: bip32.BIP32Secp256k1,
    CurveEnum.SECP256R1: bip32.BIP32Secp256r1,
    CurveEnum.ED25519: bip32.BIP32ED25519,
}


def bip32_class_on_curve(curve: CurveEnum) -> Type[BIP32Interface]:
    require(curve in BIP32_CLASS_MAPPING, f"{curve} unsupported")
    return BIP32_CLASS_MAPPING[curve]

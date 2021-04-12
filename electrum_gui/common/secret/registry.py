from typing import Type

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret import keys
from electrum_gui.common.secret.data import CurveEnum
from electrum_gui.common.secret.interfaces import KeyInterface

KEY_CLASS_MAPPING = {
    CurveEnum.SECP256K1: keys.ECDSASecp256k1,
    CurveEnum.SECP256R1: keys.ECDSASecp256r1,
    CurveEnum.ED25519: keys.ED25519,
}


def key_class_on_curve(curve: CurveEnum) -> Type[KeyInterface]:
    require(curve in KEY_CLASS_MAPPING, f"{curve} unsupported")
    return KEY_CLASS_MAPPING[curve]

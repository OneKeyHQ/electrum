import ecdsa

from electrum_gui.common.secret.keys.base import BaseECDSAKey


class ECDSASecp256k1(BaseECDSAKey):
    curve = ecdsa.curves.SECP256k1

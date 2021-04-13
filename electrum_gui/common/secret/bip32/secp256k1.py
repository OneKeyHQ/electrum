from electrum_gui.common.secret.bip32.base import BaseBIP32ECDSA
from electrum_gui.common.secret.keys.secp256k1 import ECDSASecp256k1


class BIP32Secp256k1(BaseBIP32ECDSA):
    bip32_salt = b"Bitcoin seed"
    key_class = ECDSASecp256k1

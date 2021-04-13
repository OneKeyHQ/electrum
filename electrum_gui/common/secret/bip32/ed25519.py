import hashlib
import struct

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret import utils
from electrum_gui.common.secret.bip32 import base
from electrum_gui.common.secret.interfaces import BIP32Interface
from electrum_gui.common.secret.keys.ed25519 import ED25519


class BIP32ED25519(BIP32Interface):
    bip32_salt = b"ed25519 seed"
    key_class = ED25519

    @classmethod
    def deserialize(cls, data: bytes) -> "BIP32Interface":
        depth, parent_fingerprint, child_index, chaincode, is_private, key_data = base.extract_hd_wif_data(data)
        if is_private:
            prvkey = data[1:]
            pubkey = None
        else:
            prvkey = None
            pubkey = data[1:]

        return cls(
            prvkey=prvkey,
            pubkey=pubkey,
            chain_code=chaincode,
            depth=depth,
            parent_fingerprint=parent_fingerprint,
            child_index=child_index,
        )

    def _derive(self, child_index: int, is_hardened: bool, as_private: bool) -> "BIP32Interface":
        require(is_hardened, "Hardened only")

        depth = self.depth + 1
        parent_fingerprint = self.fingerprint

        data = struct.pack("x") + self._prvkey + struct.pack(">I", child_index)
        i_64 = utils.hmac_oneshot(self.chain_code, data, hashlib.sha512)

        return self.__class__(
            prvkey=i_64[:32],
            chain_code=i_64[32:],
            depth=depth,
            parent_fingerprint=parent_fingerprint,
            child_index=child_index,
        )

    @property
    def fingerprint(self) -> bytes:
        """
        Refer to https://github.com/trezor/trezor-crypto/blob/master/bip32.c
        use b"\1" padded instead of slip-0010 b"\0", so the result of fingerprint is different from slip-0010
        """
        return utils.hash_160(b"\1" + self._pubkey)[:4]

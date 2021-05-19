from typing import Tuple

from nacl import exceptions, signing

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret.interfaces import KeyInterface


class ED25519(KeyInterface):
    def __init__(self, prvkey: bytes = None, pubkey: bytes = None):
        super(ED25519, self).__init__(prvkey=prvkey, pubkey=pubkey)

        self._prvkey = None
        self._pubkey = None

        if prvkey is not None:
            require(len(prvkey) == 32, f"Length of prvkey should be 32 on ed25519, but now is {len(prvkey)}")
            self._prvkey = signing.SigningKey(prvkey)
            self._pubkey = self._prvkey.verify_key
        else:
            require(len(pubkey) == 32, f"Length of pubkey should be 32 on ed25519, but now is {len(pubkey)}")
            self._pubkey = signing.VerifyKey(pubkey)

    def get_pubkey(self, compressed=True) -> bytes:
        return bytes(self._pubkey)

    def verify(self, digest: bytes, signature: bytes) -> bool:
        try:
            _ = self._pubkey.verify(digest, signature)
        except exceptions.BadSignatureError:
            return False
        else:
            return True

    def has_prvkey(self) -> bool:
        return self._prvkey is not None

    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        super().sign(digest)
        return self._prvkey.sign(digest).signature, 0

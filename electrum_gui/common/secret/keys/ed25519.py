from typing import Tuple

from trezorlib import _ed25519 as ed25519  # noqa replace to origin ed25519?

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret.interfaces import KeyInterface


class ED25519(KeyInterface):
    def __init__(self, prvkey: bytes = None, pubkey: bytes = None):
        super(ED25519, self).__init__(prvkey=prvkey, pubkey=pubkey)

        self._prvkey = None
        self._pubkey = None

        if prvkey is not None:
            require(len(prvkey) == 32, f"Length of prvkey should be 32 on ed25519, but now is {len(prvkey)}")
            self._prvkey = prvkey
            self._pubkey = ed25519.publickey_unsafe(self._prvkey)
        else:
            require(len(pubkey) == 32, f"Length of pubkey should be 32 on ed25519, but now is {len(pubkey)}")
            self._pubkey = pubkey

    def get_pubkey(self, compressed=True) -> bytes:
        return self._pubkey

    def verify(self, digest: bytes, signature: bytes) -> bool:
        try:
            ed25519.checkvalid(signature, digest, self.get_pubkey())
            return True
        except ed25519.SignatureMismatch:
            return False

    def has_prvkey(self) -> bool:
        return self._prvkey is not None

    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        super(ED25519, self).sign(digest)
        return ed25519.signature_unsafe(digest, self._prvkey, self._pubkey), 0

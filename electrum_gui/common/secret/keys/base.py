import hashlib
from abc import ABC
from typing import Optional, Tuple

import ecdsa

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret.interfaces import KeyInterface


class BaseECDSAKey(KeyInterface, ABC):
    curve: ecdsa.curves.Curve = None

    def __init__(self, prvkey: bytes = None, pubkey: bytes = None):
        require(self.curve is not None, f"Please specify 'curve' for <{self.__class__}>")
        super(BaseECDSAKey, self).__init__(prvkey=prvkey, pubkey=pubkey)

        self._signing_key: Optional[ecdsa.keys.SigningKey] = None
        self._verifying_key: Optional[ecdsa.keys.VerifyingKey] = None

        if prvkey is not None:
            require(len(prvkey) == 32, f"Length of prvkey should be 32 on {self.curve.name}, but now is {len(prvkey)}")
            self._signing_key = ecdsa.keys.SigningKey.from_string(prvkey, curve=self.curve, hashfunc=None)
            self._verifying_key = self._signing_key.verifying_key
        else:
            require(
                len(pubkey) in (33, 64, 65),
                f"Length of pubkey should be 33, 64 or 65 on {self.curve.name}, but now is {len(pubkey)}",
            )
            self._verifying_key = ecdsa.keys.VerifyingKey.from_string(pubkey, curve=self.curve, hashfunc=None)

    def has_prvkey(self) -> bool:
        return self._signing_key is not None

    def get_pubkey(self, compressed=True) -> bytes:
        return self._verifying_key.to_string(encoding="compressed" if compressed else "uncompressed")

    def verify(self, digest: bytes, signature: bytes) -> bool:
        try:
            return self._verifying_key.verify_digest(signature, digest)
        except ecdsa.keys.BadSignatureError:
            return False

    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        super(BaseECDSAKey, self).sign(digest)

        signature = self._signing_key.sign_digest_deterministic(
            digest, hashfunc=hashlib.sha256, sigencode=ecdsa.util.sigencode_string_canonize
        )
        rec_id = self._bruteforce_recid(digest, signature)
        return signature, rec_id

    def _bruteforce_recid(self, digest: bytes, signature: bytes) -> int:
        r, s = ecdsa.util.sigdecode_string(signature, self.curve.order)
        digest_as_number = ecdsa.util.string_to_number(digest)
        for recid in range(4):
            try:
                candidate = self._recover_public_key(digest_as_number, r, s, recid, self.curve)
                if candidate == self.get_pubkey() and self.__class__(pubkey=candidate).verify(digest, signature):
                    return recid
            except Exception as e:
                print(f"Error in trying by recid<{recid}>. error: {e}")

        raise Exception("No recid fits")

    @staticmethod
    def _recover_public_key(digest: int, r: int, s: int, recid: int, curve: ecdsa.curves.Curve) -> bytes:
        curve_fp = curve.curve
        n = curve.order
        e = digest
        x = r + (recid // 2) * n

        alpha = (pow(x, 3, curve_fp.p()) + (curve_fp.a() * x) + curve_fp.b()) % curve_fp.p()
        beta = ecdsa.numbertheory.square_root_mod_prime(alpha, curve_fp.p())
        y = beta if (beta - recid) % 2 == 0 else curve_fp.p() - beta

        generator = ecdsa.ellipticcurve.PointJacobi(curve_fp, x, y, 1, n)
        point = ecdsa.numbertheory.inverse_mod(r, n) * (s * generator + (-e % n) * curve.generator)
        verifying_key = ecdsa.VerifyingKey.from_public_point(point, curve, hashfunc=None)
        return verifying_key.to_string("compressed")

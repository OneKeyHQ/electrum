import ecdsa

from electrum_gui.common.secret.keys.base import BaseECDSAKey


class ECDSASecp256r1(BaseECDSAKey):
    curve = ecdsa.curves.NIST256p  # alias of Secp256r1 refer to RFC-4492 (Appendix A)

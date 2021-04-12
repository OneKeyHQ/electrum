from enum import IntEnum, unique


@unique
class CurveEnum(IntEnum):
    SECP256K1 = 10
    SECP256R1 = 20
    ED25519 = 30

    @classmethod
    def to_choices(cls):
        return (
            (cls.SECP256K1, "Secp256k1"),
            (cls.SECP256R1, "Secp256r1"),
            (cls.ED25519, "ED25519"),
        )

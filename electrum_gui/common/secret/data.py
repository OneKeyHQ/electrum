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


@unique
class PubKeyType(IntEnum):
    PUBKEY = 10
    XPUB = 20

    @classmethod
    def to_choices(cls):
        return (
            (cls.PUBKEY, "Public Key"),
            (cls.XPUB, "Extended Public Key"),
        )


@unique
class SecretKeyType(IntEnum):
    PRVKEY = 10
    XPRV = 20
    SEED = 30

    @classmethod
    def to_choices(cls):
        return (
            (cls.PRVKEY, "Private Key"),
            (cls.XPRV, "Extended Private Key"),
            (cls.SEED, "Master Seed"),
        )

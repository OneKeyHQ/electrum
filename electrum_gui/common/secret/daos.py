from electrum_gui.common.secret.data import CurveEnum, PubKeyType, SecretKeyType
from electrum_gui.common.secret.models import PubKeyModel, SecretKeyModel


def new_pubkey_model(
    curve: CurveEnum,
    pubkey_type: PubKeyType,
    pubkey: str,
    path: str = None,
    parent_pubkey_id: int = None,
    secret_key_id: int = None,
) -> PubKeyModel:
    return PubKeyModel(
        curve=curve,
        pubkey_type=pubkey_type,
        pubkey=pubkey,
        path=path,
        parent_pubkey_id=parent_pubkey_id,
        secret_key_id=secret_key_id,
    )


def create_pubkey_model(
    curve: CurveEnum,
    pubkey_type: PubKeyType,
    pubkey: str,
    path: str = None,
    parent_pubkey_id: int = None,
    secret_key_id: int = None,
) -> PubKeyModel:
    return PubKeyModel.create(
        curve=curve,
        pubkey_type=pubkey_type,
        pubkey=pubkey,
        path=path,
        parent_pubkey_id=parent_pubkey_id,
        secret_key_id=secret_key_id,
    )


def get_pubkey_model_by_id(pubkey_id: int) -> PubKeyModel:
    return PubKeyModel.get_by_id(pubkey_id)


def create_secret_key_model(
    secret_key_type: SecretKeyType,
    encrypted_secret_key: str,
) -> SecretKeyModel:
    return SecretKeyModel.create(
        secret_key_type=secret_key_type,
        encrypted_secret_key=encrypted_secret_key,
    )


def get_secret_key_model_by_id(secret_key_id: int) -> SecretKeyModel:
    return SecretKeyModel.get_by_id(secret_key_id)

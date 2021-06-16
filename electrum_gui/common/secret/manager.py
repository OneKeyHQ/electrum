import logging
from typing import Tuple

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.secret import daos, encrypt, exceptions, registry, utils
from electrum_gui.common.secret.data import CurveEnum, PubKeyType, SecretKeyType
from electrum_gui.common.secret.interfaces import KeyInterface, SignerInterface, VerifierInterface
from electrum_gui.common.secret.models import PubKeyModel, SecretKeyModel

logger = logging.getLogger("app.secret")


def _verify_signing_process(sk: KeyInterface, verifier: VerifierInterface = None):
    require(sk.has_prvkey())
    message = b"Hello OneKey"
    sig, _ = sk.sign(message)
    verifier = verifier or sk.as_pubkey_version()
    require(verifier.verify(message, sig))


def verify_key(curve: CurveEnum, prvkey: bytes = None, pubkey: bytes = None):
    try:
        ins = registry.key_class_on_curve(curve).from_key(prvkey=prvkey, pubkey=pubkey)
        if ins.has_prvkey():
            _verify_signing_process(ins)
    except Exception as e:
        logger.exception("Error in verify key.", e)
        raise ValueError(f"Illegal pubkey. curve: {curve.name}, pubkey: {pubkey.hex()}")


def _verify_hd_wif_key(curve: CurveEnum, xkey: str):
    try:
        node = registry.bip32_class_on_curve(curve).from_hd_wif(xkey)
        if node.has_prvkey():
            _verify_signing_process(node.prvkey_interface, node.pubkey_interface)
    except Exception as e:
        logger.exception("Error in verify hd wif key.", e)
        error_message = f"Illegal hd wif key. curve: {curve.name}"
        if xkey.startswith("xpub"):
            error_message += f", xpub: {xkey}"
        raise ValueError(error_message)


def _verify_mnemonic(mnemonic: str):
    if not utils.check_mnemonic(mnemonic):
        raise ValueError("Illegal mnemonic.")


def _verify_master_seed(master_seed: bytes):
    curve = CurveEnum.SECP256K1
    try:
        node = registry.bip32_class_on_curve(CurveEnum.SECP256K1).from_master_seed(master_seed)
        _verify_signing_process(node.prvkey_interface, node.pubkey_interface)
    except Exception as e:
        logger.exception("Error in verify master seed.", e)
        raise ValueError(f"Illegal master seed. curve: {curve.name}")


def _verify_bip32_path(path: str):
    path_as_ints = utils.decode_bip32_path(path)

    if len(path_as_ints) <= 0:
        raise ValueError(f"Illegal path. path: {path}")


def _verify_parent_pubkey_id(parent_pubkey_id: int):
    parent_pubkey = daos.get_pubkey_model_by_id(parent_pubkey_id)

    if parent_pubkey.pubkey_type != PubKeyType.XPUB:
        raise ValueError(
            f"Type of Parent Pubkey should only be XPUB, but now is {parent_pubkey.pubkey_type}. "
            f"parent_pubkey_id: {parent_pubkey_id}"
        )


def import_pubkey(
    curve: CurveEnum, pubkey: bytes, path: str = None, parent_pubkey_id: int = None, secret_key_id: int = None
) -> PubKeyModel:
    verify_key(curve, pubkey=pubkey)
    path is None or _verify_bip32_path(path)
    parent_pubkey_id is None or _verify_parent_pubkey_id(parent_pubkey_id)
    secret_key_id is None or require(daos.get_secret_key_model_by_id(secret_key_id) is not None)

    return daos.create_pubkey_model(
        curve=curve,
        pubkey_type=PubKeyType.PUBKEY,
        pubkey=pubkey.hex(),
        path=path,
        parent_pubkey_id=parent_pubkey_id,
        secret_key_id=secret_key_id,
    )


def import_xpub(
    curve: CurveEnum, xpub: str, path: str = None, parent_pubkey_id: int = None, secret_key_id: int = None
) -> PubKeyModel:
    _verify_hd_wif_key(curve, xpub)
    path is None or _verify_bip32_path(path)
    parent_pubkey_id is None or _verify_parent_pubkey_id(parent_pubkey_id)
    secret_key_id is None or require(
        daos.get_secret_key_model_by_id(secret_key_id).secret_key_type in (SecretKeyType.SEED, SecretKeyType.XPRV)
    )

    return daos.create_pubkey_model(
        curve=curve,
        pubkey_type=PubKeyType.XPUB,
        pubkey=xpub,
        path=path,
        parent_pubkey_id=parent_pubkey_id,
        secret_key_id=secret_key_id,
    )


def import_prvkey(
    password: str,
    curve: CurveEnum,
    prvkey: bytes,
    path: str = None,
    parent_pubkey_id: int = None,
) -> Tuple[PubKeyModel, SecretKeyModel]:
    require(bool(password))
    verify_key(curve, prvkey=prvkey)
    path is None or _verify_bip32_path(path)
    parent_pubkey_id is None or _verify_parent_pubkey_id(parent_pubkey_id)

    encrypted_secret_key = encrypt.encrypt_data(password, prvkey.hex())

    with db.atomic():
        secret_key_model = daos.create_secret_key_model(SecretKeyType.PRVKEY, encrypted_secret_key)
        pubkey_model = import_pubkey(
            curve=curve,
            pubkey=registry.key_class_on_curve(curve).from_key(prvkey=prvkey).get_pubkey(),
            path=path,
            parent_pubkey_id=parent_pubkey_id,
            secret_key_id=secret_key_model.id,
        )
        return pubkey_model, secret_key_model


def import_xprv(
    password: str,
    curve: CurveEnum,
    xprv: str,
    path: str = None,
    parent_pubkey_id: int = None,
) -> Tuple[PubKeyModel, SecretKeyModel]:
    require(bool(password))
    _verify_hd_wif_key(curve, xprv)
    path is None or _verify_bip32_path(path)
    parent_pubkey_id is None or _verify_parent_pubkey_id(parent_pubkey_id)

    encrypted_secret_key = encrypt.encrypt_data(password, xprv)

    with db.atomic():
        secret_key_model = daos.create_secret_key_model(SecretKeyType.XPRV, encrypted_secret_key)
        pubkey_model = import_xpub(
            curve=curve,
            xpub=registry.bip32_class_on_curve(curve).from_hd_wif(xprv).get_hd_wif(),
            path=path,
            parent_pubkey_id=parent_pubkey_id,
            secret_key_id=secret_key_model.id,
        )
        return pubkey_model, secret_key_model


def import_mnemonic(
    password: str,
    mnemonic: str,
    passphrase: str = None,
) -> SecretKeyModel:
    require(bool(password))
    passphrase = passphrase or ""
    master_seed = mnemonic_to_seed(mnemonic, passphrase=passphrase)
    _verify_master_seed(master_seed)
    encrypted_secret_key = encrypt.encrypt_data(password, master_seed.hex())

    encrypted_message = encrypt.encrypt_data(password, "|".join((mnemonic, passphrase)))
    secret_key_model = daos.create_secret_key_model(
        SecretKeyType.SEED, encrypted_secret_key, encrypted_message=encrypted_message
    )
    return secret_key_model


def export_mnemonic(password: str, secret_key_id: int) -> Tuple[str, str]:
    secret_key_model = daos.get_secret_key_model_by_id(secret_key_id)
    require(secret_key_model.secret_key_type == SecretKeyType.SEED)

    message = encrypt.decrypt_data(password, secret_key_model.encrypted_message)
    mnemonic, passphrase = message.split("|", 1)

    master_seed = encrypt.decrypt_data(password, secret_key_model.encrypted_secret_key)
    require(mnemonic_to_seed(mnemonic, passphrase).hex() == master_seed)

    return mnemonic, passphrase


def derive_by_secret_key(
    password: str, curve: CurveEnum, secret_key_id: int, path: str, target_pubkey_type: PubKeyType = PubKeyType.XPUB
) -> PubKeyModel:
    secret_key = daos.get_secret_key_model_by_id(secret_key_id)
    require(secret_key.secret_key_type in (SecretKeyType.XPRV, SecretKeyType.SEED))
    origin_secret_key = encrypt.decrypt_data(password, secret_key.encrypted_secret_key)
    bip32_cls = registry.bip32_class_on_curve(curve)

    if secret_key.secret_key_type == SecretKeyType.XPRV:
        node = bip32_cls.from_hd_wif(origin_secret_key)
    else:
        node = bip32_cls.from_master_seed(bytes.fromhex(origin_secret_key))

    sub_node = node.derive_path(path)
    pubkey = (
        sub_node.get_hd_wif() if target_pubkey_type == PubKeyType.XPUB else sub_node.pubkey_interface.get_pubkey().hex()
    )
    return daos.new_pubkey_model(
        curve=curve,
        pubkey_type=target_pubkey_type,
        pubkey=pubkey,
        path=path,
        secret_key_id=secret_key_id,
    )


def derive_by_xpub(xpub_id: int, sub_path: str, target_pubkey_type: PubKeyType = PubKeyType.XPUB) -> PubKeyModel:
    pubkey_model = daos.get_pubkey_model_by_id(xpub_id)
    require(pubkey_model.pubkey_type == PubKeyType.XPUB)
    node = registry.bip32_class_on_curve(pubkey_model.curve).from_hd_wif(pubkey_model.pubkey)
    sub_node = node.derive_path(sub_path)
    pubkey = (
        sub_node.get_hd_wif() if target_pubkey_type == PubKeyType.XPUB else sub_node.pubkey_interface.get_pubkey().hex()
    )
    path = utils.merge_bip32_paths(pubkey_model.path, sub_path)
    return daos.new_pubkey_model(
        curve=pubkey_model.curve,
        pubkey_type=target_pubkey_type,
        pubkey=pubkey,
        path=path,
        parent_pubkey_id=xpub_id,
        secret_key_id=pubkey_model.secret_key_id,
    )


def update_secret_key_password(secret_key_id: int, old_password: str, new_password: str):
    require(bool(old_password) and bool(new_password))
    secret_key = daos.get_secret_key_model_by_id(secret_key_id)

    try:
        encrypt.decrypt_data(new_password, secret_key.encrypted_secret_key)  # password changed already
        return
    except exceptions.InvalidPassword:
        pass

    encrypted_secret_key = encrypt.encrypt_data(
        new_password, encrypt.decrypt_data(old_password, secret_key.encrypted_secret_key)
    )
    encrypted_message = (
        encrypt.encrypt_data(new_password, encrypt.decrypt_data(old_password, secret_key.encrypted_message))
        if secret_key.encrypted_message
        else None
    )
    daos.update_secret_key_encrypted_data(secret_key_id, encrypted_secret_key, encrypted_message)


def get_verifier(pubkey_id: int) -> VerifierInterface:
    pubkey_model = daos.get_pubkey_model_by_id(pubkey_id)

    if pubkey_model.pubkey_type == PubKeyType.XPUB:
        return raw_create_verifier_by_xpub(pubkey_model.curve, pubkey_model.pubkey)
    else:
        return raw_create_verifier_by_pubkey(pubkey_model.curve, bytes.fromhex(pubkey_model.pubkey))


def get_signer(password: str, pubkey_id: int) -> SignerInterface:
    require(bool(password))
    pubkey_model = daos.get_pubkey_model_by_id(pubkey_id)
    require(pubkey_model.secret_key_id is not None)
    secret_key = daos.get_secret_key_model_by_id(pubkey_model.secret_key_id)
    raw_secret_key = encrypt.decrypt_data(password, secret_key.encrypted_secret_key)

    if secret_key.secret_key_type == SecretKeyType.PRVKEY:
        return raw_create_key_by_prvkey(pubkey_model.curve, bytes.fromhex(raw_secret_key))
    elif secret_key.secret_key_type == SecretKeyType.XPRV:
        return raw_create_key_by_xprv(pubkey_model.curve, raw_secret_key, pubkey_model.path)
    else:
        return raw_create_key_by_master_seed(pubkey_model.curve, bytes.fromhex(raw_secret_key), pubkey_model.path)


def get_pubkey_by_id(pubkey_id: int) -> PubKeyModel:
    return daos.get_pubkey_model_by_id(pubkey_id)


def raw_create_verifier_by_pubkey(curve: CurveEnum, pubkey: bytes) -> VerifierInterface:
    verify_key(curve, pubkey=pubkey)
    return registry.key_class_on_curve(curve).from_key(pubkey=pubkey)


def raw_create_verifier_by_xpub(curve: CurveEnum, xpub: str, path: str = None) -> VerifierInterface:
    _verify_hd_wif_key(curve, xpub)
    path is None or _verify_bip32_path(path)

    node = registry.bip32_class_on_curve(curve).from_hd_wif(xpub)
    if path:
        node = node.derive_path(path)

    return node.pubkey_interface


def generate_mnemonic(strength: int) -> str:
    return utils.generate_mnemonic(strength)


def mnemonic_to_seed(mnemonic: str, passphrase: str = None) -> bytes:
    _verify_mnemonic(mnemonic)
    return utils.mnemonic_to_seed(mnemonic, passphrase)


def raw_create_key_by_prvkey(curve: CurveEnum, prvkey: bytes) -> KeyInterface:
    verify_key(curve, prvkey=prvkey)
    return registry.key_class_on_curve(curve).from_key(prvkey=prvkey)


def raw_create_key_by_xprv(curve: CurveEnum, xprv: str, path: str = None) -> KeyInterface:
    _verify_hd_wif_key(curve, xprv)
    path is None or _verify_bip32_path(path)

    node = registry.bip32_class_on_curve(curve).from_hd_wif(xprv)
    if path:
        node = node.derive_path(path)

    return node.prvkey_interface


def raw_create_key_by_master_seed(curve: CurveEnum, master_seed: bytes, path: str = None) -> KeyInterface:
    _verify_master_seed(master_seed)
    path or _verify_bip32_path(path)

    node = registry.bip32_class_on_curve(curve).from_master_seed(master_seed)
    if path:
        node = node.derive_path(path)

    return node.prvkey_interface

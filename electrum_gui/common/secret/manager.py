import logging
from typing import Tuple

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.secret import daos, encrypt, registry, utils
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


def verify_hd_wif_key(curve: CurveEnum, xkey: str):
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


def verify_master_seed(master_seed: bytes):
    curve = CurveEnum.SECP256K1
    try:
        node = registry.bip32_class_on_curve(CurveEnum.SECP256K1).from_master_seed(master_seed)
        _verify_signing_process(node.prvkey_interface, node.pubkey_interface)
    except Exception as e:
        logger.exception("Error in verify master seed.", e)
        raise ValueError(f"Illegal master seed. curve: {curve.name}")


def verify_bip32_path(path: str):
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
    path is None or verify_bip32_path(path)
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
    verify_hd_wif_key(curve, xpub)
    path is None or verify_bip32_path(path)
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
    verify_key(curve, prvkey=prvkey)
    path is None or verify_bip32_path(path)
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
    verify_hd_wif_key(curve, xprv)
    path is None or verify_bip32_path(path)
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


def import_master_seed(
    password: str,
    master_seed: bytes,
) -> SecretKeyModel:
    verify_master_seed(master_seed)
    encrypted_secret_key = encrypt.encrypt_data(password, master_seed.hex())
    secret_key_model = daos.create_secret_key_model(SecretKeyType.SEED, encrypted_secret_key)
    return secret_key_model


def derive_by_secret_key(password: str, curve: CurveEnum, secret_key_id: int, path: str) -> PubKeyModel:
    secret_key = daos.get_secret_key_model_by_id(secret_key_id)
    require(secret_key.secret_key_type in (SecretKeyType.XPRV, SecretKeyType.SEED))
    origin_secret_key = encrypt.decrypt_data(password, secret_key.encrypted_secret_key)
    bip32_cls = registry.bip32_class_on_curve(curve)

    if secret_key.secret_key_type == SecretKeyType.XPRV:
        node = bip32_cls.from_hd_wif(origin_secret_key)
    else:
        node = bip32_cls.from_master_seed(bytes.fromhex(origin_secret_key))

    sub_node = node.derive_path(path)
    return daos.new_pubkey_model(
        curve=curve,
        pubkey_type=PubKeyType.XPUB,
        pubkey=sub_node.get_hd_wif(),
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


def get_verifier(pubkey_id: int) -> VerifierInterface:
    pubkey_model = daos.get_pubkey_model_by_id(pubkey_id)
    if pubkey_model.pubkey_type == PubKeyType.XPUB:
        return registry.bip32_class_on_curve(pubkey_model.curve).from_hd_wif(pubkey_model.pubkey).pubkey_interface
    else:
        return registry.key_class_on_curve(pubkey_model.curve).from_key(pubkey=bytes.fromhex(pubkey_model.pubkey))


def get_signer(password: str, pubkey_id: int) -> SignerInterface:
    pubkey_model = daos.get_pubkey_model_by_id(pubkey_id)
    require(pubkey_model.secret_key_id is not None)
    secret_key = daos.get_secret_key_model_by_id(pubkey_model.secret_key_id)
    origin_secret_key = encrypt.decrypt_data(password, secret_key.encrypted_secret_key)
    if secret_key.secret_key_type == SecretKeyType.PRVKEY:
        return registry.key_class_on_curve(pubkey_model.curve).from_key(prvkey=bytes.fromhex(origin_secret_key))
    else:
        bip32_cls = registry.bip32_class_on_curve(pubkey_model.curve)

        if secret_key.secret_key_type == SecretKeyType.XPRV:
            node = bip32_cls.from_hd_wif(origin_secret_key)
        else:
            node = bip32_cls.from_master_seed(bytes.fromhex(origin_secret_key))

        if pubkey_model.path:
            node = node.derive_path(pubkey_model.path)

        return node.prvkey_interface

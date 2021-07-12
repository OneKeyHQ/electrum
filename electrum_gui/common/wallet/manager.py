import datetime
import functools
import logging
from collections import defaultdict
from contextlib import contextmanager
from decimal import Decimal
from itertools import groupby
from typing import Iterable, List, Tuple, Union

import eth_account

from electrum_gui.common.basic.bip44 import BIP44Level, BIP44Path
from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.functional.timing import timing_logger
from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.coin import codes
from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.coin.data import ChainInfo
from electrum_gui.common.provider import manager as provider_manager
from electrum_gui.common.provider.data import SignedTx, TxBroadcastReceipt, UnsignedTx
from electrum_gui.common.secret import manager as secret_manager
from electrum_gui.common.secret.data import PubKeyType
from electrum_gui.common.transaction import manager as transaction_manager
from electrum_gui.common.transaction.data import TxActionStatus
from electrum_gui.common.wallet import daos, exceptions, utils
from electrum_gui.common.wallet.data import WalletType
from electrum_gui.common.wallet.handlers import get_handler_by_chain_model
from electrum_gui.common.wallet.models import AccountModel, AssetModel, WalletModel

logger = logging.getLogger("app.wallet")


def has_primary_wallet() -> bool:
    return daos.wallet.has_primary_wallet()


@contextmanager
def _require_primary_wallet_exists():
    if not has_primary_wallet():
        raise exceptions.PrimaryWalletNotExists()

    yield


@contextmanager
def _require_primary_wallet_not_exists():
    if has_primary_wallet():
        raise exceptions.PrimaryWalletAlreadyExists()

    yield


def _get_wallet_by_id(wallet_id: int) -> WalletModel:
    wallet_model = daos.wallet.get_wallet_by_id(wallet_id)
    if wallet_model is None:
        raise exceptions.WalletNotFound(wallet_id)

    return wallet_model


def _create_default_wallet(
    chain_code: str,
    name: str,
    wallet_type: WalletType,
    address: str,
    address_encoding: str = None,
    pubkey_id: int = None,
    bip44_path: str = None,
) -> dict:
    with db.atomic():
        wallet = daos.wallet.create_wallet(name, wallet_type, chain_code)
        account = daos.account.create_account(
            wallet.id,
            chain_code,
            address,
            pubkey_id=pubkey_id,
            bip44_path=bip44_path,
            address_encoding=address_encoding,
        )
        asset = daos.asset.create_asset(wallet.id, account.id, chain_code, chain_code)

    return _build_wallet_info(wallet, account, [asset])


def import_watchonly_wallet_by_address(name: str, chain_code: str, address: str) -> dict:
    address_validation = provider_manager.verify_address(chain_code, address)

    if not address_validation.is_valid:
        raise exceptions.AddressInvalidException(chain_code, address)

    return _create_default_wallet(
        chain_code,
        name,
        WalletType.WATCHONLY,
        address_validation.normalized_address,
        address_encoding=address_validation.encoding,
    )


def import_watchonly_wallet_by_pubkey(name: str, chain_code: str, pubkey: bytes, address_encoding: str = None) -> dict:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    verifier = secret_manager.raw_create_verifier_by_pubkey(chain_info.curve, pubkey)
    address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)

    return import_watchonly_wallet_by_address(name, chain_code, address)


def import_standalone_wallet_by_prvkey(
    name: str, chain_code: str, prvkey: bytes, password: str, address_encoding: str = None
) -> dict:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    verifier = secret_manager.raw_create_key_by_prvkey(chain_info.curve, prvkey).as_pubkey_version()
    address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)

    with db.atomic():
        pubkey_model, _ = secret_manager.import_prvkey(password, chain_info.curve, prvkey)
        wallet_info = _create_default_wallet(
            chain_code,
            name,
            WalletType.SOFTWARE_STANDALONE_PRVKEY,
            address,
            pubkey_id=pubkey_model.id,
            address_encoding=address_encoding,
        )

    return wallet_info


def import_standalone_wallet_by_keystore(
    name: str, chain_code: str, keyfile_json: str, keystore_password: str, password: str, address_encoding: str = None
) -> dict:
    chain_info = coin_manager.get_chain_info(chain_code)
    require(chain_info.chain_affinity in (codes.ETH, codes.CFX))
    address_encoding = address_encoding or chain_info.default_address_encoding
    prvkey = utils.decrypt_eth_keystore(keyfile_json, keystore_password)
    return import_standalone_wallet_by_prvkey(name, chain_code, prvkey, password, address_encoding)


def import_standalone_wallet_by_mnemonic(
    name: str,
    chain_code: str,
    mnemonic: str,
    password: str,
    passphrase: str = None,
    address_encoding: str = None,
    bip44_path: str = None,
) -> dict:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding

    if bip44_path is None:
        bip44_path = get_default_bip44_path(chain_code, address_encoding).to_bip44_path()
    else:
        bip44_path_ins = BIP44Path.from_bip44_path(bip44_path)
        last_hardened_level = BIP44Level[chain_info.bip44_last_hardened_level.upper()]
        require(bip44_path_ins.last_hardened_level >= last_hardened_level)

    master_seed = secret_manager.mnemonic_to_seed(mnemonic, passphrase)
    verifier = secret_manager.raw_create_key_by_master_seed(
        chain_info.curve, master_seed, bip44_path
    ).as_pubkey_version()
    address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)

    with db.atomic():
        secret_key_model = secret_manager.import_mnemonic(password, mnemonic, passphrase)
        pubkey_model = secret_manager.import_pubkey(
            chain_info.curve, verifier.get_pubkey(), path=bip44_path, secret_key_id=secret_key_model.id
        )
        wallet_info = _create_default_wallet(
            chain_code,
            name,
            WalletType.SOFTWARE_STANDALONE_MNEMONIC,
            address,
            address_encoding=address_encoding,
            pubkey_id=pubkey_model.id,
            bip44_path=bip44_path,
        )

    return wallet_info


def create_primary_wallets(
    chain_codes: List[str],
    password: str,
    mnemonic: str = None,
    passphrase: str = None,
    mnemonic_strength: int = 128,
) -> List[dict]:
    mnemonic = mnemonic or secret_manager.generate_mnemonic(mnemonic_strength)
    master_seed = secret_manager.mnemonic_to_seed(mnemonic, passphrase)
    default_wallets = []

    for chain_code in chain_codes:
        chain_info = coin_manager.get_chain_info(chain_code)
        address_encoding = chain_info.default_address_encoding
        bip44_path = get_default_bip44_path(chain_code, address_encoding).to_bip44_path()
        verifier = secret_manager.raw_create_key_by_master_seed(chain_info.curve, master_seed, path=bip44_path)
        address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)
        default_wallets.append(
            {
                "name": f"{chain_code.upper()}-1",
                "chain_code": chain_code,
                "bip44_path": bip44_path,
                "address_encoding": address_encoding,
                "address": address,
            }
        )

    return create_selected_primary_wallets(mnemonic, password, selected_wallets=default_wallets, passphrase=passphrase)


@timing_logger("search_existing_wallets")
def search_existing_wallets(
    chain_codes: List[str],
    mnemonic: str,
    passphrase: str = None,
    bip44_max_searching_address_index: int = 20,
) -> List[dict]:
    require(0 < bip44_max_searching_address_index <= 20)

    result = []
    master_seed = secret_manager.mnemonic_to_seed(mnemonic, passphrase=passphrase)

    for chain_code in chain_codes:
        chain_info = coin_manager.get_chain_info(chain_code)
        candidates: List[dict] = []

        with timing_logger(f"search_existing_{chain_code}_wallets"):
            for address_encoding, path in _generate_searching_bip44_address_paths(
                chain_info, bip44_max_searching_address_index=bip44_max_searching_address_index
            ):
                verifier = secret_manager.raw_create_key_by_master_seed(chain_info.curve, master_seed, path)
                address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)
                candidates.append(
                    {
                        "chain_code": chain_code,
                        "bip44_path": path,
                        "address_encoding": address_encoding,
                        "address": address,
                    }
                )

            existing_wallets = []
            for candidate in candidates:
                try:
                    address_info = provider_manager.get_address(candidate["chain_code"], candidate["address"])
                    candidate["balance"] = address_info.balance
                    if address_info.existing:
                        existing_wallets.append(candidate)
                except Exception as e:
                    logger.exception(f"Error in get address. chain_code: {chain_code}, address: {address}, error: {e}")

            if existing_wallets:
                existing_wallets = [
                    {
                        "name": f"{wallet['chain_code'].upper()}-{index + 1}",
                        **wallet,
                    }
                    for index, wallet in enumerate(existing_wallets)
                ]
                result.extend(existing_wallets)
            else:
                first_wallet = candidates[0]
                first_wallet["name"] = f"{first_wallet['chain_code'].upper()}-1"
                result.append(first_wallet)

    return result


def _generate_searching_bip44_address_paths(
    chain_info: ChainInfo, bip44_account: int = 0, bip44_max_searching_address_index: int = 20
) -> Iterable[Union[str, str]]:
    options = chain_info.bip44_purpose_options or {}
    default_address_encoding = chain_info.default_address_encoding

    if not options:
        options[default_address_encoding] = 44
    elif default_address_encoding in options:
        options = {default_address_encoding: options.pop(default_address_encoding), **options}

    last_hardened_level = BIP44Level[chain_info.bip44_last_hardened_level.upper()]
    target_level = BIP44Level[chain_info.bip44_target_level.upper()]
    for encoding, purpose in options.items():
        ins = BIP44Path(
            purpose=purpose,
            coin_type=chain_info.bip44_coin_type,
            account=bip44_account,
            last_hardened_level=last_hardened_level,
        ).to_target_level(target_level)

        for _ in range(bip44_max_searching_address_index):
            yield encoding, ins.to_bip44_path()
            ins = ins.next_sibling()


@_require_primary_wallet_not_exists()
def create_selected_primary_wallets(
    mnemonic: str,
    password: str,
    selected_wallets: List[dict],
    passphrase: str = None,
) -> List[dict]:
    master_seed = secret_manager.mnemonic_to_seed(mnemonic, passphrase=passphrase)
    to_be_created_wallets: List[dict] = []
    selected_wallets = sorted(selected_wallets, key=lambda i: i["chain_code"])

    for chain_code, group in groupby(selected_wallets, lambda i: i["chain_code"]):
        chain_info = coin_manager.get_chain_info(chain_code)

        for wallet in group:
            verifier = secret_manager.raw_create_key_by_master_seed(chain_info.curve, master_seed, wallet["bip44_path"])
            address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=wallet["address_encoding"])
            if address == wallet["address"]:
                to_be_created_wallets.append(
                    {
                        "curve": chain_info.curve,
                        "pubkey": verifier.get_pubkey(),
                        **wallet,
                    }
                )

    require(len(to_be_created_wallets) > 0)

    with db.atomic():
        with _require_primary_wallet_not_exists():
            secret_key_model = secret_manager.import_mnemonic(password, mnemonic, passphrase)

            created_wallets = []
            for wallet in to_be_created_wallets:
                pubkey_model = secret_manager.import_pubkey(
                    wallet["curve"], wallet["pubkey"], path=wallet["bip44_path"], secret_key_id=secret_key_model.id
                )
                wallet_info = _create_default_wallet(
                    wallet["chain_code"],
                    wallet["name"],
                    WalletType.SOFTWARE_PRIMARY,
                    wallet["address"],
                    address_encoding=wallet["address_encoding"],
                    pubkey_id=pubkey_model.id,
                    bip44_path=pubkey_model.path,
                )

                created_wallets.append(wallet_info)

    return created_wallets


def update_wallet_name(wallet_id: int, name: str):
    daos.wallet.update_wallet_name(wallet_id, name)


def update_wallet_password(wallet_id: int, old_password: str, new_password: str):
    wallet = _get_wallet_by_id(wallet_id)
    require(
        wallet.type
        in (
            WalletType.SOFTWARE_PRIMARY,
            WalletType.SOFTWARE_STANDALONE_PRVKEY,
            WalletType.SOFTWARE_STANDALONE_MNEMONIC,
        )
    )
    require(bool(old_password) and bool(new_password))
    secret_key_id = _get_wallet_secret_key_id(wallet_id)
    secret_manager.update_secret_key_password(secret_key_id, old_password, new_password)


def check_wallet_password(wallet_id: int, password: str):
    update_wallet_password(
        wallet_id, password, password
    )  # todo separate check_wallet_password from update_wallet_password


def _get_wallet_secret_key_id(wallet_id: int) -> int:
    account = daos.account.query_first_account_by_wallet(wallet_id)
    require(account is not None)
    require(account.pubkey_id is not None)
    return secret_manager.get_pubkey_by_id(account.pubkey_id).secret_key_id


@_require_primary_wallet_exists()
def create_next_derived_primary_wallet(chain_code: str, name: str, password: str, address_encoding: str = None) -> dict:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    next_derived_bip44_path = generate_next_bip44_path_for_derived_primary_wallet(
        chain_code, address_encoding
    ).to_bip44_path()

    first_primary_wallet = daos.wallet.get_first_primary_wallet()
    require(first_primary_wallet is not None)
    secret_key_id = _get_wallet_secret_key_id(first_primary_wallet.id)

    new_pubkey_model = secret_manager.derive_by_secret_key(
        password, chain_info.curve, secret_key_id, next_derived_bip44_path, target_pubkey_type=PubKeyType.PUBKEY
    )
    verifier = secret_manager.raw_create_verifier_by_pubkey(chain_info.curve, bytes.fromhex(new_pubkey_model.pubkey))
    address = provider_manager.pubkey_to_address(chain_code, verifier, encoding=address_encoding)

    with db.atomic():
        pubkey_model = secret_manager.import_pubkey(
            chain_info.curve, verifier.get_pubkey(), path=next_derived_bip44_path, secret_key_id=secret_key_id
        )
        wallet_info = _create_default_wallet(
            chain_code,
            name,
            WalletType.SOFTWARE_PRIMARY,
            address,
            address_encoding=address_encoding,
            pubkey_id=pubkey_model.id,
            bip44_path=pubkey_model.path,
        )

    return wallet_info


def generate_next_bip44_path_for_derived_primary_wallet(chain_code: str, address_encoding: str = None) -> BIP44Path:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    default_bip44_path = get_default_bip44_path(chain_code, address_encoding)
    existing_primary_wallets = daos.wallet.list_all_wallets(chain_code, WalletType.SOFTWARE_PRIMARY)

    if not existing_primary_wallets:
        return default_bip44_path
    else:
        bip44_auto_increment_level = BIP44Level[chain_info.bip44_auto_increment_level.upper()]
        target_level = BIP44Level[chain_info.bip44_target_level.upper()]
        require(bip44_auto_increment_level >= BIP44Level.ACCOUNT)

        last_account_lookup = {
            i.wallet_id: i
            for i in daos.account.query_accounts_by_wallets(
                [i.id for i in existing_primary_wallets], address_encoding=address_encoding
            )
        }
        indexes = (
            BIP44Path.from_bip44_path(i.bip44_path)
            .to_target_level(bip44_auto_increment_level)
            .index_of(bip44_auto_increment_level)
            for i in last_account_lookup.values()
            if i.bip44_path
        )
        max_index = max((-1, *indexes))
        return (
            default_bip44_path.to_target_level(bip44_auto_increment_level)
            .next_sibling(gap=max_index + 1)
            .to_target_level(target_level)
        )


def export_mnemonic(wallet_id: int, password: str) -> Tuple[str, str]:
    wallet_model = _get_wallet_by_id(wallet_id)
    require(wallet_model.type in (WalletType.SOFTWARE_PRIMARY, WalletType.SOFTWARE_STANDALONE_MNEMONIC))
    secret_key_id = _get_wallet_secret_key_id(wallet_id)
    mnemonic, passphrase = secret_manager.export_mnemonic(password, secret_key_id)
    return mnemonic, passphrase


def export_prvkey(wallet_id: int, password: str) -> str:
    wallet_model = _get_wallet_by_id(wallet_id)
    require(
        wallet_model.type
        in (WalletType.SOFTWARE_PRIMARY, WalletType.SOFTWARE_STANDALONE_MNEMONIC, WalletType.SOFTWARE_STANDALONE_PRVKEY)
    )
    account = get_default_account_by_wallet(wallet_id)
    return secret_manager.export_prvkey(password, account.pubkey_id)


def export_keystore(wallet_id: int, password: str) -> dict:
    prvk = export_prvkey(wallet_id, password)
    encrypted_private_key = eth_account.Account.encrypt(prvk, password)
    return encrypted_private_key


def get_wallet_info_by_id(wallet_id: int, only_visible: bool = True) -> dict:
    wallet_model = _get_wallet_by_id(wallet_id)
    default_account = get_default_account_by_wallet(wallet_id)
    assets = daos.asset.query_assets_by_accounts([default_account.id], only_visible=only_visible)
    assets = refresh_assets(assets)
    return _build_wallet_info(wallet_model, default_account, assets)


def get_all_assets_by_wallet(wallet_id: int, only_visible: bool = True) -> List[AssetModel]:
    default_account = get_default_account_by_wallet(wallet_id)
    return daos.asset.query_assets_by_accounts([default_account.id], only_visible=only_visible)


def _build_wallet_info(
    wallet: WalletModel, account: AccountModel, assets: List[AssetModel], coin_info_lookup: dict = None
) -> dict:
    wallet_info = {
        "wallet_id": wallet.id,
        "wallet_type": WalletType(wallet.type).name,
        "name": wallet.name,
        "chain_code": wallet.chain_code,
        "address": account.address,
        "address_encoding": account.address_encoding,
        "bip44_path": account.bip44_path,
    }
    coin_info_lookup = coin_info_lookup or {
        i.code: i for i in coin_manager.query_coins_by_codes([i.coin_code for i in assets])
    }
    assets_info = [
        {
            "coin_code": i.coin_code,
            "balance": i.balance,
            "is_visible": i.is_visible,
            "symbol": coin_info_lookup[i.coin_code].symbol,
            "decimals": coin_info_lookup[i.coin_code].decimals,
            "icon": coin_info_lookup[i.coin_code].icon,
            "token_address": coin_info_lookup[i.coin_code].token_address,
        }
        for i in assets
        if i.coin_code in coin_info_lookup
    ]

    wallet_info["assets"] = assets_info
    return wallet_info


def get_all_wallets_info(chain_code: str = None, force_update: bool = False, only_visible: bool = True) -> List[dict]:
    wallets = daos.wallet.list_all_wallets(chain_code)
    accounts = daos.account.query_accounts_by_wallets([i.id for i in wallets])

    assets = daos.asset.query_assets_by_accounts([i.id for i in accounts], only_visible=only_visible)
    assets = refresh_assets(assets, force_update=force_update)

    last_account_lookup = {i.wallet_id: i for i in accounts}  # bind the last account to the wallet
    asset_lookup = defaultdict(list)
    for asset in assets:
        asset_lookup[asset.account_id].append(asset)

    wallets_info = []
    for wallet in wallets:
        last_account = last_account_lookup.get(wallet.id)
        if last_account is None:
            logger.warning(f"Illegal state. No account found by wallet. wallet_id: {wallet.id}")
            continue

        assets_found = asset_lookup.get(last_account.id)
        if not assets_found:
            logger.warning(
                f"Illegal state. No asset found by account. wallet_id: {wallet.id}, account_id: {last_account.id}"
            )
            continue

        wallet_info = _build_wallet_info(wallet, last_account, assets_found)
        wallets_info.append(wallet_info)

    return wallets_info


def get_default_account_by_wallet(wallet_id: int) -> AccountModel:
    last_account = daos.account.query_first_account_by_wallet(wallet_id)  # assume the last account as default
    if last_account is None:
        raise exceptions.IllegalWalletState()

    return last_account


def pre_send(
    wallet_id: int,
    coin_code: str,
    to_address: str = None,
    value: int = None,
    nonce: int = None,
    fee_limit: int = None,
    fee_price_per_unit: int = None,
    payload: dict = None,
) -> dict:
    wallet = _get_wallet_by_id(wallet_id)
    chain_info = coin_manager.get_chain_info(wallet.chain_code)
    handler = get_handler_by_chain_model(chain_info.chain_model)

    if to_address:
        to_address_validation = provider_manager.verify_address(chain_info.chain_code, to_address)
        if not to_address_validation.is_valid:
            raise exceptions.IllegalWalletOperation(f"Invalid to_address: {repr(to_address)}")
        to_address = to_address_validation.normalized_address

    unsigned_tx = handler.generate_unsigned_tx(
        wallet_id, coin_code, to_address, value, nonce, fee_limit, fee_price_per_unit, payload
    )
    try:
        is_valid, validation_message = _verify_unsigned_tx(wallet_id, coin_code, unsigned_tx)
    except Exception as e:
        is_valid, validation_message = False, str(e)

    return {
        "unsigned_tx": unsigned_tx.to_dict(),
        "is_valid": is_valid,
        "validation_message": validation_message,
    }


def send(
    wallet_id: int,
    coin_code: str,
    to_address: str,
    value: int,
    password: str,
    nonce: int = None,
    fee_limit: int = None,
    fee_price_per_unit: int = None,
    payload: dict = None,
    auto_broadcast: bool = True,
) -> SignedTx:
    wallet = _get_wallet_by_id(wallet_id)
    if wallet.type == WalletType.WATCHONLY:
        raise exceptions.IllegalWalletOperation("Watchonly wallet can not send asset")

    coin_info = coin_manager.get_coin_info(coin_code)
    if coin_info.chain_code != wallet.chain_code:
        raise exceptions.IllegalWalletOperation()

    chain_info = coin_manager.get_chain_info(wallet.chain_code)
    handler = get_handler_by_chain_model(chain_info.chain_model)

    to_address_validation = provider_manager.verify_address(chain_info.chain_code, to_address)
    if not to_address_validation.is_valid:
        raise exceptions.IllegalWalletOperation()
    to_address = to_address_validation.normalized_address

    unsigned_tx = handler.generate_unsigned_tx(
        wallet_id, coin_code, to_address, value, nonce, fee_limit, fee_price_per_unit, payload
    )
    is_valid, validation_message = _verify_unsigned_tx(wallet_id, coin_code, unsigned_tx)
    if not is_valid:
        raise exceptions.IllegalUnsignedTx(validation_message)

    accounts = daos.account.query_accounts_by_addresses(wallet.id, [i.address for i in unsigned_tx.inputs])
    signed_tx = _sign_tx(wallet, accounts, password, unsigned_tx)

    if auto_broadcast:
        receipt = broadcast_transaction(wallet.chain_code, signed_tx)
        if not receipt.is_success:
            raise exceptions.UnexpectedBroadcastReceipt(
                f"Error in broadcast. txid: {receipt.txid}, signed_tx: {signed_tx.to_dict()}"
            )

    transaction_manager.create_action(
        txid=signed_tx.txid,
        status=TxActionStatus.PENDING if auto_broadcast else TxActionStatus.SIGNED,
        chain_code=chain_info.chain_code,
        coin_code=coin_code,
        value=Decimal(value),
        from_address=accounts[0].address,
        to_address=to_address,
        fee_limit=Decimal(unsigned_tx.fee_limit),
        fee_price_per_unit=unsigned_tx.fee_price_per_unit,
        nonce=-1 if unsigned_tx.nonce is None else unsigned_tx.nonce,
        raw_tx=signed_tx.raw_tx,
    )

    return signed_tx


def _verify_unsigned_tx(wallet_id: int, coin_code: str, unsigned_tx: UnsignedTx) -> Tuple[bool, str]:
    wallet = _get_wallet_by_id(wallet_id)

    input_addresses = [i.address for i in unsigned_tx.inputs or ()]
    if not input_addresses:
        return False, "No input addresses found"

    input_accounts = daos.account.query_accounts_by_addresses(wallet.id, input_addresses)
    input_accounts_address_set = {i.address for i in input_accounts}
    if (
        not input_accounts
        or not all(i in input_accounts_address_set for i in input_addresses)
        or not all(i.wallet_id == wallet_id for i in input_accounts)
    ):
        return False, "Illegal input accounts"

    output_addresses = [i.address for i in unsigned_tx.outputs or ()]
    if not output_addresses:
        return False, "No output addresses found"
    elif not all(provider_manager.verify_address(wallet.chain_code, i).is_valid for i in output_addresses):
        return False, "Invalid output address"

    # todo more verification
    return True, ""


def _sign_tx(wallet: WalletModel, accounts: List[AccountModel], password: str, unsigned_tx: UnsignedTx) -> SignedTx:
    key_mapping = {i.address: secret_manager.get_signer(password, i.pubkey_id) for i in accounts}
    signed_tx = provider_manager.sign_transaction(wallet.chain_code, unsigned_tx, key_mapping)
    return signed_tx


def broadcast_transaction(chain_code: str, signed_tx: SignedTx) -> TxBroadcastReceipt:
    receipt = provider_manager.broadcast_transaction(chain_code, signed_tx.raw_tx)
    if receipt.txid:
        require(receipt.txid == signed_tx.txid, f"Txid mismatched. expected: {signed_tx.txid}, actual: {receipt.txid}")

    txid = signed_tx.txid or receipt.txid
    if txid:
        transaction_manager.update_action_status(
            chain_code, txid, TxActionStatus.PENDING if receipt.is_success else TxActionStatus.UNEXPECTED_FAILED
        )

    receipt.txid = txid
    return receipt


def create_or_show_asset(wallet_id: int, coin_code: str):
    _ = coin_manager.get_coin_info(coin_code)  # Check coin existing only
    default_account = get_default_account_by_wallet(wallet_id)
    asset = daos.asset.get_asset_by_account_and_coin_code(default_account.id, coin_code)
    if asset is None:
        daos.asset.create_asset(wallet_id, default_account.id, default_account.chain_code, coin_code, is_visible=True)
    else:
        daos.asset.show_asset(asset.id)


def hide_asset(wallet_id: int, coin_code: str):
    _ = coin_manager.get_coin_info(coin_code)
    default_account = get_default_account_by_wallet(wallet_id)
    asset = daos.asset.get_asset_by_account_and_coin_code(default_account.id, coin_code)
    if asset is None:
        raise exceptions.IllegalWalletOperation(f"Asset not found. wallet_id: {wallet_id}, coin_code: {coin_code}")

    daos.asset.hide_asset(asset.id)


def refresh_assets(
    assets: List[AssetModel], force_update: bool = False, cache_in_seconds: int = 10
) -> List[AssetModel]:
    need_update_flag_datetime = datetime.datetime.now() - datetime.timedelta(seconds=cache_in_seconds)
    need_update_assets = assets if force_update else [i for i in assets if i.modified_time < need_update_flag_datetime]

    if not need_update_assets:
        return assets

    accounts = daos.account.query_accounts_by_ids([i.account_id for i in need_update_assets])
    accounts_lookup = {i.id: i for i in accounts}
    coins = coin_manager.query_coins_by_codes([i.coin_code for i in need_update_assets])
    coins_lookup = {i.code: i for i in coins}

    updated_assets = []
    need_update_assets = sorted(need_update_assets, key=lambda i: (i.chain_code, i.account_id))
    for chain_code, group in groupby(need_update_assets, key=lambda i: i.chain_code):
        for account_id, sub_group in groupby(group, key=lambda i: i.account_id):
            for asset in sub_group:  # todo batch
                try:
                    account = accounts_lookup[account_id]
                    coin = coins_lookup[asset.coin_code]
                    balance = provider_manager.get_balance(chain_code, account.address, coin.token_address)
                    asset.balance = Decimal(balance)
                    updated_assets.append(asset)
                except Exception as e:
                    logger.exception(
                        f"Error in get balance by asset. chain_code: {chain_code}, coin_code: {asset.coin_code}, "
                        f"account_id: {account_id}, error: {e}"
                    )

    with db.atomic():
        daos.asset.bulk_update_balance(updated_assets)

    return assets


@functools.lru_cache
def get_default_bip44_path(chain_code: str, address_encoding: str = None) -> BIP44Path:
    chain_info = coin_manager.get_chain_info(chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    purpose = chain_info.bip44_purpose_options.get(address_encoding) or 44
    bip44_last_hardened_level = BIP44Level[chain_info.bip44_last_hardened_level.upper()]
    bip44_target_level = BIP44Level[chain_info.bip44_target_level.upper()]
    bip44_path = BIP44Path(
        purpose=purpose,
        coin_type=chain_info.bip44_coin_type,
        account=0,
        last_hardened_level=bip44_last_hardened_level,
    )
    bip44_path = bip44_path.to_target_level(bip44_target_level)
    return bip44_path


def get_encoded_address_by_account_id(account_id: int, address_encoding: str = None) -> str:
    accounts = daos.account.query_accounts_by_ids([account_id])
    require(len(accounts) == 1, Exception("no corresponding account found"))
    account = accounts[0]
    require(account.pubkey_id is not None, Exception("no pubkey_id found"))

    verifier = secret_manager.get_verifier(account.pubkey_id)
    chain_info = coin_manager.get_chain_info(account.chain_code)
    address_encoding = address_encoding or chain_info.default_address_encoding
    return provider_manager.pubkey_to_address(account.chain_code, verifier, encoding=address_encoding)


@db.atomic()
def cascade_delete_wallet_related_models(wallet_id: int, password: str = None):
    wallet = _get_wallet_by_id(wallet_id)
    if wallet.type != WalletType.WATCHONLY:
        check_wallet_password(wallet_id, password)

    chain_code = wallet.chain_code
    accounts = daos.account.query_accounts_by_wallets([wallet_id])
    addresses = [i.address for i in accounts]
    related_pubkey_ids = {i.pubkey_id for i in accounts if i.pubkey_id is not None}
    if related_pubkey_ids:
        secret_manager.cascade_delete_related_models_by_pubkey_ids(list(related_pubkey_ids))

    transaction_manager.delete_actions_by_addresses(chain_code, addresses)
    daos.wallet.delete_wallet_by_id(wallet_id)
    daos.account.delete_accounts_by_wallet_id(wallet_id)
    daos.asset.delete_assets_by_wallet_id(wallet_id)


@_require_primary_wallet_exists()
def get_first_primary_wallet_id() -> int:
    return daos.wallet.get_first_primary_wallet().id


@db.atomic()
def clear_all_primary_wallets(password: str = None):
    wallets = daos.wallet.list_all_wallets(wallet_type=WalletType.SOFTWARE_PRIMARY)
    for wallet in wallets:
        cascade_delete_wallet_related_models(wallet.id, password)


def count_primary_wallet_by_chain(chain_code: str) -> int:
    return len(daos.wallet.list_all_wallets(chain_code, wallet_type=WalletType.SOFTWARE_PRIMARY))

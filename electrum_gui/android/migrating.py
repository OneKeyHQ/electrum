import logging
import time
from decimal import Decimal
from typing import List, Optional, Set, Union

from electrum import crypto, simple_config, storage, util, wallet_db
from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.coin.data import CoinInfo
from electrum_gui.common.provider import manager as provider_manager
from electrum_gui.common.provider.data import SignedTx
from electrum_gui.common.wallet import manager as wallet_manager
from electrum_gui.common.wallet.data import WalletType
from electrum_gui.common.wallet.models import AccountModel

MIGRATED_COINS: Set = {"sol", "tsol", "stc", "tstc", "cfx", "tcfx"}

logger = logging.getLogger("app.migrating")


def is_coin_migrated(coin: str) -> bool:
    return coin in MIGRATED_COINS


class GeneralWallet(object):
    def __init__(self, my_db, my_storage, config):
        self.db = my_db
        self.storage = my_storage
        self.config = config

        self.general_wallet_id = my_db.get("general_wallet_id")
        self.general_wallet_type = WalletType[my_db.get("general_wallet_type")]

        self._wallet_name = my_db.get("wallet_name")
        self._chain_code = my_db.get("chain_code")
        self._chain_info = None
        self._chain_coin = None
        self._fee_coin = None
        self._coin = my_db.get("coin")
        self._identity = my_db.get("identity")
        self._balance_info = {}
        self._address = None

        # Backward compatible
        self.use_change = False
        self.keystore = None if self.general_wallet_type == WalletType.WATCHONLY else _BypassKeystore(self)

    @classmethod
    def from_pubkey_or_addresses(
        cls, name: str, coin: str, config: simple_config.SimpleConfig, pubkey_or_addresses: str
    ):
        chain_code = coin
        pubkey_or_address = pubkey_or_addresses.split()[0]

        address_validation = provider_manager.verify_address(chain_code, pubkey_or_address)
        if address_validation.is_valid:
            wallet_info = wallet_manager.import_watchonly_wallet_by_address(
                name=name,
                chain_code=chain_code,
                address=address_validation.normalized_address,
            )
        else:
            wallet_info = wallet_manager.import_watchonly_wallet_by_pubkey(
                name=name,
                chain_code=chain_code,
                pubkey=bytes.fromhex(pubkey_or_addresses),
            )

        my_db = cls.create_wallet_db(coin, f"{coin}_imported", wallet_info)
        return cls(my_db, None, config)

    @classmethod
    def from_prvkeys(
        cls, name: str, coin: str, config: simple_config.SimpleConfig, prvkeys: str, password: str
    ) -> 'GeneralWallet':
        chain_code = coin
        prvkey = prvkeys.split()[0]
        wallet_info = wallet_manager.import_standalone_wallet_by_prvkey(
            name=name, chain_code=chain_code, prvkey=bytes.fromhex(prvkey), password=password
        )

        my_db = cls.create_wallet_db(coin, f"{coin}_imported", wallet_info)
        return cls(my_db, None, config)

    @classmethod
    def from_keystore(
        cls,
        name: str,
        coin: str,
        config: simple_config.SimpleConfig,
        keystores: str,
        keystore_password: str,
        password: str,
    ) -> 'GeneralWallet':
        chain_code = coin
        wallet_info = wallet_manager.import_standalone_wallet_by_keystore(
            name=name,
            chain_code=chain_code,
            keyfile_json=keystores,
            keystore_password=keystore_password,
            password=password,
        )

        my_db = cls.create_wallet_db(coin, f"{coin}_imported", wallet_info)
        return cls(my_db, None, config)

    def export_keystore(self, address: str, password: Optional[str]) -> dict:
        require(address == self.get_address())
        return wallet_manager.export_keystore(self.general_wallet_id, password)

    @classmethod
    def from_seed_or_bip39(
        cls,
        name: str,
        coin: str,
        config: simple_config.SimpleConfig,
        mnemonic: str,
        password: str,
        passphrase: str = None,
        bip44_path: str = None,
        as_primary_wallet: bool = False,
    ):
        require(mnemonic is not None)
        chain_code = coin

        if not as_primary_wallet:
            wallet_info = wallet_manager.import_standalone_wallet_by_mnemonic(
                name=name,
                chain_code=chain_code,
                mnemonic=mnemonic,
                password=password,
                passphrase=passphrase,
                bip44_path=bip44_path,
            )
            legacy_wallet_type = f"{coin}_imported"
        elif not wallet_manager.has_primary_wallet():
            wallet_info = wallet_manager.create_primary_wallets(
                chain_codes=[chain_code],
                password=password,
                mnemonic=mnemonic,
            )[0]
            legacy_wallet_type = f"{coin}_derived_standard"
        else:
            first_wallet_id = wallet_manager.get_first_primary_wallet_id()
            origin_mnemonic, origin_passphrase = wallet_manager.export_mnemonic(first_wallet_id, password)
            is_mnemonic_and_passphrase_match = origin_mnemonic == mnemonic and origin_passphrase == passphrase
            require(is_mnemonic_and_passphrase_match, "mnemonic or passphrase mismatched")  # just in case
            wallet_info = wallet_manager.create_next_derived_primary_wallet(
                chain_code=chain_code,
                name=name,
                password=password,
            )
            legacy_wallet_type = f"{coin}_derived_standard"

        my_db = cls.create_wallet_db(coin, legacy_wallet_type, wallet_info)
        return cls(my_db, None, config)

    @classmethod
    def create_wallet_db(cls, coin: str, legacy_wallet_type: str, wallet_info: dict):
        my_db = wallet_db.WalletDB("", manual_upgrades=False)
        my_db.put("wallet_type", legacy_wallet_type)  # Backward compatible
        my_db.put("general_wallet_id", wallet_info["wallet_id"])
        my_db.put("general_wallet_type", wallet_info["wallet_type"])
        my_db.put("wallet_name", wallet_info["name"])
        my_db.put("chain_code", wallet_info["chain_code"])
        my_db.put("coin", coin)
        my_db.put("identity", crypto.sha256(wallet_info["chain_code"] + wallet_info["address"]).hex())

        return my_db

    @property
    def coin(self) -> str:
        return self._coin

    @coin.setter
    def coin(self, chain_code: str):
        # bypass, no need any more
        pass

    @property
    def name(self):
        return self._wallet_name

    @property
    def chain_code(self):
        return self._chain_code

    @property
    def chain_info(self):
        if self._chain_info is None:
            self._chain_info = coin_manager.get_chain_info(self.chain_code)

        return self._chain_info

    @property
    def fee_coin(self):
        if self._fee_coin is None:
            self._fee_coin = coin_manager.get_coin_info(self.chain_info.fee_code)

        return self._fee_coin

    @property
    def chain_coin(self):
        if self._chain_coin is None:
            self._chain_coin = coin_manager.get_coin_info(self.chain_code)

        return self._chain_coin

    @property
    def identity(self) -> str:
        return self._identity

    def _get_default_account(self) -> AccountModel:
        return wallet_manager.get_default_account_by_wallet(self.general_wallet_id)

    def get_address(self) -> str:
        if self._address is None:
            address = self._get_default_account().address
            self._address = provider_manager.verify_address(self.chain_code, address).display_address

        return self._address

    def get_addresses(self):
        return [self.get_address()]

    def get_derivation_path(self, address):
        require(address == self.get_address())
        return self._get_default_account().bip44_path

    def ensure_storage(self, path: str):
        if self.storage is None:
            self.storage = storage.WalletStorage(path)
            if not self.storage.file_exists():
                return

        raise util.FileAlreadyExist()

    def check_customer_and_default_path(self):
        return False

    def set_name(self, name: str):
        require(len(name) > 0)
        with db.atomic():
            wallet_manager.update_wallet_name(self.general_wallet_id, name)
            self.db.put("wallet_name", name)
            self.save_db()
            self._wallet_name = name

    def get_name(self):
        return self._wallet_name

    def save_db(self):
        if self.storage:
            self.db.set_modified(True)
            self.db.write(self.storage)

    def stop(self):
        # bypass?
        pass

    def update_password(self, old_pw, new_pw, *args, **kwargs):
        if old_pw and new_pw:
            wallet_manager.update_wallet_password(self.general_wallet_id, old_pw, new_pw)

    def check_password(self, password, str_pw=None):
        if password:
            wallet_manager.check_wallet_password(self.general_wallet_id, password)

    def get_all_balance(self) -> dict:
        if (
            self._balance_info.get("time")
            and self._balance_info.get("assets")
            and time.time() - self._balance_info["time"] <= 10
        ):  # Only cache for 10s
            return self._balance_info["assets"]

        wallet_info = wallet_manager.get_wallet_info_by_id(self.general_wallet_id)
        address = wallet_info["address"]

        assets = {
            asset["coin_code"]: {"address": address, **asset}
            for asset in wallet_info["assets"]
            if asset.get("is_visible") is True
        }

        self._balance_info["assets"] = assets
        self._balance_info["time"] = time.time()
        return assets

    def get_all_token_address(self):
        return [i.token_address for i in self.get_all_token_coins()]

    def get_all_token_coins(self) -> List[CoinInfo]:
        assets = wallet_manager.get_all_assets_by_wallet(self.general_wallet_id)
        coin_codes = [i.coin_code for i in assets if i.coin_code != self.chain_code]
        coins = coin_manager.query_coins_by_codes(coin_codes)
        return coins

    def add_contract_token(self, token_symbol, token_address):
        coin = coin_manager.query_coins_by_token_addresses(self.chain_code, [token_address])[0]
        wallet_manager.create_or_show_asset(self.general_wallet_id, coin.code)

    def delete_contract_token(self, token_address):
        coin = coin_manager.query_coins_by_token_addresses(self.chain_code, [token_address])
        if not coin:
            return

        wallet_manager.hide_asset(self.general_wallet_id, coin[0].code)

    def __str__(self):
        return self.basename()

    def basename(self) -> str:
        return self.storage.basename() if self.storage else 'no name'

    def pre_send(
        self,
        to_address: str = None,
        value: Union[int, str] = None,
        token_address: str = None,
        nonce: int = None,
        fee_limit: int = None,
        fee_price_per_unit: Union[int, str] = None,
        payload: dict = None,
    ):
        coin = self._get_token_coin_or_nonce(token_address) if token_address else self.chain_coin
        require(coin is not None)
        fee_coin = self.fee_coin
        fee_decimals_multiply = pow(10, fee_coin.decimals)
        fee_price_decimals_for_legibility_multiply = pow(10, self.chain_info.fee_price_decimals_for_legibility)

        value = Decimal(value) * pow(10, coin.decimals) if isinstance(value, str) else value
        fee_price_per_unit = (
            Decimal(fee_price_per_unit) * fee_price_decimals_for_legibility_multiply  # maybe gwei in eth
            if isinstance(fee_price_per_unit, str)
            else fee_price_per_unit
        )
        result = wallet_manager.pre_send(
            self.general_wallet_id,
            coin.code,
            to_address,
            value,
            nonce=nonce,
            fee_limit=fee_limit,
            fee_price_per_unit=fee_price_per_unit,
            payload=payload,
        )
        fee_limit = result["unsigned_tx"]["fee_limit"]

        return [
            {
                "fee_price_per_unit": Decimal(fee.price) / fee_price_decimals_for_legibility_multiply,
                "time": Decimal(max(fee.time or 60, 60)) / 60,
                "fee_limit": fee_limit,
                "fee": Decimal(fee.price) * fee_limit / fee_decimals_multiply,
            }
            for fee in provider_manager.get_prices_per_unit_of_fee(self.chain_code)
        ]

    def send(
        self,
        to_address: str,
        value: int,
        password: str,
        token_address: str = None,
        nonce: int = None,
        fee_limit: int = None,
        fee_price_per_unit: int = None,
        payload: dict = None,
        auto_broadcast: bool = True,
    ) -> SignedTx:
        coin = self._get_token_coin_or_nonce(token_address) if token_address else self.chain_coin
        require(coin is not None)
        fee_price_decimals_for_legibility_multiply = pow(10, self.chain_info.fee_price_decimals_for_legibility)

        value = int(Decimal(value) * pow(10, coin.decimals)) if isinstance(value, str) else value
        fee_price_per_unit = (
            int(Decimal(fee_price_per_unit) * fee_price_decimals_for_legibility_multiply)  # maybe gwei in eth
            if isinstance(fee_price_per_unit, str)
            else fee_price_per_unit
        )

        return wallet_manager.send(
            self.general_wallet_id,
            coin.code,
            to_address,
            value,
            password,
            nonce=nonce,
            fee_limit=fee_limit,
            fee_price_per_unit=fee_price_per_unit,
            payload=payload,
            auto_broadcast=auto_broadcast,
        )

    def broadcast_transaction(self, raw_tx: str):
        return wallet_manager.broadcast_transaction(self.chain_code, SignedTx(raw_tx=raw_tx))

    def _get_token_coin_or_nonce(self, token_address: str) -> Optional[CoinInfo]:
        result = coin_manager.query_coins_by_token_addresses(self.chain_code, [token_address])
        return result[0] if result else None

    def is_mine(self, address) -> bool:
        return address == self.get_address()

    def export_private_key(self, address: str, password: Optional[str]) -> str:
        require(address == self.get_address())
        return wallet_manager.export_prvkey(self.general_wallet_id, password)

    def create_new_address(self, for_change: bool = False):
        raise NotImplementedError()

    def sign_message(self, address: str, message: str, password: str = None):
        raise NotImplementedError()

    def verify_message(self, address, message, sig):
        raise NotImplementedError()

    def clear_coin_price_cache(self):
        # bypass
        pass

    def is_watching_only(self) -> bool:
        return self.general_wallet_type == WalletType.WATCHONLY

    def get_keystore(self):
        return self.keystore

    def get_keystores(self):
        return [self.keystore] if self.keystore is not None else []

    def has_seed(self) -> bool:
        return self.keystore is not None and self.keystore.has_seed()

    def get_seed(self, password):
        if self.keystore is not None:
            return self.keystore.get_seed(password)
        else:
            return None

    def stc_spec_get_receipt_identifier_address(self):
        require(self._chain_code in ["stc", "tstc"])
        account = self._get_default_account()
        return wallet_manager.get_encoded_address_by_account_id(account.id, "BECH32")

    def delete_wallet(self, password: str = None):
        wallet_manager.cascade_delete_wallet_related_models(self.general_wallet_id, password)


class _BypassKeystore:
    def __init__(self, wallet: "GeneralWallet"):
        self.wallet = wallet
        self.general_wallet_id = self.wallet.general_wallet_id

    @property
    def xpub(self):
        return self.wallet.identity  # todo recheck

    def get_seed(self, password):
        if self.wallet.has_seed():
            mnemonic, _ = wallet_manager.export_mnemonic(self.general_wallet_id, password)
        else:
            mnemonic = None
        return mnemonic

    def get_passphrase(self, password):
        if self.has_seed():
            _, passphrase = wallet_manager.export_mnemonic(self.general_wallet_id, password)
        else:
            passphrase = None
        return passphrase

    def has_seed(self):
        return self.wallet.general_wallet_type in (WalletType.SOFTWARE_PRIMARY, WalletType.SOFTWARE_STANDALONE_MNEMONIC)

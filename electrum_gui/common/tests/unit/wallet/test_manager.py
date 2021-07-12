import datetime
import decimal
from unittest import TestCase
from unittest.mock import Mock, call, patch

from electrum_gui.common.basic.orm import test_utils
from electrum_gui.common.coin import data as coin_data
from electrum_gui.common.coin import models as coin_models
from electrum_gui.common.provider import data as provider_data
from electrum_gui.common.secret import exceptions as secret_exceptions
from electrum_gui.common.secret import models as secret_models
from electrum_gui.common.transaction import data as transaction_data
from electrum_gui.common.transaction import models as transaction_models
from electrum_gui.common.wallet import daos as wallet_daos
from electrum_gui.common.wallet import data as wallet_data
from electrum_gui.common.wallet import exceptions as wallet_exceptions
from electrum_gui.common.wallet import manager as wallet_manager
from electrum_gui.common.wallet import models as wallet_models


@test_utils.cls_test_database(
    wallet_models.WalletModel,
    wallet_models.AccountModel,
    wallet_models.AssetModel,
    secret_models.PubKeyModel,
    secret_models.SecretKeyModel,
    transaction_models.TxAction,
    coin_models.CoinModel,
)
class TestWalletManager(TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.password = "moon"
        cls.mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        cls.passphrase = "OneKey"

    def test_import_watchonly_wallet_by_address__eth(self):
        wallet_info = wallet_manager.import_watchonly_wallet_by_address(
            "ETH_WATCHONLY", "eth", "0x8Be73940864fD2B15001536E76b3ECcd85a80a5d"
        )
        self.assertEqual(
            {
                'address': '0x8be73940864fd2b15001536e76b3eccd85a80a5d',
                'address_encoding': None,
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'eth',
                        'decimals': 18,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'ETH',
                        'token_address': None,
                    }
                ],
                'bip44_path': None,
                'chain_code': 'eth',
                'name': 'ETH_WATCHONLY',
                'wallet_id': 1,
                'wallet_type': 'WATCHONLY',
            },
            wallet_info,
        )

    def test_import_watchonly_wallet_by_address__btc(self):
        self.assertEqual(
            {
                'address': '3Nu7tDXHbqtuMfMi3DMVrnLFabTvaY2FyF',
                'address_encoding': 'P2WPKH-P2SH',
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'btc',
                        'decimals': 8,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'BTC',
                        'token_address': None,
                    }
                ],
                'bip44_path': None,
                'chain_code': 'btc',
                'name': 'BTC_WATCHONLY',
                'wallet_id': 1,
                'wallet_type': 'WATCHONLY',
            },
            wallet_manager.import_watchonly_wallet_by_address(
                "BTC_WATCHONLY", "btc", "3Nu7tDXHbqtuMfMi3DMVrnLFabTvaY2FyF"
            ),
        )

    def test_import_watchonly_wallet_by_pubkey(self):
        self.assertEqual(
            {
                'address': '0x8be73940864fd2b15001536e76b3eccd85a80a5d',
                'address_encoding': None,
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'eth',
                        'decimals': 18,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'ETH',
                        'token_address': None,
                    }
                ],
                'bip44_path': None,
                'chain_code': 'eth',
                'name': 'ETH_WATCHONLY_BY_PUBKEY',
                'wallet_id': 1,
                'wallet_type': 'WATCHONLY',
            },
            wallet_manager.import_watchonly_wallet_by_pubkey(
                "ETH_WATCHONLY_BY_PUBKEY",
                "eth",
                bytes.fromhex("02deb60902c06bfed8d78e33337be995d0b3efc28fbc61b6f88cb5cfb27dc4efd1"),
            ),
        )

    def test_import_standalone_wallet_by_prvkey(self):
        self.assertEqual(
            {
                'address': '0x8be73940864fd2b15001536e76b3eccd85a80a5d',
                'address_encoding': None,
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'eth',
                        'decimals': 18,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'ETH',
                        'token_address': None,
                    }
                ],
                'bip44_path': None,
                'chain_code': 'eth',
                'name': 'ETH_BY_PRVKEY',
                'wallet_id': 1,
                'wallet_type': 'SOFTWARE_STANDALONE_PRVKEY',
            },
            wallet_manager.import_standalone_wallet_by_prvkey(
                "ETH_BY_PRVKEY",
                "eth",
                bytes.fromhex("77f22e0d920c7b59df81a629dc75c27513b5360a45d55f3253454f5d3cb23bab"),
                "moon",
            ),
        )

    def test_import_standalone_wallet_by_mnemonic(self):
        self.assertEqual(
            {
                'address': '0x8be73940864fd2b15001536e76b3eccd85a80a5d',
                'address_encoding': None,
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'eth',
                        'decimals': 18,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'ETH',
                        'token_address': None,
                    }
                ],
                'bip44_path': "m/44'/60'/0'/0/0",
                'chain_code': 'eth',
                'name': 'ETH_BY_MNEMONIC',
                'wallet_id': 1,
                'wallet_type': 'SOFTWARE_STANDALONE_MNEMONIC',
            },
            wallet_manager.import_standalone_wallet_by_mnemonic(
                "ETH_BY_MNEMONIC",
                "eth",
                self.mnemonic,
                passphrase=self.passphrase,
                password=self.password,
                bip44_path="m/44'/60'/0'/0/0",
            ),
        )

    def test_create_primary_wallets(self):
        self.assertEqual(
            [
                {
                    'address': '3Nu7tDXHbqtuMfMi3DMVrnLFabTvaY2FyF',
                    'address_encoding': 'P2WPKH-P2SH',
                    'assets': [
                        {
                            'balance': 0,
                            'coin_code': 'btc',
                            'decimals': 8,
                            'icon': None,
                            'is_visible': True,
                            'symbol': 'BTC',
                            'token_address': None,
                        }
                    ],
                    'bip44_path': "m/49'/0'/0'/0/0",
                    'chain_code': 'btc',
                    'name': 'BTC-1',
                    'wallet_id': 1,
                    'wallet_type': 'SOFTWARE_PRIMARY',
                },
                {
                    'address': '0x8be73940864fd2b15001536e76b3eccd85a80a5d',
                    'address_encoding': None,
                    'assets': [
                        {
                            'balance': 0,
                            'coin_code': 'eth',
                            'decimals': 18,
                            'icon': None,
                            'is_visible': True,
                            'symbol': 'ETH',
                            'token_address': None,
                        }
                    ],
                    'bip44_path': "m/44'/60'/0'/0/0",
                    'chain_code': 'eth',
                    'name': 'ETH-1',
                    'wallet_id': 2,
                    'wallet_type': 'SOFTWARE_PRIMARY',
                },
            ],
            wallet_manager.create_primary_wallets(
                ["btc", "eth"],
                password=self.password,
                mnemonic=self.mnemonic,
                passphrase=self.passphrase,
            ),
        )

    def test_generate_next_bip44_path_for_derived_primary_wallet(self):
        wallet_manager.create_primary_wallets(
            ["btc", "eth"],
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )

        self.assertEqual(
            "m/49'/0'/1'/0/0",
            wallet_manager.generate_next_bip44_path_for_derived_primary_wallet("btc", "P2WPKH-P2SH").to_bip44_path(),
        )
        self.assertEqual(
            "m/44'/0'/0'/0/0",
            wallet_manager.generate_next_bip44_path_for_derived_primary_wallet("btc", "P2PKH").to_bip44_path(),
        )
        self.assertEqual(
            "m/84'/0'/0'/0/0",
            wallet_manager.generate_next_bip44_path_for_derived_primary_wallet("btc", "P2WPKH").to_bip44_path(),
        )
        self.assertEqual(
            "m/44'/60'/0'/0/1",
            wallet_manager.generate_next_bip44_path_for_derived_primary_wallet("eth").to_bip44_path(),
        )
        self.assertEqual(
            "m/44'/60'/0'/0/0",
            wallet_manager.generate_next_bip44_path_for_derived_primary_wallet("bsc").to_bip44_path(),
        )

    def test_create_next_derived_primary_wallet(self):
        wallet_manager.create_primary_wallets(
            ["eth"],
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )

        self.assertEqual(
            {
                'address': '3Nu7tDXHbqtuMfMi3DMVrnLFabTvaY2FyF',
                'address_encoding': 'P2WPKH-P2SH',
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'btc',
                        'decimals': 8,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'BTC',
                        'token_address': None,
                    }
                ],
                'bip44_path': "m/49'/0'/0'/0/0",
                'chain_code': 'btc',
                'name': 'BTC-1',
                'wallet_id': 2,
                'wallet_type': 'SOFTWARE_PRIMARY',
            },
            wallet_manager.create_next_derived_primary_wallet("btc", "BTC-1", "moon"),
        )

        self.assertEqual(
            {
                'address': '0xd927952eed3a0a838bbe2db0ba5a15673003903d',
                'address_encoding': None,
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'eth',
                        'decimals': 18,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'ETH',
                        'token_address': None,
                    }
                ],
                'bip44_path': "m/44'/60'/0'/0/1",
                'chain_code': 'eth',
                'name': 'ETH-2',
                'wallet_id': 3,
                'wallet_type': 'SOFTWARE_PRIMARY',
            },
            wallet_manager.create_next_derived_primary_wallet("eth", "ETH-2", "moon"),
        )

        self.assertEqual(
            {
                'address': '34y7g9uRnjJwvu2zLJVMfrucnbzgyYc4af',
                'address_encoding': 'P2WPKH-P2SH',
                'assets': [
                    {
                        'balance': 0,
                        'coin_code': 'btc',
                        'decimals': 8,
                        'icon': None,
                        'is_visible': True,
                        'symbol': 'BTC',
                        'token_address': None,
                    }
                ],
                'bip44_path': "m/49'/0'/1'/0/0",
                'chain_code': 'btc',
                'name': 'BTC-2',
                'wallet_id': 4,
                'wallet_type': 'SOFTWARE_PRIMARY',
            },
            wallet_manager.create_next_derived_primary_wallet("btc", "BTC-2", "moon"),
        )

    def test_export_mnemonic__primary_wallet(self):
        wallet_info = wallet_manager.create_primary_wallets(
            ["eth"],
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )[0]
        self.assertEqual(
            (self.mnemonic, self.passphrase),
            wallet_manager.export_mnemonic(wallet_info["wallet_id"], self.password),
        )

    def test_export_mnemonic__standalone_mnemonic_wallet(self):
        wallet_info = wallet_manager.import_standalone_wallet_by_mnemonic(
            "ETH-1",
            "eth",
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )

        self.assertEqual(
            (self.mnemonic, self.passphrase), wallet_manager.export_mnemonic(wallet_info["wallet_id"], self.password)
        )

    @patch("electrum_gui.common.wallet.manager.provider_manager.get_address")
    def test_search_existing_wallets(self, fake_get_address):
        fake_get_address.side_effect = (
            lambda chain_code, address: provider_data.Address(address=address, balance=18888, existing=True)
            if address == "0xa0331fcfa308e488833de1fe16370b529fa7c720"
            else provider_data.Address(address=address, balance=0, existing=False)
        )

        self.assertEqual(
            [
                {
                    'address': '3Nu7tDXHbqtuMfMi3DMVrnLFabTvaY2FyF',
                    'address_encoding': 'P2WPKH-P2SH',
                    'balance': 0,
                    'bip44_path': "m/49'/0'/0'/0/0",
                    'chain_code': 'btc',
                    'name': 'BTC-1',
                },
                {
                    'address': '0xa0331fcfa308e488833de1fe16370b529fa7c720',
                    'address_encoding': None,
                    'balance': 18888,
                    'bip44_path': "m/44'/60'/0'/0/11",
                    'chain_code': 'eth',
                    'name': 'ETH-1',
                },
            ],
            wallet_manager.search_existing_wallets(["btc", "eth"], self.mnemonic, passphrase=self.passphrase),
        )

    def test_update_wallet_password(self):
        wallet_info = wallet_manager.import_standalone_wallet_by_mnemonic(
            "ETH-1",
            "eth",
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )

        with self.assertRaises(secret_exceptions.InvalidPassword):
            wallet_manager.update_wallet_password(wallet_info["wallet_id"], "hello world", "bye")

        wallet_manager.update_wallet_password(wallet_info["wallet_id"], self.password, "bye")
        wallet_manager.update_wallet_password(wallet_info["wallet_id"], "bye", self.password)

    @patch("electrum_gui.common.wallet.manager.get_handler_by_chain_model")
    @patch("electrum_gui.common.wallet.manager.coin_manager")
    @patch("electrum_gui.common.wallet.manager.provider_manager")
    @patch("electrum_gui.common.wallet.manager._verify_unsigned_tx")
    def test_pre_send(
        self, fake_verify_unsigned_tx, fake_provider_manager, fake_coin_manager, fake_get_handler_by_chain_model
    ):
        wallet = wallet_daos.wallet.create_wallet("testing", wallet_data.WalletType.SOFTWARE_PRIMARY, "eth")

        fake_handler = Mock()
        fake_get_handler_by_chain_model.return_value = fake_handler
        fake_coin_manager.get_chain_info.return_value = Mock(chain_model=coin_data.ChainModel.ACCOUNT, chain_code="eth")

        fake_verify_unsigned_tx.return_value = (False, "validate failed")

        with self.subTest("First time"):
            fake_unsigned_tx = provider_data.UnsignedTx(fee_limit=1001, fee_price_per_unit=11)
            fake_handler.generate_unsigned_tx.return_value = fake_unsigned_tx
            self.assertEqual(
                {
                    "unsigned_tx": fake_unsigned_tx.to_dict(),
                    "is_valid": False,
                    "validation_message": "validate failed",
                },
                wallet_manager.pre_send(wallet.id, "eth_usdt"),
            )
            fake_coin_manager.get_chain_info.assert_called_once_with("eth")
            fake_provider_manager.verify_address.assert_not_called()
            fake_get_handler_by_chain_model.assert_called_once_with(coin_data.ChainModel.ACCOUNT)
            fake_handler.generate_unsigned_tx.assert_called_once_with(
                wallet.id, "eth_usdt", None, None, None, None, None, None
            )
            fake_verify_unsigned_tx.assert_called_once_with(wallet.id, "eth_usdt", fake_unsigned_tx)
            fake_handler.generate_unsigned_tx.reset_mock()

        with self.subTest("Call with to_address"):
            fake_provider_manager.verify_address.return_value = provider_data.AddressValidation(
                normalized_address="fake_normal_address", display_address="fake_display_address", is_valid=True
            )
            wallet_manager.pre_send(wallet.id, "eth_usdt", "fake_display_address")
            fake_provider_manager.verify_address.assert_called_once_with("eth", "fake_display_address")
            fake_handler.generate_unsigned_tx.assert_called_once_with(
                wallet.id, "eth_usdt", "fake_normal_address", None, None, None, None, None
            )

        with self.subTest("Call with illegal to_address"):
            fake_provider_manager.verify_address.return_value = provider_data.AddressValidation(
                normalized_address="fake_normal_address", display_address="fake_display_address", is_valid=False
            )
            with self.assertRaisesRegex(
                wallet_exceptions.IllegalWalletOperation, "Invalid to_address: 'fake_display_address'"
            ):
                wallet_manager.pre_send(wallet.id, "eth_usdt", "fake_display_address")

    @patch("electrum_gui.common.wallet.manager.get_handler_by_chain_model")
    @patch("electrum_gui.common.wallet.manager.coin_manager")
    @patch("electrum_gui.common.wallet.manager.provider_manager")
    @patch("electrum_gui.common.wallet.manager.secret_manager")
    @patch("electrum_gui.common.wallet.manager.transaction_manager")
    def test_send(
        self,
        fake_transaction_manager,
        fake_secret_manager,
        fake_provider_manager,
        fake_coin_manager,
        fake_get_handler_by_chain_model,
    ):
        with self.subTest("Illegal wallet type"):
            wallet = wallet_daos.wallet.create_wallet("testing", wallet_data.WalletType.WATCHONLY, "eth")
            with self.assertRaisesRegex(
                wallet_exceptions.IllegalWalletOperation, "Watchonly wallet can not send asset"
            ):
                wallet_manager.send(wallet.id, "eth_usdt", "fake_display_address", 10, "123")

        with self.subTest("Send asset"):
            wallet = wallet_daos.wallet.create_wallet("testing", wallet_data.WalletType.SOFTWARE_PRIMARY, "eth")
            account = wallet_daos.account.create_account(wallet.id, "eth", "my_address", pubkey_id=111)
            wallet_daos.asset.create_asset(wallet.id, account.id, "eth", "eth_usdt")

            fake_handler = Mock()
            fake_unsigned_tx = provider_data.UnsignedTx(
                inputs=[provider_data.TransactionInput(address="my_address", value=10)],
                outputs=[provider_data.TransactionOutput(address="fake_normal_address", value=10)],
                nonce=3,
                fee_limit=101,
                fee_price_per_unit=11,
            )
            fake_handler.generate_unsigned_tx.return_value = fake_unsigned_tx
            fake_get_handler_by_chain_model.return_value = fake_handler

            fake_coin_manager.get_coin_info.return_value = Mock(chain_code="eth")
            fake_coin_manager.get_chain_info.return_value = Mock(
                chain_code="eth", chain_model=coin_data.ChainModel.ACCOUNT
            )

            fake_provider_manager.verify_address.return_value = provider_data.AddressValidation(
                normalized_address="fake_normal_address", display_address="fake_display_address", is_valid=True
            )
            fake_provider_manager.sign_transaction.return_value = provider_data.SignedTx(
                txid="fake_txid", raw_tx="fake_raw_tx"
            )
            fake_provider_manager.broadcast_transaction.return_value = provider_data.TxBroadcastReceipt(
                txid="fake_txid", is_success=True, receipt_code=provider_data.TxBroadcastReceiptCode.SUCCESS
            )

            fake_signer = Mock()
            fake_secret_manager.get_signer.return_value = fake_signer

            self.assertEqual(
                provider_data.SignedTx(txid="fake_txid", raw_tx="fake_raw_tx"),
                wallet_manager.send(wallet.id, "eth_usdt", "fake_display_address", 10, "123"),
            )

            fake_coin_manager.get_coin_info.assert_called_once_with("eth_usdt")
            fake_coin_manager.get_chain_info.assert_called_once_with("eth")
            fake_get_handler_by_chain_model.assert_called_once_with(coin_data.ChainModel.ACCOUNT)
            fake_provider_manager.verify_address.assert_has_calls(
                [call("eth", "fake_display_address"), call("eth", "fake_normal_address")]
            )
            fake_handler.generate_unsigned_tx.assert_called_once_with(
                wallet.id, "eth_usdt", "fake_normal_address", 10, None, None, None, None
            )
            fake_secret_manager.get_signer.assert_called_once_with("123", 111)
            fake_provider_manager.sign_transaction.assert_called_once_with(
                "eth", fake_unsigned_tx, {"my_address": fake_signer}
            )
            fake_provider_manager.broadcast_transaction.assert_called_once_with("eth", "fake_raw_tx")
            fake_transaction_manager.update_action_status.assert_called_once_with(
                "eth", "fake_txid", transaction_data.TxActionStatus.PENDING
            )
            fake_transaction_manager.create_action.assert_called_once_with(
                txid="fake_txid",
                status=transaction_data.TxActionStatus.PENDING,
                chain_code="eth",
                coin_code="eth_usdt",
                value=decimal.Decimal(10),
                from_address="my_address",
                to_address="fake_normal_address",
                fee_limit=decimal.Decimal(101),
                fee_price_per_unit=decimal.Decimal(11),
                nonce=3,
                raw_tx="fake_raw_tx",
            )

    @patch("electrum_gui.common.wallet.manager.provider_manager")
    @patch("electrum_gui.common.wallet.manager.transaction_manager")
    def test_broadcast_transaction(self, fake_transaction_manager, fake_provider_manager):
        with self.subTest("broadcast success"):
            fake_receipt = provider_data.TxBroadcastReceipt(
                is_success=True, txid="fake_txid", receipt_code=provider_data.TxBroadcastReceiptCode.SUCCESS
            )
            fake_provider_manager.broadcast_transaction.return_value = fake_receipt
            self.assertEqual(
                fake_receipt,
                wallet_manager.broadcast_transaction(
                    "eth", provider_data.SignedTx(txid="fake_txid", raw_tx="fake_raw_tx")
                ),
            )

            fake_provider_manager.broadcast_transaction.assert_called_once_with("eth", "fake_raw_tx")
            fake_transaction_manager.update_action_status.assert_called_once_with(
                "eth", "fake_txid", transaction_data.TxActionStatus.PENDING
            )
            fake_provider_manager.broadcast_transaction.reset_mock()
            fake_transaction_manager.update_action_status.reset_mock()

        with self.subTest("broadcast failed"):
            fake_receipt = provider_data.TxBroadcastReceipt(
                is_success=False, txid="fake_txid", receipt_code=provider_data.TxBroadcastReceiptCode.UNEXPECTED_FAILED
            )
            fake_provider_manager.broadcast_transaction.return_value = fake_receipt
            self.assertEqual(
                fake_receipt,
                wallet_manager.broadcast_transaction(
                    "eth", provider_data.SignedTx(txid="fake_txid", raw_tx="fake_raw_tx")
                ),
            )
            fake_provider_manager.broadcast_transaction.assert_called_once_with("eth", "fake_raw_tx")
            fake_transaction_manager.update_action_status.assert_called_once_with(
                "eth", "fake_txid", transaction_data.TxActionStatus.UNEXPECTED_FAILED
            )
            fake_provider_manager.broadcast_transaction.reset_mock()
            fake_transaction_manager.update_action_status.reset_mock()

        with self.subTest("Txid mismatched"):
            fake_receipt = provider_data.TxBroadcastReceipt(
                is_success=False, txid="fake_txid2", receipt_code=provider_data.TxBroadcastReceiptCode.UNEXPECTED_FAILED
            )
            fake_provider_manager.broadcast_transaction.return_value = fake_receipt
            with self.assertRaisesRegex(AssertionError, "Txid mismatched. expected: fake_txid, actual: fake_txid2"):
                wallet_manager.broadcast_transaction(
                    "eth", provider_data.SignedTx(txid="fake_txid", raw_tx="fake_raw_tx")
                )

        with self.subTest("Txid filling"):
            fake_receipt = provider_data.TxBroadcastReceipt(
                is_success=True, receipt_code=provider_data.TxBroadcastReceiptCode.SUCCESS
            )
            fake_provider_manager.broadcast_transaction.return_value = fake_receipt
            self.assertEqual(
                fake_receipt.clone(txid="fake_txid"),
                wallet_manager.broadcast_transaction(
                    "eth", provider_data.SignedTx(txid="fake_txid", raw_tx="fake_raw_tx")
                ),
            )

    @patch("electrum_gui.common.wallet.manager.get_default_account_by_wallet")
    @patch("electrum_gui.common.wallet.manager.coin_manager")
    def test_create_or_show_asset(self, fake_coin_manager, fake_get_default_account_by_wallet):
        with self.subTest("Create asset as expected"):
            fake_coin_manager.get_coin_info.return_value = Mock(code="eth_usdt", chain_code="eth")
            fake_get_default_account_by_wallet.return_value = Mock(id=1001, chain_code="eth")

            wallet_manager.create_or_show_asset(11, "eth_usdt")

            assets = wallet_daos.asset.query_assets_by_accounts([1001])
            self.assertEqual(1, len(assets))
            testing_asset = assets[0]
            self.assertEqual(
                (11, 1001, "eth", "eth_usdt", True),
                (
                    testing_asset.wallet_id,
                    testing_asset.account_id,
                    testing_asset.chain_code,
                    testing_asset.coin_code,
                    testing_asset.is_visible,
                ),
            )
            fake_coin_manager.get_coin_info.assert_called_once_with("eth_usdt")
            fake_get_default_account_by_wallet.assert_called_once_with(11)
            fake_coin_manager.get_coin_info.reset_mock()

        wallet_daos.asset.hide_asset(testing_asset.id)
        testing_asset = wallet_daos.asset.query_assets_by_ids([testing_asset.id])[0]
        self.assertFalse(testing_asset.is_visible)

        with self.subTest("Show created asset again"):
            wallet_manager.create_or_show_asset(11, "eth_usdt")
            testing_asset = wallet_daos.asset.query_assets_by_ids([testing_asset.id])[0]
            self.assertTrue(testing_asset.is_visible)

    @patch("electrum_gui.common.wallet.manager.get_default_account_by_wallet")
    @patch("electrum_gui.common.wallet.manager.coin_manager")
    def test_hide_asset(self, fake_coin_manager, fake_get_default_account_by_wallet):
        with self.subTest("Asset not found"):
            fake_coin_manager.get_coin_info.return_value = Mock(code="eth_usdt", chain_code="eth")
            fake_get_default_account_by_wallet.return_value = Mock(id=1001, chain_code="eth")

            with self.assertRaisesRegex(
                wallet_exceptions.IllegalWalletOperation, "Asset not found. wallet_id: 11, coin_code: eth_usdt"
            ):
                wallet_manager.hide_asset(11, "eth_usdt")

        with self.subTest("Hide asset as expected"):
            asset = wallet_daos.asset.create_asset(11, 1001, "eth", "eth_usdt")
            self.assertTrue(asset.is_visible)
            wallet_manager.hide_asset(11, "eth_usdt")
            asset = wallet_daos.asset.query_assets_by_ids([asset.id])[0]
            self.assertFalse(asset.is_visible)

    @patch("electrum_gui.common.wallet.manager.coin_manager")
    @patch("electrum_gui.common.wallet.manager.provider_manager")
    def test_refresh_assets(self, fake_provider_manager, fake_coin_manager):
        account = wallet_daos.account.create_account(11, "eth", "fake_address")
        asset_a = wallet_daos.asset.create_asset(11, account.id, "eth", "eth_usdt")
        asset_b = wallet_daos.asset.create_asset(11, account.id, "eth", "eth_cc")

        fake_coin_manager.query_coins_by_codes.return_value = [
            Mock(code="eth_usdt", token_address="contract_a"),
            Mock(code="eth_cc", token_address="contract_b"),
        ]
        fake_provider_manager.get_balance.side_effect = lambda chain_code, address, token_address: {
            "contract_a": 11,
            "contract_b": 12,
        }.get(token_address)

        with self.subTest("Refresh nothing"):
            self.assertEqual([asset_a, asset_b], wallet_manager.refresh_assets([asset_a, asset_b]))
            fake_coin_manager.query_coins_by_codes.assert_not_called()
            fake_provider_manager.get_balance.assert_not_called()

        with self.subTest("Refresh asset_b"):
            wallet_models.AssetModel.update(
                modified_time=datetime.datetime.now() - datetime.timedelta(seconds=10)
            ).where(wallet_models.AssetModel.id == asset_b.id).execute()
            asset_b = wallet_models.AssetModel.get_by_id(asset_b.id)

            asset_a, asset_b = wallet_manager.refresh_assets([asset_a, asset_b])
            self.assertEqual(12, asset_b.balance)
            fake_coin_manager.query_coins_by_codes.assert_called_once_with(["eth_cc"])
            fake_provider_manager.get_balance.assert_called_once_with("eth", "fake_address", "contract_b")
            fake_coin_manager.query_coins_by_codes.reset_mock()
            fake_provider_manager.get_balance.reset_mock()

        with self.subTest("Refresh all"):
            asset_a, asset_b = wallet_manager.refresh_assets([asset_a, asset_b], force_update=True)
            self.assertEqual(11, asset_a.balance)
            self.assertEqual(12, asset_b.balance)
            fake_coin_manager.query_coins_by_codes.assert_called_once_with(["eth_usdt", "eth_cc"])
            fake_provider_manager.get_balance.assert_has_calls(
                [
                    call("eth", "fake_address", "contract_a"),
                    call("eth", "fake_address", "contract_b"),
                ]
            )

    def test_get_default_bip44_path(self):
        self.assertEqual("m/44'/0'/0'/0/0", wallet_manager.get_default_bip44_path("btc", "P2PKH").to_bip44_path())
        self.assertEqual("m/49'/0'/0'/0/0", wallet_manager.get_default_bip44_path("btc", "P2WPKH-P2SH").to_bip44_path())
        self.assertEqual("m/84'/0'/0'/0/0", wallet_manager.get_default_bip44_path("btc", "P2WPKH").to_bip44_path())
        self.assertEqual("m/44'/60'/0'/0/0", wallet_manager.get_default_bip44_path("eth").to_bip44_path())
        self.assertEqual("m/44'/101010'/0'/0'/0'", wallet_manager.get_default_bip44_path("stc").to_bip44_path())
        self.assertEqual("m/44'/501'/0'/0'", wallet_manager.get_default_bip44_path("sol").to_bip44_path())

    def test_get_address_by_wallet_id(self):
        wallet_info = wallet_manager.create_primary_wallets(
            ["stc"],
            password=self.password,
            mnemonic=self.mnemonic,
            passphrase=self.passphrase,
        )[0]
        account_info = wallet_manager.get_default_account_by_wallet(wallet_info["wallet_id"])

        with self.subTest("bech32"):
            self.assertEqual(
                "stc1pynrlp8w98nnky4f2pz952hdjupduxlectpjhwjj8me5w5nxkdwtjf3lsnhzneemz254q3z69tkewqe8f96q",
                wallet_manager.get_encoded_address_by_account_id(account_info.id, "BECH32"),
            )

        with self.subTest("default"):
            self.assertEqual(
                "0x24c7f09dc53ce762552a088b455db2e0",
                wallet_manager.get_encoded_address_by_account_id(account_info.id),
            )

    def test_cascade_delete_wallet_related_models(self):
        wallet_info = wallet_manager.import_standalone_wallet_by_mnemonic(
            "ETH_BY_MNEMONIC",
            "eth",
            self.mnemonic,
            passphrase=self.passphrase,
            password=self.password,
            bip44_path="m/44'/60'/0'/0/0",
        )

        self.assertEqual(1, len(secret_models.SecretKeyModel.select()))
        self.assertEqual(1, len(secret_models.PubKeyModel.select()))
        self.assertEqual(1, len(wallet_models.WalletModel.select()))
        self.assertEqual(1, len(wallet_models.AccountModel.select()))
        self.assertEqual(1, len(wallet_models.AssetModel.select()))

        wallet_manager.cascade_delete_wallet_related_models(wallet_info["wallet_id"], self.password)

        self.assertEqual(0, len(secret_models.SecretKeyModel.select()))
        self.assertEqual(0, len(secret_models.PubKeyModel.select()))
        self.assertEqual(0, len(wallet_models.WalletModel.select()))
        self.assertEqual(0, len(wallet_models.AccountModel.select()))
        self.assertEqual(0, len(wallet_models.AssetModel.select()))

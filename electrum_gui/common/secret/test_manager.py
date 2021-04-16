from unittest import TestCase
from unittest.mock import patch

from electrum_gui.common.basic.orm import test_utils
from electrum_gui.common.secret import manager as secret_manager
from electrum_gui.common.secret import utils
from electrum_gui.common.secret.data import CurveEnum, PubKeyType, SecretKeyType
from electrum_gui.common.secret.models import PubKeyModel, SecretKeyModel


@test_utils.cls_test_database(PubKeyModel, SecretKeyModel)
class TestSecretManager(TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.message = b"Hello OneKey"
        cls.master_seed = b"OneKey"

        cls.account_level_path = "m/44'/60'/0'"
        cls.account_level_xpub = "xpub6D4isiEFQXfAVCF3CnvT5Y6XMfhTLS5ktxfEmogY8XARnHGEE5Q3kwggdhmfZPgkotFkcBFGzEz4NpPJGBuTjKa1CArZbVHAN11rLtut5ir"
        cls.account_level_xprv = "xprv9z5NUChMaA6sGiAa6mPSiQ9nodrxvyMuXjjdyRGvaBdSuUw5gY5oD9NCnPbfgYrV4dueoezeqdNsjKS5p3itgKpTJE3Adr1UNhaojqBo982"
        cls.account_level_signature = (
            "efaa470b160ca83ca394f71bea8e62aef5c12525ea4e233fc57cb354056ce8fb05ae7461d196736b31a5acf48fa0157b2b9543095da48643df178c9cc4623896",
            0,
        )

        cls.address_level_path = f"{cls.account_level_path}/0/0"
        cls.address_level_prvkey = "097f2126bf59ee30179c967054d7011069867eca9bcfb9f09b10d5ad3faee314"
        cls.address_level_pubkey = "032bb3c861b4a61c15e4009240d0849a25b12b4aa209ec54c7867f67ea1fe963cf"
        cls.address_level_signature = (
            "3c16acda928bd6b6e8ab71f17346cd3afe847e653ed612487adc65a6f0bf25a57b9a35af00a2f87e3f55f9dc045d579e296e666700616f2f6b9fe24945f61b05",
            1,
        )

    def test_import_pubkey(self):
        pubkey_model = secret_manager.import_pubkey(CurveEnum.SECP256K1, bytes.fromhex(self.address_level_pubkey))

        models = list(PubKeyModel.select())
        self.assertEqual(len(models), 1)

        first_model: PubKeyModel = models[0]
        self.assertEqual(first_model.id, pubkey_model.id)
        self.assertEqual(first_model.curve, CurveEnum.SECP256K1)
        self.assertEqual(first_model.pubkey_type, PubKeyType.PUBKEY)
        self.assertEqual(first_model.pubkey, self.address_level_pubkey)
        self.assertIsNone(first_model.path)
        self.assertIsNone(first_model.parent_pubkey_id)
        self.assertIsNone(first_model.secret_key_id)

    def test_import_xpub(self):
        xpub_model = secret_manager.import_xpub(CurveEnum.SECP256K1, self.account_level_xpub, self.account_level_path)

        models = list(PubKeyModel.select())
        self.assertEqual(len(models), 1)

        first_model: PubKeyModel = models[0]
        self.assertEqual(first_model.id, xpub_model.id)
        self.assertEqual(first_model.curve, CurveEnum.SECP256K1)
        self.assertEqual(first_model.pubkey_type, PubKeyType.XPUB)
        self.assertEqual(first_model.pubkey, self.account_level_xpub)
        self.assertEqual(first_model.path, self.account_level_path)
        self.assertIsNone(first_model.parent_pubkey_id)
        self.assertIsNone(first_model.secret_key_id)

    def test_import_xpub_and_sub_pubkey(self):
        xpub_model = secret_manager.import_xpub(CurveEnum.SECP256K1, self.account_level_xpub, self.account_level_path)
        pubkey_model = secret_manager.import_pubkey(
            CurveEnum.SECP256K1,
            bytes.fromhex(self.address_level_pubkey),
            self.address_level_path,
            parent_pubkey_id=xpub_model.id,
        )

        models = list(PubKeyModel.select())
        self.assertEqual(len(models), 2)
        self.assertEqual(models[0], xpub_model)
        self.assertEqual(models[1], pubkey_model)

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_import_prvkey(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: f"Encrypted<{p},{d}>"

        pubkey_model, secret_key_model = secret_manager.import_prvkey(
            "hello",
            CurveEnum.SECP256K1,
            bytes.fromhex(self.address_level_prvkey),
        )

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.address_level_prvkey)
        fake_encrypt.decrypt_data.assert_not_called()

        secret_key_models = list(SecretKeyModel.select())
        self.assertEqual(len(secret_key_models), 1)

        first_secret_key_model: SecretKeyModel = secret_key_models[0]
        self.assertEqual(first_secret_key_model.id, secret_key_model.id)
        self.assertEqual(first_secret_key_model.secret_key_type, SecretKeyType.PRVKEY)
        self.assertEqual(first_secret_key_model.encrypted_secret_key, f"Encrypted<hello,{self.address_level_prvkey}>")

        pubkey_models = list(PubKeyModel.select())
        self.assertEqual(len(pubkey_models), 1)

        first_pubkey_model: PubKeyModel = pubkey_models[0]
        self.assertEqual(first_pubkey_model.id, pubkey_model.id)
        self.assertEqual(first_pubkey_model.secret_key_id, secret_key_model.id)
        self.assertEqual(first_pubkey_model.pubkey_type, PubKeyType.PUBKEY)
        self.assertEqual(first_pubkey_model.pubkey, self.address_level_pubkey)
        self.assertEqual(first_pubkey_model.curve, CurveEnum.SECP256K1)
        self.assertIsNone(first_pubkey_model.path)
        self.assertIsNone(first_pubkey_model.parent_pubkey_id)

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_import_xprv(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: f"Encrypted<{p},{d}>"

        pubkey_model, secret_key_model = secret_manager.import_xprv(
            "hello",
            CurveEnum.SECP256K1,
            self.account_level_xprv,
            path=self.account_level_path,
        )

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.account_level_xprv)
        fake_encrypt.decrypt_data.assert_not_called()

        secret_key_models = list(SecretKeyModel.select())
        self.assertEqual(len(secret_key_models), 1)

        first_secret_key_model: SecretKeyModel = secret_key_models[0]
        self.assertEqual(first_secret_key_model.id, secret_key_model.id)
        self.assertEqual(first_secret_key_model.secret_key_type, SecretKeyType.XPRV)
        self.assertEqual(first_secret_key_model.encrypted_secret_key, f"Encrypted<hello,{self.account_level_xprv}>")

        pubkey_models = list(PubKeyModel.select())
        self.assertEqual(len(pubkey_models), 1)

        first_pubkey_model: PubKeyModel = pubkey_models[0]
        self.assertEqual(first_pubkey_model.id, pubkey_model.id)
        self.assertEqual(first_pubkey_model.secret_key_id, secret_key_model.id)
        self.assertEqual(first_pubkey_model.pubkey_type, PubKeyType.XPUB)
        self.assertEqual(first_pubkey_model.pubkey, self.account_level_xpub)
        self.assertEqual(first_pubkey_model.curve, CurveEnum.SECP256K1)
        self.assertEqual(first_pubkey_model.path, self.account_level_path)
        self.assertIsNone(first_pubkey_model.parent_pubkey_id)

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_import_master_seed(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: f"Encrypted<{p},{d}>"

        secret_key_model = secret_manager.import_master_seed("hello", self.master_seed)

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.master_seed.hex())
        fake_encrypt.decrypt_data.assert_not_called()

        secret_key_models = list(SecretKeyModel.select())
        self.assertEqual(len(secret_key_models), 1)

        first_secret_key_model: SecretKeyModel = secret_key_models[0]
        self.assertEqual(first_secret_key_model.id, secret_key_model.id)
        self.assertEqual(first_secret_key_model.secret_key_type, SecretKeyType.SEED)
        self.assertEqual(first_secret_key_model.encrypted_secret_key, f"Encrypted<hello,{self.master_seed.hex()}>")

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_derive_by_secret_key(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: d
        fake_encrypt.decrypt_data.side_effect = lambda p, d: d

        secret_key_model = secret_manager.import_master_seed("hello", self.master_seed)

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.master_seed.hex())
        fake_encrypt.decrypt_data.assert_not_called()
        fake_encrypt.encrypt_data.reset_mock()

        pubkey_model = secret_manager.derive_by_secret_key(
            "hello", CurveEnum.SECP256K1, secret_key_model.id, self.account_level_path
        )  # Only create new object, without saving to the database

        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_called_once_with("hello", secret_key_model.encrypted_secret_key)

        self.assertEqual(len(PubKeyModel.select()), 0)
        self.assertIsNone(pubkey_model.id)
        self.assertEqual(pubkey_model.secret_key_id, secret_key_model.id)
        self.assertEqual(pubkey_model.pubkey_type, PubKeyType.XPUB)
        self.assertEqual(pubkey_model.pubkey, self.account_level_xpub)
        self.assertEqual(pubkey_model.curve, CurveEnum.SECP256K1)
        self.assertEqual(pubkey_model.path, self.account_level_path)
        self.assertIsNone(pubkey_model.parent_pubkey_id)

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_derive_by_xpub(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: d

        secret_key_model = secret_manager.import_master_seed("hello", self.master_seed)

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.master_seed.hex())
        fake_encrypt.decrypt_data.assert_not_called()
        fake_encrypt.encrypt_data.reset_mock()

        xpub_model = secret_manager.import_xpub(
            CurveEnum.SECP256K1, self.account_level_xpub, self.account_level_path, secret_key_id=secret_key_model.id
        )
        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_not_called()

        sub_path = utils.diff_bip32_paths(self.account_level_path, self.address_level_path)
        self.assertEqual(sub_path, "m/0/0")

        pubkey_model = secret_manager.derive_by_xpub(
            xpub_model.id, sub_path, target_pubkey_type=PubKeyType.PUBKEY
        )  # Only create new object, without saving to the database
        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_not_called()

        self.assertEqual(len(PubKeyModel.select()), 1)
        self.assertIsNone(pubkey_model.id)
        self.assertEqual(pubkey_model.secret_key_id, secret_key_model.id)
        self.assertEqual(pubkey_model.pubkey_type, PubKeyType.PUBKEY)
        self.assertEqual(pubkey_model.pubkey, self.address_level_pubkey)
        self.assertEqual(pubkey_model.curve, CurveEnum.SECP256K1)
        self.assertEqual(pubkey_model.path, self.address_level_path)
        self.assertEqual(pubkey_model.parent_pubkey_id, xpub_model.id)

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_get_verifier(self, fake_encrypt):
        xpub_model = secret_manager.import_xpub(CurveEnum.SECP256K1, self.account_level_xpub, self.account_level_path)
        pubkey_model = secret_manager.import_pubkey(
            CurveEnum.SECP256K1,
            bytes.fromhex(self.address_level_pubkey),
            self.address_level_path,
            parent_pubkey_id=xpub_model.id,
        )
        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_not_called()

        self.assertTrue(
            secret_manager.get_verifier(xpub_model.id).verify(
                self.message, bytes.fromhex(self.account_level_signature[0])
            )
        )
        self.assertTrue(
            secret_manager.get_verifier(pubkey_model.id).verify(
                self.message, bytes.fromhex(self.address_level_signature[0])
            )
        )
        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_not_called()

    @patch("electrum_gui.common.secret.manager.encrypt")
    def test_get_signer(self, fake_encrypt):
        fake_encrypt.encrypt_data.side_effect = lambda p, d: d
        fake_encrypt.decrypt_data.side_effect = lambda p, d: d

        # 1. import master seed
        secret_key_model = secret_manager.import_master_seed("hello", self.master_seed)

        fake_encrypt.encrypt_data.assert_called_once_with("hello", self.master_seed.hex())
        fake_encrypt.decrypt_data.assert_not_called()
        fake_encrypt.encrypt_data.reset_mock()

        # 2. derive account level xpub from master seed
        temp_account_level_xpub = secret_manager.derive_by_secret_key(
            "hello", CurveEnum.SECP256K1, secret_key_model.id, self.account_level_path
        )
        imported_account_level_xpub = secret_manager.import_xpub(
            temp_account_level_xpub.curve,
            temp_account_level_xpub.pubkey,
            path=temp_account_level_xpub.path,
            parent_pubkey_id=temp_account_level_xpub.parent_pubkey_id,
            secret_key_id=temp_account_level_xpub.secret_key_id,
        )

        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_called_once_with(
            "hello", secret_key_model.encrypted_secret_key
        )  # Only expose the seed when generating xpub
        fake_encrypt.decrypt_data.reset_mock()

        # 3. derive address level pubkey from account level xpub
        temp_address_level_pubkey = secret_manager.derive_by_xpub(
            imported_account_level_xpub.id,
            utils.diff_bip32_paths(self.account_level_path, self.address_level_path),
            PubKeyType.PUBKEY,
        )
        imported_address_level_pubkey = secret_manager.import_pubkey(
            temp_address_level_pubkey.curve,
            bytes.fromhex(temp_address_level_pubkey.pubkey),
            temp_address_level_pubkey.path,
            temp_address_level_pubkey.parent_pubkey_id,
            temp_address_level_pubkey.secret_key_id,
        )

        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_not_called()

        signer = secret_manager.get_signer("hello", imported_address_level_pubkey.id)
        fake_encrypt.encrypt_data.assert_not_called()
        fake_encrypt.decrypt_data.assert_called_once_with("hello", secret_key_model.encrypted_secret_key)

        self.assertEqual(
            signer.sign(self.message), (bytes.fromhex(self.address_level_signature[0]), self.address_level_signature[1])
        )

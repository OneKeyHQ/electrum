from unittest import TestCase

from electrum_gui.common.secret import registry
from electrum_gui.common.secret.data import CurveEnum
from electrum_gui.common.secret.interfaces import KeyInterface


class TestKeys(TestCase):
    def test_keys(self):
        prvkey_bytes = bytes([11]) * 32
        digests = [bytes([i]) * 32 for i in range(12)]

        for i, (curve, key_class) in enumerate(registry.KEY_CLASS_MAPPING.items()):
            with self.subTest(f"Case-{i}-{curve}"):
                self.assertTrue(issubclass(key_class, KeyInterface))
                self.assertEqual(registry.key_class_on_curve(curve), key_class)

                prvkey: KeyInterface = key_class(prvkey=prvkey_bytes)
                self.assertTrue(prvkey.has_prvkey())

                if curve in (CurveEnum.SECP256K1, CurveEnum.SECP256R1):
                    self.assertEqual(len(prvkey.get_pubkey()), 33)
                    self.assertEqual(len(prvkey.get_pubkey(compressed=True)), 33)
                    self.assertEqual(len(prvkey.get_pubkey(compressed=False)), 65)

                pubkey: KeyInterface = key_class(pubkey=prvkey.get_pubkey())
                self.assertEqual(prvkey.get_pubkey(), pubkey.get_pubkey())
                self.assertEqual(prvkey.get_pubkey(compressed=True), pubkey.get_pubkey(compressed=True))
                self.assertEqual(prvkey.get_pubkey(compressed=False), pubkey.get_pubkey(compressed=False))

                for j, digest in enumerate(digests):
                    with self.subTest(f"Case-{i}-{curve}-Digest-{j}"):
                        signature, recid = prvkey.sign(digest)
                        self.assertTrue(recid in range(4))
                        self.assertTrue(prvkey.verify(digest, signature))
                        self.assertTrue(pubkey.verify(digest, signature))

                        with self.assertRaisesRegex(Exception, "Private key not found"):
                            pubkey.sign(digest)

                error_signature = bytes([11]) * 64
                self.assertFalse(prvkey.verify(bytes([11]) * 32, error_signature))
                self.assertFalse(pubkey.verify(bytes([11]) * 32, error_signature))

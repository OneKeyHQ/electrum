from unittest import TestCase
from unittest.mock import Mock

from electrum_gui.common.provider.chains.algo import ALGOProvider
from electrum_gui.common.provider.data import AddressValidation


class TestSTCProvider(TestCase):
    def setUp(self) -> None:
        self.fake_chain_info = Mock()
        self.fake_coins_loader = Mock()
        self.fake_client_selector = Mock()
        self.provider = ALGOProvider(
            chain_info=self.fake_chain_info,
            coins_loader=self.fake_coins_loader,
            client_selector=self.fake_client_selector,
        )

    def test_verify_address(self):
        self.assertEqual(
            AddressValidation(is_valid=True),
            self.provider.verify_address("QBMGRUUBODGYJCR6IKZTOVFNN7ARG2MIRHHNSQA3FLOYFO4ZKKT5U6UG2Q"),
        )
        self.assertEqual(
            AddressValidation(is_valid=False),
            self.provider.verify_address("qbmgruubodgyjcr6ikztovfnn7arg2mirhhnsqa3floyfo4zkkt5u6ug2q"),
        )
        self.assertEqual(
            AddressValidation(is_valid=False),
            self.provider.verify_address("QBMGRUUBODGYJCR6IKZTOVFNN7ARG2MIRHHNSQA3FLOYFO4ZKKT5U6UG2q"),
        )
        self.assertEqual(
            AddressValidation(is_valid=False),
            self.provider.verify_address("QBMGRUUBODGYJCR6IKZTOVFNN7ARG2MIRHHNSQA3FLOYFO4ZKKT5U6UG2Q1"),
        )
        self.assertEqual(
            AddressValidation(is_valid=False),
            self.provider.verify_address("QBMGRUUBODGYJCR6IKZTOVFNN7ARG2MIRHHNSQA3FLOYFO4ZKKT5U6UG2"),
        )
        self.assertEqual(self.provider.verify_address(""), AddressValidation(is_valid=False))
        self.assertEqual(self.provider.verify_address("0x"), AddressValidation(is_valid=False))

    def test_pubkey_to_address(self):
        verifier = Mock(
            get_pubkey=Mock(
                return_value=bytes(
                    x for x in bytes.fromhex("f2a21123212974149cce8b909d5297a53c3ec2bf2f2f97d7483a4ba8094ca7e5")
                )
            )
        )
        self.assertEqual(
            "6KRBCIZBFF2BJHGOROIJ2UUXUU6D5QV7F4XZPV2IHJF2QCKMU7S4ECYHUA",
            self.provider.pubkey_to_address(verifier=verifier),
        )
        verifier.get_pubkey.assert_called_once_with(compressed=False)

    def test_sign_transaction(self):
        """
        with self.subTest("Sign Algo Transfer Tx"):
            fake_signer = Mock(
                sign=Mock(
                    return_value=(
                        bytes.fromhex(
                            "b4f0eb5b9994767f8e43885d4c50f5e066f14dee8c8c72bca1d717b392cb77d0738373e3bd1a7809c587afcbc8e31185bcdf0d288a63b01bca5eb7b713bed200"
                        ),
                        0,
                    )
                ),
                suggested_params=Mock(
                    return_value=SuggestedParams(
                        0,
                        14363848,
                        14364848,
                        "SGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiI=",
                        "testnet-v1.0",
                        True,
                        "https://github.com/algorandfoundation/specs/tree/d050b3cade6d5c664df8bd729bf219f179812595",
                        1000,
                    )
                ),
                get_pubkey=Mock(
                    return_value=bytes(
                        x for x in bytes.fromhex("f2a21123212974149cce8b909d5297a53c3ec2bf2f2f97d7483a4ba8094ca7e5")
                    )
                ),
            )
            signers = {"6KRBCIZBFF2BJHGOROIJ2UUXUU6D5QV7F4XZPV2IHJF2QCKMU7S4ECYHUA": fake_signer}
            self.assertEqual(
                self.provider.sign_transaction(
                    self.provider.fill_unsigned_tx(
                        UnsignedTx(
                            inputs=[
                                TransactionInput(
                                    address="6KRBCIZBFF2BJHGOROIJ2UUXUU6D5QV7F4XZPV2IHJF2QCKMU7S4ECYHUA", value=10000
                                )
                            ],
                            outputs=[
                                TransactionOutput(
                                    address="GD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A", value=10000
                                )
                            ],
                            flat_fee=True,
                        ),
                    ),
                    signers,
                ),
                SignedTx(
                    txid="FXCX7KGHFIHI3TCFQGS5WG4WWCMGGK6XD5HR6IA6TSQFR62DGLEA",
                    raw_tx="0xb61a35af603018441b06177a8820ff2a120000000000000002000000000000000000000000000000010f5472616e73666572536372697074730c706565725f746f5f706565720107000000000000000000000000000000010353544303535443000310194d36be65a955201ec79166b88ca18e01001000040000000000000000000000000000809698000000000001000000000000000d3078313a3a5354433a3a5354438a77a36000000000fb00207b945271879962dde59a0e170219d04a1c3ae3901de95041283c473902d0b03d40b4f0eb5b9994767f8e43885d4c50f5e066f14dee8c8c72bca1d717b392cb77d0738373e3bd1a7809c587afcbc8e31185bcdf0d288a63b01bca5eb7b713bed200",
                ),
            )
        """

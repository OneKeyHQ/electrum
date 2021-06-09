from unittest import TestCase
from unittest.mock import Mock

from electrum_gui.common.provider.chains.algo import ALGOProvider
from electrum_gui.common.provider.chains.algo.sdk.future.transaction import SuggestedParams
from electrum_gui.common.provider.data import (
    AddressValidation,
    SignedTx,
    TransactionInput,
    TransactionOutput,
    UnsignedTx,
)


class TestALGOProvider(TestCase):
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
            AddressValidation(
                normalized_address="GD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A",
                display_address="GD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A",
                is_valid=True,
            ),
            self.provider.verify_address("GD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A"),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address("gd64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A"),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address("GGD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A"),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address("GD64YIY3TWGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5AB"),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address("GD64YIY3WGDMCNPP553DZPPR6LDUSFQOIJVFDPPXWEG3FVOJCCDBBHU5A"),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address(""),
        )
        self.assertEqual(
            AddressValidation(normalized_address="", display_address="", is_valid=False),
            self.provider.verify_address("0x"),
        )

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
        fake_algo_restful = Mock(
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
            )
        )

        def _client_selector_side_effect(**kwargs):
            instance_required = kwargs.get("instance_required")
            if instance_required and issubclass(instance_required, ALGOProvider):
                return fake_algo_restful

        self.fake_client_selector.side_effect = _client_selector_side_effect

        with self.subTest("Sign Algo Transfer Tx"):
            fake_signer = Mock(
                sign=Mock(
                    return_value=(
                        bytes.fromhex(
                            "590bd43db6bc49529468bff5fb390ed8d1212efca463d6b4c4997f9494ce923215777c255f6664f8eff223c13f2fe7dc1258d6c362b496b6e5361ba43ece300f"
                        ),
                        0,
                    )
                )
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
                            flat_fee=1000,
                        ),
                    ),
                    signers,
                ),
                SignedTx(
                    txid="FXCX7KGHFIHI3TCFQGS5WG4WWCMGGK6XD5HR6IA6TSQFR62DGLEA",
                    raw_tx="gqNzaWfEQFkL1D22vElSlGi/9fs5DtjRIS78pGPWtMSZf5SUzpIyFXd8JV9mZPjv8iPBPy/n3BJY1sNitJa25TYbpD7OMA+jdHhuiaNhbXTNJxCjZmVlzQPoomZ2zgDbLMijZ2VurHRlc3RuZXQtdjEuMKJnaMQgSGO1GKSzyE7IEPItTxCByw9x8FmnrCDexi9/cOUJOiKibHbOANswsKNyY3bEIDD9zCMbnYw2Ca9/e7Hl74+WOkiwchNSje+9iG2WrkiEo3NuZMQg8qIRIyEpdBSczouQnVKXpTw+wr8vL5fXSDpLqAlMp+WkdHlwZaNwYXk=",
                ),
            )

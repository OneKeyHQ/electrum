from unittest import TestCase
from unittest.mock import Mock

from electrum_gui.common.provider.chains.eth import ETHProvider, Geth
from electrum_gui.common.provider.data import (
    AddressValidation,
    EstimatedTimeOnPrice,
    PricePerUnit,
    SignedTx,
    TransactionInput,
    TransactionOutput,
    UnsignedTx,
)


class TestETHProvider(TestCase):
    def setUp(self) -> None:
        self.fake_chain_info = Mock()
        self.fake_coins_loader = Mock()
        self.fake_client_selector = Mock()
        self.provider = ETHProvider(
            chain_info=self.fake_chain_info,
            coins_loader=self.fake_coins_loader,
            client_selector=self.fake_client_selector,
        )

    def test_verify_address(self):
        self.assertEqual(
            self.provider.verify_address("0x2E5124C037871DeB014490C37a4844F7019f38bD"),
            AddressValidation(is_valid=True, encoding="checksum"),
        )
        self.assertEqual(
            self.provider.verify_address("0x2e5124c037871deb014490c37a4844f7019f38bd"),
            AddressValidation(is_valid=True, encoding="hex"),
        )
        self.assertEqual(self.provider.verify_address(""), AddressValidation(is_valid=False))
        self.assertEqual(self.provider.verify_address("0x"), AddressValidation(is_valid=False))

    def test_pubkey_to_address(self):
        verifier = Mock(get_pubkey=Mock(return_value=b"\4" + b"\0" * 64))
        self.assertEqual(
            self.provider.pubkey_to_address(verifier=verifier), "0x3f17f1962b36e491b30a40b2405849e597ba5fb5"
        )
        verifier.get_pubkey.assert_called_once_with(compressed=False)

    def test_filling_unsigned_tx(self):
        external_address_a = "0x71df3bb810127271d400f7be99cc1f4504ab4c1a"
        external_address_b = "0xa305fab8bda7e1638235b054889b3217441dd645"
        contract_address = "0x0000000037871deb014490c37a4844f7019f38bd"

        fake_client = Mock(
            get_price_per_unit_of_fee=Mock(
                return_value=PricePerUnit(
                    slow=EstimatedTimeOnPrice(price=int(20 * 1e9)),
                    normal=EstimatedTimeOnPrice(price=int(30 * 1e9)),
                    fast=EstimatedTimeOnPrice(price=int(50 * 1e9)),
                )
            ),
            get_address=Mock(return_value=Mock(nonce=11)),
        )
        fake_geth = Mock(
            is_contract=Mock(side_effect=lambda address: address == contract_address),
            estimate_gas_limit=Mock(return_value=60000),
        )

        def _client_selector_side_effect(**kwargs):
            instance_required = kwargs.get("instance_required")
            if instance_required and issubclass(instance_required, Geth):
                return fake_geth
            else:
                return fake_client

        self.fake_client_selector.side_effect = _client_selector_side_effect

        with self.subTest("Empty UnsignedTx"):
            self.assertEqual(
                self.provider.filling_unsigned_tx(
                    UnsignedTx(),
                ),
                UnsignedTx(fee_limit=21000, fee_price_per_unit=int(30 * 1e9)),
            )
            fake_client.get_price_per_unit_of_fee.assert_called_once()
            fake_client.get_address.assert_not_called()
            fake_geth.is_contract.assert_not_called()
            fake_geth.estimate_gas_limit.assert_not_called()

            fake_client.get_price_per_unit_of_fee.reset_mock()

        with self.subTest("Transfer ETH to external address with preset gas price"):
            self.assertEqual(
                self.provider.filling_unsigned_tx(
                    UnsignedTx(
                        inputs=[TransactionInput(address=external_address_a, value=21)],
                        outputs=[TransactionOutput(address=external_address_b, value=21)],
                        fee_price_per_unit=int(102 * 1e9),
                    )
                ),
                UnsignedTx(
                    inputs=[TransactionInput(address=external_address_a, value=21)],
                    outputs=[TransactionOutput(address=external_address_b, value=21)],
                    nonce=11,
                    fee_price_per_unit=int(102 * 1e9),
                    fee_limit=21000,
                ),
            )
            fake_client.get_price_per_unit_of_fee.assert_not_called()
            fake_client.get_address.assert_called_once_with(external_address_a)
            fake_geth.is_contract.assert_called_once_with(external_address_b)
            fake_geth.estimate_gas_limit.assert_not_called()

            fake_client.get_address.reset_mock()
            fake_geth.is_contract.reset_mock()

        with self.subTest("Transfer ETH to contract address with preset nonce"):
            self.assertEqual(
                self.provider.filling_unsigned_tx(
                    UnsignedTx(
                        inputs=[TransactionInput(address=external_address_a, value=21)],
                        outputs=[TransactionOutput(address=contract_address, value=21)],
                        nonce=101,
                    )
                ),
                UnsignedTx(
                    inputs=[TransactionInput(address=external_address_a, value=21)],
                    outputs=[TransactionOutput(address=contract_address, value=21)],
                    nonce=101,
                    fee_price_per_unit=int(30 * 1e9),
                    fee_limit=int(60000 * 1.2),
                ),
            )
            fake_client.get_price_per_unit_of_fee.assert_called_once()
            fake_client.get_address.assert_not_called()
            fake_geth.is_contract.assert_called_once_with(contract_address)
            fake_geth.estimate_gas_limit.assert_called_once_with(external_address_a, contract_address, 21, None)

            fake_client.get_price_per_unit_of_fee.reset_mock()
            fake_geth.is_contract.reset_mock()
            fake_geth.estimate_gas_limit.reset_mock()

        with self.subTest("Transfer ERC20 with preset gas price and lower gas limit"):
            erc20_transfer_data = "0xa9059cbb000000000000000000000000a305fab8bda7e1638235b054889b3217441dd6450000000000000000000000000000000000000000000000000000000000000015"
            self.assertEqual(
                self.provider.filling_unsigned_tx(
                    UnsignedTx(
                        inputs=[
                            TransactionInput(
                                address=external_address_a,
                                value=21,
                                token_address=contract_address,
                            )
                        ],
                        outputs=[
                            TransactionOutput(
                                address=external_address_b,
                                value=21,
                                token_address=contract_address,
                            )
                        ],
                        fee_price_per_unit=int(102 * 1e9),
                        fee_limit=40000,
                    )
                ),
                UnsignedTx(
                    inputs=[
                        TransactionInput(
                            address=external_address_a,
                            value=21,
                            token_address=contract_address,
                        )
                    ],
                    outputs=[
                        TransactionOutput(
                            address=external_address_b,
                            value=21,
                            token_address=contract_address,
                        )
                    ],
                    nonce=11,
                    fee_price_per_unit=int(102 * 1e9),
                    fee_limit=int(60000 * 1.2),  # Update to the latest estimated gas limit and keep 20% buffer
                    payload={"data": erc20_transfer_data},
                ),
            )
            fake_client.get_price_per_unit_of_fee.assert_not_called()
            fake_client.get_address.assert_called_once_with(external_address_a)
            fake_geth.is_contract.assert_not_called()
            fake_geth.estimate_gas_limit.assert_called_once_with(
                external_address_a, contract_address, 0, erc20_transfer_data
            )

            fake_client.get_address.reset_mock()
            fake_geth.estimate_gas_limit.reset_mock()

    def test_sign_transaction(self):
        self.fake_chain_info.chain_id = 1

        with self.subTest("Sign ETH Transfer Tx"):
            fake_signer = Mock(
                sign=Mock(
                    return_value=(
                        bytes.fromhex(
                            "18c8df4036ef8e80434b789d84e14bbbc4db461bfcc14ffdf4faf4784cd8bf4f557d08ae22b87620bbadfb75e27a33ce4fb03aabd95c30925b4483315ab4493f"
                        ),
                        0,
                    )
                )
            )
            key_mapping = {"0x71df3bb810127271d400f7be99cc1f4504ab4c1a": fake_signer}
            self.assertEqual(
                self.provider.sign_transaction(
                    UnsignedTx(
                        inputs=[
                            TransactionInput(
                                address="0x71df3bb810127271d400f7be99cc1f4504ab4c1a", value=14995659910000000000
                            )
                        ],
                        outputs=[
                            TransactionOutput(
                                address="0xa305fab8bda7e1638235b054889b3217441dd645", value=14995659910000000000
                            )
                        ],
                        nonce=0,
                        fee_limit=21000,
                        fee_price_per_unit=107670000000,
                    ),
                    key_mapping,
                ),
                SignedTx(
                    txid="0xd27c78c026978846312d70cb56f8e2863b5480a37159dab9e22fb8cdd5127469",
                    raw_tx="0xf86c80851911a1d18082520894a305fab8bda7e1638235b054889b3217441dd64588d01b493cdc1dbc008025a018c8df4036ef8e80434b789d84e14bbbc4db461bfcc14ffdf4faf4784cd8bf4fa0557d08ae22b87620bbadfb75e27a33ce4fb03aabd95c30925b4483315ab4493f",
                ),
            )
            fake_signer.sign.assert_called_once_with(
                bytes.fromhex("6aa4f3fdc54226f2f47593ee65addbf5f7f698e42c2cdca878e2feab197083e0")
            )

        with self.subTest("Sign ERC20 Transfer Tx"):
            fake_signer = Mock(
                sign=Mock(
                    return_value=(
                        bytes.fromhex(
                            "ff00510948d652626624d8f518309a66f7ec2149da47735f378e90c267c579f822afc54c308fcddd4a19b313ecf03be9ee328e670e9109f141902dd89e43aaf7"
                        ),
                        1,
                    )
                )
            )
            key_mapping = {"0x9ce42ba2d6bb04f1e464520b044012187782f869": fake_signer}

            self.assertEqual(
                self.provider.sign_transaction(
                    UnsignedTx(
                        inputs=[
                            TransactionInput(
                                address="0x9ce42ba2d6bb04f1e464520b044012187782f869",
                                value=13170924111,
                                token_address="0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48",
                            )
                        ],
                        outputs=[
                            TransactionOutput(
                                address="0xa305fab8bda7e1638235b054889b3217441dd645",
                                value=13170924111,
                                token_address="0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48",
                            )
                        ],
                        nonce=0,
                        fee_limit=57393,
                        fee_price_per_unit=int(100 * 1e9),
                        payload={
                            "data": "0xa9059cbb000000000000000000000000a305fab8bda7e1638235b054889b3217441dd64500000000000000000000000000000000000000000000000000000003110c5a4f"
                        },
                    ),
                    key_mapping,
                ),
                SignedTx(
                    txid="0xaee8c4369180309b99abc48b736fbda7a70c1de014f902cb2fc2e3441a72e4fa",
                    raw_tx="0xf8a98085174876e80082e03194a0b86991c6218b36c1d19d4a2e9eb0ce3606eb4880b844a9059cbb000000000000000000000000a305fab8bda7e1638235b054889b3217441dd64500000000000000000000000000000000000000000000000000000003110c5a4f26a0ff00510948d652626624d8f518309a66f7ec2149da47735f378e90c267c579f8a022afc54c308fcddd4a19b313ecf03be9ee328e670e9109f141902dd89e43aaf7",
                ),
            )

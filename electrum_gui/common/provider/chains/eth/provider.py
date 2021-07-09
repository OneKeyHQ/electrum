from typing import Dict, Tuple

import eth_abi
import eth_account
import eth_keys
import eth_typing
import eth_utils
from eth_account._utils import transactions as eth_account_transactions  # noqa
from trezorlib import ethereum as trezor_ethereum

from electrum_gui.common.basic import bip44
from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.hardware import interfaces as hardware_interfaces
from electrum_gui.common.provider import data
from electrum_gui.common.provider.chains.eth import Geth
from electrum_gui.common.provider.data import AddressValidation, SignedTx, UnsignedTx
from electrum_gui.common.provider.interfaces import HardwareSupportingMixin, ProviderInterface
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


def _add_0x_prefix(value: str) -> str:
    return eth_utils.add_0x_prefix(eth_typing.HexStr(value))


def _remove_0x_prefix(value: str) -> str:
    return eth_utils.remove_0x_prefix(eth_typing.HexStr(value))


class _EthKey(object):
    def __init__(self, signer: SignerInterface):
        self.signer = signer

    def sign_msg_hash(self, digest: bytes):
        sig, rec_id = self.signer.sign(digest)
        return eth_keys.keys.Signature(sig + bytes([rec_id]))


class ETHProvider(ProviderInterface, HardwareSupportingMixin):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid = eth_utils.is_address(address)
        normalized_address, display_address = (
            (address.lower(), eth_utils.to_checksum_address(address)) if is_valid else ("", "")
        )
        return AddressValidation(
            normalized_address=normalized_address,
            display_address=display_address,
            is_valid=is_valid,
        )

    def pubkey_to_address(self, verifier: VerifierInterface, encoding: str = None) -> str:
        pubkey = verifier.get_pubkey(compressed=False)
        address = _add_0x_prefix(eth_utils.keccak(pubkey[-64:])[-20:].hex())
        return address

    @property
    def geth(self) -> Geth:
        return self.client_selector(instance_required=Geth)

    def fill_unsigned_tx(self, unsigned_tx: UnsignedTx) -> UnsignedTx:
        fee_price_per_unit = unsigned_tx.fee_price_per_unit or self.client.get_prices_per_unit_of_fee().normal.price
        nonce = unsigned_tx.nonce
        payload = unsigned_tx.payload.copy()
        tx_input = unsigned_tx.inputs[0] if unsigned_tx.inputs else None
        tx_output = unsigned_tx.outputs[0] if unsigned_tx.outputs else None
        fee_limit = unsigned_tx.fee_limit

        if tx_input is not None and tx_output is not None:
            from_address = tx_input.address
            to_address = tx_output.address
            value = tx_output.value
            token_address = tx_output.token_address

            if nonce is None:
                nonce = self.client.get_address(from_address).nonce

            if token_address is None:
                data = payload.get("data")
            else:
                data = _add_0x_prefix(
                    "a9059cbb" + eth_abi.encode_abi(("address", "uint256"), (to_address, value)).hex()
                )  # method_selector(transfer) + byte32_pad(address) + byte32_pad(value)
                value = 0
                to_address = token_address

            if data:
                payload["data"] = data

            if not fee_limit:
                estimate_fee_limit = self.geth.estimate_gas_limit(from_address, to_address, value, data)
                fee_limit = (
                    round(estimate_fee_limit * 1.2)
                    if token_address or self.geth.is_contract(to_address)
                    else estimate_fee_limit
                )

        fee_limit = fee_limit or 21000

        return unsigned_tx.clone(
            inputs=[tx_input] if tx_input is not None else [],
            outputs=[tx_output] if tx_output is not None else [],
            fee_limit=fee_limit,
            fee_price_per_unit=fee_price_per_unit,
            nonce=nonce,
            payload=payload,
        )

    def sign_transaction(self, unsigned_tx: UnsignedTx, signers: Dict[str, SignerInterface]) -> SignedTx:
        require(len(unsigned_tx.inputs) == 1 and len(unsigned_tx.outputs) == 1)
        from_address = unsigned_tx.inputs[0].address
        require(signers.get(from_address) is not None)

        eth_key = _EthKey(signers[from_address])
        tx_dict = self._build_unsigned_tx_dict(unsigned_tx)

        _, _, _, encoded_tx = eth_account.account.sign_transaction_dict(eth_key, tx_dict)
        return SignedTx(
            txid=_add_0x_prefix(eth_utils.keccak(encoded_tx).hex()),
            raw_tx=_add_0x_prefix(encoded_tx.hex()),
        )

    def get_token_info_by_address(self, token_address: str) -> Tuple[str, str, int]:
        return self.geth.get_token_info_by_address(token_address)

    def _build_unsigned_tx_dict(self, unsigned_tx: UnsignedTx) -> dict:
        output = unsigned_tx.outputs[0]
        is_erc20_transfer = bool(output.token_address)
        to_address = output.token_address if is_erc20_transfer else output.address
        value = 0 if is_erc20_transfer else output.value
        return {
            "to": eth_utils.to_checksum_address(to_address),
            "value": value,
            "gas": unsigned_tx.fee_limit,
            "gasPrice": unsigned_tx.fee_price_per_unit,
            "nonce": unsigned_tx.nonce,
            "data": _add_0x_prefix(unsigned_tx.payload.get("data") or "0x"),
            "chainId": int(self.chain_info.chain_id),
        }

    def hardware_get_xpub(
        self,
        hardware_client: hardware_interfaces.HardwareClientInterface,
        bip44_path: bip44.BIP44Path,
        confirm_on_device: bool = False,
    ) -> str:
        return trezor_ethereum.get_public_node(
            hardware_client, n=bip44_path.to_bip44_int_path(), show_display=confirm_on_device
        ).xpub

    def hardware_get_address(
        self,
        hardware_client: hardware_interfaces.HardwareClientInterface,
        bip44_path: bip44.BIP44Path,
        confirm_on_device: bool = False,
    ) -> str:
        address = trezor_ethereum.get_address(
            hardware_client, n=bip44_path.to_bip44_int_path(), show_display=confirm_on_device
        )
        return self.verify_address(address).normalized_address

    def hardware_sign_transaction(
        self,
        hardware_client: hardware_interfaces.HardwareClientInterface,
        bip44_path_of_signers: Dict[str, bip44.BIP44Path],
        unsigned_tx: data.UnsignedTx,
    ) -> data.SignedTx:
        require(len(unsigned_tx.inputs) == 1 and len(unsigned_tx.outputs) == 1)
        from_address = unsigned_tx.inputs[0].address
        require(bip44_path_of_signers.get(from_address) is not None)

        tx_dict = self._build_unsigned_tx_dict(unsigned_tx)
        v, r, s = trezor_ethereum.sign_tx(
            hardware_client,
            n=bip44_path_of_signers[from_address].to_bip44_int_path(),
            nonce=tx_dict["nonce"],
            gas_price=tx_dict["gasPrice"],
            gas_limit=tx_dict["gas"],
            to=tx_dict["to"],
            value=tx_dict["value"],
            data=bytes.fromhex(_remove_0x_prefix(tx_dict["data"])) if tx_dict.get("data") else None,
            chain_id=tx_dict["chainId"],
        )
        encoded_tx = eth_account_transactions.encode_transaction(
            eth_account_transactions.serializable_unsigned_transaction_from_dict(tx_dict),
            (v, eth_utils.big_endian_to_int(r), eth_utils.big_endian_to_int(s)),
        )

        return SignedTx(
            txid=_add_0x_prefix(eth_utils.keccak(encoded_tx).hex()),
            raw_tx=_add_0x_prefix(encoded_tx.hex()),
        )

    def hardware_sign_message(
        self,
        hardware_client: hardware_interfaces.HardwareClientInterface,
        signer_bip44_path: bip44.BIP44Path,
        message: str,
    ) -> str:
        return _add_0x_prefix(
            trezor_ethereum.sign_message(
                hardware_client,
                n=signer_bip44_path.to_bip44_int_path(),
                message=message,
            ).signature.hex()
        )

    def hardware_verify_message(
        self,
        hardware_client: hardware_interfaces.HardwareClientInterface,
        address: str,
        message: str,
        signature: str,
    ) -> bool:
        signature = bytes.fromhex(_remove_0x_prefix(signature))

        return trezor_ethereum.verify_message(
            hardware_client,
            address=address,
            message=message,
            signature=signature,
        )

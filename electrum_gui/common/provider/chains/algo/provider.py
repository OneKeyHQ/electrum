import base64
from typing import Dict, Tuple

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.provider.chains.algo import ALGORestful
from electrum_gui.common.provider.chains.algo.sdk import constants, encoding
from electrum_gui.common.provider.chains.algo.sdk.future.transaction import PaymentTxn, SignedTransaction
from electrum_gui.common.provider.data import AddressValidation, SignedTx, UnsignedTx
from electrum_gui.common.provider.interfaces import ProviderInterface
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


class _ALGKey(object):
    def __init__(self, signer: SignerInterface):
        self.signer = signer

    def sign_msg_hash(self, data: bytes):
        sig, _ = self.signer.sign(data)
        return sig


class ALGOProvider(ProviderInterface):
    def verify_address(self, address: str) -> AddressValidation:
        _normalized_address, _display_address = "", ""
        is_valid = encoding.is_valid_address(address)
        if is_valid:
            _normalized_address, _display_address = address, address
        return AddressValidation(
            normalized_address=_normalized_address, display_address=_display_address, is_valid=is_valid
        )

    def pubkey_to_address(self, verifier: VerifierInterface, enc: str = None) -> str:
        pubkey = verifier.get_pubkey(compressed=False)
        address = encoding.encode_address(pubkey)
        return address

    @property
    def algo_restful(self) -> ALGORestful:
        return self.client_selector(instance_required=ALGOProvider)

    def fill_unsigned_tx(self, unsigned_tx: UnsignedTx) -> UnsignedTx:
        params = self.algo_restful.suggested_params()
        params.flat_fee = True
        params.fee = unsigned_tx.flat_fee or params.min_fee
        payload = unsigned_tx.payload.copy()
        tx_input = unsigned_tx.inputs[0] if unsigned_tx.inputs else None
        tx_output = unsigned_tx.outputs[0] if unsigned_tx.outputs else None

        if tx_input is not None and tx_output is not None:
            from_address = tx_input.address
            to_address = tx_output.address
            value = tx_output.value
            pay_tx = PaymentTxn(from_address, params, to_address, value)
            payload["txScript"] = pay_tx

        return unsigned_tx.clone(
            inputs=[tx_input] if tx_input is not None else [],
            outputs=[tx_output] if tx_output is not None else [],
            flat_fee=params.fee,
            payload=payload,
        )

    def sign_transaction(self, unsigned_tx: UnsignedTx, signers: Dict[str, SignerInterface]) -> SignedTx:
        require(len(unsigned_tx.inputs) == 1 and len(unsigned_tx.outputs) == 1)
        from_address = unsigned_tx.inputs[0].address
        require(signers.get(from_address) is not None)
        require(unsigned_tx.payload.get("txScript") is not None)

        algo_key = _ALGKey(signers[from_address])

        txn = encoding.msgpack_encode(unsigned_tx.payload["txScript"])
        signature = algo_key.sign_msg_hash(constants.txid_prefix + base64.b64decode(txn))

        signature = base64.b64encode(signature).decode()
        stx = SignedTransaction(unsigned_tx.payload["txScript"], signature, None)

        return SignedTx(
            txid=stx.get_txid(),
            raw_tx=encoding.msgpack_encode(stx),
        )

    def get_token_info_by_address(self, token_address: str) -> Tuple[str, str, int]:
        raise NotImplementedError()

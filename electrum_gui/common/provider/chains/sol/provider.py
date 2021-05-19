import base64
from typing import Dict, Optional, Tuple, Union

import base58
import spl.token.instructions as spl_token
from solana import system_program
from solana.blockhash import Blockhash
from solana.publickey import PublicKey
from solana.rpc import types
from solana.transaction import SIG_LENGTH, SigPubkeyPair, Transaction
from spl.token.constants import TOKEN_PROGRAM_ID

from electrum_gui.common.basic.functional.require import require, require_not_none
from electrum_gui.common.basic.request.exceptions import RPCError
from electrum_gui.common.provider.data import AddressValidation, SignedTx, UnsignedTx
from electrum_gui.common.provider.interfaces import ProviderInterface
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


class SOLProvider(ProviderInterface):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid = False
        try:
            _ = PublicKey(address)
            is_valid = True
        except ValueError:
            pass
        return AddressValidation(is_valid=is_valid, encoding="b58", display_address=address, normalized_address=address)

    def pubkey_to_address(self, verifier: VerifierInterface, encoding: str = None) -> str:
        address = PublicKey(verifier.get_pubkey())
        return str(address)

    def fill_unsigned_tx(self, unsigned_tx: UnsignedTx) -> UnsignedTx:
        unsigned_tx.fee_limit = 5000
        unsigned_tx.fee_price_per_unit = 0
        unsigned_tx.nonce = 0
        return unsigned_tx

    def get_token_info_by_address(self, token_address: str) -> Tuple[str, str, int]:
        _ = PublicKey(token_address)
        response: types.RPCResponse = self.client.rpc.get_account_info(token_address, encoding="jsonParsed")
        if not response.get("error"):
            token_info = response.get("result")["value"]
            require_not_none(token_info, "invalid token address")
            account_info = token_info["data"]["parsed"]
            require(account_info["type"] == "mint", "invalid token_address")
            decimals = account_info["info"]["decimals"]
            return token_address[:4].upper(), token_address[:4], decimals
        else:
            raise RPCError(response["error"]["code"], response["error"]["message"])

    def sign_transaction(self, unsigned_tx: Union[UnsignedTx], signers: Dict[str, SignerInterface]) -> SignedTx:
        sender = unsigned_tx.inputs[0].address
        receiver = unsigned_tx.outputs[0].address
        amount = unsigned_tx.outputs[0].value
        token_address = unsigned_tx.outputs[0].token_address
        tx = self._build_tx(sender, receiver, amount, token_address)
        signature_pair = SigPubkeyPair(pubkey=PublicKey(unsigned_tx.inputs[0].address))
        tx.signatures.append(signature_pair)
        sign_data = tx.serialize_message()
        sig = signers[sender].sign(sign_data)[0]
        require(len(sig) == SIG_LENGTH, "signature has invalid length")
        signature_pair.signature = sig
        txid = base58.b58encode(sig).decode()
        raw_tx = base64.b64encode(tx.serialize()).decode()
        return SignedTx(txid=txid, raw_tx=raw_tx)

    def _build_tx(self, from_addr: str, to_addr: str, amount: int, mint_address: Optional[str] = None) -> Transaction:

        transfer_tx = Transaction()
        sender = PublicKey(from_addr)
        receiver = PublicKey(to_addr)
        if mint_address is None:
            # SOL transfer
            transfer_tx.add(
                system_program.transfer(
                    system_program.TransferParams(from_pubkey=sender, to_pubkey=receiver, lamports=amount)
                )
            )
        else:
            # SPL-Token transfer
            token_sender = spl_token.get_associated_token_address(
                spl_token.AssociatedTokenAccountParams(owner=sender, mint=PublicKey(mint_address))
            )
            # assume that receiver is system account. todo: maybe not good enough
            token_receiver = spl_token.get_associated_token_address(
                spl_token.AssociatedTokenAccountParams(owner=receiver, mint=PublicKey(mint_address))
            )
            res = self.client.rpc.get_account_info(token_receiver, encoding="jsonParsed")
            if res.get("error"):
                raise RPCError(res["error"]["code"], res["error"]["message"])
            account_info = res.get("result")["value"]
            # 账户未初始化
            if account_info is None:
                transfer_tx.add(
                    spl_token.create_associated_token_account(
                        spl_token.AssociatedTokenAccountParams(
                            owner=receiver, mint=PublicKey(mint_address), payer=sender
                        )
                    )
                )

            transfer_tx.add(
                spl_token.transfer(
                    spl_token.TransferParams(
                        program_id=TOKEN_PROGRAM_ID,
                        source=token_sender,
                        dest=token_receiver,
                        owner=sender,
                        amount=amount,
                        signers=[sender],
                    )
                )
            )
        # something like nonce
        recent_blockhash = self.client.rpc.get_recent_blockhash().get("result")["value"]["blockhash"]
        transfer_tx.recent_blockhash = Blockhash(recent_blockhash)
        return transfer_tx

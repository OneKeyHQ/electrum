import time
from typing import List, Optional

import solana.rpc.types as types
from solana.publickey import PublicKey
from solana.rpc.api import Client

from electrum_gui.common.basic.request.exceptions import RPCError
from electrum_gui.common.provider.data import (
    Address,
    BlockHeader,
    ClientInfo,
    EstimatedTimeOnPrice,
    PricesPerUnit,
    Transaction,
    TransactionStatus,
    TxBroadcastReceipt,
    TxBroadcastReceiptCode,
    TxPaginate,
)
from electrum_gui.common.provider.exceptions import TransactionNotFound
from electrum_gui.common.provider.interfaces import ClientInterface, SearchTransactionMixin


class Solana(ClientInterface, SearchTransactionMixin):
    def __init__(self, url: str):
        self.rpc = Client(url)

    def get_info(self) -> ClientInfo:
        return ClientInfo(name="sol", best_block_number=0, is_ready=self.rpc.is_connected())

    def get_address(self, address: str) -> Address:
        # validate address
        pubkey = PublicKey(address)
        balance = 0
        existing = False
        response: types.RPCResponse = self.rpc.get_account_info(pubkey, encoding="jsonParsed")
        if not response.get("error"):
            account_info = response.get('result')['value']
            if account_info is not None:
                balance = account_info["lamports"]
                existing = True
        else:
            raise RPCError(response['error']['code'], response['error']['message'])
        return Address(address=address, balance=balance, existing=existing)

    def get_transaction_by_txid(self, txid: str) -> Transaction:
        response: types.RPCResponse = self.rpc.get_confirmed_transaction(tx_sig=txid)
        if not response.get('error'):
            result = response.get('result')
            if result is None:
                raise TransactionNotFound(txid)
            # todo: show balance change
            return Transaction(
                txid=txid,
                fee=result["mate"]["fee"],
                status=result["meta"]["err"] is None,
                nonce=result["transaction"]["recentBlockhash"],
                block_header=BlockHeader(
                    block_time=result["blockTime"],
                    block_number=result["slot"],
                    block_hash="",
                ),
            )

        else:
            raise RPCError(response['error']['code'], response['error']['message'])

    def get_prices_per_unit_of_fee(self) -> PricesPerUnit:
        response: types.RPCResponse = self.rpc.get_fees()
        if not response.get("error"):
            lamports_per_sig = response.get("result")["value"]["feeCalculator"]["lamportsPerSignature"]
            fee = EstimatedTimeOnPrice(lamports_per_sig, int(time.time()))
        else:
            raise RPCError(response['error']['code'], response['error']['message'])
        return PricesPerUnit(normal=fee, fast=fee, slow=fee)

    def get_balance(self, address: str, token_address: Optional[str] = None) -> int:
        """
        :param address:
        :param token_address: spl-token mint address
        :return: the balance in format (base unit as int, normal unit as str)
        """
        address = self.get_address(address)
        if token_address is not None:
            opts = types.TokenAccountOpts(mint=PublicKey(token_address))
            owner = address.address
            response: types.RPCResponse = self.rpc.get_token_accounts_by_owner(PublicKey(owner), opts=opts)
            balance_int = 0
            # balance_float = 0.0
            if response.get("error"):
                raise RPCError(response["error"]["code"], response["error"]["message"])
            for token_account in response.get('result')['value']:
                info = token_account['account']['data']['parsed']['info']
                assert info['owner'] == owner, "incorrect owner"
                balance_int += int(info['tokenAmount']['amount'])
                # balance_float += float(info['tokenAmount']['uiAmountString'])
            return balance_int
        else:
            return address.balance

    def broadcast_transaction(self, raw_tx: str) -> TxBroadcastReceipt:
        res: types.RPCResponse = self.rpc.send_raw_transaction(raw_tx)
        signature = res.get("result")
        # response: types.RPCResponse = self.rpc.get_signature_statuses(signature)
        # status = response["result"]["value"]["confirmations"]
        # if not response.get("error"):
        return TxBroadcastReceipt(
            is_success=True,
            receipt_code=TxBroadcastReceiptCode.SUCCESS,
            # if status is None or status > 0 else TxBroadcastReceiptCode.UNKNOWN,
            txid=signature,
        )
        # else:
        #     raise RPCError(response["error"]["code"], response["error"]["message"])

    def search_txs_by_address(self, address: str, paginate: Optional[TxPaginate] = None) -> List[Transaction]:
        if paginate:
            # todo:
            pass
        response = self.rpc.get_confirmed_signature_for_address2(address)
        if not response.get("error"):
            txs = []
            for transaction in response.get("result"):
                tx = Transaction(
                    txid=transaction["signature"],
                    status=TransactionStatus.CONFIRM_SUCCESS
                    if transaction["confirmationStatus"] == "finalized"
                    else TransactionStatus.PENDING,
                    block_header=BlockHeader(
                        block_number=transaction["slot"],
                        block_time=transaction["blockTime"],
                        block_hash="",
                    ),
                )
                txs.append(tx)
            return txs
        else:
            raise RPCError(response["error"]["code"], response["error"]["message"])

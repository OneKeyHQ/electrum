import json
from decimal import Decimal
from typing import List

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.functional.text import force_text
from electrum_gui.common.basic.request.exceptions import ResponseException
from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.provider.data import (
    Address,
    BlockHeader,
    EstimatedTimeOnPrice,
    PricePerUnit,
    ProviderInfo,
    Token,
    Transaction,
    TransactionFee,
    TransactionInput,
    TransactionOutput,
    TransactionStatus,
    TxBroadcastReceipt,
    TxBroadcastReceiptCode,
)
from electrum_gui.common.provider.exceptions import TransactionNotFound
from electrum_gui.common.provider.interfaces import ProviderInterface


class BlockBook(ProviderInterface):
    __raw_tx_status_mapping__ = {
        -1: TransactionStatus.IN_MEMPOOL,
        0: TransactionStatus.REVERED,
        1: TransactionStatus.CONFIRMED,
    }

    def __init__(self, url: str):
        self.restful = RestfulRequest(url)

    def get_info(self) -> ProviderInfo:
        resp = self.restful.get("/api")

        return ProviderInfo(
            name="blockbook",
            best_block_number=int(resp["blockbook"].get("bestHeight", 0)),
            is_ready=resp["blockbook"].get("inSync") is True,
            desc=resp["blockbook"].get("about"),
        )

    def get_address(self, address: str) -> Address:
        resp = self._get_raw_address_info(address, details="basic")

        return Address(
            address=address,
            balance=int(resp["balance"]),
            nonce=int(resp["nonce"]),
            existing=True,
        )

    def _get_raw_address_info(self, address: str, details: str, **kwargs) -> dict:
        resp = self.restful.get(f"/api/v2/address/{address}", params=dict(details=details, **kwargs))
        require(resp["address"].lower() == address.lower())
        return resp

    def get_balance(self, address: str, token: Token = None) -> int:
        if not token:
            return super(BlockBook, self).get_balance(address)
        else:
            resp = self._get_raw_address_info(address, details="tokenBalances")
            tokens = {
                token_dict["contract"].lower(): token_dict["balance"]
                for token_dict in (resp.get("tokens") or ())
                if token_dict.get("contract") and token_dict.get("balance")
            }
            balance = tokens.get(token.contract.lower(), 0)
            return int(balance)

    def get_transaction_by_txid(self, txid: str) -> Transaction:
        try:
            resp = self.restful.get(f"/api/v2/tx/{txid}")
            return self._populate_transaction(resp)
        except ResponseException as e:
            if e.response is not None and "not found" in force_text(e.response.text):
                raise TransactionNotFound(txid)
            else:
                raise e

    def _populate_transaction(self, raw_tx: dict) -> Transaction:
        ethereum_data = raw_tx.get("ethereumSpecific") or {}
        token_transfers = raw_tx.get("tokenTransfers") or []

        block_header = (
            BlockHeader(
                block_hash=raw_tx["blockHash"],
                block_number=raw_tx["blockHeight"],
                block_time=raw_tx["blockTime"],
                confirmations=raw_tx["confirmations"],
            )
            if raw_tx.get("blockHash")
            else None
        )

        gas_limit = ethereum_data.get("gasLimit", 0)
        fee = TransactionFee(
            limit=int(gas_limit),
            usage=int(ethereum_data.get("gasUsed") or gas_limit),
            price_per_unit=int(ethereum_data.get("gasPrice", 1)),
        )
        sender = raw_tx["vin"][0]["addresses"][0]
        receiver = raw_tx["vout"][0]["addresses"][0]
        value = int(raw_tx["vout"][0]["value"])

        return Transaction(
            txid=raw_tx["txid"],
            inputs=[
                TransactionInput(address=sender, value=value),
                *(
                    TransactionInput(address=i.get("from"), value=int(i.get("value") or 0), token_address=i["token"])
                    for i in token_transfers
                    if i.get("token")
                ),
            ],
            outputs=[
                TransactionOutput(address=receiver, value=value),
                *(
                    TransactionOutput(address=i.get("to"), value=int(i.get("value") or 0), token_address=i["token"])
                    for i in token_transfers
                    if i.get("token")
                ),
            ],
            status=self.__raw_tx_status_mapping__.get(ethereum_data.get("status")) or TransactionStatus.UNKNOWN,
            block_header=block_header,
            fee=fee,
            raw_tx=json.dumps(raw_tx),
        )

    def search_txs_by_address(self, address: str) -> List[Transaction]:
        resp = self._get_raw_address_info(address, details="txs")
        txs = [self._populate_transaction(i) for i in resp.get("transactions", ())]

        return txs

    def search_txids_by_address(self, address: str) -> List[str]:
        resp = self._get_raw_address_info(address, details="txids")
        txids = [i for i in resp.get("txids", ())]

        return txids

    def broadcast_transaction(self, raw_tx: str) -> TxBroadcastReceipt:
        if not raw_tx.startswith("0x"):
            raw_tx += "0x"

        try:
            resp = self.restful.get(f"/api/v2/sendtx/{raw_tx}")
        except ResponseException as e:
            try:
                resp = e.response.json()
            except ValueError:
                resp = dict()

        txid = resp.get("result")
        if txid:
            return TxBroadcastReceipt(is_success=True, receipt_code=TxBroadcastReceiptCode.SUCCESS, txid=txid)

        error_message = resp.get("error", "")
        if "already known" in error_message:
            return TxBroadcastReceipt(
                is_success=True,
                receipt_code=TxBroadcastReceiptCode.ALREADY_KNOWN,
            )
        elif "nonce too low" in error_message:
            return TxBroadcastReceipt(
                is_success=False,
                receipt_code=TxBroadcastReceiptCode.NONCE_TOO_LOW,
                receipt_message=error_message,
            )
        else:
            return TxBroadcastReceipt(
                is_success=False, receipt_code=TxBroadcastReceiptCode.UNEXPECTED_FAILED, receipt_message=error_message
            )

    def get_price_per_unit_of_fee(self) -> PricePerUnit:
        num_of_block = 10  # just a number, trezor does case what it is
        resp = self.restful.get(f"/api/v2/estimatefee/{num_of_block}")

        slow = Decimal(resp["result"])
        normal = slow * Decimal(1.25)
        fast = slow * Decimal(1.5)

        to_wei_multiple = Decimal(1e18)
        min_wei = 1e9
        normal = int(max(normal * to_wei_multiple, min_wei))
        slow = int(max(slow * to_wei_multiple, min_wei))
        fast = int(max(fast * to_wei_multiple, min_wei))

        return PricePerUnit(
            fast=EstimatedTimeOnPrice(price=fast, time=60),
            normal=EstimatedTimeOnPrice(price=normal, time=180),
            slow=EstimatedTimeOnPrice(price=slow, time=600),
        )

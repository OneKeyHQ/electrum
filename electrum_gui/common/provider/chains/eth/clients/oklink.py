import json
import time
from typing import List, Optional

from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.provider.chains.eth import Geth
from electrum_gui.common.provider.data import (
    BlockHeader,
    ClientInfo,
    Transaction,
    TransactionFee,
    TransactionInput,
    TransactionOutput,
    TransactionStatus,
    TxPaginate,
)
from electrum_gui.common.provider.interfaces import SearchTransactionMixin


class OKLink(Geth, SearchTransactionMixin):
    # __symbol = "OKEXCHAIN"
    # __symbol = "OKEXCHAIN_TEST"

    def __init__(self, oklink_url: str, geth_url: str, net_symbol: str, api_keys: List[str] = None):
        super().__init__(geth_url)
        self.restful = RestfulRequest(oklink_url)
        self.api_key = api_keys[0] if api_keys else None
        self.net_symbol = net_symbol

    def get_info(self) -> ClientInfo:
        resp = self.restful.get("/api/explorer/v1/info/summary", headers={"x-apiKey": self.api_key})

        is_ready = False
        best_block_number = 0
        if isinstance(resp, dict):
            for item in resp["data"]:
                if item["symbol"] != self.net_symbol:
                    continue
                last_block_time = item["lastBlockTimeU0"]
                is_ready = time.time() - last_block_time / 1e3 < 120
                best_block_number = item["height"]
                break

        return ClientInfo(
            name="OKLink",
            best_block_number=best_block_number,
            is_ready=is_ready,
        )

    def search_txs_by_address(self, address: str, paginate: Optional[TxPaginate] = None) -> List[Transaction]:
        resp = self.restful.get(
            f"/api/explorer/v1/{self.net_symbol}/addresses/{address.lower()}/transactions",
            headers={"x-apiKey": self.api_key},
            **self._paging(paginate),
        )
        raw_txs = resp["data"]["hits"]

        txs = []
        for raw_tx in raw_txs:
            block_header = BlockHeader(
                block_hash=raw_tx["blockHash"],
                block_number=raw_tx["blockHeight"],
                block_time=raw_tx["blockTimeU0"] / 1e3,
            )

            status = TransactionStatus.PENDING
            receipt_status = raw_tx.get("status")
            if receipt_status == "FAILED":
                status = TransactionStatus.CONFIRM_REVERTED
            elif receipt_status == "SUCCESS" and raw_tx.get("confirm") > 0:
                status = TransactionStatus.CONFIRM_SUCCESS

            gas_limit = raw_tx["gasLimit"]
            gas_used = raw_tx.get("gasUsed") or gas_limit
            fee = TransactionFee(limit=gas_limit, used=gas_used, price_per_unit=raw_tx.get("gasPrice"))
            sender = raw_tx.get("from", "")[0]
            receiver, value = "", 0
            receivers = raw_tx.get("to", "")
            for item in receivers:
                if item.get("address", "") == address.lower():
                    receiver = address.lower()
                    value = raw_tx.get("value", 0)

            tx = Transaction(
                txid=raw_tx["hash"],
                block_header=block_header,
                inputs=[TransactionInput(address=sender, value=value)],
                outputs=[TransactionOutput(address=receiver, value=value)],
                status=status,
                fee=fee,
                raw_tx=json.dumps(raw_tx),
                nonce=int(raw_tx.get("nonce", 0)),
            )
            txs.append(tx)

        return txs

    @staticmethod
    def _paging(paginate: Optional[TxPaginate]) -> dict:
        payload = {}
        if paginate is None:
            return payload

        if paginate.page_number is not None:
            payload["offset"] = paginate.page_number * (paginate.items_per_page - 1)

        if paginate.items_per_page is not None:
            payload["limit"] = paginate.items_per_page

        return payload

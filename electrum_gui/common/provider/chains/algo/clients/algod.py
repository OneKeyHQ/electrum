from typing import List, Optional

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.functional.text import force_text
from electrum_gui.common.basic.request.exceptions import RequestException, ResponseException
from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.provider.chains.eth.clients import utils
from electrum_gui.common.provider.data import (
    Address,
    BlockHeader,
    ClientInfo,
    EstimatedTimeOnPrice,
    PricesPerUnit,
    Transaction,
    TransactionFee,
    TransactionInput,
    TransactionOutput,
    TransactionStatus,
    TxBroadcastReceipt,
    TxBroadcastReceiptCode,
    TxPaginate,
)
from electrum_gui.common.provider.exceptions import FailedToGetGasPrices, TransactionNotFound
from electrum_gui.common.provider.interfaces import ClientInterface, SearchTransactionMixin


class Algod(ClientInterface, SearchTransactionMixin):
    __raw_tx_status_mapping__ = {
        -1: TransactionStatus.PENDING,
        0: TransactionStatus.CONFIRM_REVERTED,
        1: TransactionStatus.CONFIRM_SUCCESS,
    }

    def __init__(self, url: str, api_keys: List[str] = None):
        self.restful = RestfulRequest(url, timeout=10, headers={"x-api-key": api_keys[0] if api_keys else None})

    def get_info(self) -> ClientInfo:
        resp = self.restful.get("/ps2/v2/status")

        is_ready = resp.get("catchup-time", 1) == 0
        if is_ready:
            time_since_last_round = resp.get("time-since-last-round")
            is_ready = time_since_last_round < 1e9 * 60

        return ClientInfo(
            name="algod",
            best_block_number=int(resp.get("last-round", 0)),
            is_ready=is_ready,
            desc="",
        )

    def get_address(self, address: str) -> Address:
        resp = self._get_raw_address_info(address)

        return Address(
            address=address,
            balance=int(resp["amount"]),
            existing=bool(resp["amount"]) or bool(resp["assets"]),
        )

    def _get_raw_address_info(self, address: str, **kwargs) -> dict:
        resp = self.restful.get(f"/ps2/v2/accounts/{address}", params=dict(**kwargs))
        require(resp["address"] == address)
        return resp

    def get_balance(self, address: str, token_address: Optional[str] = None) -> int:
        if token_address is None:
            return super(Algod, self).get_balance(address)
        else:
            resp = self._get_raw_address_info(address)
            tokens = {
                str(token_dict["asset-id"]): token_dict["amount"]
                for token_dict in (resp.get("assets") or ())
                if token_dict.get("asset-id") and token_dict.get("amount")
            }
            balance = tokens.get(token_address, 0)
            return int(balance)

    def get_transaction_by_txid(self, txid: str) -> Transaction:
        try:
            # todo handle pending tx
            # pending = self.restful.get(f"/ps2/v2/accounts/{txid}")
            resp = self.restful.get(f"/idx2/v2/transactions/{txid}")
        except ResponseException as e:
            if e.response is not None and "no transaction found" in force_text(e.response.text):
                raise TransactionNotFound(txid)
            else:
                raise e

        tx = resp.get("transaction", {})
        require(txid == tx.get("id"))

        block_header = BlockHeader(
            block_hash=resp["transaction"].get("blockHash", ""),
            block_number=resp["transaction"].get("confirmed-round", 0),
            block_time=0,
        )
        status = TransactionStatus.CONFIRM_SUCCESS

        fee = TransactionFee(
            limit=1,
            used=tx.get("fee"),
            price_per_unit=tx.get("fee"),
        )
        sender = tx.get("sender", "")
        receiver = tx["payment-transaction"].get("receiver", "")
        value = tx["payment-transaction"].get("amount", 0)

        return Transaction(
            txid=txid,
            inputs=[TransactionInput(address=sender, value=value)],
            outputs=[TransactionOutput(address=receiver, value=value)],
            status=status,
            block_header=block_header,
            fee=fee,
            nonce=0,
        )

    def search_txs_by_address(
        self,
        address: str,
        paginate: Optional[TxPaginate] = None,
    ) -> List[Transaction]:
        # resp = self.restful.get(f"/idx2/v2/accounts/{address}/transactions", params=dict(**self._paging(paginate)))
        txs = [Transaction]
        return txs

    @staticmethod
    def _paging(paginate: Optional[TxPaginate]) -> dict:
        payload = {}
        if paginate is None:
            return payload

        if paginate.cursor is not None:
            payload["next"] = paginate.cursor

        if paginate.items_per_page is not None:
            payload["limit"] = paginate.items_per_page

        return payload

    def broadcast_transaction(self, raw_tx: str) -> TxBroadcastReceipt:
        try:
            resp = self.restful.post("/ps2/v2/transaction", data=raw_tx)
        except ResponseException as e:
            try:
                resp = e.response.json()
            except ValueError:
                resp = dict()

        txid = resp.get("txId")
        if txid:
            return TxBroadcastReceipt(is_success=True, receipt_code=TxBroadcastReceiptCode.SUCCESS, txid=txid)
        else:
            return utils.handle_broadcast_error(resp.get("error") or "")

    def get_prices_per_unit_of_fee(self) -> PricesPerUnit:
        try:
            resp = self.restful.get("/ps2/v2/transactions/params")
        except RequestException:
            raise FailedToGetGasPrices()
        min_fee = resp.get("min-fee", 1000)

        return PricesPerUnit(
            fast=EstimatedTimeOnPrice(price=min_fee, time=60),
            normal=EstimatedTimeOnPrice(price=min_fee, time=180),
            slow=EstimatedTimeOnPrice(price=min_fee, time=600),
        )

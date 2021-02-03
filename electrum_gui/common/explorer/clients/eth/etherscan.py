import json
from typing import List

from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.explorer.data.enums import TransactionStatus, TxBroadcastReceiptCode
from electrum_gui.common.explorer.data.interfaces import ExplorerInterface
from electrum_gui.common.explorer.data.objects import (
    Address,
    BlockHeader,
    EstimatedTimeOnPrice,
    ExplorerInfo,
    PricePerUnit,
    Token,
    Transaction,
    TransactionFee,
    TxBroadcastReceipt,
)


class Etherscan(ExplorerInterface):
    def __init__(self, base_url: str, api_key: str = None):
        self.restful = RestfulRequest(base_url)
        self.api_key = api_key

    def _call_action(self, module: str, action: str, **kwargs) -> dict:
        kwargs = kwargs if kwargs is not None else dict()
        kwargs.update(dict(module=module, action=action))
        return self._call_with_api_key("/api", data=kwargs)

    def _call_with_api_key(self, path: str, data: dict) -> dict:
        if self.api_key:
            data["apikey"] = self.api_key

        resp = self.restful.post(path, data=data)
        return resp

    def get_explorer_info(self) -> ExplorerInfo:
        resp = self._call_action("proxy", "eth_blockNumber")
        return ExplorerInfo(
            name="etherscan",
            best_block_number=int(resp["result"], base=16),
            is_ready=True,
        )

    def get_address(self, address: str) -> Address:
        resp = self._call_action("account", "balance", address=address, tag="latest")
        balance = int(resp["result"])

        resp = self._call_action("proxy", "eth_getTransactionCount", address=address)
        nonce = int(resp["result"], base=16)
        return Address(address=address, balance=balance, nonce=nonce)

    def get_balance(self, address: str, token: Token = None) -> int:
        if not token:
            return super(Etherscan, self).get_balance(address)
        else:
            resp = self._call_action("account", "tokenbalance", address=address, contractaddress=token.contract)
            return int(resp.get("result", 0))

    def get_transaction_by_txid(self, txid: str) -> Transaction:
        resp = self._call_action("proxy", "eth_getTransactionByHash", txhash=txid)
        raw_tx = resp["result"]
        block_header = (
            BlockHeader(
                block_hash=raw_tx["blockHash"],
                block_number=int(raw_tx["blockNumber"], base=16),
                block_time=0,
            )
            if raw_tx.get("blockHash")
            else None
        )

        if block_header:
            resp = self._call_action("proxy", "eth_getTransactionReceipt", txhash=txid)
            receipt = resp["result"]
        else:
            receipt = None

        status = TransactionStatus.IN_MEMPOOL
        receipt_status = receipt["status"] if receipt else None
        if receipt_status == "0x0":
            status = TransactionStatus.REVERED
        elif receipt_status == "0x1":
            status = TransactionStatus.CONFIRMED

        gas_limit = int(raw_tx["gas"], base=16)
        gas_usage = int(receipt["gasUsed"], 0) if receipt else None
        gas_usage = gas_usage or gas_limit
        fee = TransactionFee(
            limit=gas_limit,
            usage=gas_usage,
            price_per_unit=int(raw_tx["gasPrice"], base=16),
        )

        return Transaction(
            txid=raw_tx["hash"],
            block_header=block_header,
            source=raw_tx["from"],
            target=raw_tx["to"],
            value=int(raw_tx["value"], base=16),
            status=status,
            fee=fee,
            raw_tx=json.dumps(raw_tx),
        )

    def search_txs_by_address(self, address: str) -> List[Transaction]:
        resp = self._call_action("account", "txlist", address=address, sort="desc")
        raw_txs = resp["result"]

        txs = []
        for raw_tx in raw_txs:
            block_header = BlockHeader(
                block_hash=raw_tx["blockHash"],
                block_number=int(raw_tx["blockNumber"]),
                block_time=int(raw_tx["timeStamp"]),
            )

            status = TransactionStatus.IN_MEMPOOL
            receipt_status = raw_tx.get("txreceipt_status")
            if receipt_status == "0":
                status = TransactionStatus.REVERED
            elif receipt_status == "1":
                status = TransactionStatus.CONFIRMED

            gas_limit = int(raw_tx["gas"])
            gas_usage = int(raw_tx.get("gasUsed")) or gas_limit
            fee = TransactionFee(limit=gas_limit, usage=gas_usage, price_per_unit=int(raw_tx["gasPrice"]))

            tx = Transaction(
                txid=raw_tx["hash"],
                block_header=block_header,
                source=raw_tx["from"],
                target=raw_tx["to"],
                value=int(raw_tx["value"]),
                status=status,
                fee=fee,
                raw_tx=json.dumps(raw_tx),
            )
            txs.append(tx)

        return txs

    def broadcast_transaction(self, raw_tx: str) -> TxBroadcastReceipt:
        if not raw_tx.startswith("0x"):
            raw_tx += "0x"

        resp = self._call_action("proxy", "eth_sendRawTransaction", hex=raw_tx)

        txid = resp.get("result")
        if txid:
            return TxBroadcastReceipt(is_success=True, receipt_code=TxBroadcastReceiptCode.SUCCESS, txid=txid)

        error_message = resp["error"].get("message", "") if resp.get("error") else ""
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
        resp = self._call_action("gastracker", "gasoracle")
        result = resp.get("result", dict())

        slow = int(result["SafeGasPrice"] * 1e9)
        normal = int(result["ProposeGasPrice"] * 1e9)
        fast = int(result["FastGasPrice"] * 1e9)

        return PricePerUnit(
            fast=EstimatedTimeOnPrice(price=fast, time=60),
            normal=EstimatedTimeOnPrice(price=normal, time=180),
            slow=EstimatedTimeOnPrice(price=slow, time=600),
        )

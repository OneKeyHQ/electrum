from functools import partial
from typing import Any

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.request.exceptions import JsonRPCException
from electrum_gui.common.basic.request.json_rpc import JsonRPCRequest
from electrum_gui.common.provider.chains.eth.clients import utils
from electrum_gui.common.provider.data import (
    Address,
    BlockHeader,
    ClientInfo,
    EstimatedTimeOnPrice,
    PricePerUnit,
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
from electrum_gui.common.provider.interfaces import ClientInterface

_hex2int = partial(int, base=16)


class Geth(ClientInterface):
    __LAST_BLOCK__ = "latest"

    def __init__(self, url: str):
        self.rpc = JsonRPCRequest(url)

    def get_info(self) -> ClientInfo:
        block_number = self.rpc.call("eth_blockNumber", params=[])
        return ClientInfo(
            "geth",
            best_block_number=_hex2int(block_number),
            is_ready=True,
        )

    def get_address(self, address: str) -> Address:
        _balance, _nonce = self.rpc.batch_call(
            [
                ("eth_getBalance", [address, self.__LAST_BLOCK__]),
                ("eth_getTransactionCount", [address, self.__LAST_BLOCK__]),
            ]
        )  # Maybe __LAST_BLOCK__ refers to a different blocks in some case
        balance = _hex2int(_balance)
        nonce = _hex2int(_nonce)
        return Address(address=address, balance=balance, nonce=nonce, existing=(bool(balance) or bool(nonce)))

    def get_balance(self, address: str, token: Token = None) -> int:
        if not token:
            return super(Geth, self).get_balance(address)
        else:
            call_balance_of = (
                "0x70a08231000000000000000000000000" + address[2:]
            )  # method_selector(balance_of) + byte32_pad(address)
            resp = self.eth_call({"to": token.contract, "data": call_balance_of})

            try:
                return _hex2int(resp[:66])
            except ValueError:
                return 0

    def eth_call(self, call_data: dict) -> Any:
        return self.rpc.call("eth_call", [call_data, self.__LAST_BLOCK__])

    def get_transaction_by_txid(self, txid: str) -> Transaction:
        tx, receipt = self.rpc.batch_call(
            [
                ("eth_getTransactionByHash", [txid]),
                ("eth_getTransactionReceipt", [txid]),
            ]
        )
        if not tx:
            raise TransactionNotFound(txid)
        else:
            require(txid == tx.get("hash"))

        if receipt:
            block_header = BlockHeader(
                block_hash=receipt.get("blockHash", ""),
                block_number=_hex2int(receipt.get("blockNumber", "0x0")),
                block_time=0,
            )
            status = (
                TransactionStatus.CONFIRM_SUCCESS
                if receipt.get("status") == "0x1"
                else TransactionStatus.CONFIRM_REVERTED
            )
            gas_used = _hex2int(receipt.get("gasUsed", "0x0"))
        else:
            block_header = None
            status = TransactionStatus.PENDING
            gas_used = None

        gas_limit = _hex2int(tx.get("gas", "0x0"))
        fee = TransactionFee(
            limit=gas_limit,
            used=gas_used or gas_limit,
            price_per_unit=_hex2int(tx.get("gasPrice", "0x0")),
        )
        sender = tx.get("from", "").lower()
        receiver = tx.get("to", "").lower()
        value = _hex2int(tx.get("value", "0x0"))

        return Transaction(
            txid=txid,
            inputs=[TransactionInput(address=sender, value=value)],
            outputs=[TransactionOutput(address=receiver, value=value)],
            status=status,
            block_header=block_header,
            fee=fee,
            nonce=_hex2int(tx["nonce"]),
        )

    def broadcast_transaction(self, raw_tx: str) -> TxBroadcastReceipt:
        try:
            txid = self.rpc.call("eth_sendRawTransaction", params=[raw_tx])
            return TxBroadcastReceipt(
                txid=txid,
                is_success=True,
                receipt_code=TxBroadcastReceiptCode.SUCCESS,
            )
        except JsonRPCException as e:
            json_response = e.json_response
            if isinstance(json_response, dict) and "error" in json_response:
                error_message = json_response.get("error", {}).get("message") or ""
                return utils.populate_error_broadcast_receipt(error_message)

            raise e

    def get_price_per_unit_of_fee(self) -> PricePerUnit:
        resp = self.rpc.call("eth_gasPrice", params=[])

        min_wei = int(1e9)
        slow = int(max(_hex2int(resp), min_wei))
        normal = int(max(slow * 1.25, min_wei))
        fast = int(max(slow * 1.5, min_wei))

        return PricePerUnit(
            fast=EstimatedTimeOnPrice(price=fast, time=60),
            normal=EstimatedTimeOnPrice(price=normal, time=180),
            slow=EstimatedTimeOnPrice(price=slow, time=600),
        )

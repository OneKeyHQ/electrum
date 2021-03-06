import functools
import time
from typing import Any, List, Optional, Union

import eth_utils

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.request.exceptions import JsonRPCException
from electrum_gui.common.basic.request.json_rpc import JsonRPCRequest
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
)
from electrum_gui.common.provider.exceptions import FailedToGetGasPrices, TransactionNotFound
from electrum_gui.common.provider.interfaces import BatchGetAddressMixin, ClientInterface

_hex2int = functools.partial(int, base=16)


def _extract_eth_call_str_result(data: bytes) -> str:
    payload_offset = int.from_bytes(data[:32], "big")
    payload = data[payload_offset:]
    str_length = int.from_bytes(payload[:32], "big")
    str_result = payload[32 : 32 + str_length].decode()
    return str_result


class InvalidContractAddress(ValueError):
    # TODO: organize exceptions better
    def __init__(self, address):
        super(InvalidContractAddress, self).__init__(f"Invalid contract address {address}.")


class Geth(ClientInterface, BatchGetAddressMixin):
    __LAST_BLOCK__ = "latest"

    def __init__(self, url: str):
        self.rpc = JsonRPCRequest(url)

    def get_info(self) -> ClientInfo:
        the_latest_block = self.rpc.call("eth_getBlockByNumber", params=["latest", False])
        return ClientInfo(
            "geth",
            best_block_number=_hex2int(the_latest_block["number"]),
            is_ready=time.time() - _hex2int(the_latest_block["timestamp"]) < 120,
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

    def batch_get_address(self, addresses: List[str]) -> List[Address]:
        _call_body = []
        for address in addresses:
            _call_body.extend(
                [
                    # Maybe __LAST_BLOCK__ refers to a different blocks in some case
                    ("eth_getBalance", [address, self.__LAST_BLOCK__]),
                    ("eth_getTransactionCount", [address, self.__LAST_BLOCK__]),
                ]
            )
        result = self.rpc.batch_call(_call_body, timeout=10)

        _resp_body = []
        result_iterator = iter(result)
        for _address, _balance_str, _nonce_str in zip(addresses, result_iterator, result_iterator):
            _balance = _hex2int(_balance_str)
            _nonce = _hex2int(_nonce_str)
            _resp_body.append(
                Address(address=_address, balance=_balance, nonce=_nonce, existing=(bool(_balance) or bool(_nonce)))
            )
        return _resp_body

    def get_balance(self, address: str, token_address: Optional[str] = None) -> int:
        if token_address is None:
            return super(Geth, self).get_balance(address)
        else:
            call_balance_of = (
                "0x70a08231000000000000000000000000" + address[2:]
            )  # method_selector(balance_of) + byte32_pad(address)
            resp = self.eth_call({"to": token_address, "data": call_balance_of})

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
                return utils.handle_broadcast_error(error_message)

            raise e

    def get_prices_per_unit_of_fee(self) -> PricesPerUnit:
        try:
            resp = self.rpc.call("eth_gasPrice", params=[])
        except JsonRPCException:
            raise FailedToGetGasPrices()

        min_wei = int(1e9)
        slow = int(max(_hex2int(resp), min_wei))
        normal = int(max(slow * 1.25, min_wei))
        fast = int(max(slow * 1.5, min_wei))

        return PricesPerUnit(
            normal=EstimatedTimeOnPrice(price=normal, time=180),
            others=[
                EstimatedTimeOnPrice(price=slow, time=600),
                EstimatedTimeOnPrice(price=fast, time=60),
            ],
        )

    def estimate_gas_limit(self, from_address: str, to_address: str, value: int, data: str = None) -> int:
        resp = self.rpc.call(
            "eth_estimateGas",
            params=[{"from": from_address, "to": to_address, "value": hex(value), "data": data or "0x"}],
        )
        return _hex2int(resp)

    def get_contract_code(self, address: str) -> str:
        resp = self.rpc.call("eth_getCode", params=[address, self.__LAST_BLOCK__])
        return eth_utils.remove_0x_prefix(resp)

    @functools.lru_cache
    def is_contract(self, address: str) -> bool:
        return len(self.get_contract_code(address)) > 0

    def get_token_info_by_address(self, token_address: str) -> str:
        # >>> eth_utils.keccak("symbol()".encode())[:4].hex()
        # '95d89b41'
        # >>> eth_utils.keccak("name()".encode())[:4].hex()
        # '06fdde03'
        # >>> eth_utils.keccak("decimals()".encode())[:4].hex()
        # '313ce567'
        symbol_resp, name_resp, decimals_resp = self.call_contract(
            token_address, ["0x95d89b41", "0x06fdde03", "0x313ce567"]
        )
        return (
            _extract_eth_call_str_result(bytes.fromhex(symbol_resp[2:])),
            _extract_eth_call_str_result(bytes.fromhex(name_resp[2:])),
            _hex2int(decimals_resp),
        )

    def call_contract(self, contract_address: str, data: Union[str, List[str]]) -> Union[str, List[str]]:
        if not self.is_contract(contract_address):
            raise InvalidContractAddress(contract_address)

        if isinstance(data, list):
            return self.rpc.batch_call(
                [
                    ("eth_call", [{"to": contract_address, "data": call_data}, self.__LAST_BLOCK__])
                    for call_data in data
                ],
                ignore_errors=True,
            )
        else:
            return self.eth_call({"to": contract_address, "data": data})

import json
from typing import List

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.basic.functional.text import force_text
from electrum_gui.common.basic.request.exceptions import ResponseException
from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.explorer.data.enums import TransactionStatus
from electrum_gui.common.explorer.data.objects import (
    Transaction,
    Address,
    BlockHeader,
    AddressBalance,
    TransactionFee,
    ExplorerInfo,
)
from electrum_gui.common.explorer.data.exceptions import (
    TransactionNotFound,
)
from electrum_gui.common.explorer.data.interfaces import (
    ExplorerInterface,
)


class TrezorETH(ExplorerInterface):
    __raw_tx_status_mapping__ = {
        -1: TransactionStatus.IN_MEMPOOL,
        0: TransactionStatus.REVERED,
        1: TransactionStatus.CONFIRMED,
    }

    def __init__(self, base_url: str):
        self.restful = RestfulRequest(base_url)

    def get_explorer_info(self) -> ExplorerInfo:
        resp = self.restful.get("/api")
        require(resp["blockbook"]["coin"] == "Ethereum")

        return ExplorerInfo(
            name="trezor",
            best_block_number=int(resp["blockbook"].get("bestHeight", 0)),
            is_ready=resp["blockbook"].get("inSync") is True,
            desc=resp["blockbook"].get("about"),
        )

    def get_address(self, address: str) -> Address:
        resp = self.restful.get(
            f"/api/v2/address/{address}", params=dict(details="basic")
        )
        require(resp["address"].lower() == address.lower())

        return Address(
            address=address,
            balance=AddressBalance(
                available=int(resp["balance"]),
                pending=int(resp["unconfirmedBalance"]),
            ),
            nonce=int(resp["nonce"]),
            existing=True,
        )

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
        ethereum_data = raw_tx["ethereumSpecific"]

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

        fee = TransactionFee(
            limit=int(ethereum_data.get("gasLimit", 0)),
            usage=int(ethereum_data.get("gasUsed", ethereum_data.get("gasLimit", 0))),
            price_per_unit=int(ethereum_data.get("gasPrice", 1)),
        )

        return Transaction(
            txid=raw_tx["txid"],
            source=raw_tx["vin"][0]["addresses"][0],
            target=raw_tx["vout"][0]["addresses"][0],
            value=int(raw_tx["vout"][0]["value"]),
            status=self.__raw_tx_status_mapping__.get(
                ethereum_data.get("status"), TransactionStatus.UNKNOWN
            ),
            block_header=block_header,
            fee=fee,
            raw_tx=json.dumps(raw_tx),
        )

    def search_txs_by_address(self, address: str) -> List[Transaction]:
        resp = self.restful.get(
            f"/api/v2/address/{address}", params=dict(details="txs")
        )
        require(resp["address"].lower() == address.lower())

        txs = [self._populate_transaction(i) for i in resp.get("transactions", ())]
        return txs

    def search_txids_by_address(self, address: str) -> List[str]:
        resp = self.restful.get(
            f"/api/v2/address/{address}", params=dict(details="txids")
        )
        require(resp["address"].lower() == address.lower())

        txids = [i for i in resp.get("txids", ())]
        return txids

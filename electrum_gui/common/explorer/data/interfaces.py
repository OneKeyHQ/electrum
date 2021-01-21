from abc import ABC, abstractmethod
from typing import List

from electrum_gui.common.explorer.data.enums import TransactionStatus
from electrum_gui.common.explorer.data.objects import (
    Address,
    Transaction,
    ExplorerInfo,
)
from electrum_gui.common.explorer.data.exceptions import (
    TransactionNotFound,
)


class ExplorerInterface(ABC):
    @abstractmethod
    def get_explorer_info(self) -> ExplorerInfo:
        """
        Get explorer information
        :return: ChainInfo
        """

    @abstractmethod
    def get_address(self, address: str) -> Address:
        """
        Get address information by address str
        todo token?
        :param address: address
        :return: Address
        """

    @abstractmethod
    def get_transaction_by_txid(self, txid: str) -> Transaction:
        """
        Get transaction by txid
        :param txid: transaction hash
        :return: Transaction
        """

    def get_transaction_status(self, txid: str) -> TransactionStatus:
        """
        Get transaction status by txid
        :param txid: transaction hash
        :return: TransactionStatus
        """
        try:
            return self.get_transaction_by_txid(txid).status
        except TransactionNotFound:
            return TransactionStatus.UNKNOWN

    @abstractmethod
    def search_txs_by_address(self, address: str) -> List[Transaction]:
        """
        Search transactions by address
        todo paging? token?
        :param address: address
        :return: list of Transaction
        """

    def search_txids_by_address(self, address: str) -> List[str]:
        """
        Search transaction hash by address
        :param address: address
        :return: list of txid
        """
        txs = self.search_txs_by_address(address)

        txids = {i.txid for i in txs}
        txids = list(txids)
        return txids

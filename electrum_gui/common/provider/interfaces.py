import abc
from typing import Callable, Dict, List, Optional, Tuple

from electrum_gui.common.coin import data as coin_data
from electrum_gui.common.provider import data, exceptions
from electrum_gui.common.secret import interfaces as secret_interfaces


class ClientInterface(abc.ABC):
    @abc.abstractmethod
    def get_info(self) -> data.ClientInfo:
        """
        Get information of client
        :return: ClientInfo
        """

    @property
    def is_ready(self) -> bool:
        """
        Is client ready?
        :return: ready or not
        """
        return self.get_info().is_ready

    @abc.abstractmethod
    def get_address(self, address: str) -> data.Address:
        """
        Get address information by address str
        :param address: address
        :return: Address
        """

    def get_balance(self, address: str, token_address: Optional[str] = None) -> int:
        """
        get address balance
        :param token_address:
        :param address: address
        :return: balance
        """
        return self.get_address(address).balance

    @abc.abstractmethod
    def get_transaction_by_txid(self, txid: str) -> data.Transaction:
        """
        Get transaction by txid
        :param txid: transaction hash
        :return: Transaction
        :raise: raise TransactionNotFound if target tx not found
        """

    def get_transaction_status(self, txid: str) -> data.TransactionStatus:
        """
        Get transaction status by txid
        :param txid: transaction hash
        :return: TransactionStatus
        """
        try:
            return self.get_transaction_by_txid(txid).status
        except exceptions.TransactionNotFound:
            return data.TransactionStatus.UNKNOWN

    @abc.abstractmethod
    def broadcast_transaction(self, raw_tx: str) -> data.TxBroadcastReceipt:
        """
        push transaction to chain
        :param raw_tx: transaction in str
        :return: txid, optional
        """

    @abc.abstractmethod
    def get_prices_per_unit_of_fee(self) -> data.PricesPerUnit:
        """
        get the price per unit of the fee, likes the gas_price on eth
        :return: price per unit
        """

    def utxo_can_spend(self, utxo: data.UTXO) -> bool:
        """
        Check whether the UTXO is unspent
        :param utxo:
        :return: is unspent or not
        """
        raise Exception("Unsupported")


class BatchGetAddressMixin(abc.ABC):
    @abc.abstractmethod
    def batch_get_address(self, addresses: List[str]) -> List[data.Address]:
        """
        Batch to get address information by address str list
        :param addresses: List[address]
        :return: List[Address]
        """


class SearchTransactionMixin(abc.ABC):
    def search_txs_by_address(
        self,
        address: str,
        paginate: Optional[data.TxPaginate] = None,
    ) -> List[data.Transaction]:
        """
        Search transactions by address
        :param address: address
        :param paginate: paginate supports, optional
        :return: list of Transaction
        """
        return []

    def search_txids_by_address(
        self,
        address: str,
        paginate: Optional[data.TxPaginate] = None,
    ) -> List[str]:
        """
        Search transaction hash by address
        :param address: address
        :param paginate: paginate supports, optional
        :return: list of txid
        """
        txs = self.search_txs_by_address(address)

        txids = {i.txid for i in txs}
        txids = list(txids)
        return txids


class SearchUTXOMixin(abc.ABC):
    @abc.abstractmethod
    def search_utxos_by_address(self, address: str) -> List[data.UTXO]:
        """
        Search UTXOs by address
        :param address: address
        :return: list of UTXO
        todo paginate?
        """


class ProviderInterface(abc.ABC):
    def __init__(
        self,
        chain_info: coin_data.ChainInfo,
        coins_loader: Callable[[], List[coin_data.CoinInfo]],
        client_selector: Callable,
    ):
        self.chain_info = chain_info
        self.coins_loader = coins_loader
        self.client_selector = client_selector

    @property
    def client(self):
        return self.client_selector()

    @abc.abstractmethod
    def verify_address(self, address: str) -> data.AddressValidation:
        """
        Check whether the address can be recognized
        :param address: address
        :return: AddressValidation
        """

    @abc.abstractmethod
    def pubkey_to_address(self, verifier: secret_interfaces.VerifierInterface, encoding: str = None) -> str:
        """
        Convert pubkey to address
        :param verifier: VerifierInterface
        :param encoding: encoding of address, optional
        :return: address
        """

    @abc.abstractmethod
    def fill_unsigned_tx(self, unsigned_tx: data.UnsignedTx) -> data.UnsignedTx:
        """
        Filling unsigned tx as much as possible
        :param unsigned_tx: incomplete UnsignedTx
        :return: filled UnsignedTx
        """

    @abc.abstractmethod
    def sign_transaction(
        self, unsigned_tx: data.UnsignedTx, signers: Dict[str, secret_interfaces.SignerInterface]
    ) -> data.SignedTx:
        """
        Sign transaction
        :param unsigned_tx: complete UnsignedTx
        :param signers: mapping of address to SignerInterface
        :return: SignedTx
        """

    @abc.abstractmethod
    def get_token_info_by_address(self, token_address: str) -> Tuple[str, str, int]:
        """
        Get the base information (symbol, name, decimals) of a token on the chain.
        :param token_address:
        :return: Tuple[str, str, int], token symbol, token name, token decimals
        """

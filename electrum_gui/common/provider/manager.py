from typing import Dict, List, Optional, Tuple

from electrum_gui.common.provider import exceptions
from electrum_gui.common.provider.data import (
    UTXO,
    Address,
    AddressValidation,
    PricePerUnit,
    SignedTx,
    Transaction,
    TransactionStatus,
    TxBroadcastReceipt,
    TxPaginate,
    UnsignedTx,
)
from electrum_gui.common.provider.exceptions import NoAvailableClient
from electrum_gui.common.provider.interfaces import BatchGetAddressMixin, SearchTransactionMixin, SearchUTXOMixin
from electrum_gui.common.provider.loader import get_client_by_chain, get_provider_by_chain
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


def get_address(chain_code: str, address: str) -> Address:
    return get_client_by_chain(chain_code).get_address(address)


def batch_get_address(chain_code: str, addresses: List[str]) -> List[Address]:
    try:
        client = get_client_by_chain(chain_code, instance_required=BatchGetAddressMixin)
        return client.batch_get_address(addresses)
    except exceptions.NoAvailableClient:
        client = get_client_by_chain(chain_code)
        return [client.get_address(i) for i in addresses]


def get_balance(chain_code: str, address: str, token_address: Optional[str] = None) -> int:
    # TODO: raise specific exceptions for callers to catch. This also applies
    # to the APIs in this module.
    return get_client_by_chain(chain_code).get_balance(address, token_address=token_address)


def get_transaction_by_txid(chain_code: str, txid: str) -> Transaction:
    return get_client_by_chain(chain_code).get_transaction_by_txid(txid)


def get_transaction_status(chain_code: str, txid: str) -> TransactionStatus:
    return get_client_by_chain(chain_code).get_transaction_status(txid)


def search_txs_by_address(
    chain_code: str,
    address: str,
    paginate: Optional[TxPaginate] = None,
) -> List[Transaction]:
    try:
        return get_client_by_chain(chain_code, instance_required=SearchTransactionMixin).search_txs_by_address(
            address, paginate=paginate
        )
    except NoAvailableClient:
        return []


def search_txids_by_address(
    chain_code: str,
    address: str,
    paginate: Optional[TxPaginate] = None,
) -> List[str]:
    try:
        return get_client_by_chain(chain_code, instance_required=SearchTransactionMixin).search_txids_by_address(
            address, paginate=paginate
        )
    except NoAvailableClient:
        return []


def broadcast_transaction(chain_code: str, raw_tx: str) -> TxBroadcastReceipt:
    return get_client_by_chain(chain_code).broadcast_transaction(raw_tx)


def get_price_per_unit_of_fee(chain_code: str) -> PricePerUnit:
    return get_client_by_chain(chain_code).get_price_per_unit_of_fee()


def verify_address(chain_code: str, address: str) -> AddressValidation:
    return get_provider_by_chain(chain_code).verify_address(address)


def pubkey_to_address(chain_code: str, verifier: VerifierInterface, encoding: str = None) -> str:
    return get_provider_by_chain(chain_code).pubkey_to_address(verifier, encoding=encoding)


def fill_unsigned_tx(chain_code: str, unsigned_tx: UnsignedTx) -> UnsignedTx:
    return get_provider_by_chain(chain_code).fill_unsigned_tx(unsigned_tx)


def sign_transaction(chain_code: str, unsigned_tx: UnsignedTx, signers: Dict[str, SignerInterface]) -> SignedTx:
    return get_provider_by_chain(chain_code).sign_transaction(unsigned_tx, signers)


def utxo_can_spend(chain_code: str, utxo: UTXO) -> bool:
    return get_client_by_chain(chain_code).utxo_can_spend(utxo)


def search_utxos_by_address(chain_code: str, address: str) -> List[UTXO]:
    return get_client_by_chain(chain_code, instance_required=SearchUTXOMixin).search_utxos_by_address(address)


def get_token_info_by_address(chain_code: str, token_address: str) -> Tuple[str, str, int]:
    return get_provider_by_chain(chain_code).get_token_info_by_address(token_address)

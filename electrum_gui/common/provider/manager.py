from typing import Dict, List, Optional

from electrum_gui.common.provider.data import (
    UTXO,
    Address,
    AddressValidation,
    PricePerUnit,
    SignedTx,
    Token,
    Transaction,
    TransactionStatus,
    TxBroadcastReceipt,
    TxPaginate,
    UnsignedTx,
)
from electrum_gui.common.provider.exceptions import NoAvailableClient
from electrum_gui.common.provider.interfaces import SearchTransactionMixin, SearchUTXOMixin
from electrum_gui.common.provider.loader import get_client_by_chain, get_provider_by_chain
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


def get_address(chain_code: str, address: str) -> Address:
    return get_client_by_chain(chain_code).get_address(address)


def get_balance(chain_code: str, address: str, token: Token = None) -> int:
    return get_client_by_chain(chain_code).get_balance(address, token=token)


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


def filling_unsigned_tx(chain_code: str, unsigned_tx: UnsignedTx) -> UnsignedTx:
    return get_provider_by_chain(chain_code).filling_unsigned_tx(unsigned_tx)


def sign_transaction(chain_code: str, unsigned_tx: UnsignedTx, key_mapping: Dict[str, SignerInterface]) -> SignedTx:
    return get_provider_by_chain(chain_code).sign_transaction(unsigned_tx, key_mapping)


def utxo_can_spend(chain_code: str, utxo: UTXO) -> bool:
    return get_client_by_chain(chain_code).utxo_can_spend(utxo)


def search_utxos_by_address(chain_code: str, address: str) -> List[UTXO]:
    return get_client_by_chain(chain_code, instance_required=SearchUTXOMixin).search_utxos_by_address(address)

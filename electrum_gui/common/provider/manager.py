from typing import List, Optional

from electrum_gui.common.provider.data import (
    Address,
    AddressValidation,
    PricePerUnit,
    Token,
    Transaction,
    TransactionStatus,
    TxBroadcastReceipt,
    TxPaginate,
)
from electrum_gui.common.provider.loader import get_client_by_chain, get_provider_by_chain


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
    return get_client_by_chain(chain_code).search_txs_by_address(address, paginate=paginate)


def search_txids_by_address(
    chain_code: str,
    address: str,
    paginate: Optional[TxPaginate] = None,
) -> List[str]:
    return get_client_by_chain(chain_code).search_txids_by_address(address, paginate=paginate)


def broadcast_transaction(chain_code: str, raw_tx: str) -> TxBroadcastReceipt:
    return get_client_by_chain(chain_code).broadcast_transaction(raw_tx)


def get_price_per_unit_of_fee(chain_code: str) -> PricePerUnit:
    return get_client_by_chain(chain_code).get_price_per_unit_of_fee()


def verify_address(chain_code: str, address: str) -> AddressValidation:
    return get_provider_by_chain(chain_code).verify_address(address)

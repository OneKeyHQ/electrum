from dataclasses import dataclass, field
from enum import IntEnum, unique
from typing import List, Optional

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin


@unique
class TransactionStatus(IntEnum):
    UNKNOWN = 0
    PENDING = 10
    CONFIRM_REVERTED = 99
    CONFIRM_SUCCESS = 100


@unique
class TxBroadcastReceiptCode(IntEnum):
    UNKNOWN = 0
    UNEXPECTED_FAILED = 10
    SUCCESS = 100
    ALREADY_KNOWN = 101
    NONCE_TOO_LOW = 102


@dataclass
class ProviderInfo(DataClassMixin):
    name: str
    best_block_number: int
    is_ready: bool
    desc: str = ""


@dataclass
class BlockHeader(DataClassMixin):
    block_hash: str
    block_number: int
    block_time: int  # in seconds
    confirmations: int = 0


@dataclass
class TransactionFee(DataClassMixin):
    limit: int
    used: int
    price_per_unit: int = 1


@dataclass
class TransactionInput(DataClassMixin):
    address: str
    value: int
    token_address: Optional[str] = None


@dataclass
class TransactionOutput(DataClassMixin):
    address: str
    value: int
    token_address: Optional[str] = None


@dataclass
class Transaction(DataClassMixin):
    txid: str
    status: TransactionStatus
    inputs: List[TransactionInput] = field(default=list)
    outputs: List[TransactionOutput] = field(default=list)
    fee: TransactionFee = None
    block_header: BlockHeader = None
    raw_tx: str = ""
    nonce: int = -1


@dataclass
class TxPaginate(DataClassMixin):
    start_block_number: int
    page_number: int = 1  # start from 1
    items_per_page: int = None


@dataclass
class Address(DataClassMixin):
    address: str
    balance: int
    existing: bool
    nonce: int = 0
    payload: dict = field(default_factory=dict)


@dataclass
class Token(DataClassMixin):
    contract: str


@dataclass
class TxBroadcastReceipt(DataClassMixin):
    is_success: bool
    receipt_code: TxBroadcastReceiptCode
    receipt_message: str = None
    txid: str = None


@dataclass
class EstimatedTimeOnPrice(DataClassMixin):
    price: int
    time: int = None  # in seconds


@dataclass
class PricePerUnit(DataClassMixin):
    slow: EstimatedTimeOnPrice
    normal: EstimatedTimeOnPrice
    fast: EstimatedTimeOnPrice

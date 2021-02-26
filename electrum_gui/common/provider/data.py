from dataclasses import dataclass, field
from enum import IntEnum, unique

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin


@unique
class TransactionStatus(IntEnum):
    UNKNOWN = 0
    IN_MEMPOOL = 10
    REVERED = 99
    CONFIRMED = 100


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
    usage: int
    price_per_unit: int = 1


@dataclass
class Transaction(DataClassMixin):
    txid: str
    target: str
    value: int
    status: TransactionStatus
    fee: TransactionFee = None
    block_header: BlockHeader = None
    source: str = ""
    raw_tx: str = ""


@dataclass
class Address(DataClassMixin):
    address: str
    balance: int
    existing: bool = True
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

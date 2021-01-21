from dataclasses import dataclass, field

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin
from electrum_gui.common.explorer.data.enums import TransactionStatus


@dataclass
class ExplorerInfo(DataClassMixin):
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
class AddressBalance(DataClassMixin):
    available: int
    pending: int = 0
    frozen: int = 0
    payload: dict = field(default_factory=dict)


@dataclass
class Address(DataClassMixin):
    address: str
    balance: AddressBalance
    existing: bool = True
    nonce: int = 0
    payload: dict = field(default_factory=dict)

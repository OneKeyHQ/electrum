from dataclasses import dataclass, field
from enum import IntEnum, unique
from typing import Dict, List, Optional

from electrum import i18n
from electrum import util as electrum_utils
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
    RBF_UNDERPRICE = 103
    ETH_GAS_PRICE_TOO_LOW = 104
    ETH_GAS_LIMIT_EXCEEDED = 105


@dataclass
class ClientInfo(DataClassMixin):
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
class UTXO(DataClassMixin):
    txid: str
    vout: int
    value: int


@dataclass
class TransactionInput(DataClassMixin):
    address: str
    value: int
    token_address: Optional[str] = None
    utxo: Optional[UTXO] = None


@dataclass
class TransactionOutput(DataClassMixin):
    address: str
    value: int
    token_address: Optional[str] = None


@dataclass
class Transaction(DataClassMixin):
    txid: str
    status: TransactionStatus
    inputs: List[TransactionInput] = field(default_factory=list)
    outputs: List[TransactionOutput] = field(default_factory=list)
    fee: TransactionFee = None
    block_header: BlockHeader = None
    raw_tx: str = ""
    nonce: int = -1

    @property
    def detailed_status(self) -> str:
        if self.status == TransactionStatus.CONFIRM_REVERTED:
            return i18n._("Sending failure")
        elif self.status == TransactionStatus.CONFIRM_SUCCESS:
            if self.block_header is not None and self.block_header.confirmations > 0:
                return i18n._("{} confirmations").format(self.block_header.confirmations)
            else:
                return i18n._("Confirmed")
        else:
            return i18n._("Unconfirmed")

    @property
    def show_status(self) -> List:
        if self.status == TransactionStatus.CONFIRM_REVERTED:
            return [2, i18n._("Sending failure")]
        elif self.status == TransactionStatus.CONFIRM_SUCCESS:
            return [3, i18n._("Confirmed")]
        else:
            return [1, i18n._("Unconfirmed")]

    @property
    def date_str(self) -> str:
        if self.block_header is not None:
            return electrum_utils.format_time(self.block_header.block_time)
        else:
            return i18n._("Unknown")

    @property
    def height(self) -> int:
        if self.block_header is not None:
            return self.block_header.block_number
        else:
            return 0

    @property
    def confirmations(self) -> int:
        if self.block_header is not None:
            return self.block_header.confirmations
        else:
            return 0


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
class PricesPerUnit(DataClassMixin):
    slow: EstimatedTimeOnPrice
    normal: EstimatedTimeOnPrice
    fast: EstimatedTimeOnPrice
    extra_prices: Dict[str, EstimatedTimeOnPrice] = field(default_factory=dict)

    def __iter__(self):
        all_prices = {**self.extra_prices, "slow": self.slow, "normal": self.normal, "fast": self.fast}
        for description, price in sorted(
            all_prices.items(), key=lambda item: (item[1].time, item[1].price), reverse=True
        ):
            yield description, price


@dataclass
class AddressValidation(DataClassMixin):
    is_valid: bool
    encoding: Optional[str] = None


@dataclass
class UnsignedTx(DataClassMixin):
    inputs: List[TransactionInput] = field(default_factory=list)
    outputs: List[TransactionOutput] = field(default_factory=list)
    nonce: int = None
    fee_limit: int = None
    fee_price_per_unit: int = None
    payload: dict = field(default_factory=dict)


@dataclass
class SignedTx(DataClassMixin):
    txid: str
    raw_tx: str

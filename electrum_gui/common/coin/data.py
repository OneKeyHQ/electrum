from dataclasses import dataclass, field
from enum import IntEnum, unique
from typing import List, Optional

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin


@unique
class ChainModel(IntEnum):
    UTXO = 10
    ACCOUNT = 20


@dataclass
class ChainInfo(DataClassMixin):
    chain_code: str  # unique chain coin
    fee_code: str  # which coin is used to provide fee (omni chain uses btc, neo uses neo_gas etc.)
    name: str  # full name of chain
    chain_model: ChainModel  # model of chain (UTXO, Account etc.)
    chain_affinity: str  # mark chain affinity
    qr_code_prefix: str  # QR code prefix of address
    clients: List[dict] = field(default_factory=list)  # config of clients
    chain_id: Optional[str] = None  # optional, identify multi forked chains by chain_id (use by eth etc.)


@dataclass
class CoinInfo(DataClassMixin):
    code: str  # unique code
    chain_code: str  # which chain does it belong to

    name: str  # full name of coin
    symbol: str  # symbol of coin

    decimals: int  # decimals of coin
    icon: Optional[str] = None  # icon url of coin

    token_address: Optional[str] = None  # optional, used by tokens

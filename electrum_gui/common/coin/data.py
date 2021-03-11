from dataclasses import dataclass
from typing import Optional

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin


@dataclass
class ChainInfo(DataClassMixin):
    chain_code: str  # unique chain coin
    fee_code: str  # which coin is used to provide fee (omni chain uses btc, neo uses neo_gas etc.)
    name: str  # full name of chain
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

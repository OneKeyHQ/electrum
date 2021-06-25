from dataclasses import dataclass, field
from enum import IntEnum, unique
from typing import Optional

from electrum_gui.common.basic.dataclass.dataclass import DataClassMixin
from electrum_gui.common.secret.data import CurveEnum


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
    curve: CurveEnum  # curve type
    chain_affinity: str  # mark chain affinity
    qr_code_prefix: str  # QR code prefix of address
    bip44_coin_type: int  # coin_type of bip44 path
    bip44_target_level: str = (
        "ADDRESS_INDEX"  # Derive to 'ADDRESS_INDEX' as default (options: ACCOUNT, CHANGE, ADDRESS_INDEX)
    )
    bip44_auto_increment_level: str = "ADDRESS_INDEX"  # Auto increase 'ADDRESS_INDEX' level to derive new address (options: ACCOUNT, CHANGE, ADDRESS_INDEX)
    bip44_last_hardened_level: str = (
        "ACCOUNT"  # hardened to 'ACCOUNT' level as default, but hardened to 'ADDRESS_INDEX' level in ED25519 curve
    )
    default_address_encoding: Optional[str] = None
    chain_id: Optional[str] = None  # optional, identify multi forked chains by chain_id (use by eth etc.)
    bip44_purpose_options: dict = field(default_factory=dict)
    fee_price_decimals_for_legibility: int = 0  # (gwei in eth etc.)


@dataclass
class CoinInfo(DataClassMixin):
    code: str  # unique code
    chain_code: str  # which chain does it belong to

    name: str  # full name of coin
    symbol: str  # symbol of coin

    decimals: int  # decimals of coin
    icon: Optional[str] = None  # icon url of coin

    token_address: Optional[str] = None  # optional, used by tokens

import datetime
from decimal import Decimal
from typing import Iterable, List, Optional, Set

from electrum_gui.common.transaction.data import TxActionStatus
from electrum_gui.common.transaction.models import TxAction


def new_action(
    txid: str,
    status: TxActionStatus,
    chain_code: str,
    coin_code: str,
    value: Decimal,
    symbol: str,
    decimals: int,
    from_address: str,
    to_address: str,
    fee_limit: Decimal,
    raw_tx: str,
    fee_used: Decimal = 0,
    fee_price_per_unit: Decimal = 1,
    block_number: int = None,
    block_hash: str = None,
    block_time: int = None,
    index: int = 0,
    nonce: int = -1,
) -> TxAction:
    return TxAction(
        txid=txid,
        status=status,
        chain_code=chain_code,
        coin_code=coin_code,
        value=value,
        symbol=symbol,
        decimals=decimals,
        from_address=from_address,
        to_address=to_address,
        fee_limit=fee_limit,
        raw_tx=raw_tx,
        fee_used=fee_used,
        fee_price_per_unit=fee_price_per_unit,
        block_number=block_number,
        block_hash=block_hash,
        block_time=block_time,
        index=index,
        nonce=nonce,
    )


def bulk_create(actions: Iterable[TxAction]):
    return TxAction.bulk_create(actions, 100)


def on_actions_confirmed(
    chain_code: str,
    txid: str,
    status: TxActionStatus,
    fee_used: Decimal,
    block_number: int,
    block_hash: str,
    block_time: int,
):
    return (
        TxAction.update(
            status=status,
            fee_used=fee_used,
            block_number=block_number,
            block_hash=block_hash,
            block_time=block_time,
            modified_time=datetime.datetime.now(),
        )
        .where(TxAction.chain_code == chain_code, TxAction.txid == txid)
        .execute()
    )


def update_actions_status(
    chain_code: str,
    txid: str,
    status: TxActionStatus,
):
    return (
        TxAction.update(
            status=status,
            modified_time=datetime.datetime.now(),
        )
        .where(TxAction.chain_code == chain_code, TxAction.txid == txid)
        .execute()
    )


def query_actions_by_address(
    chain_code: str,
    address: str,
    txid: Optional[str] = None,
    page_number: int = 1,
    items_per_page: int = 100,
) -> List[TxAction]:
    expressions = [
        TxAction.chain_code == chain_code,
        (TxAction.from_address.collate("NOCASE") == address) | (TxAction.to_address.collate("NOCASE") == address),
    ]

    if txid is not None:
        expressions.append(TxAction.txid == txid)

    return list(
        TxAction.select()
        .where(*expressions)
        .order_by(TxAction.block_time.desc(nulls="first"))
        .paginate(page_number, items_per_page)
    )


def get_action_by_id(action_id: int) -> Optional[TxAction]:
    return TxAction.get_or_none(TxAction.id == action_id)


def query_actions_by_status(
    status: TxActionStatus,
    chain_code: str = None,
) -> List[TxAction]:
    expressions = [TxAction.status == status]

    if chain_code is not None:
        expressions.append(TxAction.chain_code == chain_code)

    models = TxAction.select().where(*expressions)
    return list(models)


def get_last_confirmed_action_by_address(chain_code: str, address: str) -> Optional[TxAction]:
    return (
        TxAction.select()
        .where(
            TxAction.chain_code == chain_code,
            TxAction.block_time.is_null(False),
            (TxAction.from_address.collate("NOCASE") == address) | (TxAction.to_address.collate("NOCASE") == address),
        )
        .order_by(TxAction.block_time.desc())
        .first()
    )


def filter_existing_txids(chain_code: str, txids: Iterable[str]) -> Set[str]:
    models = TxAction.select().where(
        TxAction.chain_code == chain_code,
        TxAction.txid << txids,
    )
    return {i.txid for i in models}

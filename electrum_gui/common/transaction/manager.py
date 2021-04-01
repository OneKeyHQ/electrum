import datetime
import itertools
import logging
from decimal import Decimal
from typing import Iterable, List, Optional, Tuple

from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.coin.data import ChainModel
from electrum_gui.common.provider import provider_manager
from electrum_gui.common.provider.data import Transaction, TxPaginate
from electrum_gui.common.transaction import daos
from electrum_gui.common.transaction.data import TX_TO_ACTION_STATUS_DIRECT_MAPPING, TxActionStatus
from electrum_gui.common.transaction.models import TxAction

logger = logging.getLogger("app.transaction")


def create_action(
    txid: str,
    status: TxActionStatus,
    chain_code: str,
    coin_code: str,
    value: Decimal,
    from_address: str,
    to_address: str,
    fee_limit: Decimal,
    raw_tx: str,
    **kwargs,
) -> TxAction:
    coin = coin_manager.get_coin_info(coin_code)
    return daos.new_action(
        txid=txid,
        status=status,
        chain_code=chain_code,
        coin_code=coin_code,
        value=value,
        decimals=coin.decimals,
        symbol=coin.symbol,
        from_address=from_address,
        to_address=to_address,
        fee_limit=fee_limit,
        raw_tx=raw_tx,
        **kwargs,
    ).save()


def get_action_by_id(action_id: int) -> TxAction:
    return daos.get_action_by_id(action_id)


def query_actions_by_address(
    chain_code: str,
    address: str,
    txid: Optional[str] = None,
    page_number: int = 1,
    items_per_page: int = 100,
) -> List[TxAction]:
    return daos.query_actions_by_address(
        chain_code, address, txid=txid, page_number=page_number, items_per_page=items_per_page
    )


def update_pending_actions(chain_code: Optional[str] = None):
    pending_actions = daos.query_actions_by_status(TxActionStatus.PENDING, chain_code=chain_code)

    if not pending_actions:
        return

    txids_of_chain = {(i.chain_code, i.txid) for i in pending_actions}
    confirmed_txids = set()

    for chain_code, tx in _query_transactions_of_chain(txids_of_chain):
        try:
            action_status = TX_TO_ACTION_STATUS_DIRECT_MAPPING.get(tx.status)
            if tx.fee is None or tx.block_header is None or action_status is None:
                continue

            daos.on_actions_confirmed(
                chain_code=chain_code,
                txid=tx.txid,
                status=action_status,
                fee_used=Decimal(tx.fee.used),
                block_hash=tx.block_header.block_hash,
                block_number=tx.block_header.block_number,
                block_time=tx.block_header.block_time,
            )
            confirmed_txids.add(tx.txid)
            logger.info(
                f"TxAction confirmed. chain_code: {chain_code}, txid: {tx.txid}, action_status: {action_status}"
            )
        except Exception as e:
            logger.exception(f"Error in updating actions. chain_code: {chain_code}, txid: {tx.txid}", e, tx)

    unconfirmed_actions = [i for i in pending_actions if i.txid not in confirmed_txids]
    if not unconfirmed_actions:
        return

    now = datetime.datetime.now()
    too_old = datetime.timedelta(days=3)
    too_old_txids = {(i.chain_code, i.txid) for i in unconfirmed_actions if now - i.created_time >= too_old}

    with db.atomic():
        for chain_code, txid in too_old_txids:
            daos.update_actions_status(chain_code, txid, status=TxActionStatus.UNKNOWN)


def _query_transactions_of_chain(txids_of_chain: Iterable[Tuple[str, str]]) -> Iterable[Tuple[str, Transaction]]:
    txids_of_chain = sorted(txids_of_chain, key=lambda i: i[0])  # in order to use itertools.groupby

    for chain_code, group in itertools.groupby(txids_of_chain, key=lambda i: i[0]):
        for (_, txid) in group:
            try:
                yield chain_code, provider_manager.get_transaction_by_txid(chain_code, txid)
            except Exception as e:
                logger.exception(f"Error in getting transaction by txid. chain_code: {chain_code}, txid: {txid}", e)


def _search_txs_by_address(
    chain_code: str, address: str, last_confirmed_action: TxAction = None
) -> Iterable[Transaction]:
    try:
        if last_confirmed_action is not None:
            paginate = TxPaginate(start_block_number=last_confirmed_action.block_number)
        else:
            paginate = None

        transactions = provider_manager.search_txs_by_address(chain_code, address, paginate=paginate)

        return transactions
    except Exception as e:
        logger.exception(
            f"Error in searching txs by address. chain_code: {chain_code}, "
            f"address: {address}, last_confirmed_action: {last_confirmed_action}",
            e,
        )
        return []


def _tx_action_factory__account_model(chain_code: str, transactions: Iterable[Transaction]) -> Iterable[TxAction]:
    transactions = [i for i in transactions if i.status in TX_TO_ACTION_STATUS_DIRECT_MAPPING]
    token_addresses = set()
    for tx in transactions:
        for tx_input in tx.inputs:
            token_addresses.add(tx_input.token_address)

    main_coin = coin_manager.get_coin_info(chain_code)
    tokens = coin_manager.query_coins_by_token_addresses(chain_code, list(token_addresses))
    tokens = {i.token_address: i for i in tokens if i.token_address}

    for tx in transactions:
        status = TX_TO_ACTION_STATUS_DIRECT_MAPPING.get(tx.status)

        for index, (tx_input, tx_output) in enumerate(zip(tx.inputs, tx.outputs)):
            token_address = tx_output.token_address
            if not tx_input.address or not tx_output.address or (token_address and token_address not in tokens):
                continue

            coin = main_coin if not token_address else tokens[token_address]
            info = dict(
                txid=tx.txid,
                status=status,
                chain_code=chain_code,
                coin_code=coin.code,
                value=Decimal(tx_output.value),
                symbol=coin.symbol,
                decimals=coin.decimals,
                from_address=tx_input.address,
                to_address=tx_output.address,
                fee_limit=Decimal(tx.fee.limit),
                fee_price_per_unit=Decimal(tx.fee.price_per_unit),
                raw_tx=tx.raw_tx,
                index=index,
            )

            if tx.block_header:
                info.update(
                    dict(
                        fee_used=Decimal(tx.fee.used),
                        block_number=tx.block_header.block_number,
                        block_hash=tx.block_header.block_hash,
                        block_time=tx.block_header.block_time,
                    )
                )

            if index == 0 and tx.nonce is not None and tx.nonce >= 0:
                info["nonce"] = tx.nonce

            yield daos.new_action(**info)


_TX_ACTION_FACTORY_REGISTRY = {
    ChainModel.ACCOUNT: _tx_action_factory__account_model,
}


def sync_address_actions(chain_code: str, address: str, force_update: bool = False):
    chain_info = coin_manager.get_chain_info(chain_code)
    action_factory = _TX_ACTION_FACTORY_REGISTRY.get(chain_info.chain_model)
    if not action_factory:
        raise Exception(
            f"This chain model isn't supported yet. chain_code: {chain_code}, chain_model: {chain_info.chain_model}"
        )

    if not force_update:
        last_confirmed_action = daos.get_last_confirmed_action_by_address(chain_code, address)
    else:
        last_confirmed_action = None

    raw_txs = _search_txs_by_address(chain_code, address, last_confirmed_action=last_confirmed_action)
    raw_txs = (i for i in raw_txs if i.status in TX_TO_ACTION_STATUS_DIRECT_MAPPING)
    txs = {i.txid: i for i in raw_txs}

    txids = set(txs.keys())
    existing_txids = daos.filter_existing_txids(chain_code, txids)
    syncing_txids = txids - existing_txids

    if not syncing_txids:
        return

    with db.atomic():
        actions = action_factory(chain_code, (i for i in txs.values() if i.txid in syncing_txids))
        daos.bulk_create(actions)

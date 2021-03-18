from typing import List, Optional, Set

from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.coin.data import CoinInfo
from electrum_gui.common.coin.models import CoinModel


def add_coin(*coins: CoinInfo):
    models = (CoinModel(**i.to_dict()) for i in coins)
    with db.atomic():
        CoinModel.bulk_create(models, 100)


def get_coin_info(coin_code: str) -> Optional[CoinInfo]:
    model = CoinModel.get_or_none(CoinModel.code == coin_code)
    return model.to_dataclass() if model else None


def query_coins_by_codes(coin_codes: Set[str]) -> List[CoinInfo]:
    models = CoinModel.select().where(CoinModel.code << coin_codes)
    return [i.to_dataclass() for i in models]


def get_all_coins() -> List[CoinInfo]:
    return [i.to_dataclass() for i in CoinModel.select()]


def get_coins_by_chain(chain_code: str) -> List[CoinInfo]:
    models = CoinModel.select().where(CoinModel.chain_code == chain_code)
    return [i.to_dataclass() for i in models]


def update_coin_info(coin_code: str, name: str = None, icon: str = None):
    payload = {}
    if name is not None:
        payload["name"] = name
    if icon is not None:
        payload["icon"] = icon

    CoinModel.update(payload).where(CoinModel.code == coin_code).execute()


def query_coins_by_token_addresses(chain_code: str, token_addresses: List[str]) -> List[CoinInfo]:
    return list(
        CoinModel.select().where(
            CoinModel.chain_code == chain_code,
            CoinModel.token_address << token_addresses,
        )
    )

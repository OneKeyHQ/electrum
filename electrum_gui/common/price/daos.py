import datetime
import functools
from decimal import Decimal
from typing import Dict, Set, Tuple

from electrum_gui.common.price.data import Channel
from electrum_gui.common.price.models import Price


def create_or_update(
    coin_code: str,
    unit: str,
    channel: Channel,
    price: Decimal,
):
    model, is_newborn = Price.get_or_create(
        coin_code=coin_code,
        unit=unit,
        channel=channel,
        defaults=dict(price=price),
    )

    if not is_newborn:
        Price.update(
            price=price,
            modified_time=datetime.datetime.now(),
        ).where(Price.id == model.id).execute()


def get_last_price(
    coin_code: str,
    unit: str,
    default: Decimal = 0,
    channel: Channel = None,
) -> Decimal:
    query = [Price.coin_code == coin_code, Price.unit == unit]
    if channel is not None:
        query.append(Price.channel == channel)

    model = Price.select().where(*query).order_by(Price.modified_time.desc()).first()
    return model.price if model else default


def load_price_by_pairs(pairs: Set[Tuple[str, str]]) -> Dict[Tuple[str, str], Decimal]:
    expression = [(Price.coin_code == pair[0]) & (Price.unit == pair[1]) for pair in pairs]
    expression = functools.reduce(lambda a, b: a | b, expression)

    pair_prices = {}
    models = Price.select().where(expression)

    for i in models:
        pair_prices[(i.coin_code, i.unit)] = i.price

    return pair_prices

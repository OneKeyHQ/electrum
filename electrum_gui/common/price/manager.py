import functools
import logging
from decimal import Decimal
from typing import Callable, Dict, Iterable, List, Sequence, Set, Tuple

from electrum_gui.common.basic.functional.timing import timing_logger
from electrum_gui.common.basic.functional.wraps import cache_it
from electrum_gui.common.basic.orm.database import db
from electrum_gui.common.basic.ticker.utils import on_interval
from electrum_gui.common.coin import codes
from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.conf import settings
from electrum_gui.common.price import daos
from electrum_gui.common.price.channels.coingecko import Coingecko
from electrum_gui.common.price.data import Channel
from electrum_gui.common.price.interfaces import PriceChannelInterface

logger = logging.getLogger("app.price")

_registry: Dict[Channel, Callable[[], PriceChannelInterface]] = {
    Channel.CGK: functools.partial(Coingecko, settings.COINGECKO_API_HOST),
}


def pricing(coin_codes: List[str] = None):
    if not coin_codes:
        coins = coin_manager.get_all_coins()
    else:
        coins = coin_manager.query_coins_by_codes(coin_codes)

    if not coins:
        return

    for channel_type, channel_creator in _registry.items():
        try:
            channel = channel_creator()

            for price in channel.pricing(coins):
                daos.create_or_update(
                    coin_code=price.coin_code,
                    unit=price.unit,
                    channel=channel_type,
                    price=price.price,
                )
        except Exception as e:
            logger.exception(f"Error in running channel. channel_type: {channel_type}, error: {e}")


@cache_it(timeout=5 * 60)
def get_last_price(coin_code: str, unit: str, default: Decimal = 0) -> Decimal:
    coin_code = settings.PRICING_COIN_MAPPING.get(coin_code) or coin_code
    unit = settings.PRICING_COIN_MAPPING.get(unit) or unit

    paths = list(_generate_searching_paths(coin_code, unit))
    pairs = _split_paths_to_pairs(paths)
    pair_prices = daos.load_price_by_pairs(pairs)

    price = 0
    for path in paths:
        if len(path) < 2:
            continue

        price = 1
        for i in range(len(path) - 1):
            input_code, output_code = path[i].lower(), path[i + 1].lower()
            if input_code != output_code:
                rate = pair_prices.get((input_code, output_code)) or 0
                if rate <= 0:
                    reversed_rate = pair_prices.get((output_code, input_code)) or 0
                    rate = 1 / reversed_rate if reversed_rate > 0 else rate
            else:
                rate = 1

            price *= rate
            if price <= 0:  # got invalid path
                break

        if price > 0:  # got price already
            break

    price = price if price > 0 else default
    return Decimal(price)


def _generate_searching_paths(coin_code: str, unit: str) -> Iterable[Sequence[str]]:
    yield coin_code, unit

    if coin_code == codes.BTC or unit == codes.BTC:
        return

    yield coin_code, codes.BTC, unit

    coin = coin_manager.get_coin_info(coin_code, nullable=True)
    if coin and coin.chain_code not in (coin_code, unit, codes.BTC):
        yield coin_code, coin.chain_code, unit
        yield coin_code, coin.chain_code, codes.BTC, unit


def _split_paths_to_pairs(paths: List[Sequence[str]]) -> Set[Tuple[str, str]]:
    pairs = set()

    for path in paths:
        if len(path) < 2:
            continue

        for i in range(len(path) - 1):
            input_code, output_code = path[i].lower(), path[i + 1].lower()
            if input_code != output_code:
                pairs.add((input_code, output_code))
                pairs.add((output_code, input_code))

    return pairs


@on_interval(15 * 60)
@timing_logger("price_manager.on_ticker_signal")
@db
def on_ticker_signal():
    pricing()

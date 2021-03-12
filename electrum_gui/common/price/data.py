from collections import namedtuple
from enum import IntEnum, unique


@unique
class Channel(IntEnum):
    CGK = 10

    @classmethod
    def to_choices(cls):
        return ((cls.CGK, "Coingecko"),)


YieldedPrice = namedtuple("YieldedPrice", ["coin_code", "price", "unit"])

from abc import ABC, abstractmethod
from typing import Iterable

from electrum_gui.common.coin.data import CoinInfo
from electrum_gui.common.price.data import YieldedPrice


class PriceChannelInterface(ABC):
    @abstractmethod
    def pricing(self, coins: Iterable[CoinInfo]) -> Iterable[YieldedPrice]:
        pass

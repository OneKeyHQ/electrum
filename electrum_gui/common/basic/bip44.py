from enum import IntEnum, unique
from typing import List, Optional

from electrum_gui.common.basic.functional.require import require


@unique
class BIP44Level(IntEnum):
    PURPOSE = 1
    COIN_TYPE = 2
    ACCOUNT = 3
    CHANGE = 4
    ADDRESS_INDEX = 5


class BIP44Path(object):
    def __init__(
        self,
        purpose: int,
        coin_type: int,
        account: int,
        change: int = None,
        address_index: int = None,
        last_hardened_level: BIP44Level = BIP44Level.ACCOUNT,
    ):
        self._levels = [purpose, coin_type, account]

        if change is not None:
            self._levels.append(change)

        if address_index is not None:
            require(len(self._levels) == BIP44Level.CHANGE)
            self._levels.append(address_index)

        self._last_hardened_level = last_hardened_level

    @property
    def last_hardened_level(self) -> BIP44Level:
        return self._last_hardened_level

    def index_of(self, level: BIP44Level) -> Optional[int]:
        if len(self._levels) >= level:
            return self._levels[level - 1]
        else:
            return None

    def to_bip44_path(self) -> str:
        levels = ["m"]

        for level_enum, level in zip(sorted(BIP44Level), self._levels):
            level = str(level)
            if level_enum <= self._last_hardened_level:
                level += "'"

            levels.append(level)

        return "/".join(levels)

    def __str__(self):
        return self.to_bip44_path()

    def __repr__(self):
        return f"BIP44<{self}>"

    def __eq__(self, other):
        return self._levels == getattr(other, "_levels", None) and self.last_hardened_level == getattr(
            other, "last_hardened_level", None
        )

    @classmethod
    def from_bip44_path(cls, path: str) -> "BIP44Path":
        if path.startswith("m"):
            path = path[2:]  # remove m/

        splits = path.split("/")
        if len(splits) < BIP44Level.ACCOUNT:
            raise ValueError(f"Only supports BIP44 path higher than or equal to account level, but now is {repr(path)}")

        levels = []
        last_hardened_level = None

        for level_enum, split in zip(sorted(BIP44Level), splits):
            if split.endswith("'") or split.endswith("h"):
                split = split[:-1]
                if (last_hardened_level is None and level_enum == BIP44Level.PURPOSE) or (
                    last_hardened_level is not None and level_enum - 1 == last_hardened_level
                ):
                    last_hardened_level = level_enum
                else:
                    raise ValueError("Hardened level interrupt")

            levels.append(int(split))

        if not last_hardened_level or last_hardened_level < BIP44Level.ACCOUNT:
            raise ValueError(
                f"Hardened level should higher than or equal to account level, but now is {repr(last_hardened_level)}. "
                f"path: {repr(path)}"
            )

        return cls(*levels, last_hardened_level=last_hardened_level)

    def next_sibling(self, gap: int = 1) -> "BIP44Path":
        next_levels = self._levels.copy()
        next_levels[-1] += gap
        return self.__class__(*next_levels, last_hardened_level=self._last_hardened_level)

    def to_target_level(self, target_level: BIP44Level, value_filling_if_none: int = 0) -> "BIP44Path":
        if target_level < BIP44Level.ACCOUNT:
            raise ValueError(f"The target level should higher than account level, but now is {repr(target_level)}")

        levels = self._levels[:target_level]
        if len(levels) < target_level:
            levels.extend([value_filling_if_none] * (target_level - len(levels)))

        return self.__class__(*levels, last_hardened_level=self._last_hardened_level)

    @classmethod
    def from_bip44_int_path(cls, ints: List[int]) -> "BIP44Path":
        import electrum.bip32

        return cls.from_bip44_path(electrum.bip32.convert_bip32_intpath_to_strpath(ints))

    def to_bip44_int_path(self) -> List[int]:
        import electrum.bip32

        return electrum.bip32.convert_bip32_path_to_list_of_uint32(self.to_bip44_path())

from enum import IntEnum, unique


@unique
class TransactionStatus(IntEnum):
    REVERED = -99
    UNKNOWN = 0
    IN_MEMPOOL = 10
    CONFIRMED = 100

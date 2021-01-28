from enum import IntEnum, unique


@unique
class TransactionStatus(IntEnum):
    UNKNOWN = 0
    IN_MEMPOOL = 10
    REVERED = 99
    CONFIRMED = 100


@unique
class TxBroadcastReceiptCode(IntEnum):
    UNKNOWN = 0
    UNEXPECTED_FAILED = 10
    SUCCESS = 100
    ALREADY_KNOWN = 101
    NONCE_TOO_LOW = 102

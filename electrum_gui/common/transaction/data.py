from enum import IntEnum, unique

from electrum_gui.common.provider.data import TransactionStatus


@unique
class TxActionStatus(IntEnum):
    UNEXPECTED_FAILED = -2
    UNKNOWN = -1
    PENDING = 10
    REPLACED = 20
    CONFIRM_REVERTED = 99
    CONFIRM_SUCCESS = 100

    @classmethod
    def to_choices(cls):
        return (
            (cls.UNEXPECTED_FAILED, "Unexpected Failed"),
            (cls.PENDING, "Pending"),
            (cls.REPLACED, "Replaced"),
            (cls.CONFIRM_REVERTED, "Confirm Reverted"),
            (cls.CONFIRM_SUCCESS, "Confirmed Success"),
        )


TX_TO_ACTION_STATUS_DIRECT_MAPPING = {
    TransactionStatus.PENDING: TxActionStatus.PENDING,
    TransactionStatus.CONFIRM_SUCCESS: TxActionStatus.CONFIRM_SUCCESS,
    TransactionStatus.CONFIRM_REVERTED: TxActionStatus.CONFIRM_REVERTED,
}

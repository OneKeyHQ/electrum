from electrum_gui.common.provider.data import TxBroadcastReceipt, TxBroadcastReceiptCode

_MAPPING = {
    "already known": TxBroadcastReceiptCode.ALREADY_KNOWN,
    "nonce too low": TxBroadcastReceiptCode.NONCE_TOO_LOW,
    "transaction underpriced": TxBroadcastReceiptCode.RBF_UNDERPRICE,
    "gas too low": TxBroadcastReceiptCode.ETH_GAS_PRICE_TOO_LOW,
    "gas limit exceeded": TxBroadcastReceiptCode.ETH_GAS_LIMIT_EXCEEDED,
}


def populate_error_broadcast_receipt(error_message: str) -> TxBroadcastReceipt:
    receipt_code = TxBroadcastReceiptCode.UNEXPECTED_FAILED

    for keywords, code in _MAPPING.items():
        if keywords in error_message:
            receipt_code = code
            break

    is_success = receipt_code == TxBroadcastReceiptCode.ALREADY_KNOWN
    return TxBroadcastReceipt(
        is_success=is_success,
        receipt_code=receipt_code,
        receipt_message=error_message,
    )

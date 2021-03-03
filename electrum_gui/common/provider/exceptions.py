from typing import Any


class TransactionNotFound(Exception):
    def __init__(self, txid: str):
        super(TransactionNotFound, self).__init__(repr(txid))
        self.txid = txid


class NoAvailableProvider(Exception):
    def __init__(self, chain_code: str, providers: list, instance_required: Any):
        super(NoAvailableProvider, self).__init__(
            f"chain_code: {repr(chain_code)}, providers: {providers}, instance_required: {instance_required}"
        )

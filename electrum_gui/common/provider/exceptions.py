from typing import Any


class TransactionNotFound(Exception):
    def __init__(self, txid: str):
        super(TransactionNotFound, self).__init__(repr(txid))
        self.txid = txid


class NoAvailableClient(Exception):
    def __init__(self, chain_code: str, candidates: list, instance_required: Any):
        super(NoAvailableClient, self).__init__(
            f"chain_code: {repr(chain_code)}, candidates: {candidates}, instance_required: {instance_required}"
        )


class ProviderClassNotFound(Exception):
    def __init__(self, chain_code: str, path: str):
        super(ProviderClassNotFound, self).__init__(f"chain_code: {repr(chain_code)}, path: {path}")

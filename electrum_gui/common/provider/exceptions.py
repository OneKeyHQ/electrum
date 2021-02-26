class TransactionNotFound(Exception):
    def __init__(self, txid: str):
        super(TransactionNotFound, self).__init__(repr(txid))
        self.txid = txid


class ProviderNotFound(Exception):
    def __init__(self, chain_code: str):
        super(ProviderNotFound, self).__init__(repr(chain_code))


class ProvidersAllDown(Exception):
    def __init__(self, chain_code: str, candidates: list):
        super(ProvidersAllDown, self).__init__(f"{repr(chain_code)}, candidates: {candidates}")

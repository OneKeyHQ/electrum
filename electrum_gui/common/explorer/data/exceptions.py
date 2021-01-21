class ExplorerException(Exception):
    pass


class TransactionNotFound(ExplorerException):
    def __init__(self, txid: str):
        super(TransactionNotFound, self).__init__(f"Transaction {repr(txid)} not found")
        self.txid = txid

class ChainNotFound(Exception):
    def __init__(self, chain_code: str):
        super(ChainNotFound, self).__init__(repr(chain_code))


class CoinNotFound(Exception):
    def __init__(self, coin_code: str):
        super(CoinNotFound, self).__init__(repr(coin_code))

from threading import Timer

class CreateWalletInfo():
    def __init__(self):
        self.clear_info()

    def clear_info(self):
        self.seed = ''
        self.wallet_info = []
        self.derived_info = []

    def add_seed(self, seed):
        self.seed = seed

    # def add_wallet_info(self, coin_type, name):
    #     info = {"coin_type":coin_type, "name":name}
    #     self.wallet_info.append(info)

    @staticmethod
    def create_wallet_info(coin_type, name):
        return [{'coin_type':coin_type, 'name':name}]

    def add_derived_info(self, derived_info):
        if derived_info is not None:
            self.derived_info = derived_info

    def add_wallet_info(self, wallet_info):
        if wallet_info is not None:
            self.wallet_info = wallet_info

    def to_json(self):
        d = {
            'seed': self.seed,
            'wallet_info': self.wallet_info,
            'derived_info': self.derived_info,
        }
        return d

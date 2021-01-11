from threading import Timer
from electrum.i18n import _
SUPPORT_COIN = ['btc', 'eth', 'bsc']

class SortWalletList():
    def __init__(self, wallet_list):
        self.btc_list = {}
        self.eth_list = {}
        self.bsc_list = {}
        self.init_coin_list(wallet_list)

    def init_coin_list(self, wallet_list):
        for wallet in wallet_list:
            for key, info in wallet.items():
                if info['type'][0:3] == 'btc':
                    self.btc_list[key] = info
                elif info['type'][0:3] == 'eth':
                    self.eth_list[key] = info
                elif info['type'][0:3] == 'bsc':
                    self.bsc_list[key] = info

    def sort_wallet_by_hd(self, wallets_info):
        hd_wallets = {}

        for key, info in wallets_info.items():
            if -1 != info['type'].find('derived'):
                hd_wallets[key] = info
        return hd_wallets

    def get_wallets_by_hd(self):
        hd_wallets_btc = {}
        hd_wallets_eth = {}
        hd_wallets_bsc = {}

        hd_wallets_btc = self.sort_wallet_by_hd(self.btc_list)
        hd_wallets_eth = self.sort_wallet_by_hd(self.eth_list)
        hd_wallets_bsc = self.sort_wallet_by_hd(self.bsc_list)

        return {**hd_wallets_btc, **hd_wallets_eth, **hd_wallets_bsc}

    def sort_wallet_by_coin(self, wallets_info):
        hd_wallets = {}
        standalone_wallets = {}
        hw_wallets = {}
        import_walletw = {}
        watchonly_wallets = {}
        for key, info in wallets_info.items():
            if -1 != info['type'].find('hw'):
                hw_wallets[key] = info
            elif -1 != info['type'].find('private'):
                import_walletw[key] = info
            elif -1 != info['type'].find('derived'):
                hd_wallets[key] = info
            elif -1 != info['type'].find('watch'):
                watchonly_wallets[key] = info
            else:
                standalone_wallets[key] = info
        return {**hd_wallets, **standalone_wallets, **import_walletw, **hw_wallets, **watchonly_wallets}


    def get_wallets_by_coin(self, coin):
        if coin == "btc":
            return self.sort_wallet_by_coin(wallets_info=self.btc_list)
        elif coin == "eth":
            return self.sort_wallet_by_coin(wallets_info=self.eth_list)
        elif coin == "bsc":
            return self.sort_wallet_by_coin(wallets_info=self.bsc_list)
        else:
            raise BaseException(_("Unsupported coin types"))
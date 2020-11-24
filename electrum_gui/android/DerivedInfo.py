from threading import Timer

RECOVERY_DERIVAT_NUM = 20

class DerivedInfo():
    def __init__(self, config, coin):
        self.coin = coin
        self.config = config
        self.hd_wallet = None
        self.derived_account_id = self.init_list()
        self.recovery_num = []

    def update_recovery_info(self, accound_id):
        self.recovery_num.append(accound_id)

    def init_recovery_num(self):
        self.recovery_num = []

    def clear_recovery_info(self):
        self.recovery_num.clear()

    def get_coin(self):
        return self.coin

    def set_hd_wallet(self, hd_wallet):
        self.hd_wallet = hd_wallet

    def get_hd_wallet(self):
        return self.hd_wallet

    def get_list(self):
        return self.derived_account_id

    def init_list(self):
        account_list = self.config.get('%s_derived_account_id_num' % self.coin, [])
        if len(account_list) == 0:
            account_list = [i + 1 for i in range(RECOVERY_DERIVAT_NUM)]
            self.config.set_key('%s_derived_account_id_num' % self.coin, account_list)
        return account_list

    def reset_list(self):
        self.derived_account_id.clear()
        self.derived_account_id = [i + 1 for i in range(RECOVERY_DERIVAT_NUM)]
        for i in self.recovery_num:
            self.update_list(i)
        self.clear_recovery_info()

    def update_list(self, account_id):
        if self.derived_account_id.__contains__(account_id):
            self.derived_account_id.remove(account_id)
            self.config.set_key('%s_derived_account_id_num' % self.coin, self.derived_account_id)
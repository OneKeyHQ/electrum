from threading import Timer

RECOVERY_DERIVAT_NUM = 20

class DerivedInfo():
    def __init__(self, config):
        self.config = config
        self.derived_account_id = self.init_list()
        self.recovery_num = []

    def update_recovery_info(self, accound_id):
        self.recovery_num.append(accound_id)

    def init_recovery_num(self):
        self.recovery_num = []

    def clear_recovery_info(self):
        self.recovery_num.clear()

    def get_list(self):
        return self.derived_account_id

    def init_list(self):
        account_list = [i for i in range(RECOVERY_DERIVAT_NUM)]
        return account_list

    def reset_list(self):
        self.derived_account_id.clear()
        self.derived_account_id = [i for i in range(RECOVERY_DERIVAT_NUM)]
        for i in self.recovery_num:
            self.update_list(i)
        self.clear_recovery_info()

    def update_list(self, account_id):
        if self.derived_account_id.__contains__(int(account_id)):
            self.derived_account_id.remove(int(account_id))

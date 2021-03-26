DEFAULT_SW_DERIVED_NUM = 20
DEFAULT_HW_DERIVED_NUM = 10000


class DerivedInfo:
    def __init__(self, config, hw):
        self.config = config
        self._max_num = DEFAULT_HW_DERIVED_NUM if hw else DEFAULT_SW_DERIVED_NUM
        self._derived_account_id = []
        self._recovery_num = []

    @property
    def recovery_num(self):
        return self._recovery_num

    def update_recovery_info(self, accound_id):
        self._recovery_num.append(int(accound_id))

    def _clear_recovery_info(self):
        self._recovery_num.clear()

    def get_list(self):
        return self._derived_account_id

    def reset_list(self):
        self._derived_account_id = [i for i in range(self._max_num)]
        for account_id in self._recovery_num:
            if account_id not in self._derived_account_id:
                continue
            self._derived_account_id.remove(account_id)
        self._clear_recovery_info()

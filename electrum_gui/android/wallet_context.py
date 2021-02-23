import itertools
import os
import time
from typing import Generator, List, Tuple

from electrum import simple_config, util
from electrum_gui.android import derived_info


class WalletContext(object):
    def __init__(self, config: simple_config.SimpleConfig, user_dir: str) -> None:
        # NOTE: we use user config to store these infomation, should consider
        # using database instead.
        self.config = config

        # TODO: This wallets_dir stuff should be moved into the storage submodule,
        # as well as the stored_wallets property below.
        self.wallets_dir = util.standardize_path(os.path.join(user_dir, 'wallets'))
        util.make_dir(self.wallets_dir)

        # NOTE: The values of the following dict are of type dict. However only
        # the 'type' value in it is meaningful, therefore name it _type_info.
        # Details of self._type_info:
        #   Key: sha256 of the first address of the wallet, see
        #        AndroidCommands.get_unique_path() in console.py
        #   Value: a dict, explained below:
        #          'type': a str indicating the wallet type
        #          'time': a timestamp used only to sort items in
        #                  self._type_info, see self.get_stored_wallets_types()
        #          'xpubs': always [],  TODO: should be removed?
        #          'seed': always '',  TODO: should be removed?
        self._type_info = self._init_type_info()
        self._save_type_info()

        # Details of self._backup_info:
        #   Key: a xpub (wallet.keystore.xpub)
        #        or xpub + lowercase coin name (see
        #        AndroidCommands.get_hd_wallet_encode_seed() in console.py)
        #   Value: always False
        self._backup_info = self.config.get('backupinfo', {})
        # Details of self._derived_info:
        #   Key: a xpub or xpub + lowercase coin name, same as self._backup_info
        #   Value: a dict with keys are 'name' and 'account_id'
        self._derived_info = self.config.get('derived_info', {})

    @property
    def stored_wallets(self):
        return set(os.listdir(self.wallets_dir))

    def _init_type_info(self) -> dict:
        stored_wallets = self.stored_wallets
        saved_info = self.config.get('all_wallet_type_info', {})
        new_info = {}

        for address_digest, info in saved_info.items():
            if address_digest not in stored_wallets:
                continue
            if 'time' not in info:
                info['time'] = time.time()
            if 'xpubs' not in info:
                info['xpubs'] = []
            info['seed'] = ''
            new_info[address_digest] = info

        return new_info

    def _save_type_info(self) -> None:
        self.config.set_key('all_wallet_type_info', self._type_info)

    def clear_type_info(self) -> None:
        self._type_info = {}
        self._save_type_info()

    def get_stored_wallets_types(self) -> List[Tuple[str, str]]:
        stored_wallets = self.stored_wallets

        unknowns = list(zip(stored_wallets - set(self._type_info.keys()), itertools.repeat('unknow')))

        saved_type_info = {
            address_digest: type_info
            for address_digest, type_info in self._type_info.items()
            if address_digest in stored_wallets
        }
        saved = [
            (kv[0], kv[1]['type']) for kv in sorted(saved_type_info.items(), key=lambda kv: kv[1]['time'], reverse=True)
        ]

        return unknowns + saved

    def set_wallet_type(self, address_digest: str, wallet_type: str) -> None:
        self._type_info[address_digest] = {'type': wallet_type, 'time': time.time(), 'seed': ''}
        self._save_type_info()

    def remove_type_info(self, address_digest: str) -> None:
        if self._type_info.pop(address_digest, None) is not None:
            self._save_type_info()

    def is_hd(self, address_digest: str) -> bool:
        wallet_type = self._type_info.get(address_digest, {}).get('type', '')
        return '-hw-' not in wallet_type and ('-hd-' in wallet_type or '-derived-' in wallet_type)

    def is_derived(self, address_digest: str) -> bool:
        wallet_type = self._type_info.get(address_digest, {}).get('type', '')
        return '-derived-' in wallet_type

    def is_hw(self, address_digest: str) -> bool:
        wallet_type = self._type_info.get(address_digest, {}).get('type', '')
        return '-hw-' in wallet_type

    def _save_backup_info(self) -> None:
        self.config.set_key('backupinfo', self._backup_info)

    def clear_backup_info(self) -> None:
        self._backup_info = {}
        self._save_backup_info()

    def set_backup_info(self, xpub: str) -> None:
        if xpub not in self._backup_info:
            self._backup_info[xpub] = False
            self._save_backup_info()

    def get_backup_flag(self, xpub: str) -> bool:
        return xpub not in self._backup_info

    def remove_backup_info(self, xpub: str) -> None:
        if self._backup_info.pop(xpub, None) is not None:
            self._save_backup_info()

    def _save_derived_info(self) -> None:
        self.config.set_key('derived_info', self._derived_info)

    def clear_derived_info(self) -> None:
        self._derived_info = {}
        self._save_derived_info()

    def iter_derived_wallets(self, xpub: str) -> Generator[dict, None, None]:
        for derived_wallet in self._derived_info.get(xpub, []):
            yield derived_wallet

    def get_derived_num(self, xpub: str) -> int:
        return len(self._derived_info.get(xpub, []))

    def add_derived_wallet(self, xpub: str, name: str, account_id: str) -> None:
        if xpub not in self._derived_info:
            self._derived_info[xpub] = []
        elif self.get_derived_num(xpub) > derived_info.RECOVERY_DERIVAT_NUM:  # too many wallets
            return
        elif account_id in (w['account_id'] for w in self.iter_derived_wallets(xpub)):  # already present
            return

        self._derived_info[xpub].append({'name': name, 'account_id': account_id})
        self._save_derived_info()

    def remove_derived_wallet(self, xpub: str, account_id: str) -> None:
        wallets_before_delete = self._derived_info.get(xpub, [])
        if not wallets_before_delete:
            return

        new_wallets = [wallet for wallet in wallets_before_delete if wallet['account_id'] != account_id]

        if len(new_wallets) < len(wallets_before_delete):
            self._derived_info[xpub] = new_wallets
            self._save_derived_info()

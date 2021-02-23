import itertools
import os
import time
from typing import List, Tuple

from electrum import simple_config, util


class WalletContext(object):
    def __init__(self, config: simple_config.SimpleConfig, user_dir: str) -> None:
        self.config = config

        # TODO: This wallets_dir stuff should be moved into the storage submodule,
        # as well as the stored_wallets property below.
        self.wallets_dir = util.standardize_path(os.path.join(user_dir, 'wallets'))
        util.make_dir(self.wallets_dir)

        # NOTE: The values of the following dict are of type dict. However only
        # the 'type' value in it is meaningful, therefore name it _type_info.
        self._type_info = self._init_type_info()
        self._save_type_info()

        self._backup_info = self.config.get('backupinfo', {})

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

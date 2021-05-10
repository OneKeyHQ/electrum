import functools
import itertools
import os
import re
import time
from typing import Generator, List, Optional, Set, Tuple

from electrum import simple_config, util

_COIN_PRIORITY = {
    "btc": 3,
    "eth": 2,
    "bsc": 1,
    "heco": 1,
    "okt": 1,
}

_WALLET_TYPE_DIVIDE_PATTERN = re.compile(r"[_\-]")


def split_coin_from_wallet_type(wallet_type: str) -> str:
    return re.split(_WALLET_TYPE_DIVIDE_PATTERN, wallet_type)[0]


def _get_wallet_priority_by_type(wallet_type: str) -> int:
    if "hw" in wallet_type:
        return 2
    elif "private" in wallet_type:
        return 3
    elif "derived" in wallet_type:
        return 5
    elif "watch" in wallet_type:
        return 1
    else:
        return 4


def _type_info_basic_filter_func(wallet_id: str, stored_wallets: Set[str]) -> bool:
    return wallet_id in stored_wallets and ".tmp." not in wallet_id and ".tmptest." not in wallet_id


def _type_info_default_extra_filter_func(_wallet_type: str) -> bool:
    return True


def _type_info_wallet_type_is_hd(wallet_type: str) -> bool:
    return '-hw-' not in wallet_type and '-derived-' in wallet_type


def _type_info_wallet_type_is_hw(wallet_type: str) -> bool:
    return '-hw-' in wallet_type


def _type_info_filter_wallet_type_by_coin(coin: str, wallet_type: str) -> bool:
    return split_coin_from_wallet_type(wallet_type) == coin


def _type_info_default_sort_func(type_info_item: Tuple[str, dict]) -> float:
    return type_info_item[1]["time"]


def _type_info_by_coin_and_time_sort_func(type_info_item: Tuple[str, dict]) -> Tuple[int, float]:
    return _COIN_PRIORITY.get(split_coin_from_wallet_type(type_info_item[1]["type"]), 0), type_info_item[1]["time"]


def _type_info_by_type_and_time_sort_func(type_info_item: Tuple[str, dict]) -> Tuple[int, float]:
    return _get_wallet_priority_by_type(type_info_item[1]["type"]), type_info_item[1]["time"]


def _type_info_by_customised_sort_func(type_info_item: Tuple[str, dict]) -> int:
    return type_info_item[1]["order"]


class WalletContext(object):
    def __init__(self, config: simple_config.SimpleConfig, user_dir: str) -> None:
        # NOTE: we use user config to store these information, should consider
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
        # Details of self._wallet_location_info:
        #   Key: wallet_type
        #   Value: a list with wallet location information
        self._wallet_location_info = self.config.get('wallet_location_info', {})

    @property
    def stored_wallets(self):
        return set(os.listdir(self.wallets_dir))

    def _init_type_info(self) -> dict:
        stored_wallets = self.stored_wallets
        saved_info = self.config.get('all_wallet_type_info', {})
        new_info = {}

        for wallet_id, info in saved_info.items():
            if wallet_id not in stored_wallets:
                continue
            if 'time' not in info:
                info['time'] = time.time()
            if 'xpubs' not in info:
                info['xpubs'] = []
            new_info[wallet_id] = info

        return new_info

    def _save_type_info(self) -> None:
        self.config.set_key('all_wallet_type_info', self._type_info)

    def clear_type_info(self) -> None:
        self._type_info = {}
        self._save_type_info()

    def get_stored_wallets_types(
        self, generic_wallet_type: Optional[str] = None, coin: Optional[str] = None
    ) -> List[Tuple[str, str]]:

        unknowns = []

        stored_wallets = self.stored_wallets
        extra_filter_func = _type_info_default_extra_filter_func
        sort_func = _type_info_default_sort_func
        order_info = {}
        if generic_wallet_type is not None:
            # Sort by coin (btc > eth > bsc > ...)
            # Filter by generic wallet type
            sort_func = _type_info_by_coin_and_time_sort_func
            order_info = dict(zip(self.get_wallet_location_info(generic_wallet_type), itertools.count()))
            if generic_wallet_type == "hd":
                extra_filter_func = _type_info_wallet_type_is_hd
            elif generic_wallet_type == "hw":
                extra_filter_func = _type_info_wallet_type_is_hw
        elif coin is not None:
            # Filter by coin
            # Sort by wallet_type (derived > normal > private > hw > watch)
            extra_filter_func = functools.partial(_type_info_filter_wallet_type_by_coin, coin)
            sort_func = _type_info_by_type_and_time_sort_func
            order_info = dict(zip(self.get_wallet_location_info(coin), itertools.count()))
        else:
            # We want all stored wallets.
            unknowns = list(zip(stored_wallets - set(self._type_info.keys()), itertools.repeat('unknow')))

        saved_wallets = {}
        for wallet_id, type_info in self._type_info.items():
            if not (_type_info_basic_filter_func(wallet_id, stored_wallets) and extra_filter_func(type_info["type"])):
                continue
            saved_wallet_info = dict(**type_info, order=order_info.get(wallet_id, 1e10))
            saved_wallets[wallet_id] = saved_wallet_info

        saved = [kv for kv in sorted(saved_wallets.items(), key=sort_func, reverse=True)]
        saved = [(kv[0], kv[1]['type']) for kv in sorted(saved, key=_type_info_by_customised_sort_func)]
        return unknowns + saved

    def set_wallet_type(self, wallet_id: str, wallet_type: str) -> None:
        self._type_info[wallet_id] = {'type': wallet_type, 'time': time.time()}
        self._save_type_info()

    def remove_type_info(self, wallet_id: str) -> None:
        if self._type_info.pop(wallet_id, None) is not None:
            self._save_type_info()

    def is_hd(self, wallet_id: str) -> bool:
        # Essentially, this method equals a simple return of:
        # return not self.is_hw(wallet_id) and self.is_derived(wallet_id)
        wallet_type = self._type_info.get(wallet_id, {}).get('type', '')
        return _type_info_wallet_type_is_hd(wallet_type)

    def is_derived(self, wallet_id: str) -> bool:
        wallet_type = self._type_info.get(wallet_id, {}).get('type', '')
        return '-derived-' in wallet_type

    def is_hw(self, wallet_id: str) -> bool:
        wallet_type = self._type_info.get(wallet_id, {}).get('type', '')
        return _type_info_wallet_type_is_hw(wallet_type)

    def _save_wallet_location_info(self) -> None:
        self.config.set_key('wallet_location_info', self._wallet_location_info)

    def set_wallet_location_info(self, location_info: list, wallet_type: str) -> None:
        if self.get_wallet_location_info(wallet_type) != location_info:
            self._wallet_location_info[wallet_type] = location_info
            self._save_wallet_location_info()

    def get_wallet_location_info(self, wallet_type: str) -> list:
        return self._wallet_location_info.get(wallet_type, [])

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

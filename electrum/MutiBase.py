# -*- coding: utf-8 -*-
#
# Electrum - lightweight Bitcoin client
# Copyright (C) 2016 Thomas Voegtlin
#
# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation files
# (the "Software"), to deal in the Software without restriction,
# including without limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of the Software,
# and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

import os
from typing import List, TYPE_CHECKING, Tuple, NamedTuple, Any, Dict, Optional

from . import bitcoin
from . import keystore
from . import mnemonic
from .bip32 import is_bip32_derivation, xpub_type, normalize_bip32_derivation, BIP32Node, root_fp_and_der_prefix_from_xkey
from .keystore import purpose48_derivation, bip44_derivation
from .wallet import (Imported_Wallet, Standard_Wallet, Multisig_Wallet,
                     wallet_types, Wallet, Abstract_Wallet)
from .storage import (WalletStorage,
                      get_derivation_used_for_hw_device_encryption)
from .i18n import _
from .util import UserCancelled, InvalidPassword, WalletFileException
from .logging import Logger
from .simple_config import SimpleConfig
from .wallet_db import WalletDB

class ScriptTypeNotSupported(Exception): pass

class MutiBase(Logger):

    def __init__(self, config: SimpleConfig):
        super(MutiBase, self).__init__()
        Logger.__init__(self)
        self.config = config
        self.data = {}
        self.pw_args = None
        self.keystores = []
        self.seed_type = None

    def get_keystores_info(self):
        return [k for k in map(lambda x: x.xpub, self.keystores)]


    def delete_xpub(self, xpub):
        find = False
        for pos, pub in enumerate(map(lambda x: x.xpub, self.keystores)):
            if pub == xpub:
                find = True
                self.keystores.pop(pos)
                break

        if not find:
            raise Exception("the xpub to be delete not in keystore")

    def set_multi_wallet_info(self, path, m, n):
        if n == 1 and m == 1:
            self.wallet_type = 'standard'
            self.data['wallet_type'] = 'standard'
        else:
            self.wallet_type = 'multisig'
            multisig_type = "%dof%d" % (m, n)
            self.data['wallet_type'] = multisig_type
            self.n = n
            self.m = m
        self.path = path
        print("=================set_multi_wallet_info ok....")

    @staticmethod
    def get_eth_keystore(xpub, coin, device_id=''):
        from .keystore import hardware_keystore
        print("restore_from_xpub in....")
        is_valid = keystore.is_bip32_key(xpub)
        if is_valid:
            print("valid is true....")
            try:
                derivation = bip44_derivation(0, bip43_purpose=44, coin=coin)
                d = {
                    'type': 'hardware',
                    'hw_type': 'trezor',
                    'derivation': derivation,
                    'xpub': xpub,
                    'label': 'device_info.label',
                    'device_id': device_id,
                }
                k = hardware_keystore(d)
                return k
            except Exception as e:
                raise e
        else:
            raise Exception("invaild type of xpub")

    def restore_from_xpub(self, xpub, device_id):
        from .keystore import hardware_keystore
        print("restore_from_xpub in....")
        is_valid = keystore.is_bip32_key(xpub)
        if is_valid:
            print("valid is true....")
            #k = keystore.from_master_key(xpub)
            try:
                if self.wallet_type == 'multisig':
                    derivation = purpose48_derivation(0, xtype='p2wsh')
                    #derivation = bip44_derivation(0, bip43_purpose=48)
                else:
                    derivation = bip44_derivation(0, bip43_purpose=84)
                d = {
                    'type': 'hardware',
                    'hw_type': 'trezor',
                    'derivation': derivation,
                    'xpub': xpub,
                    'label': 'device_info.label',
                    'device_id': device_id,
                }
                k = hardware_keystore(d)
                self.on_keystore(k)
            except Exception as e:
                raise e
        else:
            raise BaseException(_("Unavailable xpub"))

    def on_restore_seed(self, seed, is_bip39, is_ext):
        self.seed_type = 'bip39' if is_bip39 else mnemonic.seed_type(seed)
        if self.seed_type == 'bip39':
            f = lambda passphrase: self.on_restore_bip39(seed, passphrase)
            self.passphrase_dialog(run_next=f, is_restoring=True) if is_ext else f('')
        elif self.seed_type in ['standard', 'segwit']:
            f = lambda passphrase: self.run('create_keystore', seed, passphrase)
            self.passphrase_dialog(run_next=f, is_restoring=True) if is_ext else f('')
        elif self.seed_type == 'old':
            self.run('create_keystore', seed, '')
        elif mnemonic.is_any_2fa_seed_type(self.seed_type):
            self.load_2fa()
            self.run('on_restore_seed', seed, is_ext)
        else:
            raise Exception('Unknown seed type', self.seed_type)

    def on_restore_bip39(self, seed, passphrase):
        def f(derivation, script_type):
            derivation = normalize_bip32_derivation(derivation)
            self.run('on_bip43', seed, passphrase, derivation, script_type)
        self.derivation_and_script_type_dialog(f)

    def create_keystore(self, seed, passphrase):
        k = keystore.from_seed(seed, passphrase, self.wallet_type == 'multisig')
        return self.on_keystore(k)

    def on_bip43(self, seed, passphrase, derivation, script_type):
        k = keystore.from_bip39_seed(seed, passphrase, derivation, xtype=script_type)
        self.on_keystore(k)

    def on_keystore(self, k):
        has_xpub = isinstance(k, keystore.Xpub)
        if has_xpub:
            t1 = xpub_type(k.xpub)
        if self.wallet_type == 'standard':
            if has_xpub and t1 not in ['standard', 'p2wpkh', 'p2wpkh-p2sh']:
                raise Exception(_('Wrong key type') + ' %s'%t1)
            self.keystores.append(k)
            #self.run('create_wallet')
        elif self.wallet_type == 'multisig':
            assert has_xpub
            if t1 not in ['standard', 'p2wsh', 'p2wsh-p2sh']:
                raise Exception(('Wrong key type') + ' %s' % t1)
            if k.xpub in map(lambda x: x.xpub, self.keystores):
                raise Exception('Error: duplicate master public key')
            if len(self.keystores)<self.n:
                self.keystores.append(k)
            else:
                raise Exception("len(xpub) > n")

    def get_cosigner_num(self):
        return self.m,self.n

    def create_storage(self,path, password, hide_type=False, encrypt_storage=False, storage_enc_version=None):
        encrypt_keystore = any(k.may_have_password() for k in self.keystores)

        if self.wallet_type == 'standard':
            #self.data['seed_type'] = self.seed_type
            self.data['seed_type'] = 'segwit'
            keys = self.keystores[0].dump()
            self.data['keystore'] = keys
        elif self.wallet_type == 'multisig':
            for i, k in enumerate(self.keystores):
                self.data['x%d/'%(i+1)] = k.dump()
        elif self.wallet_type == 'imported':
            if len(self.keystores) > 0:
                keys = self.keystores[0].dump()
                self.data['keystore'] = keys
        else:
            raise Exception('Unknown wallet type')

        self.pw_args = path, password, encrypt_storage, storage_enc_version

        if len(self.keystores) == 0:
            raise Exception('keystores is empty')
        if not self.pw_args:
            raise Exception("args wrong")

        storage = WalletStorage(path)
        if encrypt_storage:
            storage.set_password(password, enc_version=storage_enc_version)
        db = WalletDB('', manual_upgrades=False)
        db.set_keystore_encryption(bool(password) and encrypt_keystore)
        for key, value in self.data.items():
            db.put(key, value)
        db.load_plugins()
        if not hide_type:
            db.write(storage)
        return storage, db
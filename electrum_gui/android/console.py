from __future__ import absolute_import, division, print_function

import binascii
import copy
from code import InteractiveConsole
import json
import os
import time
from decimal import Decimal
from os.path import exists, join
import pkgutil
import unittest
import threading
import random
import string
from electrum.bitcoin import base_decode, is_address
from electrum.keystore import bip44_derivation, bip44_eth_derivation, purpose48_derivation
from electrum.plugin import Plugins
from electrum.plugins.trezor.clientbase import TrezorClientBase
from electrum.transaction import PartialTransaction, Transaction, TxOutput, PartialTxOutput, tx_from_any, TxInput, \
    PartialTxInput
from electrum import commands, daemon, keystore, simple_config, storage, util, bitcoin
from electrum.util import bfh, Fiat, create_and_start_event_loop, decimal_point_to_base_unit_name, standardize_path
from electrum.util import user_dir as get_dir
from electrum import MutiBase
from electrum.i18n import _
from electrum.storage import WalletStorage
#from electrum.eth_wallet import (Imported_Eth_Wallet, Standard_Eth_Wallet, Eth_Wallet)
from electrum.wallet import (Imported_Wallet, Standard_Wallet, Wallet, Deterministic_Wallet)
from electrum.bitcoin import is_address, hash_160, COIN, TYPE_ADDRESS
from electrum.interface import ServerAddr
from electrum import mnemonic
from electrum.mnemonic import Wordlist
from mnemonic import Mnemonic
from electrum.address_synchronizer import TX_HEIGHT_LOCAL, TX_HEIGHT_FUTURE
from electrum.bip32 import BIP32Node, convert_bip32_path_to_list_of_uint32 as parse_path
from electrum.network import Network, TxBroadcastError, BestEffortRequestFailed
from electrum import ecc
#from electrum.pywalib import InvalidPasswordException
#from electrum.pywalib import ChainID, PyWalib, ERC20NotExistsException, InvalidValueException, InvalidAddress, InsufficientFundsException
from trezorlib.customer_ui import CustomerUI
from trezorlib import (
    btc,
    exceptions,
    firmware,
    protobuf,
    messages,
    device,
)
from trezorlib.cli import trezorctl
from electrum.wallet_db import WalletDB
from enum import Enum
from .firmware_sign_nordic_dfu import parse
from electrum import constants

COIN_POS = 2
ACCOUNT_POS = 3
RECOVERY_DERIVAT_NUM = 20

class Status(Enum):
    net = 1
    broadcast = 2
    sign = 3
    update_wallet = 4
    update_status = 5
    update_history = 6
    update_interfaces = 7
    create_wallet_error = 8


class AndroidConsole(InteractiveConsole):
    """`interact` must be run on a background thread, because it blocks waiting for input.
    """

    def __init__(self, app, cmds):
        namespace = dict(c=cmds, context=app)
        namespace.update({name: CommandWrapper(cmds, name) for name in all_commands})
        namespace.update(help=Help())
        InteractiveConsole.__init__(self, locals=namespace)

    def interact(self):
        try:
            InteractiveConsole.interact(
                self, banner=(
                        _("WARNING!") + "\n" +
                        _("Do not enter code here that you don't understand. Executing the wrong "
                          "code could lead to your coins being irreversibly lost.") + "\n" +
                        "Type 'help' for available commands and variables."))
        except SystemExit:
            pass


class CommandWrapper:
    def __init__(self, cmds, name):
        self.cmds = cmds
        self.name = name

    def __call__(self, *args, **kwargs):
        return getattr(self.cmds, self.name)(*args, **kwargs)


class Help:
    def __repr__(self):
        return self.help()

    def __call__(self, *args):
        print(self.help(*args))

    def help(self, name_or_wrapper=None):
        if name_or_wrapper is None:
            return ("Commands:\n" +
                    "\n".join(f"  {cmd}" for name, cmd in sorted(all_commands.items())) +
                    "\nType help(<command>) for more details.\n"
                    "The following variables are also available: "
                    "c.config, c.daemon, c.network, c.wallet, context")
        else:
            if isinstance(name_or_wrapper, CommandWrapper):
                cmd = all_commands[name_or_wrapper.name]
            else:
                cmd = all_commands[name_or_wrapper]
            return f"{cmd}\n{cmd.description}"


# Adds additional commands which aren't available over JSON RPC.
class AndroidCommands(commands.Commands):
    def __init__(self, android_id=None, config=None, user_dir=None, callback=None):
        self.asyncio_loop, self._stop_loop, self._loop_thread = create_and_start_event_loop()  # TODO:close loop
        config_options = {}
        config_options['auto_connect'] = True
        if config is None:
            self.config = simple_config.SimpleConfig(config_options)
        else:
            self.config = config
        if user_dir is None:
            self.user_dir = get_dir()
        else:
            self.user_dir = user_dir
        fd = daemon.get_file_descriptor(self.config)
        if not fd:
            raise BaseException("Daemon already running")  # Same wording as in daemon.py.

        # Initialize here rather than in start() so the DaemonModel has a chance to register
        # its callback before the daemon threads start.
        self.daemon = daemon.Daemon(self.config, fd)
        #self.pywalib = PyWalib(chain_id=ChainID.CUSTOMER)
        self.network = self.daemon.network
        self.daemon_running = False
        self.wizard = None
        self.plugin = Plugins(self.config, 'cmdline')
        self.label_plugin = self.plugin.load_plugin("labels")
        self.label_flag = self.config.get('use_labels', False)
        self.callbackIntent = None
        self.btc_hd_wallet = None
        self.eth_hd_wallet = None
        self.check_pw_wallet = None
        self.wallet = None
        self.client = None
        self.recovery_wallets = {}
        self.path = ''
        self.backup_info = self.config.get("backupinfo", {})
        self.derived_info = self.config.get("derived_info", dict())
        self.btc_derived_account_id = self.init_account_id_list('btc')
        self.eth_derived_account_id = self.init_account_id_list('eth')
        ran_str = self.config.get("ra_str", None)
        if ran_str is None:
            ran_str = ''.join(random.sample(string.ascii_letters + string.digits, 8))
            self.config.set_key("ra_str", ran_str)
        self.android_id = android_id + ran_str
        self.lock = threading.RLock()
        self.update_local_wallet_info()
        if self.network:
            interests = ['wallet_updated', 'network_updated', 'blockchain_updated',
                         'status', 'new_transaction', 'verified', 'set_server_status']
            util.register_callback(self.on_network_event, interests)
            util.register_callback(self.on_fee, ['fee'])
            #self.network.register_callback(self.on_fee_histogram, ['fee_histogram'])
            util.register_callback(self.on_quotes, ['on_quotes'])
            util.register_callback(self.on_history, ['on_history'])
        self.fiat_unit = self.daemon.fx.ccy if self.daemon.fx.is_enabled() else ''
        self.decimal_point = self.config.get('decimal_point', util.DECIMAL_POINT_DEFAULT)
        for k, v in util.base_units_inverse.items():
            if k == self.decimal_point:
                self.base_unit = v

        self.num_zeros = int(self.config.get('num_zeros', 0))
        self.config.set_key('log_to_file', True, save=True)
        self.rbf = self.config.get("use_rbf", True)
        self.ccy = self.daemon.fx.get_currency()
        self.pre_balance_info = ""
        self.addr_index = 0
        self.coins = list()
        self.rbf_tx = ""
        self.m = 0
        self.n = 0
        # self.sync_timer = None
        self.config.set_key('auto_connect', True, True)
        t = threading.Timer(5.0, self.timer_action)
        t.start()

        if 'iOS_DATA' in os.environ:
            from .custom_objc import *
            from .ioscallback import *
            self.my_handler = CallHandler.alloc().initWithValue(42)
            self.set_callback_fun(self.my_handler)
        elif 'ANDROID_DATA' in os.environ:
            if callback is not None:
                self.set_callback_fun(callback)
        self.start()

    def init_account_id_list(self, coin):
        account_list = self.config.get('%s_derived_account_id_num' %coin, [])
        if len(account_list) == 0:
            account_list = [i + 1 for i in range(RECOVERY_DERIVAT_NUM)]
            self.config.set_key('%s_derived_account_id_num' %coin, account_list)
        return account_list

    # BEGIN commands from the argparse interface.
    def stop_loop(self):
        self.asyncio_loop.call_soon_threadsafe(self._stop_loop.set_result, 1)
        self._loop_thread.join(timeout=1)

    def update_local_wallet_info(self):
        try:
            self.local_wallet_info = self.config.get("all_wallet_type_info", {})
            new_wallet_info = {}
            name_wallets = ([name for name in os.listdir(self._wallet_path())])
            for name, info in self.local_wallet_info.items():
                if name_wallets.__contains__(name):
                    if not info.__contains__('time'):
                        info['time'] = time.time()
                    if not info.__contains__('xpubs'):
                        info['xpubs'] = []
                    #if not info.__contains__('seed'):
                    info['seed'] = ""
                    new_wallet_info[name] = info
            self.local_wallet_info = new_wallet_info
            self.config.set_key('all_wallet_type_info', self.local_wallet_info)
        except BaseException as e:
            raise e

    def is_valiad_xpub(self, xpub):
        try:
            return keystore.is_bip32_key(xpub)
        except BaseException as e:
            raise e

    def on_fee(self, event, *arg):
        try:
            self.fee_status = self.config.get_fee_status()
        except BaseException as e:
            raise e

    # def on_fee_histogram(self, *args):
    #     self.update_history()

    def on_quotes(self, d):
        if self.wallet is not None and -1 == self.wallet.wallet_type.find("eth"):
            self.update_status()
        # self.update_history()

    def on_history(self, d):
        if self.wallet:
            self.wallet.clear_coin_price_cache()
        # self.update_history()

    def update_status(self):
        out = {}
        status = ''
        if not self.wallet:
            return
        if self.network is None or not self.network.is_connected():
            print("network is ========offline")
            status = _("Offline")
        elif self.network.is_connected():
            self.num_blocks = self.network.get_local_height()
            server_height = self.network.get_server_height()
            server_lag = self.num_blocks - server_height
            if not self.wallet.up_to_date or server_height == 0:
                num_sent, num_answered = self.wallet.get_history_sync_state_details()
                status = ("{} [size=18dp]({}/{})[/size]"
                          .format(_("Synchronizing..."), num_answered, num_sent))
            elif server_lag > 1:
                status = _("Server is lagging ({} blocks)").format(server_lag)
            else:
                c, u, x = self.wallet.get_balance()
                text = _("Balance") + ": %s " % (self.format_amount_and_units(c))
                out['balance'] = self.format_amount(c)
                out['fiat'] = self.daemon.fx.format_amount_and_units(c) if self.daemon.fx else None
                if u:
                    out['unconfirmed'] = self.format_amount(u, is_diff=True).strip()
                    text += " [%s unconfirmed]" % (self.format_amount(u, is_diff=True).strip())
                if x:
                    out['unmatured'] = self.format_amount(x, is_diff=True).strip()
                    text += " [%s unmatured]" % (self.format_amount(x, is_diff=True).strip())
                if self.wallet.lnworker:
                    l = self.wallet.lnworker.get_balance()
                    text += u'    \U0001f5f2 %s' % (self.format_amount_and_units(l).strip())

                # append fiat balance and price
                if self.daemon.fx.is_enabled():
                    text += self.daemon.fx.get_fiat_status_text(c + u + x, self.base_unit, self.decimal_point) or ''
           # print("update_statue out = %s" % (out))
            self.callbackIntent.onCallback("update_status=%s" %json.dumps(out))

    def get_remove_flag(self, tx_hash):
        height = self.wallet.get_tx_height(tx_hash).height
        if height in [TX_HEIGHT_FUTURE, TX_HEIGHT_LOCAL]:
            return True
        else:
            return False

    def remove_local_tx(self, delete_tx):
        '''
        :param delete_tx: tx_hash that you need to delete
        :return :
        '''
        to_delete = {delete_tx}
        to_delete |= self.wallet.get_depending_transactions(delete_tx)
        for tx in to_delete:
            self.wallet.remove_transaction(tx)
            self.delete_tx(tx)
        self.wallet.save_db()
        return ""
        # need to update at least: history_list, utxo_list, address_list
        # self.parent.need_update.set()

    def delete_tx(self, hash):
        try:
            if self.label_flag and self.wallet.wallet_type != "standard":
                self.label_plugin.push_tx(self.wallet, 'deltx', hash)
        except BaseException as e:
            if e != 'Could not decode:':
                print(f"push_tx delete_tx error {e}")
            pass

    def get_wallet_info(self):
        wallet_info = {}
        wallet_info['balance'] = self.balance
        wallet_info['fiat_balance'] = self.fiat_balance
        wallet_info['name'] = self.wallet.basename()
        return json.dumps(wallet_info)

    def update_interfaces(self):
        net_params = self.network.get_parameters()
        self.num_nodes = len(self.network.get_interfaces())
        self.num_chains = len(self.network.get_blockchains())
        chain = self.network.blockchain()
        self.blockchain_forkpoint = chain.get_max_forkpoint()
        self.blockchain_name = chain.get_name()
        interface = self.network.interface
        if interface:
            self.server_host = interface.host
        else:
            self.server_host = str(net_params.server.host) + ' (connecting...)'
        self.proxy_config = net_params.proxy or {}
        mode = self.proxy_config.get('mode')
        host = self.proxy_config.get('host')
        port = self.proxy_config.get('port')
        self.proxy_str = (host + ':' + port) if mode else _('None')
        # self.callbackIntent.onCallback("update_interfaces")

    def update_wallet(self):
        self.update_status()
        # self.callbackIntent.onCallback("update_wallet")

    def on_network_event(self, event, *args):
        if self.wallet is not None and -1 == self.wallet.wallet_type.find("eth"):
            if event == 'network_updated':
                self.update_interfaces()
                self.update_status()
            elif event == 'wallet_updated':
                self.update_status()
                self.update_wallet()
            elif event == 'blockchain_updated':
                # to update number of confirmations in history
                self.update_wallet()
            elif event == 'status':
                self.update_status()
            elif event == 'new_transaction':
                self.update_wallet()
            elif event == 'verified':
                self.update_wallet()
            elif event == 'set_server_status':
                if self.callbackIntent is not None:
                    self.callbackIntent.onCallback("set_server_status=%s" %args[0])

    def timer_action(self):
        if self.wallet is not None and -1 == self.wallet.wallet_type.find("eth"):
            self.update_wallet()
            self.update_interfaces()
            t = threading.Timer(5.0, self.timer_action)
            t.start()

    def daemon_action(self):
        self.daemon_running = True
        self.daemon.run_daemon()

    def start(self):
        t1 = threading.Thread(target=self.daemon_action)
        t1.setDaemon(True)
        t1.start()
        # import time
        # time.sleep(1.0)
        print("parent thread")

    def status(self):
        """Get daemon status"""
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        return self.daemon.run_daemon({"subcommand": "status"})

    def stop(self):
        """Stop the daemon"""
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        self.daemon.stop()
        self.daemon.join()
        self.daemon_running = False

    def get_wallet_type(self, name):
        return self.local_wallet_info.get(name)

    def load_wallet(self, name, password=None):
        '''
        load an wallet
        :param name: wallet name as a string
        :param password: the wallet password as a string
        :return:
        '''
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        path = self._wallet_path(name)
        wallet = self.daemon.get_wallet(path)
        if not wallet:
            storage = WalletStorage(path)
            if not storage.file_exists():
                raise BaseException("Not find file %s" % path)
            if storage.is_encrypted():
                if not password:
                    raise BaseException(util.InvalidPassword())
                storage.decrypt(password)
            db = WalletDB(storage.read(), manual_upgrades=False)
            if db.requires_split():
                return
            if db.requires_upgrade():
                return
            if db.get_action():
                return
            if -1 != db.get('wallet_type').find("eth"):
                print("load eth wallet")
                #wallet = Eth_Wallet(db, storage, config=self.config)
            else:
                wallet = Wallet(db, storage, config=self.config)
                wallet.start_network(self.network)
            if self.local_wallet_info.__contains__(name):
                if -1 != self.local_wallet_info.get(name)['type'].find('btc-hd'):
                    self.btc_hd_wallet = wallet
                    self.check_pw_wallet = self.btc_hd_wallet
                if -1 != self.local_wallet_info.get(name)['type'].find('eth-hd'):
                    self.eth_hd_wallet = wallet
            self.daemon.add_wallet(wallet)
        return ""

    def close_wallet(self, name=None):
        """Close a wallet"""
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        self.daemon.stop_wallet(self._wallet_path(name))
        return ""

    def set_syn_server(self, flag):
        '''
        Enable/disable sync server
        :param flag: flag as bool
        :return:
        '''
        try:
            self.label_flag = flag
            self.config.set_key('use_labels', bool(flag))
            if self.label_flag and self.wallet and self.wallet.wallet_type != "btc-standard" and self.wallet.wallet_type != "eth-standard":
                self.label_plugin.load_wallet(self.wallet)
        except Exception as e:
            raise BaseException(e)
        return ""
    # #set callback##############################
    def set_callback_fun(self, callbackIntent):
        self.callbackIntent = callbackIntent

    # craete multi wallet##############################
    def set_multi_wallet_info(self, name, m, n):
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        if self.wizard is not None:
            self.wizard = None
        self.wizard = MutiBase.MutiBase(self.config)
        path = self._wallet_path(name)
        print("console:set_multi_wallet_info:path = %s---------" % path)
        self.wizard.set_multi_wallet_info(path, m, n)
        self.m = m
        self.n = n

    def add_xpub(self, xpub, device_id=None):
        try:
            print(f"add_xpub........{xpub, device_id}")
            self._assert_daemon_running()
            self._assert_wizard_isvalid()
            if BIP32Node.from_xkey(xpub).xtype != 'p2wsh' and self.n >= 2:
                xpub = BIP32Node.get_p2wsh_from_other(xpub)
            self.wizard.restore_from_xpub(xpub, device_id)
        except Exception as e:
            raise BaseException(e)

    def get_device_info(self):
        '''
        Get hardware device info
        :return:
        '''
        try:
            self._assert_wallet_isvalid()
            device_info = self.wallet.get_device_info()
            return json.dumps(device_info)
        except BaseException as e:
            raise e
        return ""

    def delete_xpub(self, xpub):
        '''
        Delete xpub when create multi-signature wallet
        :param xpub: WIF pubkey
        :return:
        '''
        try:
            self._assert_daemon_running()
            self._assert_wizard_isvalid()
            self.wizard.delete_xpub(xpub)
        except Exception as e:
            raise BaseException(e)
        return ""

    def get_keystores_info(self):
        try:
            self._assert_daemon_running()
            self._assert_wizard_isvalid()
            ret = self.wizard.get_keystores_info()
        except Exception as e:
            raise BaseException(e)
        return ret

    def set_sync_server_host(self, ip, port):
        '''
        Set sync server host/port
        :param ip: the server host (exp..."127.0.0.1")
        :param port: the server port (exp..."port")
        :return:
        '''
        try:
            if self.label_flag:
                self.label_plugin.set_host(ip, port)
                self.config.set_key('sync_server_host', '%s:%s' % (ip, port))
        except BaseException as e:
            raise e
        return ""

    def get_sync_server_host(self):
        '''
        Get sync server host,you can pull label/xpubs/tx to server
        :return:
        '''
        try:
            return self.config.get('sync_server_host', "39.105.86.163:8080")
        except BaseException as e:
            raise e

    def get_cosigner_num(self):
        try:
            self._assert_daemon_running()
            self._assert_wizard_isvalid()
        except Exception as e:
            raise BaseException(e)
        return self.wizard.get_cosigner_num()

    def create_multi_wallet(self, name, hd=False, hide_type=False, derived=False):
        try:
            self._assert_daemon_running()
            self._assert_wizard_isvalid()
            path = self._wallet_path(name)
            wallet_type = "btc-hw-%s-%s" % (self.m, self.n)
            keystores = self.get_keystores_info()
            print(f"keystores---------------{keystores}")
            if not hide_type:
                for key, value in self.local_wallet_info.items():
                    num = 0
                    for xpub in keystores:
                        if value['xpubs'].__contains__(xpub):
                            num += 1
                    if num == len(keystores) and value['type'] == wallet_type:
                        raise BaseException(f"The same xpubs have create wallet, name={key}")
            storage, db = self.wizard.create_storage(path=path, password='', hide_type=hide_type)
        except Exception as e:
            raise BaseException(e)
        if storage:
            wallet = Wallet(db, storage, config=self.config)
            wallet.status_flag = ("btc-hw-%s-%s" % (self.m, self.n))
            wallet.hide_type = hide_type
            wallet.start_network(self.daemon.network)
            self.daemon.add_wallet(wallet)
            if hd:
                self.btc_hd_wallet = wallet
                self.check_pw_wallet = self.btc_hd_wallet
                wallet_type = "btc-hd-hw-%s-%s" % (self.m, self.n)
            elif derived:
                wallet_type = "btc-hw-derived-%s-%s" % (self.m, self.n)

            if not hide_type:
                wallet_info = {}
                wallet_info['type'] = wallet_type
                wallet_info['time'] = time.time()
                wallet_info['xpubs'] = keystores
                wallet_info['seed'] = ""
                self.local_wallet_info[name] = wallet_info
                self.config.set_key('all_wallet_type_info', self.local_wallet_info)
            # if self.wallet:
            #     self.close_wallet()
            self.wallet = wallet
            self.wallet_name = wallet.basename()
            print("console:create_multi_wallet:wallet_name = %s---------" % self.wallet_name)
            # self.select_wallet(self.wallet_name)
            if self.label_flag and not hide_type:
                wallet_name = ""
                if wallet_type[0:1] == '1':
                    wallet_name = name
                else:
                    wallet_name = "共管钱包"
                self.label_plugin.create_wallet(self.wallet, wallet_type, wallet_name)
        self.wizard = None

    def pull_tx_infos(self):
        try:
            self._assert_wallet_isvalid()
            if self.label_flag and self.wallet.wallet_type != "standard":
                data = self.label_plugin.pull_tx(self.wallet)
                data_list = json.loads(data)
                except_list = []
                data_list.reverse()
                for txinfo in data_list:
                    try:
                        tx = tx_from_any(txinfo['tx'])
                        tx.deserialize()
                        self.do_save(tx)
                        # print(f"pull_tx_infos============ save ok {txinfo['tx_hash']}")
                    except BaseException as e:
                        # print(f"except-------- {txinfo['tx_hash'],txinfo['tx']}")
                        temp_data = {}
                        temp_data['tx_hash'] = txinfo['tx_hash']
                        temp_data['error'] = str(e)
                        except_list.append(temp_data)
                        pass
                # return json.dumps(except_list)
            # self.sync_timer = threading.Timer(5.0, self.pull_tx_infos)
            # self.sync_timer.start()
        except BaseException as e:
            print(f"eeeeeeeeeeeeeee {str(e)}")
            raise BaseException(e)

    def bulk_create_wallet(self, wallets_info):
        '''
        Create wallets in bulk
        :param wallets_info:[{m,n,name,[xpub1,xpub2,xpub3]}, ....]
        :return:
        '''
        wallets_list = json.loads(wallets_info)
        create_failed_into = {}
        for m, n, name, xpubs in wallets_list:
            try:
                self.import_create_hw_wallet(name, m, n, xpubs)
            except BaseException as e:
                create_failed_into[name] = str(e)
        return json.dumps(create_failed_into)

    def import_create_hw_wallet(self, name, m, n, xpubs, hide_type=False, hd=False, derived=False):
        '''
        Create a wallet
        :param name: wallet name as str
        :param m: number of consigner as str
        :param n: number of signers as str
        :param xpubs: all xpubs as [xpub1, xpub2....]
        :param hide_type: whether to create a hidden wallet as bool
        :param hd: whether to create hd wallet as bool
        :param derived: whether to create hd derived wallet as bool
        :return: the wallet info recovered
        '''
        try:
            print(f"xpubs=========={m, n, xpubs}")
            self.set_multi_wallet_info(name, m, n)
            xpubs_list = json.loads(xpubs)
            for xpub_info in xpubs_list:
                if len(xpub_info) == 2:
                    self.add_xpub(xpub_info[0], xpub_info[1])
                else:
                    self.add_xpub(xpub_info)
            self.create_multi_wallet(name, hd=hd, hide_type=hide_type, derived=derived)
            if hd:
                return self.recovery_hd_derived_wallet_from_hw(hd_xpub=xpubs_list[0])
        except BaseException as e:
            raise e
        return ""
    ############
    def get_wallet_info_from_server(self, xpub):
        '''
        Get all wallet info that created by the xpub
        :param xpub: xpub from read by hardware as str
        :return:
        '''
        try:
            if self.label_flag:
                Vpub_data = []
                title = ('Vpub' if constants.net.TESTNET else 'Zpub')
                if xpub[0:4] == title:
                    Vpub_data = json.loads(self.label_plugin.pull_xpub(xpub))
                    xpub = BIP32Node.get_p2wpkh_from_p2wsh(xpub)
                vpub_data = json.loads(self.label_plugin.pull_xpub(xpub))
                return json.dumps(Vpub_data + vpub_data if Vpub_data is not None else vpub_data)
        except BaseException as e:
            raise e
        return ""
    # #create tx#########################
    def get_default_fee_status(self):
        '''
        Get default fee,now is ETA
        :return: the fee info as 180sat/byte
        '''
        try:
            x = 1
            self.config.set_key('mempool_fees', x == 2)
            self.config.set_key('dynamic_fees', x > 0)
            return self.config.get_fee_status()
        except BaseException as e:
            raise e
        return ""

    def get_amount(self, amount):
        try:
            x = Decimal(str(amount))
        except:
            return None
        # scale it to max allowed precision, make it an int
        power = pow(10, self.decimal_point)
        max_prec_amount = int(power * x)
        return max_prec_amount

    def set_dust(self, dust_flag):
        '''
        Enable/disable use dust
        :param dust_flag: as bool
        :return:
        '''
        if dust_flag != self.config.get('dust_flag', True):
            self.dust_flag = dust_flag
            self.config.set_key('dust_flag', self.dust_flag)
        return ""

    def parse_output(self, outputs):
        all_output_add = json.loads(outputs)
        outputs_addrs = []
        for key in all_output_add:
            for address, amount in key.items():
                outputs_addrs.append(PartialTxOutput.from_address_and_value(address, self.get_amount(amount)))
                print("console.mktx[%s] wallet_type = %s use_change=%s add = %s" % (
                    self.wallet, self.wallet.wallet_type, self.wallet.use_change, self.wallet.get_addresses()))
        return outputs_addrs

    def get_coins(self, coins_info):
        coins = []
        for utxo in self.coins:
            info = utxo.to_json()
            temp_utxo = {}
            temp_utxo[info['prevout_hash']] = info['address']
            if coins_info.__contains__(temp_utxo):
                coins.append(utxo)
        return coins

    def get_fee_by_feerate(self, outputs, message, feerate, customer=None):
        '''
        Get fee info when transfer
        :param outputs: outputs info as json [{addr1, value}, ...]
        :param message: what you want say as sting
        :param feerate: feerate retruned by get_default_fee_status api
        :param customer: User choose coin as bool
        :return: json
        '''
        try:
            self._assert_wallet_isvalid()
            outputs_addrs = self.parse_output(outputs)
            if customer is None:
                coins = self.wallet.get_spendable_coins(domain=None)
            else:
                coins = self.get_coins(json.loads(customer))
            print(f"coins=========={coins}")
            c, u, x = self.wallet.get_balance()
            if not coins and self.config.get('confirmed_only', False):
                raise BaseException("Please use unconfirmed coins")
            fee_per_kb = 1000 * Decimal(feerate)
            from functools import partial
            fee_estimator = partial(self.config.estimate_fee_for_feerate, fee_per_kb)
            # tx = self.wallet.make_unsigned_transaction(coins=coins, outputs = outputs_addrs, fee=self.get_amount(fee_estimator))
            tx = self.wallet.make_unsigned_transaction(coins=coins, outputs=outputs_addrs, fee=fee_estimator)
            tx.set_rbf(self.rbf)
            self.wallet.set_label(tx.txid(), message)
            size = tx.estimated_size()
            fee = tx.get_fee()
            print("feee-----%s size =%s" % (fee, size))
            self.tx = tx
            print("console:mkun:tx====%s" % self.tx)
            tx_details = self.wallet.get_tx_info(tx)
            print("tx_details 1111111 = %s" % json.dumps(tx_details))

            dyn = self.config.is_dynfee()
            mempool = self.config.use_mempool_fees()
            pos = self.config.get_depth_level() if mempool else self.config.get_fee_level()
            fee_rate = feerate*1000 if feerate is None else self.config.fee_per_kb()
            target, tooltip = self.config.get_fee_text(pos, dyn, mempool, fee_rate)
            block = target.split()[-2]
            ret_data = {
                'amount': tx_details.amount,
                'size': size,
                'fee': tx_details.fee,
                'time': 7 if target.split()[-2] == 'next' else int(target.split()[-2]) * 7,
                'tx': str(self.tx)
            }
            return json.dumps(ret_data)
        except Exception as e:
            raise BaseException(e)
        return ""

    def mktx(self, tx=None):
        '''
        Confirm to create transaction
        :param tx: tx that created by get_fee_by_feerate
        :return: tx info
        '''
        try:
            self._assert_wallet_isvalid()
            tx = tx_from_any(tx)
            tx.deserialize()
            try:
                self.do_save(tx)
            except BaseException as e:
                pass
        except Exception as e:
            raise BaseException(e)

        ret_data = {
            'tx': str(self.tx)
        }
        try:
            if self.label_flag and self.wallet.wallet_type != "standard":
                self.label_plugin.push_tx(self.wallet, 'createtx', tx.txid(), str(self.tx))
        except Exception as e:
            print(f"push_tx createtx error {e}")
            pass
        json_str = json.dumps(ret_data)
        return json_str

    def deserialize(self, raw_tx):
        try:
            tx = Transaction(raw_tx)
            tx.deserialize()
        except Exception as e:
            raise BaseException(e)
        return ""

    # ### coinjoin
    # def join_tx_with_another(self, tx: 'PartialTransaction', other_tx: 'PartialTransaction') -> None:
    #     if tx is None or other_tx is None:
    #         raise BaseException("tx or other_tx is empty")
    #     try:
    #         print(f"join_tx_with_another.....in.....")
    #         tx = tx_from_any(tx)
    #         other_tx = tx_from_any(other_tx)
    #         if not isinstance(tx, PartialTransaction):
    #             raise BaseException('TX must partial transactions.')
    #     except BaseException as e:
    #         raise BaseException(("Bixin was unable to parse your transaction") + ":\n" + repr(e))
    #     try:
    #         print(f"join_tx_with_another.......{tx, other_tx}")
    #         tx.join_with_other_psbt(other_tx)
    #     except BaseException as e:
    #         raise BaseException(("Error joining partial transactions") + ":\n" + repr(e))
    #     return tx.serialize_as_bytes().hex()

    # def export_for_coinjoin(self, export_tx) -> PartialTransaction:
    #     if export_tx is None:
    #         raise BaseException("export_tx is empty")
    #     export_tx = tx_from_any(export_tx)
    #     if not isinstance(export_tx, PartialTransaction):
    #         raise BaseException("Can only export partial transactions for coinjoins.")
    #     tx = copy.deepcopy(export_tx)
    #     tx.prepare_for_export_for_coinjoin()
    #     return tx.serialize_as_bytes().hex()
    # ####

    def format_amount(self, x, is_diff=False, whitespaces=False):
        return util.format_satoshis(x, is_diff=is_diff, num_zeros=self.num_zeros, decimal_point=self.decimal_point, whitespaces=whitespaces)

    def base_unit(self):
        return util.decimal_point_to_base_unit_name(self.decimal_point)

    # set use unconfirmed coin
    def set_unconf(self, x):
        '''
        Enable/disable spend confirmed_only input
        :param x: as bool
        :return:
        '''
        self.config.set_key('confirmed_only', bool(x))
        return ""

    # fiat balance
    def get_currencies(self):
        '''
        Get fiat list
        :return:json exp:{'CNY', 'USD'...}
        '''
        self._assert_daemon_running()
        currencies = sorted(self.daemon.fx.get_currencies(self.daemon.fx.get_history_config()))
        return json.dumps(currencies)

    def get_exchanges(self):
        '''
        Get exchange server list
        :return: json exp:{'exchanges', ...}
        '''
        if not self.daemon.fx: return
        b = self.daemon.fx.is_enabled()
        if b:
            h = self.daemon.fx.get_history_config()
            c = self.daemon.fx.get_currency()
            exchanges = self.daemon.fx.get_exchanges_by_ccy(c, h)
        else:
            exchanges = self.daemon.fx.get_exchanges_by_ccy('USD', False)
        return sorted(exchanges)

    def set_exchange(self, exchange):
        '''
        Set exchange server
        :param exchange: exchange server name
        :return:
        '''
        if self.daemon.fx and self.daemon.fx.is_enabled() and exchange and exchange != self.daemon.fx.exchange.name():
            self.daemon.fx.set_exchange(exchange)
        return ""

    def set_currency(self, ccy):
        '''
        Set fiat
        :param ccy: fiat as str
        :return:
        '''
        self.daemon.fx.set_enabled(True)
        if ccy != self.ccy:
            self.daemon.fx.set_currency(ccy)
            self.ccy = ccy
        self.update_status()
        return ""

    def get_exchange_currency(self, type, amount):
        '''
        You can get coin to fiat or get fiat to coin
        :param type: base/fiat as str
        :param amount: value
        exp:
            if you want get fiat from coin,like this:
                get_exchange_currency("base", 1)
            return: 1,000.34 CNY

            if you want get coin from fiat, like this:
                get_exchange_currency("fiat", 1000)
            return: 1 mBTC
        '''
        text = ""
        rate = self.daemon.fx.exchange_rate() if self.daemon.fx else Decimal('NaN')
        if rate.is_nan() or amount is None:
            return text
        else:
            if type == "base":
                amount = self.get_amount(amount)
                text = self.daemon.fx.ccy_amount_str(amount * Decimal(rate) / COIN, False)
            elif type == "fiat":
                text = self.format_amount((int(Decimal(amount) / Decimal(rate) * COIN)))
            return text

    def set_base_uint(self, base_unit):
        '''
        Set base unit for(BTC/mBTC/bits/sat)
        :param base_unit: (BTC or mBTC or bits or sat) as str
        :return:
        '''
        self.base_unit = base_unit
        self.decimal_point = util.base_unit_name_to_decimal_point(self.base_unit)
        self.config.set_key('decimal_point', self.decimal_point, True)
        self.update_status()
        return ""

    def format_amount_and_units(self, amount):
        try:
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        text = self.format_amount(amount) + ' ' + self.base_unit
        x = self.daemon.fx.format_amount_and_units(amount) if self.daemon.fx else None
        if text and x:
            text += ' (%s)' % x
        return text

    # #proxy
    def set_proxy(self, proxy_mode, proxy_host, proxy_port, proxy_user, proxy_password):
        '''
        Set proxy server
        :param proxy_mode: SOCK4/SOCK5 as str
        :param proxy_host: server ip
        :param proxy_port: server port
        :param proxy_user: login user that you registed
        :param proxy_password: login password that you registed
        :return:
        '''
        try:
            net_params = self.network.get_parameters()
            proxy = None
            if (proxy_mode != "" and proxy_host != "" and proxy_port != ""):
                proxy = {'mode': str(proxy_mode).lower(),
                         'host': str(proxy_host),
                         'port': str(proxy_port),
                         'user': str(proxy_user),
                         'password': str(proxy_password)}
            net_params = net_params._replace(proxy=proxy)
            self.network.run_from_another_thread(self.network.set_parameters(net_params))
        except BaseException as e:
            raise e
        return ""

    # #qr api
    def get_raw_tx_from_qr_data(self, data):
        try:
            from electrum.util import bh2u
            return bh2u(base_decode(data, None, base=43))
        except BaseException as e:
            raise e

    def get_qr_data_from_raw_tx(self, raw_tx):
        try:
            from electrum.bitcoin import base_encode, bfh
            text = bfh(raw_tx)
            return base_encode(text, base=43)
        except BaseException as e:
            raise e

    def recover_tx_info(self, tx):
        try:
            tx = tx_from_any(str(tx))
            temp_tx = copy.deepcopy(tx)
            temp_tx.deserialize()
            temp_tx.add_info_from_wallet(self.wallet)
            return temp_tx
        except BaseException as e:
            raise e

    def get_tx_info_from_raw(self, raw_tx):
        '''
        You can get detail info from a raw_tx
        :param raw_tx: raw tx
        :return:
        '''
        try:
            print("console:get_tx_info_from_raw:tx===%s" % raw_tx)
            tx = self.recover_tx_info(raw_tx)
        except Exception as e:
            tx = None
            raise BaseException(e)
        data = {}
        data = self.get_details_info(tx)
        return data

    def get_details_info(self, tx):
        try:
            self._assert_wallet_isvalid()
        except Exception as e:
            raise BaseException(e)
        tx_details = self.wallet.get_tx_info(tx)
        if 'Partially signed' in tx_details.status:
            temp_s, _ = tx.signature_count()
            s = int(temp_s / len(tx.inputs()))
            r = len(self.wallet.get_keystores())
        elif 'Unsigned' in tx_details.status:
            s = 0
            r = len(self.wallet.get_keystores())
        else:
            if self.wallet.wallet_type == "standard":
                s = r = len(self.wallet.get_keystores())
            else:
                s, r = self.wallet.wallet_type.split("of", 1)
        in_list = []
        if isinstance(tx, PartialTransaction):
            for i in tx.inputs():
                in_info = {}
                in_info['addr'] = i.address
                if not in_list.__contains__(in_info):
                    in_list.append(in_info)
        out_list = []
        for index, o in enumerate(tx.outputs()):
            address, value = o.address, o.value
            out_info = {}
            out_info['addr'] = address
            out_info['amount'] = self.format_amount_and_units(value)
            out_info['is_change'] = True if (index == len(tx.outputs())-1) and (len(tx.outputs()) != 1) else False
            out_list.append(out_info)

        amount_str = ""
        if tx_details.amount is None:
            amount_str = "Transaction unrelated to your wallet"
        else:
            amount_str = self.format_amount_and_units(tx_details.amount)

        ret_data = {
            'txid': tx_details.txid,
            'can_broadcast': tx_details.can_broadcast,
            'amount': amount_str,
            'fee': self.format_amount_and_units(tx_details.fee),
            'description': self.wallet.get_label(tx_details.txid),
            'tx_status': tx_details.status,
            'sign_status': [s, r],
            'output_addr': out_list,
            'input_addr': in_list,
            'height': tx_details.tx_mined_status.height,
            'cosigner': [x.xpub for x in self.wallet.get_keystores()],
            'tx': str(tx)
        }
        json_data = json.dumps(ret_data)
        return json_data

    # invoices
    def delete_invoice(self, key):
        try:
            self._assert_wallet_isvalid()
            self.wallet.delete_invoice(key)
        except Exception as e:
            raise BaseException(e)

    def get_invoices(self):
        try:
            self._assert_wallet_isvalid()
            return self.wallet.get_invoices()
        except Exception as e:
            raise BaseException(e)

    def do_save(self, tx):
        try:
            if not self.wallet.add_transaction(tx):
                raise BaseException(
                    ("Transaction could not be saved.") + "\n" + ("It conflicts with current history. tx=") + tx.txid())
        except BaseException as e:
            raise BaseException(e)
        else:
            self.wallet.save_db()

    def update_invoices(self, old_tx, new_tx):
        try:
            self._assert_wallet_isvalid()
            self.wallet.update_invoice(old_tx, new_tx)
        except Exception as e:
            raise BaseException(e)

    def clear_invoices(self):
        try:
            self._assert_wallet_isvalid()
            self.wallet.clear_invoices()
        except Exception as e:
            raise BaseException(e)

    ##get history
    def get_all_tx_list(self, search_type=None):
        '''
        Get the histroy list with the wallet that you select
        :param search_type: None/send/receive as str
        :return:
        '''
        try:
            self.pull_tx_infos()
        except BaseException as e:
            print(f"get_all_tx_list......{e}")
            pass
        history_data = []
        try:
            history_info = self.get_history_tx()
        except BaseException as e:
            raise e
        history_dict = json.loads(history_info)
        if search_type is None:
            history_data = history_dict
        elif search_type == 'send':
            for info in history_dict:
                if info['is_mine']:
                    history_data.append(info)
        elif search_type == 'receive':
            for info in history_dict:
                if not info['is_mine']:
                    history_data.append(info)

        all_data = []
        for i in history_data:
            i['type'] = 'history'
            data = self.get_tx_info(i['tx_hash'])
            i['tx_status'] = json.loads(data)['tx_status']
            all_data.append(i)
        return json.dumps(all_data)

    ##history
    def get_history_tx(self):
        try:
            self._assert_wallet_isvalid()
        except Exception as e:
            raise BaseException(e)
        history = reversed(self.wallet.get_history())
        all_data = [self.get_card(*item) for item in history]
        return json.dumps(all_data)

    def get_tx_info(self, tx_hash):
        '''
        Get detail info by tx_hash
        :param tx_hash:
        :return:
        '''
        try:
            self._assert_wallet_isvalid()
        except Exception as e:
            raise BaseException(e)
        tx = self.wallet.db.get_transaction(tx_hash)
        if not tx:
            raise BaseException('get transaction info failed')
        # tx = PartialTransaction.from_tx(tx)
        label = self.wallet.get_label(tx_hash) or None
        tx = copy.deepcopy(tx)
        try:
            tx.deserialize()
        except Exception as e:
            raise e
        tx.add_info_from_wallet(self.wallet)
        return self.get_details_info(tx)

    def get_card(self, tx_hash, tx_mined_status, delta, fee, balance):
        try:
            self._assert_wallet_isvalid()
            self._assert_daemon_running()
        except Exception as e:
            raise BaseException(e)
        status, status_str = self.wallet.get_tx_status(tx_hash, tx_mined_status)
        label = self.wallet.get_label(tx_hash) if tx_hash else _('Pruned transaction outputs')
        ri = {}
        ri['tx_hash'] = tx_hash
        ri['date'] = status_str
        ri['message'] = label
        ri['confirmations'] = tx_mined_status.conf
        if delta is not None:
            ri['is_mine'] = delta < 0
            if delta < 0: delta = - delta
            ri['amount'] = self.format_amount_and_units(delta)
            if self.fiat_unit:
                fx = self.daemon.fx
                fiat_value = delta / Decimal(bitcoin.COIN) * self.wallet.price_at_timestamp(tx_hash, fx.timestamp_rate)
                fiat_value = Fiat(fiat_value, fx.ccy)
                ri['quote_text'] = fiat_value.to_ui_string()
        return ri

    def get_wallet_address_show_UI(self, next=None):
        '''
        Get receving address
        :param next: if you want change address, you can set the param
        :return:
        '''
        try:
            self._assert_wallet_isvalid()
            self.show_addr = self.config.get('%s_show_addr' % self.wallet, self.wallet.get_addresses()[0])
            if next:
                addr = self.wallet.create_new_address(False)
                self.show_addr = addr
                self.config.set_key('%s_show_addr' % self.wallet, self.show_addr)
            data = util.create_bip21_uri(self.show_addr, "", "")
        except Exception as e:
            raise BaseException(e)
        data_json = {}
        data_json['qr_data'] = data
        data_json['addr'] = self.show_addr
        return json.dumps(data_json)

    def get_all_funded_address(self):
        '''
        Get a address list of have balance
        :return:
        '''
        try:
            self._assert_wallet_isvalid()
            all_addr = self.wallet.get_addresses()
            funded_addrs_list = []
            for addr in all_addr:
                c, u, x = self.wallet.get_addr_balance(addr)
                balance = c + u + x
                if balance == 0:
                    continue
                funded_addr = {}
                funded_addr['address'] = addr
                funded_addr['balance'] = self.format_amount_and_units(balance)
                funded_addrs_list.append(funded_addr)
            return json.dumps(funded_addrs_list)
        except Exception as e:
            raise BaseException(e)
        return ""

    def get_unspend_utxos(self):
        '''
        Get unspend utxos if you need
        :return:
        '''
        try:
            coins = []
            for txin in self.wallet.get_utxos():
                d = txin.to_json()
                dust_sat = int(d['value_sats'])
                v = d.pop("value_sats")
                d["value"] = self.format_amount(v) + ' ' + self.base_unit
                if dust_sat <= 546:
                    if self.config.get('dust_flag', True):
                        continue
                coins.append(d)
                self.coins.append(txin)
            return coins
        except BaseException as e:
            raise e
        return ""

    def save_tx_to_file(self, path, tx):
        '''
        Save the psbt/tx to path
        :param path: path as str
        :param tx: tx as str
        :return:
        '''
        try:
            if tx is None:
                raise BaseException("tx is empty")
            tx = tx_from_any(tx)
            if isinstance(tx, PartialTransaction):
                tx.finalize_psbt()
            if tx.is_complete():  # network tx hex
                path += '.txn'
                with open(path, "w+") as f:
                    network_tx_hex = tx.serialize_to_network()
                    f.write(network_tx_hex + '\n')
            else:  # if partial: PSBT bytes
                assert isinstance(tx, PartialTransaction)
                path += '.psbt'
                with open(path, "wb+") as f:
                    f.write(tx.serialize_as_bytes())
        except Exception as e:
            raise BaseException(e)
        return ""

    def read_tx_from_file(self, path):
        '''
        Import tx info from path
        :param path: path as str
        :return:
        '''
        try:
            with open(path, "rb") as f:
                file_content = f.read()
        except (ValueError, IOError, os.error) as reason:
            raise BaseException("BiXin was unable to open your transaction file")
        tx = tx_from_any(file_content)
        return tx

    ##Analyze QR data
    def parse_address(self, data):
        data = data.strip()
        try:
            uri = util.parse_URI(data)
            if uri.__contains__('amount'):
                uri['amount'] = self.format_amount_and_units(uri['amount'])
            return uri
        except Exception as e:
            raise Exception(e)
        return ""

    def parse_tx(self, data):
        # try to decode transaction
        from electrum.transaction import Transaction
        from electrum.util import bh2u
        try:
            # text = bh2u(base_decode(data, base=43))
            tx = self.recover_tx_info(data)
        except Exception as e:
            tx = None
            raise BaseException(e)

        data = self.get_details_info(tx)
        return data

    def parse_pr(self, data):
        '''
        Parse qr code which generated by address or tx
        :param data: qr cata as str
        :return:
        '''
        add_status_flag = False
        tx_status_flag = False
        add_data = {}
        try:
            add_data = self.parse_address(data)
            add_status_flag = True
        except BaseException as e:
            add_status_flag = False

        try:
            tx_data = self.parse_tx(data)
            tx_status_flag = True
        except BaseException as e:
            tx_status_flag = False

        out_data = {}
        if (add_status_flag):
            out_data['type'] = 1
            out_data['data'] = add_data
        elif (tx_status_flag):
            out_data['type'] = 2
            out_data['data'] = json.loads(tx_data)
        else:
            out_data['type'] = 3
            out_data['data'] = "parse pr error"
        return json.dumps(out_data)

    def broadcast_tx(self, tx):
        '''
        Broadcast the tx
        :param tx: tx as str
        :return: 'success'/raise except
        '''
        if self.network and self.network.is_connected():
            status = False
            try:
                if isinstance(tx, str):
                    tx = tx_from_any(tx)
                    tx.deserialize()
                self.network.run_from_another_thread(self.network.broadcast_transaction(tx))
            except TxBroadcastError as e:
                msg = e.get_message_for_gui()
                raise BaseException(msg)
            except BestEffortRequestFailed as e:
                msg = repr(e)
                raise BaseException(msg)
            else:
                print("--------broadcast ok............")
                return "success"
                status, msg = True, tx.txid()
        #          self.callbackIntent.onCallback(Status.broadcast, msg)
        else:
            raise BaseException(('Cannot broadcast transaction') + ':\n' + ('Not connected'))
        return ""

    def set_use_change(self, status_change):
        '''
        Enable/disable change address
        :param status_change: as bool
        :return:
        '''
        try:
            self._assert_wallet_isvalid()
        except Exception as e:
            raise BaseException(e)
        if self.wallet.use_change == status_change:
            return
        self.config.set_key('use_change', status_change, False)
        self.wallet.use_change = status_change
        return ""

    def sign_message(self, address, message, path='android_usb', password=None):
        '''
        :param address: must bitcoin address, must in current wallet as str
        :param message: message need be signed as str
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param password: as str
        :return:
        '''
        try:
            if path:
                self.get_client(path=path)
            self._assert_wallet_isvalid()
            address = address.strip()
            message = message.strip()
            if not bitcoin.is_address(address):
                raise BaseException('Invalid Bitcoin address.')
            if self.wallet.is_watching_only():
                raise BaseException('This is a watching-only wallet.')
            if not self.wallet.is_mine(address):
                raise BaseException('Address not in wallet.')
            txin_type = self.wallet.get_txin_type(address)
            if txin_type not in ['p2pkh', 'p2wpkh', 'p2wpkh-p2sh']:
                raise BaseException('Cannot sign messages with this type of address:%s\n\n' % txin_type)
            sig = self.wallet.sign_message(address, message, password)
            import base64
            return base64.b64encode(sig).decode('ascii')
        except Exception as e:
            raise BaseException(e)
        return ""

    def verify_message(self, address, message, signature):
        '''
        Verify the message that you signed by sign_message
        :param address: must bitcoin address as str
        :param message: message as str
        :param signature: sign info retured by sign_message api
        :return: true/false as bool
        '''
        address = address.strip()
        message = message.strip().encode('utf-8')
        if not bitcoin.is_address(address):
            raise BaseException('Invalid Bitcoin address.')
        try:
            # This can throw on invalid base64
            import base64
            sig = base64.b64decode(str(signature))
            verified = ecc.verify_message_with_address(address, sig, message)
        except Exception as e:
            verified = False
        return verified

    ###############
    # def sign_eth_tx(self, path='android_usb', to_addr, value, password=None, symbol=None):
    #     checksum_to_address = self.pywalib.web3.toChecksumAddress(self.wallet.get_addresses()[0])
    #     if symbol is None:
    #         send = self.pywalib.send_transaction(self.wallet.get_account(checksum_to_address, password), checksum_to_address,
    #                                           to_addr, value)
    #     else:
    #         con_addr = self.wallet.get_contract_token(symbol)
    #         send = self.pywalib.send_transaction(self.wallet.get_account(checksum_to_address, password), checksum_to_address,
    #                                          to_addr, value,
    #                                          contract=con_addr)

    def sign_tx(self, tx, path='android_usb', password=None):
        '''
        Sign one transaction
        :param tx: tx info as str
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param password: password as str
        :return: signed tx on success and raise except on failure
        '''
        try:
            if path:
                self.get_client(path=path)
            self._assert_wallet_isvalid()
            tx = tx_from_any(tx)
            tx.deserialize()
            sign_tx = self.wallet.sign_transaction(tx, password)
            print("=======sign_tx.serialize=%s" % sign_tx.serialize_as_bytes().hex())
            try:
                self.do_save(sign_tx)
            except:
                pass
            try:
                if self.label_flag and self.wallet.wallet_type != "standard":
                    self.label_plugin.push_tx(self.wallet, 'signtx', tx.txid(), str(sign_tx))
            except Exception as e:
                print(f"push_tx signtx error {e}")
                pass
            return self.get_tx_info_from_raw(sign_tx)
        except Exception as e:
            raise BaseException(e)
        return ""

    ##connection with terzorlib#########################
    def hardware_verify(self, msg, path='android_usb'):
        '''
        Anti-counterfeiting verification
        :param msg: msg as str
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return: json like {"data":, "cert":, "signature":,}
        '''
        client = self.get_client(path=path)
        try:
            from hashlib import sha256
            digest = sha256(msg.encode("utf-8")).digest()
            response = device.se_verify(client.client, digest)
        except Exception as e:
            raise BaseException(e)
        verify_info = {}
        verify_info['data'] = msg
        verify_info['cert'] = response.cert.hex()
        verify_info['signature'] = response.signature.hex()
        return json.dumps(verify_info)

    def backup_wallet(self, path='android_usb'):
        '''
        Backup wallet by se
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = client.backup()
        except Exception as e:
            raise BaseException(e)
        return response

    def se_proxy(self, message, path='android_usb') -> str:
        client = self.get_client(path=path)
        try:
            response = client.se_proxy(message)
        except Exception as e:
            raise BaseException(e)
        return response

    def bixin_backup_device(self, path='android_usb'):
        '''
        Export seed
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = client.bixin_backup_device()
        except Exception as e:
            raise BaseException(e)
        print(f"seed......{response}")
        return response

    def bixin_load_device(self, mnemonics, path='android_usb'):
        '''
        Import seed
        :param mnemonics: as str
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = client.bixin_load_device(mnemonics=mnemonics)
        except Exception as e:
            raise BaseException(e)
        return response

    def recovery_wallet(self, path='android_usb', *args):
        '''
        Recovery wallet by encryption
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param args: encryption data as str
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = client.recovery(*args)
        except Exception as e:
            raise BaseException(e)
        return response

    def bx_inquire_whitelist(self, path='android_usb', **kwargs):
        '''
        Inquire
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param kwargs:
            type:2=inquire
            addr_in: addreess as str
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = json.dumps(client.bx_inquire_whitelist(**kwargs))
        except Exception as e:
            raise BaseException(e)
        return response

    def bx_add_or_delete_whitelist(self, path='android_usb', **kwargs):
        '''
        Add and delete whitelist
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param kwargs:
            type:0=add 1=delete
            addr_in: addreess as str
        :return:
        '''
        client = self.get_client(path=path)
        try:
            response = json.dumps(client.bx_add_or_delete_whitelist(**kwargs))
        except Exception as e:
            raise BaseException(e)
        return response

    def apply_setting(self, path='nfc', **kwargs):
        '''
        Set the hardware function
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param kwargs:
            下边罗列的参数 使用哪个传哪个 不用都传
            label                 钱包名称
            language              语言
            use_passphrase        隐藏钱包
            auto_lock_delay_ms    自动解锁时间
            fastpay_pin           快捷支付是否需要验PIN
            use_ble               是否启动蓝牙
            use_se                是否启动se
            is_bixinapp           设置是否是币信app
            fastpay_confirm       快捷支付是否需要摁键确认
            fastpay_money_limit   快捷支付额度限制
            fastpay_times         快捷支付次数
        :return:
        '''
        client = self.get_client(path=path)
        try:
            resp = device.apply_settings(client.client, **kwargs)
            if resp == "Settings applied":
                return 1
            else:
                return 0
        except Exception as e:
            raise BaseException(e)
        return ""

    def init(self, path='android_usb', label="BIXIN KEY", language='english', use_se=True):
        '''
        Activate the device
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param label: name as str
        :param language: as str
        :param use_se: as bool
        :return:
        '''
        client = self.get_client(path=path)
        try:
            if use_se:
                device.apply_settings(client.client, use_se=True)
            response = client.reset_device(skip_backup=True, language=language, pin_protection=False,
                                           passphrase_protection=True, label=label)
        except Exception as e:
            raise BaseException(e)
        if response == "Device successfully initialized":
            return 1
        else:
            return 0

    def reset_pin(self, path='android_usb') -> int:
        '''
        Reset pin
        :param path:NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        try:
            client = self.get_client(path)
            resp = client.set_pin(False)
        except Exception as e:
            if isinstance(e, exceptions.PinException):
                return 0
            else:
                raise BaseException(e)
        if resp == "PIN changed":
            return 1
        else:
            return 0

    def show_address(self, address, path='android_usb'):
        '''
        Verify address on hardware
        :param address: address as str
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        try:
            plugin = self.plugin.get_plugin("trezor")
            plugin.show_address(path=path, ui=CustomerUI(), wallet=self.wallet, address=address)
        except Exception as e:
            raise BaseException(e)
        return ""

    def wipe_device(self, path='android_usb') -> int:
        '''
        Reset device
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        try:
            client = self.get_client(path)
            resp = client.wipe_device()
        except Exception as e:
            if isinstance(e, exceptions.PinException):
                return 0
            else:
                raise BaseException(e)
        if resp == "Device wiped":
            return 1
        else:
            return 0

    def get_passphrase_status(self, path='android_usb'):
        # self.client = None
        # self.path = ''
        client = self.get_client(path=path)
        return client.features.passphrase_protection

    def get_client(self, path='android_usb', ui=CustomerUI(), clean=False):
        plugin = self.plugin.get_plugin("trezor")
        if clean:
            plugin.clean()
        return plugin.get_client(path=path, ui=ui)

    def is_encrypted_with_hw_device(self):
        ret = self.wallet.storage.is_encrypted_with_hw_device()
        print(f"hw device....{ret}")
        return ""

    def get_feature(self, path='android_usb'):
        '''
        Get hardware information
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        '''
        with self.lock:
            client = self.get_client(path=path, clean=True)
        return json.dumps(protobuf.to_dict(client.features))

    def get_xpub_from_hw(self, path='android_usb', _type='p2wsh', is_creating=True, account_id=None):
        '''
        Get extended public key from hardware
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param _type: p2wsh/p2wsh/p2pkh/p2pkh-p2sh as str
        :return: xpub str
        '''
        client = self.get_client(path=path)
        if account_id is None:
            account_id = 0
        if _type == "p2wsh":
            derivation = purpose48_derivation(account_id, xtype='p2wsh')
            #derivation = bip44_derivation(account_id, bip43_purpose=48)
        elif _type == 'p2wpkh':
            derivation = bip44_derivation(account_id, bip43_purpose=84)
        elif _type == 'p2pkh':
            derivation = bip44_derivation(account_id, bip43_purpose=44)
        elif _type == 'p2pkh-p2sh':
            derivation = bip44_derivation(account_id, bip43_purpose=49)
        try:
            xpub = client.get_xpub(derivation, _type, creating=is_creating)
        except Exception as e:
            raise BaseException(e)
        return xpub
    
    def create_btc_hw_derived_wallet(self, path='android_usb', _type='p2pkh'):
        '''
        Create derived wallet by hardware
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param _type: p2wsh/p2wsh/p2pkh/p2pkh-p2sh as str
        :return: xpub as str
        '''
        if len(self.btc_derived_account_id) == 0:
            raise BaseException("Support up to 20 derived wallets")
        #derivat_path = bip44_derivation(self.btc_derived_account_id[0], 44)
        xpub = self.get_xpub_from_hw(path=path, _type=_type, account_id=self.btc_derived_account_id[0])
        #self.update_account_id_list(self.btc_derived_account_id[0])
        return xpub
        
    def firmware_update(
            self,
            filename,
            path,
            type="",
            fingerprint=None,
            skip_check=True,
            raw=False,
            dry_run=False,
    ):
        """
        Upload new firmware to device.
        Note : Device must be in bootloader mode.
        """
        # self.client = None
        # self.path = ''
        client = self.get_client(path)
        features = client.features
        if not features.bootloader_mode:
            resp = client.client.call(messages.BixinReboot())
            if not dry_run and not isinstance(resp, messages.Success):
                raise RuntimeError("Device turn into bootloader failed")
            time.sleep(2)
            client.client.init_device()
        if not dry_run and not client.features.bootloader_mode:
            raise BaseException("Please switch your device to bootloader mode.")

        bootloader_onev2 = features.major_version == 1 and features.minor_version >= 8

        if filename:
            try:
                if type:
                    data = parse(filename)
                else:
                    with open(filename, "rb") as file:
                        data = file.read()
            except Exception as e:
                raise BaseException(e)
        else:
            raise BaseException("Please Give The File Name")

        if not raw and not skip_check:
            try:
                version, fw = firmware.parse(data)
            except Exception as e:
                raise BaseException(e)

            trezorctl.validate_firmware(version, fw, fingerprint)
            if (
                    bootloader_onev2
                    and version == firmware.FirmwareFormat.TREZOR_ONE
                    and not fw.embedded_onev2
            ):
                raise BaseException("Firmware is too old for your device. Aborting.")
            elif not bootloader_onev2 and version == firmware.FirmwareFormat.TREZOR_ONE_V2:
                raise BaseException("You need to upgrade to bootloader 1.8.0 first.")

            if features.major_version not in trezorctl.ALLOWED_FIRMWARE_FORMATS:
                raise BaseException("trezorctl doesn't know your device version. Aborting.")
            elif version not in trezorctl.ALLOWED_FIRMWARE_FORMATS[features.major_version]:
                raise BaseException("Firmware does not match your device, aborting.")

        if not raw:
            # special handling for embedded-OneV2 format:
            # for bootloader < 1.8, keep the embedding
            # for bootloader 1.8.0 and up, strip the old OneV1 header
            if bootloader_onev2 and data[:4] == b"TRZR" and data[256: 256 + 4] == b"TRZF":
                print("Extracting embedded firmware image (fingerprint may change).")
                data = data[256:]

        if dry_run:
            print("Dry run. Not uploading firmware to device.")
        else:
            try:
                if features.major_version == 1 and features.firmware_present is not False:
                    # Trezor One does not send ButtonRequest
                    print("Please confirm the action on your Trezor device")
                return firmware.update(client.client, data, type=type)
            except exceptions.Cancelled:
                print("Update aborted on device.")
            except exceptions.TrezorException as e:
                raise BaseException("Update failed: {}".format(e))

    ####################################################
    ## app wallet
    def export_seed(self, password):
        '''
        Export seed by on-chain wallet
        :param password: password by str
        :return: Mnemonic as str
        '''
        if not self.wallet.has_seed():
            raise BaseException('This wallet has no seed')
        keystore = self.wallet.get_keystore()
        try:
            seed = keystore.get_seed(password)
            passphrase = keystore.get_passphrase(password)
            return seed + passphrase
        except BaseException as e:
            raise e
        return ""

    def has_seed(self):
        '''
        Check if the wallet have seed
        :return: True/False as bool
        '''
        if not self.wallet.has_seed():
            raise BaseException('This wallet has no seed')
        return self.wallet.has_seed()

    # def check_seed(self, check_seed, password):
    #     try:
    #         self._assert_wallet_isvalid()
    #         if not self.wallet.has_seed():
    #             raise BaseException('This wallet has no seed')
    #         keystore = self.wallet.get_keystore()
    #         seed = keystore.get_seed(password)
    #         if seed != check_seed:
    #             raise BaseException("pair seed failed")
    #         print("pair seed successfule.....")
    #     except BaseException as e:
    #         raise BaseException(e)

    def is_seed(self, x):
        return mnemonic.is_seed(x)

    # def is_exist_seed(self, seed):
    #     ks = keystore.from_seed(seed, '', False)
    #     ks44 = keystore.from_bip39_seed(seed, '', bip44_derivation(0, bip43_purpose=44))
    #     ks84 = keystore.from_bip39_seed(seed, '', bip44_derivation(0, bip43_purpose=84))
    #     ks49 = keystore.from_bip39_seed(seed, '', bip44_derivation(0, bip43_purpose=49))
    #     for key, value in self.local_wallet_info.items():
    #         if value['xpubs'][0] == ks.xpub or value['xpubs'][0] == ks44.xpub or value['xpubs'][0] == ks84.xpub or \
    #                 value['xpubs'][0] == ks49.xpub:
    #             raise BaseException(f"The same seed have create wallet, name={key}")


    def get_addrs_from_seed(self, seed, passphrase=""):
        '''
        Get p2wpkh/p2wpkh-p2sh/p2pkh/electrum address by one seed
        :param seed: seed as str
        :param passphrase: passphrase if you need
        :return:
        '''
        list_type_info = ["p2wpkh", "p2wpkh-p2sh", "p2pkh", "electrum"]
        out = {}
        for type in list_type_info:
            bip39_derivation = ""
            if type == "p2pkh":
                bip39_derivation = bip44_derivation(0, bip43_purpose=44)
            elif type == "p2wpkh":
                bip39_derivation = bip44_derivation(0, bip43_purpose=84)
            elif type == "p2wpkh-p2sh":
                bip39_derivation = bip44_derivation(0, bip43_purpose=49)

            if type == "electrum":
                from electrum.mnemonic import Mnemonic
                bip32_seed = Mnemonic.mnemonic_to_seed(seed, passphrase)
                rootnode = BIP32Node.from_rootseed(bip32_seed, xtype="standard")
                node = rootnode.subkey_at_private_derivation("m/0'/")
            else:
                bip32_seed = keystore.bip39_to_seed(seed, passphrase)
                rootnode = BIP32Node.from_rootseed(bip32_seed, xtype=("standard" if type == "p2pkh" else type))
                node = rootnode.subkey_at_private_derivation(bip39_derivation)

            from electrum import bip32
            xpub_master = bip32.xpub_from_xprv(node.to_xprv())
            node_master = BIP32Node.from_xkey(xpub_master)
            xpub = node_master.subkey_at_public_derivation((0,)).to_xpub()
            node = BIP32Node.from_xkey(xpub).subkey_at_public_derivation((0,))
            pubkey = node.eckey.get_public_key_bytes(compressed=True).hex()
            addr = bitcoin.pubkey_to_address('p2wpkh' if type == 'electrum' else type, pubkey)

            temp = {}
            temp['addr'] = addr
            temp['derivation'] = bip39_derivation
            out[type] = temp
        print(f"out==addr = {out}")
        return json.dumps(out)

    def update_backup_info(self, encrypt_seed):
        '''
        Add wallet backup info to config file
        :param encrypt_seed: encrypt_seed is key as str
        :return:
        '''
        try:
            if not self.backup_info.__contains__(encrypt_seed):
                self.backup_info[encrypt_seed] = False
                self.config.set_key("backupinfo", self.backup_info)
        except BaseException as e:
            raise e
        return ""

    def get_backup_info(self):
        '''
        Get backup status
        :return: True/False as bool
        '''
        try:
            if self.wallet.has_seed():
                return self.backup_info[self.wallet.keystore.seed]
        except BaseException as e:
            raise e
        return ""

    def delete_backup_info(self):
        '''
        Delete one backup status in the config
        :return:
        '''
        try:
            seed = self.wallet.keystore.seed
            if self.backup_info.__contains__(seed):
                del self.backup_info[seed]
                self.config.set_key("backupinfo", self.backup_info)
        except BaseException as e:
            raise e
        return ""

    def create_hd_wallet(self, password, seed=None, passphrase=""):
        '''
        Create hd wallet
        :param password: password as str
        :param seed: import create hd wallet if seed is not None
        :return:
            will return created derived wallet if seed is not none
            new Mnemonic if seed is None
        '''
        self._assert_daemon_running()
        new_seed = None
        if seed is None:
            seed = Mnemonic("english").generate()
            new_seed = seed
            print("Your wallet generation seed is:\n\"%s\"" % seed)
            print("seed type = %s" % type(seed))

        ###create BTC-1
        self.create("BTC-1", password, seed=seed, passphrase=passphrase, bip39_derivation=bip44_derivation(0, 84), hd=True)
        ###create ETH-1
        self.create_eth("ETH-1", password, seed=seed, passphrase=passphrase, bip39_derivation=bip44_eth_derivation(0, 44), hd=True)
        if new_seed is None:
            return self.recovery_hd_derived_wallet(password, seed, passphrase)
        else:
            wallet = self.daemon._wallets[self._wallet_path("BTC-1")]
            encrypt_seed = wallet.keystore.seed
            self.update_backup_info(encrypt_seed)
        return new_seed

    def create_eth(self, name, password=None, seed=None, passphrase="", addresses=None, privkeys=None, keystores=None, bip39_derivation=None, hd=False):
        """Create or restore a new wallet"""
        print("CREATE in....name = %s" % name)
        '''
        try:
            if not hd:
                if addresses is None:
                    self.check_password(password)
        except BaseException as e:
            raise e
        new_seed = ""
        ks = None
        wallet_type = 'eth-standard'
        path = self._wallet_path(name)
        if exists(path):
            raise BaseException("path is exist")
        storage = WalletStorage(path)
        db = WalletDB('', manual_upgrades=False)
        if addresses is not None:
            wallet = Imported_Eth_Wallet(db, storage, config=self.config)
            addresses = addresses.split()
            good_inputs, bad_inputs = wallet.import_addresses(addresses, write_to_disk=False)
            # FIXME tell user about bad_inputs
            if not good_inputs:
                raise BaseException("None of the given address can be imported")
        elif privkeys is not None or keystores is not None:
            if keystores is not None:
                try:
                    from eth_account import Account
                    privkeys = Account.decrypt(keystores, password).hex()
                except ValueError:
                    raise InvalidPasswordException()

            k = keystore.Imported_KeyStore({})
            db.put('keystore', k.dump())
            wallet = Imported_Eth_Wallet(db, storage, config=self.config)
            keys = keystore.get_eth_private_key(privkeys, allow_spaces_inside_key=False)
            good_inputs, bad_inputs = wallet.import_private_keys(keys, None, write_to_disk=False)
            # FIXME tell user about bad_inputs
            if not good_inputs:
                raise BaseException("None of the given privkeys can be imported")
        else:
            if bip39_derivation is not None:
                ks = keystore.from_bip39_seed(seed, passphrase, bip39_derivation)
                if int(bip39_derivation.split('/')[ACCOUNT_POS].split('\'')[0]) != 0:
                    wallet_type = 'eth-derived-standard'
                    self.update_devired_wallet_info(bip39_derivation, self.eth_hd_wallet.get_keystore().xpub, name)
            else:
                if seed is None:
                    seed = Mnemonic("english").generate()
                    new_seed = seed
                    print("Your wallet generation seed is:\n\"%s\"" % seed)
                    print("seed type = %s" % type(seed))
                save_flag = False
                if new_seed is not None:
                    save_flag = True
                ks, bip32_seed = keystore.from_eth_bip39_seed(seed, passphrase, derivation=bip44_eth_derivation(0, 44), create=save_flag)
            db.put('keystore', ks.dump())
            wallet = Standard_Eth_Wallet(db, storage, config=self.config)
        wallet.update_password(old_pw=None, new_pw=password, str_pw=self.android_id, encrypt_storage=True)
        wallet.save_db()
        self.daemon.add_wallet(wallet)
        if hd and seed is not None:
            self.eth_hd_wallet = wallet
            #self.check_pw_wallet = self.btc_hd_wallet
            wallet_type = 'eth-hd-standard'
        # ####test
        # # data = wallet.has_seed()
        # # inti = wallet.is_watching_only()
        # address = wallet.get_addresses()
        # # seed = wallet.get_seed(password)
        # # priv = wallet.export_private_key(address[0], password)
        # # keysor = wallet.export_keystore(address[0], password)
        # addrs = wallet.get_addresses()
        # checksum_from_address = self.pywalib.web3.toChecksumAddress(address[0])
        # status = self.pywalib.web3.isChecksumAddress(checksum_from_address)
        # ba = self.pywalib.get_balance(checksum_from_address)
        # #status-erc = wallet.pywalib.web3.isChecksumAddress('0x6b175474e89094c44da98b954eedeac495271d0f')
        # #account = wallet.get_account(addrs[0], password)
        # wallet.add_contract_token('eos', PyWalib.get_web3().toChecksumAddress('0x7241646b38a7f4693bcefa45707be2b117e58819'))
        # balance = self.pywalib.get_balance(PyWalib.get_web3().toChecksumAddress(addrs[0]), wallet.get_contract_token("eos"))
        # all_balance = wallet.get_all_balance(checksum_from_address)
        # #checksum_to_address = wallet.pywalib.web3.toChecksumAddress("0x28c5841c18fd74c7c6a09c6df9c91139c202e39a")
        # # con_addr = wallet.get_contract_token("eos")
        # # send = self.pywalib.send_transaction(wallet.get_account(addrs[0], password), addrs[0],
        # #                                      "0x28c5841c18fd74c7c6a09c6df9c91139c202e39a", 1)
        # # send = self.pywalib.send_transaction(wallet.get_account(addrs[0], password), addrs[0], "0x28c5841c18fd74c7c6a09c6df9c91139c202e39a", 1, token_symbol="eos", contract=con_addr)

        ####test
        wallet_info = {}
        wallet_info['type'] = 'eth-hd-standard' if hd and seed is not None else "eth-standard"
        wallet_info['time'] = time.time()
        wallet_info['xpubs'] = ''
        wallet_info['seed'] = ''
        self.local_wallet_info[name] = wallet_info
        self.config.set_key('all_wallet_type_info', self.local_wallet_info)
        # if self.label_flag:
        #     self.label_plugin.load_wallet(self.wallet, None)
        return new_seed
        '''

    def is_watch_only(self):
        '''
        Check if it is watch only wallet
        :return: True/False as bool
        '''
        self._assert_wallet_isvalid()
        return self.wallet.is_watching_only()

    def load_all_wallet(self):
        '''
        Load all wallet info
        :return:
        '''
        name_wallets = sorted([name for name in os.listdir(self._wallet_path())])
        name_info = {}
        for name in name_wallets:
            self.load_wallet(name, password=self.android_id)
        return ""

    def update_wallet_password(self, old_password, new_password):
        '''
        Update password
        :param old_password: old_password as str
        :param new_password: new_password as str
        :return:
        '''
        self._assert_daemon_running()
        for name, wallet in self.daemon._wallets:
            wallet.update_password(old_pw=old_password, new_pw=new_password)
        return ""

    def check_password(self, password):
        try:
            if self.check_pw_wallet is None:
                self.check_pw_wallet = self.get_check_wallet()
            self.check_pw_wallet.check_password(password, str_pw=self.android_id)
        except BaseException as e:
            raise e

    def update_account_id_list(self, account_list, account_id, type):
        if account_list.__contains__(account_id):
            account_list.remove(account_id)
            self.config.set_key('%s_derived_account_id_num' % type, account_list)


    def recovery_confirmed(self, name_list, hw=False):
        '''
        If you import hd wallet by seed, you will get a name list
        and you need confirm which wallets you want to import
        :param name_list: wallets you want to import as list like {[name, name2,...]}
        :param hw: True if you recovery from hardware
        :return:
        '''
        recovery_btc_num = []
        recovery_eth_num = []
        if len(name_list) != 0:
            for name in name_list:
                path = self._wallet_path(name)
                if self.recovery_wallets.__contains__(path):
                    recovery_info = self.recovery_wallets.get(path)
                    wallet = recovery_info['wallet']
                    self.daemon.add_wallet(wallet)
                    wallet.hide_type = False
                    wallet.save_db()
                    coin_flag = ''
                    if -1 != wallet.wallet_type.find('eth'):
                        coin_flag = 'eth'
                        recovery_eth_num.append(recovery_info['account_id'])
                    else:
                        coin_flag = 'btc'
                        recovery_btc_num.append(recovery_info['account_id'])
                    #self.update_account_id_list(self.recovery_wallets.get(name).account_id)
                    ### add info to config
                    wallet_info = {}
                    wallet_info['type'] = '%s-hw-derived-%s-%s' %(coin_flag, wallet.m, wallet.n) if hw else ('%s-derived-standard' %coin_flag)
                    wallet_info['time'] = time.time()
                    wallet_info['xpubs'] = [wallet.get_keystore().xpub]
                    wallet_info['seed'] = ''
                    #print(f"crate()-----------{wallet.get_keystore().xpub}")
                    self.local_wallet_info[name] = wallet_info
                    self.config.set_key('all_wallet_type_info', self.local_wallet_info)
        self.recovery_wallets.clear()
        self.btc_derived_account_id.clear()
        self.eth_derived_account_id.clear()
        self.btc_derived_account_id = [i + 1 for i in range(RECOVERY_DERIVAT_NUM)]
        self.eth_derived_account_id = [i + 1 for i in range(RECOVERY_DERIVAT_NUM)]
        for i in recovery_btc_num:
            self.update_account_id_list(self.btc_derived_account_id, i, 'btc')
        for i in recovery_eth_num:
            self.update_account_id_list(self.eth_derived_account_id, i, 'eth')
        return ""

    def delete_derived_wallet(self, hd_wallet):
        for xpub, derived_wallet in self.derived_info.items():
            if xpub == hd_wallet.get_keystore().xpub:
                for derived_info in derived_wallet:
                    try:
                        self.delete_wallet_from_deamon(self._wallet_path(derived_info['name']))
                        if self.local_wallet_info.__contains__(name):
                            self.local_wallet_info.pop(name)
                            self.config.set_key('all_wallet_type_info', self.local_wallet_info)
                        # os.remove(self._wallet_path(name))
                    except Exception as e:
                        raise BaseException(e)

    def get_check_wallet(self):
        wallets = self.daemon.get_wallets()
        key = sorted(wallets.keys())[0]
       # value = wallets.values()[0]
        return wallets[key]

    def filter_wallet(self):
        recovery_list = []
        ##TODO:fileter eth
        for name, wallet_info in self.recovery_wallets.items():
            try:
                wallet = wallet_info['wallet']
                if -1 != wallet.wallet_type.find('eth'):
                    print("TODO get eth history tx wallet")
                else:
                    history = reversed(wallet.get_history())
                    for item in history:
                        show_info = {}
                        c, _, _ = wallet.get_balance()
                        show_info['balance'] = self.format_amount_and_units(c)
                        show_info['name'] = str(wallet)
                        recovery_list.append(show_info)
                        break
            except BaseException as e:
                raise e
        return recovery_list

    def recovery_import_create_hw_wallet(self, name, m, n, xpubs):
        try:
            print(f"xpubs=========={m, n, xpubs}")
            self.set_multi_wallet_info(name, m, n)
            for xpub_info in xpubs:
                self.add_xpub(xpub_info)

            path = self._wallet_path(name)
            keystores = self.get_keystores_info()
            print(f"keystores---------------{keystores}")
                # if not hide_type:
                #     for key, value in self.local_wallet_info.items():
                #         num = 0
                #         for xpub in keystores:
                #             if value['xpubs'].__contains__(xpub):
                #                 num += 1
                #         if num == len(keystores) and value['type'] == wallet_type:
                #             raise BaseException(f"The same xpubs have create wallet, name={key}")
            storage, db = self.wizard.create_storage(path=path, password='', hide_type=True)
        except Exception as e:
            raise BaseException(e)
        if storage:
            wallet = Wallet(db, storage, config=self.config)
            #wallet.status_flag = ("btc-hw-%s-%s" % (m, n))
            wallet.hide_type = True
            wallet.start_network(self.daemon.network)
            self.recovery_wallets[path] = wallet
            self.wizard = None

    #TODO:need chomfirm button on onekey
    def recovery_hd_derived_wallet_from_hw(self, hd_xpub=None):
        #recovery from config
        for xpub, derived_wallet in self.derived_info.items():
            if xpub == self.btc_hd_wallet.get_keystore().xpub:
                for derived_info in derived_wallet:
                    xpub = self.get_xpub_from_hw(_type='p2pkh', account_id=derived_info['account_id'])
                    self.recovery_import_create_hw_wallet(derived_info['name'], 1, 1, [xpub])
                    self.update_account_id_list(self.btc_derived_account_id, derived_info['account_id'], 'btc')
        ##recovery 1-19
        for i in self.btc_derived_account_id:
            xpub = self.get_xpub_from_hw(_type='p2pkh', account_id=i)
            self.recovery_import_create_hw_wallet(derived_info['name'], 1, 1, [xpub])

        ##recovery from server
        info = self.get_wallet_info_from_server(hd_xpub)
        info_list = json.loads(info)
        for wallet_info in info_list:
            if wallet_info['walletType'] != '1-1':
                self.recovery_import_create_hw_wallet(wallet_info['walletName'], 1, 1, wallet_info['xpubs'])
        btc_recovery_list = self.filter_wallet()
        return json.dumps(btc_recovery_list)

    def recovery_wallet(self, hd_wallet, account_list, coin, derived_fun, seed, password, passphrase):
        for xpub, derived_wallet in self.derived_info.items():
            if xpub == hd_wallet.get_keystore().xpub:
                for derived_info in derived_wallet:
                    self.recovery_create(derived_info['name'], seed, password=password, bip39_derivation=derived_fun(derived_info['account_id'], 44), passphrase=passphrase)
                    self.update_account_id_list(self.btc_derived_account_id, derived_info['account_id'], coin)

        ##create the wallet 0~19 that not in the config file, the wallet must have tx history
        for i in account_list:
            self.recovery_create("%s_derived_%s" %(coin, i), seed, password=password, bip39_derivation=derived_fun(i, 44), passphrase=passphrase)

    def recovery_hd_derived_wallet(self, password, seed, passphrase=''):
        print("TODO, create 0~19 wallet")
        ## recovery btc wallet
        ##create the wallet that saved in config file
        self.recovery_wallet(self.btc_hd_wallet, self.btc_derived_account_id, "btc", bip44_derivation, seed, password, passphrase)
        self.recovery_wallet(self.eth_hd_wallet, self.eth_derived_account_id, "eth", bip44_eth_derivation, seed, password, passphrase)
        recovery_list = self.filter_wallet()
        return json.dumps(recovery_list)

    def create_derived_wallet(self, name, password, coin):
        '''
        Create BTC/ETH derived wallet
        :param name: name as str
        :param password: password as str
        :param coin: btc/eth as str
        :return:
        '''
        try:
            self.check_password(password)
        except BaseException as e:
            raise e
        account_list = self.btc_derived_account_id if coin == 'btc' else self.eth_derived_account_id
        hd_wallet = self.btc_hd_wallet if coin == 'btc' else self.eth_hd_wallet
        derived_fun = bip44_derivation if coin == 'btc' else bip44_eth_derivation
        if len(account_list) == 0:
            raise BaseException("Support up to 20 derived wallets")
        derivat_path = derived_fun(account_list[0], 44)
        if coin == 'btc':
            self.create(name, password, seed=hd_wallet.get_seed(password), bip39_derivation=derivat_path)
        else:
            self.create_eth(name, password, seed=hd_wallet.get_seed(password), bip39_derivation=derivat_path)
        self.update_account_id_list(account_list, account_list[0], coin)
        return ""

    def recovery_create(self, name, seed, password, bip39_derivation, passphrase=''):
        try:
            self.check_password(password)
        except BaseException as e:
            raise e
        coin_type = int(bip39_derivation.split('/')[COIN_POS].split('\'')[0])
        path = self._wallet_path(name)
        if exists(path):
            raise BaseException("path is exist")
        storage = WalletStorage(path)
        db = WalletDB('', manual_upgrades=False)
        ks = keystore.from_bip39_seed(seed, passphrase, bip39_derivation)
        db.put('keystore', ks.dump())
        if coin_type == 60:
            print("")
            #wallet = Standard_Eth_Wallet(db, storage, config=self.config)
        else:
            wallet = Standard_Wallet(db, storage, config=self.config)
        wallet.hide_type = True
        wallet.update_password(old_pw=None, new_pw=password, str_pw=self.android_id, encrypt_storage=True)
        if coin_type != 60:
            wallet.start_network(self.daemon.network)
        wallet.save_db()
        path = wallet.storage.path
        path = standardize_path(path)
        wallet_info = {}
        wallet_info['wallet'] = wallet
        wallet_info['account_id'] = int(bip39_derivation.split('/')[ACCOUNT_POS].split('\'')[0])
        self.recovery_wallets[path] = wallet_info

    def update_devired_wallet_info(self, bip39_derivation, xpub, name):
        self._assert_hd_wallet_isvalid()
        wallet_info = {}
        wallet_info['name'] = name
        wallet_info['account_id'] = bip39_derivation.split('/')[ACCOUNT_POS][0]
        if not self.derived_info.__contains__(xpub):
            derivated_wallets = []
            derivated_wallets.append(wallet_info)
            self.derived_info[xpub] = derivated_wallets
            self.config.set_key("derived_info", self.derived_info)
        else:
            save_flag = False
            if len(self.derived_info[xpub]) <= RECOVERY_DERIVAT_NUM:
                derived_wallet = self.derived_info[xpub]
                for info in derived_wallet:
                    if info['account_id'] == wallet_info['account_id']:
                        save_flag = True
                if not save_flag:
                    self.derived_info[xpub].append(wallet_info)
                    self.config.set_key("derived_info", self.derived_info)

    def get_all_mnemonic(self):
        '''
        get all mnemonic, num is 2048
        '''
        return Wordlist.from_file('english.txt')

    def get_all_wallet_balance(self):
        '''
        Get all wallet balances
        :return:
        '''
        try:
            all_balance = float(0)
            for name, wallet in self.daemon._wallets.items():
                if wallet.wallet_type[0:3] == "eth":
                    print(f"")
                    #fiat = wallet.get_all_balance()
                else:
                    c,u,x = wallet.get_balance()
                    balance = self.daemon.fx.format_amount_and_units(c) if self.daemon.fx else None
                    str = float(balance.split()[0].replace(',', ""))
                all_balance += str
            return ('%s %s' %(all_balance, self.ccy))
        except BaseException as e:
            raise e
        return ""

    def check_file_exist(self, password=None, seed=None, master=None, addresses=None, privkeys=None):
        self._assert_daemon_running()
        error_msg = BaseException("The file already exists")
        if seed is not None:
            for _, wallet in self.daemon._wallets.items():
                if wallet.has_seed():
                    wallet_seed = wallet.get_seed(password)
                    #passphrase = wallet.get_passphrase(password)
                    if wallet_seed == seed:
                        raise error_msg
        elif master is not None:
            for _, wallet in self.daemon._wallets.items():
                if isinstance(wallet, Deterministic_Wallet):
                    if wallet.get_keystores().xpub == master:
                        raise error_msg
        elif addresses is not None:
            for _, wallet in self.daemon._wallets.items():
                if addresses in wallet.get_addresses():
                    raise error_msg
        elif privkeys is not None:
            for _, wallet in self.daemon._wallets.items():
                if isinstance(wallet, Imported_Wallet):
                    if wallet.can_import_privkey():
                        ecc.ECPrivkey(bfh(privkeys))
                        pubkey = ecc.ECPrivkey(bfh(privkeys)).get_public_key_hex(compressed=True)
                        try:
                            wallet.keystore.get_private_key(pubkey, password)
                        except BaseException as e:
                            raise error_msg

    def create(self, name, password, seed_type="segwit", seed=None, passphrase="", bip39_derivation=None,
               master=None, addresses=None, privkeys=None, hd=False):
        """Create or restore a new wallet"""
        print("CREATE in....name = %s" % name)
        try:
            self.check_file_exist(password=password, seed=seed, master=master, addresses=addresses, privkeys=privkeys)
            if not hd:
                if addresses is None:
                    self.check_password(password)
        except BaseException as e:
            raise e

        if name == "":
            name = ''.join(random.sample(string.ascii_letters + string.digits, 8))+'.tmp.file'
        new_seed = ""
        wallet_type = "btc-standard"
        path = self._wallet_path(name)
        if exists(path):
            raise BaseException("path is exist")
        storage = WalletStorage(path)
        db = WalletDB('', manual_upgrades=False)
        if addresses is not None:
            wallet = Imported_Wallet(db, storage, config=self.config)
            addresses = addresses.split()
            good_inputs, bad_inputs = wallet.import_addresses(addresses, write_to_disk=False)
            # FIXME tell user about bad_inputs
            if not good_inputs:
                raise BaseException("None of the given address can be imported")
        elif privkeys is not None:
            ks = keystore.Imported_KeyStore({})
            db.put('keystore', ks.dump())
            wallet = Imported_Wallet(db, storage, config=self.config)
            try:
                #TODO:need script(p2pkh/p2wpkh/p2wpkh-p2sh)
                ecc.ECPrivkey(bfh(privkeys))
                keys = [bfh(privkeys)]
            except BaseException as e:
                keys = keystore.get_private_keys(privkeys, allow_spaces_inside_key=False)
            good_inputs, bad_inputs = wallet.import_private_keys(keys, None, write_to_disk=False)
            # FIXME tell user about bad_inputs
            if not good_inputs:
                raise BaseException("None of the given privkeys can be imported")
        else:
            if bip39_derivation is not None:
                if keystore.is_seed(seed):
                    ks = keystore.from_seed(seed, passphrase, False)
                else:
                    is_checksum, is_wordlist = keystore.bip39_is_checksum_valid(seed)
                    if not is_checksum:
                        raise BaseException("bip39 seed invalid")
                    ks = keystore.from_bip39_seed(seed, passphrase, bip39_derivation)
                if int(bip39_derivation.split('/')[ACCOUNT_POS].split('\'')[0]) != 0:
                    wallet_type = 'btc-derived-standard'
                    self.update_devired_wallet_info(bip39_derivation, self.btc_hd_wallet.get_keystore().xpub, name)
            elif master is not None:
                ks = keystore.from_master_key(master)
            else:
                if seed is None:
                    seed = Mnemonic("english").generate()
                    #seed = mnemonic.Mnemonic('en').make_seed(seed_type=seed_type)
                    new_seed = seed
                    print("Your wallet generation seed is:\n\"%s\"" % seed)
                    print("seed type = %s" % type(seed))
                if keystore.is_seed(seed):
                    ks = keystore.from_seed(seed, passphrase, False)
                else:
                    is_checksum, is_wordlist = keystore.bip39_is_checksum_valid(seed)
                    if not is_checksum:
                        raise BaseException("bip39 seed invalid")
                    ks = keystore.from_bip39_seed(seed, passphrase, bip44_derivation(0, 44))
            db.put('keystore', ks.dump())
            wallet = Standard_Wallet(db, storage, config=self.config)
        if hd and seed is not None:
            wallet.status_flag = "hd"
            self.btc_hd_wallet = wallet
            self.check_pw_wallet = self.btc_hd_wallet
            wallet_type = 'btc-hd-standard'
        wallet.update_password(old_pw=None, new_pw=password, str_pw=self.android_id, encrypt_storage=True)
        wallet.start_network(self.daemon.network)
        wallet.save_db()
        self.daemon.add_wallet(wallet)
        if -1 != name.find("tmp.file"):
            addr = wallet.get_addresses()[0]
            new_name = '%s...%s' %(addr[0:6], addr[-6:])
            self.rename_wallet(name, new_name)
        wallet_info = {}
        wallet_info['type'] = wallet_type
        wallet_info['time'] = time.time()
        wallet_info['xpubs'] = "wallet.get_keystore().xpub"
        wallet_info['seed'] = ""
        #print(f"crate()-----------{wallet.get_keystore().xpub}")
        self.local_wallet_info[name] = wallet_info
        self.config.set_key('all_wallet_type_info', self.local_wallet_info)
        # if self.label_flag:
        #     self.label_plugin.load_wallet(self.wallet, None)
        if new_seed != '':
            encrypt_seed = wallet.keystore.seed
            self.update_backup_info(encrypt_seed)
        return new_seed

    # END commands from the argparse interface.

    # BEGIN commands which only exist here.
    #####
    # rbf api
    def set_rbf(self, status_rbf):
        '''
        Enable/disable rbf
        :param status_rbf:True/False as bool
        :return:
        '''
        use_rbf = self.config.get('use_rbf', True)
        if use_rbf == status_rbf:
            return
        self.config.set_key('use_rbf', status_rbf)
        self.rbf = status_rbf
        return ""

    def get_rbf_status(self, tx_hash):
        try:
            tx = self.wallet.db.get_transaction(tx_hash)
            if not tx:
                return False
            height = self.wallet.get_tx_height(tx_hash).height
            _, is_mine, v, fee = self.wallet.get_wallet_delta(tx)
            is_unconfirmed = height <= 0
            if tx:
                # note: the current implementation of RBF *needs* the old tx fee
                rbf = is_mine and self.rbf and fee is not None and is_unconfirmed
                if rbf:
                    return True
                else:
                    return False
        except BaseException as e:
            raise e

    def format_fee_rate(self, fee_rate):
        # fee_rate is in sat/kB
        return util.format_fee_satoshis(fee_rate / 1000, num_zeros=self.num_zeros) + ' sat/byte'

    def get_rbf_fee_info(self, tx_hash):
        tx = self.wallet.db.get_transaction(tx_hash)
        if not tx:
            raise BaseException("get transaction failed")
        txid = tx.txid()
        assert txid
        fee = self.wallet.get_tx_fee(txid)
        if fee is None:
            raise BaseException("Can't bump fee: unknown fee for original transaction.")
        tx_size = tx.estimated_size()
        old_fee_rate = fee / tx_size  # sat/vbyte
        new_rate = Decimal(max(old_fee_rate * 1.5, old_fee_rate + 1)).quantize(Decimal('0.0'))
        new_tx = json.loads(self.create_bump_fee(tx_hash, str(new_rate)))
        ret_data = {
            'current_feerate': self.format_fee_rate(1000 * old_fee_rate),
            'new_feerate': str(new_rate),
            'fee': new_tx['fee'],
            'tx': new_tx['new_tx']
        }
        return json.dumps(ret_data)

    # TODO:new_tx in history or invoices, need test

    def create_bump_fee(self, tx_hash, new_fee_rate):
        try:
            print("create bump fee tx_hash---------=%s" % tx_hash)
            tx = self.wallet.db.get_transaction(tx_hash)
            if not tx:
                return False
            coins = self.wallet.get_spendable_coins(None, nonlocal_only=False)
            new_tx = self.wallet.bump_fee(tx=tx, new_fee_rate=new_fee_rate, coins=coins)
            size = new_tx.estimated_size()
            fee = new_tx.get_fee()
        except BaseException as e:
            raise BaseException(e)

        new_tx.set_rbf(self.rbf)
        out = {
            'new_tx': str(new_tx),
            'fee': fee
        }
        self.rbf_tx = new_tx
        # self.update_invoices(tx, new_tx.serialize_as_bytes().hex())
        return json.dumps(out)

    def confirm_rbf_tx(self, tx_hash):
        try:
            self.do_save(self.rbf_tx)
        except:
            pass
        try:
            if self.label_flag and self.wallet.wallet_type != "standard":
                self.label_plugin.push_tx(self.wallet, 'rbftx', self.rbf_tx.txid(), str(self.rbf_tx), tx_hash_old=tx_hash)
        except Exception as e:
            print(f"push_tx rbftx error {e}")
            pass
        return self.rbf_tx
     

    ###cpfp api###
    def get_rbf_or_cpfp_status(self, tx_hash):
        try:
            status = {}
            tx = self.wallet.db.get_transaction(tx_hash)
            if not tx:
                raise BaseException("tx is None")
            tx_details = self.wallet.get_tx_info(tx)
            is_unconfirmed = tx_details.tx_mined_status.height <= 0
            if is_unconfirmed and tx:
                # note: the current implementation of rbf *needs* the old tx fee
                if tx_details.can_bump and tx_details.fee is not None:
                    status['rbf'] = True
                else:
                    child_tx = self.wallet.cpfp(tx, 0)
                    if child_tx:
                        status['cpfp'] = True
            return json.dumps(status)
        except BaseException as e:
            raise e

    def get_cpfp_info(self, tx_hash, suggested_feerate = None):
        try:
            self._assert_wallet_isvalid()
            parent_tx = self.wallet.db.get_transaction(tx_hash)
            if not parent_tx:
                raise BaseException("get transaction failed")
            info = {}
            child_tx = self.wallet.cpfp(parent_tx, 0)
            if child_tx:
                total_size = parent_tx.estimated_size() + child_tx.estimated_size()
                parent_txid = parent_tx.txid()
                assert parent_txid
                parent_fee = self.wallet.get_tx_fee(parent_txid)
                if parent_fee is None:
                    raise BaseException("can't cpfp: unknown fee for parent transaction.")
                info['total_size'] = '(%s) bytes' % total_size
                max_fee = child_tx.output_value()
                info['input_amount'] = self.format_amount(max_fee) + ' ' + self.base_unit

                def get_child_fee_from_total_feerate(fee_per_kb):
                    fee = fee_per_kb * total_size / 1000 - parent_fee
                    fee = min(max_fee, fee)
                    fee = max(total_size, fee)  # pay at least 1 sat/byte for combined size
                    return fee

                if suggested_feerate is None:
                    suggested_feerate = self.config.fee_per_kb()
                else:
                    suggested_feerate = suggested_feerate*1000
                if suggested_feerate is None:
                    raise BaseException(f'''{_("can't cpfp'")}: {_('dynamic fee estimates not available')}''')


                parent_feerate = parent_fee / parent_tx.estimated_size() * 1000
                info['parent_feerate'] = self.format_fee_rate(parent_feerate) if parent_feerate else ''
                info['fee_rate_for_child'] = self.format_fee_rate(suggested_feerate) if suggested_feerate else ''
                fee_for_child = get_child_fee_from_total_feerate(suggested_feerate)
                info['fee_for_child'] = util.format_satoshis_plain(fee_for_child, decimal_point=self.decimal_point)
                if fee_for_child is None:
                    raise BaseException("fee_for_child is none")
                out_amt = max_fee - fee_for_child
                out_amt_str = (self.format_amount(out_amt) + ' ' + self.base_unit) if out_amt else ''
                info['output_amount'] = out_amt_str
                comb_fee = parent_fee + fee_for_child
                comb_fee_str = (self.format_amount(comb_fee) + ' ' + self.base_unit) if comb_fee else ''
                info['total_fee'] = comb_fee_str
                comb_feerate = comb_fee / total_size * 1000
                comb_feerate_str = self.format_fee_rate(comb_feerate) if comb_feerate else ''
                info['total_feerate'] = comb_feerate_str

                if fee_for_child is None:
                    raise BaseException("fee for chaild is none")  # fee left empty, treat is as "cancel"
                if fee_for_child > max_fee:
                    raise BaseException('max fee exceeded')
            return json.dumps(info)
        except BaseException as e:
            raise e

    def create_cpfp_tx(self, tx_hash, fee_for_child):
        try:
            self._assert_wallet_isvalid()
            parent_tx = self.wallet.db.get_transaction(tx_hash)
            if not parent_tx:
                raise BaseException("get transaction failed")
            new_tx = self.wallet.cpfp(parent_tx, self.get_amount(fee_for_child))
            new_tx.set_rbf(self.rbf)
            out = {
                'new_tx': str(new_tx)
            }

            try:
                self.do_save(new_tx)
                if self.label_flag and self.wallet.wallet_type != "standard":
                    self.label_plugin.push_tx(self.wallet, 'createtx', new_tx.txid(), str(new_tx))
            except BaseException as e:
                print(f"cpfp push_tx createtx error {e}")
                pass
            return json.dumps(out)
        except BaseException as e:
            raise e

    #######

    # network server
    def get_default_server(self):
        '''
        Get default electrum server
        :return: json like {'host':'127.0.0.1', 'port':'3456'}
        '''
        try:
            self._assert_daemon_running()
            net_params = self.network.get_parameters()
            host, port, protocol = net_params.server.host, net_params.server.port, net_params.server.protocol
        except BaseException as e:
            raise e

        default_server = {
            'host': host,
            'port': port,
        }
        return json.dumps(default_server)

    def set_server(self, host, port):
        '''
        Custom server
        :param host: host as str
        :param port: port as str
        :return:
        '''
        try:
            self._assert_daemon_running()
            net_params = self.network.get_parameters()
            try:
                server = ServerAddr.from_str_with_inference('%s:%s' %(host, port))
                if not server: raise Exception("failed to parse")
            except BaseException as e:
                raise e
            net_params = net_params._replace(server=server,
                                             auto_connect=True)
            self.network.run_from_another_thread(self.network.set_parameters(net_params))
        except BaseException as e:
            raise e
        return ""

    def get_server_list(self):
        '''
        Get Servers
        :return: servers info as json
        '''
        try:
            self._assert_daemon_running()
            servers = self.daemon.network.get_servers()
        except BaseException as e:
            raise e
        return json.dumps(servers)

    def rename_wallet(self, old_name, new_name):
        '''
        Rename the wallet
        :param old_name: old name as str
        :param new_name: new name as str
        :return:
        '''
        try:
            self._assert_daemon_running()
            if old_name is None or new_name is None:
                raise BaseException("wallet_name can't be none")
            else:
                self.daemon.delete_wallet(old_name)
                os.rename(self._wallet_path(old_name), self._wallet_path(new_name))
                temp_local_wallet_info = {}
                for key, value in self.local_wallet_info.items():
                    if key == old_name:
                        temp_local_wallet_info[new_name] = value
                    else:
                        temp_local_wallet_info[key] = value
                self.local_wallet_info = temp_local_wallet_info
                self.config.set_key('all_wallet_type_info', self.local_wallet_info)
                self.load_wallet(new_name, password=self.android_id)
                self.select_wallet(new_name)
                return new_name
        except BaseException as e:
            raise e
        return ""

    def select_wallet(self, name):
        '''
        Select wallet by name
        :param name: name as str
        :return:
        '''
        try:
            self._assert_daemon_running()
            if name is None:
                self.wallet = None
            else:
                self.wallet = self.daemon._wallets[self._wallet_path(name)]

            self.wallet.use_change = self.config.get('use_change', False)

            if -1 != self.wallet.wallet_type.find("eth"):
                print("ETH get balance")
                # addrs = self.wallet.get_addresses()
                # checksum_from_address = self.pywalib.web3.toChecksumAddress(addrs[0])
                # all_balance = self.wallet.get_all_balance(checksum_from_address)
            else:
                c, u, x = self.wallet.get_balance()
                print("console.select_wallet %s %s %s==============" % (c, u, x))
                print("console.select_wallet[%s] blance = %s wallet_type = %s use_change=%s add = %s " % (
                    name, self.format_amount_and_units(c), self.wallet.wallet_type, self.wallet.use_change,
                    self.wallet.get_addresses()))
                util.trigger_callback("wallet_updated", self.wallet)

                fait = self.daemon.fx.format_amount_and_units(c) if self.daemon.fx else None
                info = {
                    "balance": self.format_amount(c) + ' (%s)' % fait,
                    "name": name
                }
                if self.label_flag and self.wallet.wallet_type != "standard":
                    self.label_plugin.load_wallet(self.wallet)
            return json.dumps(info)
        except BaseException as e:
            raise BaseException(e)
        return ""

    def list_wallets(self):
        """List available wallets"""
        name_wallets = sorted([name for name in os.listdir(self._wallet_path())])
        name_info = {}
        for name in name_wallets:
            name_info[name] = self.local_wallet_info.get(name) if self.local_wallet_info.__contains__(name) else {
                'type': 'unknow', 'time': time.time(), 'xpubs': [], 'seed': ''}

        name_info = sorted(name_info.items(), key=lambda item: item[1]['time'], reverse=True)
        out = []
        for key, value in name_info:
            temp_info = {}
            addr_type = {}
            addr_type['type'] = value['type']
            wallet_temp = self.daemon._wallets[self._wallet_path(key)]
            addr_type['addr'] = wallet_temp.get_addresses()[0]
            temp_info[key] = addr_type
            out.append(temp_info)
        return json.dumps(out)

    def delete_wallet_from_deamon(self, name):
        try:
            self._assert_daemon_running()
            self.daemon.delete_wallet(name)
        except BaseException as e:
            raise BaseException(e)

    def delete_wallet(self, name=None):
        """Delete a wallet"""
        try:
            self.delete_wallet_from_deamon(self._wallet_path(name))
            if self.local_wallet_info.__contains__(name):
                wallet_type = self.local_wallet_info.get(name)['type']
                if wallet_type == "btc-hd-standard" or -1 != wallet_type.find("btc-hd-hw")\
                    or wallet_type == "eth-hd-standard" or -1 != wallet_type.find("eth-hd-hw"):
                    ## delete all derived wallet
                    self.delete_derived_wallet(self.btc_hd_wallet if -1 != wallet_type.find('btc') else self.eth_hd_wallet)
                elif wallet_type == 'btc-derived-standard' or -1 != wallet_type.find('btc-hw-derived')\
                     or wallet_type == 'eth-derived-standard' or -1 != wallet_type.find('eth-hw-derived'):
                    raise BaseException("HD derivted wallet not be delete")
                self.local_wallet_info.pop(name)
                self.config.set_key('all_wallet_type_info', self.local_wallet_info)
            # os.remove(self._wallet_path(name))
        except Exception as e:
            raise BaseException(e)

    def _assert_daemon_running(self):
        if not self.daemon_running:
            raise BaseException("Daemon not running")
            # Same wording as in electrum script.

    def _assert_wizard_isvalid(self):
        if self.wizard is None:
            raise BaseException("Wizard not running")
            # Log callbacks on stderr so they'll appear in the console activity.

    def _assert_wallet_isvalid(self):
        if self.wallet is None:
            raise BaseException("Wallet is None")
            # Log callbacks on stderr so they'll appear in the console activity.

    def _assert_hd_wallet_isvalid(self):
        if self.btc_hd_wallet is None and self.eth_hd_wallet is None:
            raise BaseException("HD Wallet is None")
            # Log callbacks on stderr so they'll appear in the console activity.

    def _on_callback(self, *args):
        util.print_stderr("[Callback] " + ", ".join(repr(x) for x in args))

    def _wallet_path(self, name=""):
        if name is None:
            if not self.wallet:
                raise ValueError("No wallet selected")
            return self.wallet.storage.path
        else:
            wallets_dir = join(self.user_dir, "wallets")
            util.make_dir(wallets_dir)
            return util.standardize_path(join(wallets_dir, name))


all_commands = commands.known_commands.copy()
for name, func in vars(AndroidCommands).items():
    if not name.startswith("_"):
        all_commands[name] = commands.Command(func, "")

SP_SET_METHODS = {
    bool: "putBoolean",
    float: "putFloat",
    int: "putLong",
    str: "putString",
}

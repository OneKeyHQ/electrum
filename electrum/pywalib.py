#!/usr/bin/env python
# -*- coding: utf-8 -*-
import http
import math
import os
import json
import time
from contextlib import contextmanager
from enum import Enum
from os.path import expanduser
from typing import List
from urllib.parse import urljoin

from electrum.util import Ticker, make_aiohttp_session
import requests
# from eth_accounts.account_utils import AccountUtils
from eth_keyfile import keyfile
from eth_utils import to_checksum_address
from web3 import HTTPProvider, Web3

from electrum_gui.common.provider.manager import get_provider_by_chain
from .i18n import _
from electrum_gui.common.provider.data import Token, TransactionStatus
from electrum_gui.common.provider.clients import eth as eth_clients
from electrum_gui.common.provider.interfaces import ProviderInterface
from . import util
from .eth_transaction import Eth_Transaction
from decimal import Decimal
import sqlite3
from .network import Network
from electrum.constants import read_json
eth_servers = {}

ETHERSCAN_API_KEY = "R796P9T31MEA24P8FNDZBCA88UHW8YCNVW"
INFURA_PROJECT_ID = "f001ce716b6e4a33a557f74df6fe8eff"
CMC_API_KEY = "e134e277-0927-4e00-8fb5-0e14598516b3"
ROUND_DIGITS = 3
DEFAULT_GAS_PRICE_WEI = int(20 * 1e9)
DEFAULT_GAS_LIMIT = 21000
GWEI_BASE = 1000000000
DEFAULT_GAS_SPEED = 1
KEYSTORE_DIR_PREFIX = expanduser("~")
# default pyethapp keystore path
KEYSTORE_DIR_SUFFIX = ".electrum/eth/keystore/"

# REQUESTS_HEADERS = {
#     "User-Agent": "https://github.com/AndreMiras/PyWallet",
# }

import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

class InsufficientFundsException(Exception):
    """
    Raised when user want to send funds and have insufficient balance on address
    """
    pass


class InsufficientERC20FundsException(Exception):
    """
    Raised when user want to send ERC20 contract tokens and have insufficient balance
    of these tokens on wallet's address
    """
    pass


class ERC20NotExistsException(Exception):
    """
    Raised when user want manipulate with token which doesn't exist in wallet.
    """
    pass


class InvalidTransactionNonceException(Exception):
    """
    Raised when duplicated nonce occur or any other problem with nonce
    """
    pass


class InvalidValueException(Exception):
    """
    Raised when some of expected values is not correct.
    """
    pass

class InvalidAddress(ValueError):
    """
    The supplied address does not have a valid checksum, as defined in EIP-55
    """
    pass

class InvalidPasswordException(Exception):
    """
    Raised when invalid password was entered.
    """
    pass


class InfuraErrorException(Exception):
    """
    Raised when wallet cannot connect to infura node.
    """

class UnknownEtherscanException(Exception):
    pass


class NoTransactionFoundException(UnknownEtherscanException):
    pass


def get_abi_json():
    root_dir = os.path.dirname(os.path.abspath(__file__))
    abi_path = os.path.join(root_dir, '.', 'abi.json')
    with open(abi_path) as f:
        fitcoin = json.load(f)
    return fitcoin

def handle_etherscan_response_json(response_json):
    """Raises an exception on unexpected response json."""
    status = response_json["status"]
    message = response_json["message"]
    if status != "1":
        if message == "No transactions found":
            raise NoTransactionFoundException()
        else:
            raise UnknownEtherscanException(response_json)
    #assert message == "OK"


def handle_etherscan_response_status(status_code):
    """Raises an exception on unexpected response status."""
    if status_code != http.HTTPStatus.OK:
        raise UnknownEtherscanException(status_code)


def handle_etherscan_response(response):
    """Raises an exception on unexpected response."""
    handle_etherscan_response_status(response.status_code)
    handle_etherscan_response_json(response.json())


def requests_get(url):
    try:
        return requests.get(url, headers=headers, timeout=2, verify=False)
    except BaseException as e:
        raise e

headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36"
}

class PyWalib:
    server_config = None
    coin_symbol = None
    web3 = None
    market_server = None
    market_server_dict = None
    tx_list_server = None
    gas_server = None
    symbols_price = {}
    config = None
    chain_type = None
    chain_id = None
    conn = None
    cursor = None
    def __init__(self, config, chain_type="mainnet", path=""):
        PyWalib.chain_type = chain_type
        PyWalib.config = config
        PyWalib.conn = sqlite3.connect(path)
        PyWalib.cursor = self.conn.cursor()
        self.create_db()
        self.init_symbols()

    def create_db(self):
        PyWalib.cursor.execute("CREATE TABLE IF NOT EXISTS txlist (tx_hash TEXT PRIMARY KEY, address TEXT, time INTEGER, tx TEXT)")

    def init_symbols(self):
        symbol_list = self.config.get("symbol_list", {'ETH':'','HT':'', 'BNB': ''})
        for cache_key in symbol_list:
            from_cur, contract_address = cache_key.split(":") if ":" in cache_key else (cache_key, None)
            PyWalib.symbols_price[cache_key] = PyWalib.get_currency(from_cur, 'BTC', contract_address=contract_address)

        global symbol_ticker
        symbol_ticker = Ticker(5*60, self.get_symbols_price)
        symbol_ticker.start()

    def get_symbols_price(self):
        try:
            for cache_key, price in PyWalib.symbols_price.items():
                from_cur, contract_address = cache_key.split(":") if ":" in cache_key else (cache_key, None)
                PyWalib.symbols_price[cache_key] = self.get_currency(from_cur, 'BTC', contract_address=contract_address)
                PyWalib.config.set_key("symbol_list", PyWalib.symbols_price)
                time.sleep(1)
        except BaseException as e:
            raise e

    @classmethod
    def get_json(cls, url):
        network = Network.get_instance()
        proxy = network.proxy if network else None
        with make_aiohttp_session(proxy) as session:
            with session.get(url) as response:
                response.raise_for_status()
                # set content_type to None to disable checking MIME type
                return response.json(content_type=None)

    @classmethod
    def get_currency(cls, from_cur, to_cur, contract_address=None):
        if (
                from_cur
                and from_cur.lower() == "eth"
                and contract_address
                and PyWalib.market_server_dict
                and PyWalib.market_server_dict.get("cgk")
        ):
            try:
                contract_address = contract_address.lower()
                url = urljoin(PyWalib.market_server_dict["cgk"], "/api/v3/simple/token_price/ethereum")
                response = requests.get(
                    url, params={"vs_currencies": "btc", "contract_addresses": contract_address}
                ).json()
                if not response or not response.get(contract_address) or not response[contract_address].get("btc"):
                    return 0
                else:
                    return response[contract_address]["btc"]
            except Exception:
                return 0

        try:
            out_price = {}
            for server in PyWalib.market_server:
                for name, url in server.items():
                    if name == "coinbase":
                        try:
                            url += from_cur
                            response = requests.get(url, timeout=5, verify=False)
                            json = response.json()
                            return [str(Decimal(rate)) for (ccy, rate) in json["data"]["rates"].items()
                                    if ccy == to_cur][0]
                        except BaseException:
                            pass
                    # if name == "binance":
                    #     url += from_cur.upper()+to_cur.upper()
                    #     try:
                    #         response = requests.get(url, timeout=3, verify=False)
                    #         obj = response.json()
                    #         out_price[name] = obj['data']['lastprice']
                    #     except BaseException as e:
                    #         pass
                    elif name == 'bixin':
                        url += from_cur.upper() + '/' + to_cur.upper()
                        try:
                            response = requests.get(url, timeout=3, verify=False)
                            obj = response.json()
                            #out_price[name] = obj['data']['price']
                            return obj['data']['price']
                        except BaseException:
                            pass
                    # elif name == 'huobi':
                    #     url += from_cur.lower() + to_cur.lower()
                    #     try:
                    #         response = requests.get(url, timeout=3, verify=False)
                    #         obj = response.json()
                    #         out_price[name] = (obj['data']['bid'][0] + obj['data']['ask'][0])/2.0
                    #     except BaseException as e:
                    #         pass
                    # elif name == 'ok':
                    #     print("TODO")
                    elif name == "cmc":
                        try:
                            response = requests.get(url, headers={"X-CMC_PRO_API_KEY": CMC_API_KEY},
                                                    params={"symbol": from_cur, "convert": to_cur}).json()
                            price = response["data"][from_cur.upper()][0]["quote"][to_cur.upper()]["price"]
                            return price
                        except BaseException:
                            pass

            # return_price = 0.0
            # for price in out_price.values():
            #     return_price += float(price)
            # return return_price/len(out_price)
        except BaseException as e:
            print(f"get symbol price error {e}")
            pass

    @staticmethod
    def get_web3():
        return PyWalib.web3

    @staticmethod
    def set_server(info):
        PyWalib.market_server = info['Market']
        PyWalib.market_server_dict = {list(i.keys())[0]: list(i.values())[0] for i in info['Market'] if i}
        PyWalib.tx_list_server = info['TxliServer']
        PyWalib.gas_server = info['GasServer']
        PyWalib.coin_symbol = info["symbol"]
        PyWalib.server_config = info
        for i in info['Provider']:
            if PyWalib.chain_type in i:
                url = i[PyWalib.chain_type]
                chain_id = i['chainid']
        PyWalib.web3 = Web3(HTTPProvider(url))
        PyWalib.chain_id = chain_id
        if hasattr(PyWalib, "__explorer__"):
            delattr(PyWalib, "__explorer__")

    @classmethod
    @contextmanager
    def override_server(cls, config):
        cur_config = cls.server_config

        try:
            cls.set_server(config)
            yield
        finally:
            cls.set_server(cur_config)

    @staticmethod
    def get_coin_price(from_cur, contract_address=None):
        try:
            from_cur = from_cur.upper()
            cache_key = f"{from_cur}:{contract_address}" if contract_address else from_cur

            if cache_key in PyWalib.symbols_price:
                symbol_price = PyWalib.symbols_price[cache_key]
                return (symbol_price if symbol_price is not None
                        else PyWalib.get_currency(from_cur, 'BTC', contract_address=contract_address))
            else:
                symbol_price = PyWalib.get_currency(from_cur, 'BTC', contract_address=contract_address)
                PyWalib.symbols_price[cache_key] = symbol_price
                PyWalib.config.set_key("symbol_list", PyWalib.symbols_price)
                return symbol_price
        except BaseException as e:
            return '0'

    def get_gas_price(self, coin) -> dict:
        if coin in ("bsc", "heco"):
            price_per_unit = self.get_provider().get_price_per_unit_of_fee()
            return {
                "fast": {
                    "gas_price": int(price_per_unit.fast.price / 1e9),
                    "time": 1,
                },
                "normal": {
                    "gas_price": int(price_per_unit.normal.price / 1e9),
                    "time": 2,
                },
                "slow": {
                    "gas_price": int(price_per_unit.slow.price / 1e9),
                    "time": 3,
                },
            }
        elif PyWalib.gas_server is not None:
            response = requests.get(PyWalib.gas_server, headers=headers)
            obj = response.json()
            out = dict()
            if obj['code'] == 200:
                estimated_time = {
                    "rapid": 0.25,
                    "fast": 1,
                    "standard": 3,
                    "slow": 10
                }
                out.update({
                    key: {
                        "gas_price": int(self.web3.fromWei(price, "gwei")),
                        "time": estimated_time[key]
                    }
                    for key, price in obj["data"].items() if key in estimated_time
                })

            if "standard" in out:
                out["normal"] = out["standard"]
                out.pop("standard")
            return out

    def get_max_use_gas(self, gas_price):
        gas = gas_price * DEFAULT_GAS_LIMIT
        return self.web3.fromWei(gas * GWEI_BASE, 'ether')


    @classmethod
    def get_best_time_by_gas_price(cls, gas_price: float, estimate_gas_prices: dict) -> float:
        if "rapid" in estimate_gas_prices and gas_price >= estimate_gas_prices["rapid"]["gas_price"]:
            return estimate_gas_prices["rapid"]["time"]
        elif "fast" in estimate_gas_prices and gas_price >= estimate_gas_prices["fast"]["gas_price"]:
            return estimate_gas_prices["fast"]["time"]
        elif "standard" in estimate_gas_prices and gas_price >= estimate_gas_prices["standard"]["gas_price"]:
            return estimate_gas_prices["standard"]["time"]
        else:
            return 10

    def estimate_gas_limit(self, from_address, to_address=None, contract=None, value="0", data=None) -> int:
        try:
            if not to_address:
                return 40000

            estimate_payload = {}
            to_address = self.web3.toChecksumAddress(to_address)

            if contract:
                data = Eth_Transaction.get_tx_erc20_data_field(to_address,
                                                               int(Decimal(value) * pow(10, contract.get_decimals())))
                estimate_payload.update({
                    "to": contract.get_address(),
                    "data": data,
                    "value": 0
                })
            elif data or self.is_contract(to_address):
                estimate_payload.update({
                    "to": to_address,
                    "value": self.web3.toWei(value, "ether"),
                })
                if data:
                    estimate_payload["data"] = data

            if not estimate_payload:
                return DEFAULT_GAS_LIMIT

            estimate_payload.update({"from": from_address})
            gas_limit = self.web3.eth.estimateGas(estimate_payload)
            gas_limit = int(gas_limit * 1.2)
            return gas_limit
        except Exception as e:
            return 40000

    def get_transaction(self, from_address, to_address, value,
                        contract=None, gas_price=None, none=None, gas_limit=None, data=None):
        if (
            not self.web3.isAddress(from_address)
            or not self.web3.isAddress(to_address)
            or (contract and not self.web3.isAddress(contract.get_address()))
        ):
            raise InvalidAddress()

        from_address = self.web3.toChecksumAddress(from_address)
        to_address = self.web3.toChecksumAddress(to_address)

        try:
            if contract:
                decimal = contract.get_decimals()
                value = int(Decimal(value) * pow(10, decimal))
            else:
                value = self.web3.toWei(value, "ether")

            gas_price = DEFAULT_GAS_PRICE_WEI if gas_price is None else self.web3.toWei(gas_price, "gwei")

            assert value > 0
            assert gas_price > 0

            if gas_limit:
                gas_limit = int(gas_limit)
                assert gas_limit > 0
        except (ValueError, AssertionError, TypeError):
            raise InvalidValueException()

        if contract:
            # check whether there is sufficient ERC20 token balance
            erc20_balance = self._get_balance_inner(from_address, contract)
            if value > erc20_balance:
                raise InsufficientERC20FundsException()

        nonce = self.get_nonce(from_address) if none is None else int(none)

        tx_payload = dict(chain_id=int(PyWalib.chain_id), gas_price=gas_price, nonce=nonce,
                          to_address=contract.get_address() if contract else to_address,
                          value=0 if contract else value)

        if not data and contract:
            data = Eth_Transaction.get_tx_erc20_data_field(to_address, value)
        if data:
            tx_payload["data"] = data

        if not gas_limit:
            if not contract and not self.is_contract(to_address):
                gas_limit = DEFAULT_GAS_LIMIT
            else:
                params = {
                    "from": from_address,
                    "to": tx_payload["to_address"],
                    "value": tx_payload["value"],
                }
                if tx_payload.get("data"):
                    params["data"] = tx_payload["data"]

                gas_limit = self.web3.eth.estimateGas(params)

        tx_payload["gas"] = gas_limit
        tx_dict = Eth_Transaction.build_transaction(**tx_payload)

        # check whether there is sufficient eth balance for this transaction
        balance = self._get_balance_inner(from_address)
        fee_cost = int(tx_dict['gas'] * tx_dict['gasPrice'])
        eth_required = fee_cost + (value if not contract else 0)

        if eth_required > balance:
            raise InsufficientFundsException()

        return tx_dict

    @classmethod
    def get_token_info(cls, symbol, contract_address):
        from .eth_contract import Eth_Contract
        contract_address = PyWalib.web3.toChecksumAddress(contract_address)
        contract = Eth_Contract(symbol, contract_address)
        token_info = {
            "chain_id": PyWalib.chain_id,
            "decimals" : contract.get_decimals(),
            "address" : contract_address,
            "symbol" : contract.get_symbol(),
            "name" : contract.get_name(),
            "logoURI": "",
            "rank": 0
        }
        return token_info

    @classmethod
    def get_nonce(cls,  address):
        return cls.get_provider().get_address(address).nonce

    def is_contract(self, address) -> bool:
        return len(self.web3.eth.getCode(self.web3.toChecksumAddress(address))) > 0

    def sign_and_send_tx(self, account, tx_dict):
        tx_hex = Eth_Transaction.sign_transaction(account, tx_dict)
        return self._send_tx(tx_hex)

    def serialize_and_send_tx(self, tx_dict, vrs):
        tx_hex = Eth_Transaction.serialize_tx(tx_dict, vrs)
        return self._send_tx(tx_hex)

    @classmethod
    def _send_tx(cls, tx_hex: str) -> str:
        return cls.get_provider().broadcast_transaction(tx_hex).txid

    @classmethod
    def get_balance(cls, wallet_address, contract=None):
        balance = cls._get_balance_inner(wallet_address, contract)
        if contract is None:
            return "eth", cls.web3.fromWei(balance, "ether")
        else:
            erc_balance = Decimal(balance) / pow(10, Decimal(contract.contract_decimals))
            return contract.get_symbol(), erc_balance

    @classmethod
    def _get_balance_inner(cls, address: str, contract=None):
        try:
            return cls.get_provider().get_balance(address,
                                                  token=None if not contract else Token(contract=contract.get_address()))
        except Exception:
            return 0

    @classmethod
    def get_chain_code(cls) -> str:
        chain_code = cls.server_config["id"]

        if cls.chain_type == "testnet":
            chain_code = f"t{chain_code}"

        return chain_code

    @classmethod
    def get_provider(cls) -> ProviderInterface:
        chain_code = cls.server_config["id"]  # TODO Confirm that the 'id' field has not been used before?

        if cls.chain_type == "testnet":
            chain_code = f"t{chain_code}"

        return get_provider_by_chain(chain_code)

    @classmethod
    def _search_tx_actions(cls, address):
        txs = cls.get_provider().search_txs_by_address(address)
        actions = []

        for tx in txs:
            common_params = {
                "txid": tx.txid,
                "status": tx.status,
                "block_header": tx.block_header,
                "fee": tx.fee,
            }
            for tx_input, tx_output in zip(tx.inputs, tx.outputs):
                action = {
                    **common_params,
                    "from": tx_input.address,
                    "to": tx_output.address,
                    "value": tx_output.value,
                }

                if tx_output.token_address:
                    action["token_address"] = tx_output.token_address

                actions.append(action)

        return actions

    @classmethod
    def get_transaction_history(cls, address, contract=None, search_type=None):
        address = address.lower()
        actions = cls._search_tx_actions(address)

        if contract:
            contract_address = contract.address.lower()
            actions = (i for i in actions if i.get("token_address") and i["token_address"].lower() == contract_address)
        else:
            actions = (i for i in actions if not i.get("token_address"))

        if search_type == "send":
            actions = (i for i in actions if i.get("from") and i["from"].lower() == address)
        elif search_type == "receive":
            actions = (i for i in actions if i.get("to") and i["to"].lower() == address)

        actions = list(actions)
        output_txs = []

        eth_last_price = Decimal(cls.get_coin_price(cls.coin_symbol) or 0)

        if contract:
            coin_last_price = Decimal(cls.get_coin_price(cls.coin_symbol, contract_address=contract.address) or 0)
            decimal_multiply = pow(10, Decimal(contract.contract_decimals))
        else:
            coin_last_price = eth_last_price
            decimal_multiply = pow(10, Decimal(18))

        for action in actions:
            block_header = action["block_header"]
            target_address = action["to"]
            amount = Decimal(action["value"]) / decimal_multiply
            fiat = amount * coin_last_price
            fee = Decimal(cls.web3.fromWei(action["fee"].usage * action["fee"].price_per_unit, "ether"))
            fee_fiat = eth_last_price * fee

            tx_status = _("Unconfirmed")
            show_status = [1, _("Unconfirmed")]
            if action["status"] == TransactionStatus.REVERED:
                tx_status = _("Sending failure")
                show_status = [2, _("Sending failure")]
            elif action["status"] == TransactionStatus.CONFIRMED:
                tx_status = (
                    _("{} confirmations").format(block_header.confirmations)
                    if block_header and block_header.confirmations > 0
                    else _("Confirmed")
                )
                show_status = [3, _("Confirmed")]

            output_tx = {
                "type": "history",
                "coin": contract.symbol if contract else cls.coin_symbol,
                "tx_status": tx_status,
                "show_status": show_status,
                'fee': fee,
                'fee_fiat': fee_fiat,
                "date": util.format_time(block_header.block_time) if block_header else _("Unknown"),
                "tx_hash": action["txid"],
                "is_mine": action["from"].lower() == address,
                'height': block_header.block_number if block_header else -2,
                "confirmations": block_header.confirmations if block_header else 0,
                'input_addr': [action["from"]],
                'output_addr': [action["to"]],
                "address": f"{target_address[:6]}...{target_address[-6:]}",
                "amount": amount,
                "fiat": fiat
            }
            output_txs.append(output_tx)

        return output_txs

    @classmethod
    def get_all_txid(cls, address) -> List[str]:
        return cls.get_provider().search_txids_by_address(address)

    @classmethod
    def get_transaction_info(cls, txid) -> dict:
        tx = cls.get_provider().get_transaction_by_txid(txid)
        last_price = Decimal(cls.get_coin_price(cls.coin_symbol) or "0")
        amount = Decimal(cls.web3.fromWei(tx.outputs[0].value, "ether"))
        fiat = last_price * amount
        fee = Decimal(cls.web3.fromWei(tx.fee.usage * tx.fee.price_per_unit, "ether"))
        fee_fiat = last_price * fee

        tx_status = _("Unconfirmed")
        show_status = [1, _("Unconfirmed")]
        if tx.status == TransactionStatus.REVERED:
            tx_status = _("Sending failure")
            show_status = [2, _("Sending failure")]
        elif tx.status == TransactionStatus.CONFIRMED:
            tx_status = (
                _("{} confirmations").format(tx.block_header.confirmations)
                if tx.block_header and tx.block_header.confirmations > 0
                else _("Confirmed")
            )
            show_status = [3, _("Confirmed")]

        return {
            'txid': txid,
            'can_broadcast': False,
            'amount': amount,
            "fiat": fiat,
            'fee': fee,
            'fee_fiat': fee_fiat,
            'description': "",
            'tx_status': tx_status,
            "show_status": show_status,
            'sign_status': None,
            'output_addr': [tx.outputs[0].address],
            'input_addr': [tx.inputs[0].address],
            'height': tx.block_header.block_number if tx.block_header else -2,
            'cosigner': [],
            'tx': tx.raw_tx,
        }

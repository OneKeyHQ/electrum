#!/usr/bin/env python
# -*- coding: utf-8 -*-
import http
import math
import os
import json
from enum import Enum
from os.path import expanduser
from electrum_gui.android.utils import Ticker
import requests
# from eth_accounts.account_utils import AccountUtils
from eth_keyfile import keyfile
from eth_utils import to_checksum_address
from web3 import HTTPProvider, Web3
from .eth_transaction import Eth_Transaction
from decimal import Decimal
from electrum.constants import read_json
eth_servers = {}

ETHERSCAN_API_KEY = "R796P9T31MEA24P8FNDZBCA88UHW8YCNVW"
INFURA_PROJECT_ID = "f001ce716b6e4a33a557f74df6fe8eff"
ROUND_DIGITS = 3
DEFAULT_GAS_PRICE_GWEI = 4
DEFAULT_GAS_LIMIT = 25000
GWEI_BASE = 1000000000
DEFAULT_GAS_SPEED = 1
KEYSTORE_DIR_PREFIX = expanduser("~")
# default pyethapp keystore path
KEYSTORE_DIR_SUFFIX = ".electrum/eth/keystore/"

REQUESTS_HEADERS = {
    "User-Agent": "https://github.com/AndreMiras/PyWallet",
}

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

class ChainID(Enum):
    MAINNET = 1
    MORDEN = 2
    ROPSTEN = 3
    CUSTOMER = 11


class HTTPProviderFactory:
    global eth_servers
    eth_servers = read_json('eth_servers.json', {})
    PROVIDER_URLS = {
        ChainID.MAINNET: f"{eth_servers['Provider']['mainnet']+INFURA_PROJECT_ID}",
        ChainID.ROPSTEN: f"{eth_servers['Provider']['ropsten']+INFURA_PROJECT_ID}",
        ChainID.CUSTOMER: f"{eth_servers['Provider']['customer']}",
    }

    @classmethod
    def create(cls, chain_id=ChainID.MAINNET) -> HTTPProvider:
        url = cls.PROVIDER_URLS[chain_id]
        return HTTPProvider(url)


def get_etherscan_prefix(chain_id=ChainID.MAINNET) -> str:
    PREFIXES = {
        ChainID.MAINNET: eth_servers['Etherscan']['mainnet'],
        ChainID.ROPSTEN: eth_servers['Etherscan']['ropsten'],
    }
    return PREFIXES[chain_id]


def handle_etherscan_response_json(response_json):
    """Raises an exception on unexpected response json."""
    status = response_json["status"]
    message = response_json["message"]
    if status != "1":
        if message == "No transactions found":
            raise NoTransactionFoundException()
        else:
            raise UnknownEtherscanException(response_json)
    assert message == "OK"


def handle_etherscan_response_status(status_code):
    """Raises an exception on unexpected response status."""
    if status_code != http.HTTPStatus.OK:
        raise UnknownEtherscanException(status_code)


def handle_etherscan_response(response):
    """Raises an exception on unexpected response."""
    handle_etherscan_response_status(response.status_code)
    handle_etherscan_response_json(response.json())


def requests_get(url):
    return requests.get(url, headers=REQUESTS_HEADERS)

headers = {
        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36"
}

class PyWalib:
    web3 = None
    symbols_price = {}
    config = None
    chain_id = None
    def __init__(self, config, chain_id=ChainID.MAINNET):
        PyWalib.chain_id = chain_id
        PyWalib.config = config
        self.provider = HTTPProviderFactory.create(PyWalib.chain_id)
        PyWalib.web3 = Web3(self.provider)
        self.init_symbols()

    def init_symbols(self):
        symbol_list = self.config.get("symbol_list", {'ETH':'','EOS':''})
        for symbol in symbol_list:
            PyWalib.symbols_price[symbol] = self.get_currency(symbol, 'BTC')
        global symbol_ticker
        symbol_ticker = Ticker(5.0, self.get_symbols_price)
        symbol_ticker.start()

    def get_symbols_price(self):
        try:
            for symbol, price in PyWalib.symbols_price.items():
                PyWalib.symbols_price[symbol] = self.get_currency(symbol, 'BTC')
                PyWalib.config.set_key("symbol_list", PyWalib.symbols_price)
            print(f"symbol info.......{PyWalib.symbols_price}")
        except BaseException as e:
            raise e

    @staticmethod
    def get_currency(from_cur, to_cur):
        try:
            url = eth_servers['Market']
            url += from_cur.upper()+'/'+to_cur.upper()
            response = requests.get(url, timeout=2, verify=False)
            obj = response.json()
            return obj['data']['price']
        except BaseException as e:
            print(f"get symbol price error {e}")
            pass

    @staticmethod
    def get_web3():
        return PyWalib.web3

    @staticmethod
    def get_coin_price(from_cur):
        try:
            from_cur = from_cur.upper()
            if from_cur in PyWalib.symbols_price:
                return PyWalib.symbols_price[from_cur]
            else:
                symbol_price = PyWalib.get_currency(from_cur, 'BTC')
                PyWalib.symbols_price[from_cur] = symbol_price
                PyWalib.config.set_key("symbol_list", PyWalib.symbols_price)
                return symbol_price
        except BaseException as e:
            raise e


    def get_gas_price(self):
        try:
            response = requests.get(eth_servers['GasServer'], headers=headers)
            obj = response.json()
            out = dict()
            if obj['code'] == 200:
                for type, wei in obj['data'].items():
                    fee_info = dict()
                    fee_info['price'] = int(self.web3.fromWei(wei, "gwei"))
                    if type == "rapid":
                        fee_info['time'] = "15 Seconds"
                    elif type == "fast":
                        fee_info['time'] = "1 Minute"
                    elif type == "standard":
                        fee_info['time'] = "3 Minutes"
                    elif type == "timestamp":
                        fee_info['time'] = "> 10 Minutes"
                    out[type] = fee_info
            return json.dumps(out)
        except BaseException as ex:
            raise ex

    def get_transaction(self, from_address, to_address, value, contract=None, gasprice = DEFAULT_GAS_PRICE_GWEI * (10 ** 9)):
        try:
            float(value)
        except ValueError:
            raise InvalidValueException()

        if contract is None:  # create ETH transaction dictionary
            tx_dict = Eth_Transaction.build_transaction(
                to_address=self.web3.toChecksumAddress(to_address),
                value=self.web3.toWei(value, "ether"),
                gas=DEFAULT_GAS_LIMIT,  # fixed gasLimit to transfer ether from one EOA to another EOA (doesn't include contracts)
                #gas_price=self.web3.eth.gasPrice * gas_price_speed,
                gas_price=self.web3.toWei(gasprice, "gwei"),
                # be careful about sending more transactions in row, nonce will be duplicated
                nonce=self.web3.eth.getTransactionCount(self.web3.toChecksumAddress(from_address)),
                chain_id=PyWalib.chain_id.value
            )
        else:  # create ERC20 contract transaction dictionary
            erc20_decimals = contract.get_decimals()
            # token_amount = int(float(value) * (10 ** erc20_decimals))
            token_amount = int(float(value))
            data_for_contract = Eth_Transaction.get_tx_erc20_data_field(to_address, token_amount)

            # check whether there is sufficient ERC20 token balance
            _, erc20_balance = self.get_balance(self.web3.toChecksumAddress(from_address), contract)
            if float(value) > erc20_balance:
                raise InsufficientERC20FundsException()

            addr = self.web3.toChecksumAddress(contract.get_address())
            #calculate how much gas I need, unused gas is returned to the wallet
            estimated_gas = self.web3.eth.estimateGas(
                {'to': contract.get_address(),
                 'from': self.web3.toChecksumAddress(from_address),
                 'data': data_for_contract
                 })

            tx_dict = Eth_Transaction.build_transaction(
                to_address=contract.get_address(),  # receiver address is defined in data field for this contract
                value=0,  # amount of tokens to send is defined in data field for contract
                gas=estimated_gas,
                gas_price=self.web3.toWei(gasprice, "gwei"),
                # be careful about sending more transactions in row, nonce will be duplicated
                nonce=self.web3.eth.getTransactionCount(self.web3.toChecksumAddress(from_address)),
                chain_id=PyWalib.chain_id.value,
                data=data_for_contract
            )

        # check whether to address is valid checksum address
        if not self.web3.isChecksumAddress(self.web3.toChecksumAddress(to_address)):
            raise InvalidAddress()

        # check whether there is sufficient eth balance for this transaction
        #_, balance = self.get_balance(from_address)
        balance = self.web3.fromWei(self.web3.eth.getBalance(self.web3.toChecksumAddress(from_address)), 'ether')
        transaction_const_wei = tx_dict['gas'] * tx_dict['gasPrice']
        transaction_const_eth = self.web3.fromWei(transaction_const_wei, 'ether')
        if contract is None:
            if (transaction_const_eth + Decimal(value)) > balance:
                raise InsufficientFundsException()
        else:
            if transaction_const_eth > balance:
                raise InsufficientFundsException()
        return tx_dict

    def sign_and_send_tx(self, account, tx_dict):
        tx_hash = Eth_Transaction.send_transaction(tx_dict)

        print('Pending', end='', flush=True)
        while True:
            tx_receipt = self.web3.eth.getTransactionReceipt(tx_hash)
            if tx_receipt is None:
                print('.', end='', flush=True)
                import time
                time.sleep(1)
            else:
                print('\nTransaction mined!')
                break

        return tx_hash

    def serialize_and_send_tx(self, tx_dict, vrs):
        tx_hash = Eth_Transaction.serialize_and_send_tx(self.web3, tx_dict, vrs)
        print('Pending', end='', flush=True)
        while True:
            tx_receipt = self.web3.eth.getTransactionReceipt(tx_hash)
            if tx_receipt is None:
                print('.', end='', flush=True)
                import time

                time.sleep(1)
            else:
                print('\nTransaction mined!')
                break

    @staticmethod
    def get_balance(wallet_address, contract=None):
        if contract is None:
            eth_balance = PyWalib.get_web3().fromWei(PyWalib.get_web3().eth.getBalance(wallet_address), 'ether')
            return "eth", eth_balance
        else:
            erc_balance = contract.get_balance(wallet_address)
            return contract.get_symbol(), erc_balance

    # def get_balance_web3(self, address):
    #     """
    #     The balance is returned in ETH rounded to the second decimal.
    #     """
    #     address = to_checksum_address(address)
    #     balance_wei = self.web3.eth.getBalance(address)
    #     balance_eth = balance_wei / float(pow(10, 18))
    #     balance_eth = round(balance_eth, ROUND_DIGITS)
    #     return balance_eth

    @staticmethod
    def get_transaction_history(address):
        """
        Retrieves the transaction history from etherscan.io.
        """
        address = to_checksum_address(address)
        url = get_etherscan_prefix(PyWalib.chain_id)
        url += (
            '?module=account&action=txlist'
            '&sort=asc'
            f'&address={address}'
            f'&apikey={ETHERSCAN_API_KEY}'
        )
        response = requests_get(url)
        handle_etherscan_response(response)
        response_json = response.json()
        transactions = response_json['result']
        for transaction in transactions:
            value_wei = int(transaction['value'])
            value_eth = value_wei / float(pow(10, 18))
            value_eth = round(value_eth, ROUND_DIGITS)
            from_address = to_checksum_address(transaction['from'])
            to_address = transaction['to']
            # on contract creation, "to" is replaced by the "contractAddress"
            if not to_address:
                to_address = transaction['contractAddress']
            to_address = to_checksum_address(to_address)
            sent = from_address == address
            received = not sent
            extra_dict = {
                'value_eth': value_eth,
                'sent': sent,
                'received': received,
                'from_address': from_address,
                'to_address': to_address,
            }
            transaction.update({'extra_dict': extra_dict})
        # sort by timeStamp
        transactions.sort(key=lambda x: x['timeStamp'])
        return transactions

    @staticmethod
    def get_out_transaction_history(address):
        """
        Retrieves the outbound transaction history from Etherscan.
        """
        transactions = PyWalib.get_transaction_history(address, PyWalib.chain_id)
        out_transactions = []
        for transaction in transactions:
            if transaction['extra_dict']['sent']:
                out_transactions.append(transaction)
        return out_transactions

    # TODO: can be removed since the migration to web3
    @staticmethod
    def get_nonce(address):
        """
        Gets the nonce by counting the list of outbound transactions from
        Etherscan.
        """
        try:
            out_transactions = PyWalib.get_out_transaction_history(
                address, PyWalib.chain_id)
        except NoTransactionFoundException:
            out_transactions = []
        nonce = len(out_transactions)
        return nonce

    @staticmethod
    def handle_web3_exception(exception: ValueError):
        """
        Raises the appropriated typed exception on web3 ValueError exception.
        """
        error = exception.args[0]
        code = error.get("code")
        if code in [-32000, -32010]:
            raise InsufficientFundsException(error)
        else:
            raise UnknownEtherscanException(error)
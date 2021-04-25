#!/usr/bin/env python
# -*- coding: utf-8 -*-
import http
import json
import os
import sqlite3
from contextlib import contextmanager
from os.path import expanduser

import requests
import urllib3
from web3 import HTTPProvider, Web3

from electrum.util import make_aiohttp_session

from .i18n import _
from .network import Network

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


urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


class InsufficientFundsException(Exception):
    """
    Raised when user want to send funds and have insufficient balance on address
    """

    def __init__(self, *args):
        super(InsufficientFundsException, self).__init__(_("Insufficient balance"), *args)


class InsufficientERC20FundsException(Exception):
    """
    Raised when user want to send ERC20 contract tokens and have insufficient balance
    of these tokens on wallet's address
    """

    def __init__(self, *args):
        super(InsufficientERC20FundsException, self).__init__(_("Insufficient ERC20 token balance"), *args)


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

    def __init__(self, *args):
        super(InvalidValueException, self).__init__(_("Invalid Value"), *args)


class InvalidAddress(ValueError):
    """
    The supplied address does not have a valid checksum, as defined in EIP-55
    """

    def __init__(self, *args):
        super(InvalidAddress, self).__init__(_("Unavailable eth addresses"), *args)


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
    # assert message == "OK"


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

    def create_db(self):
        PyWalib.cursor.execute(
            "CREATE TABLE IF NOT EXISTS txlist (tx_hash TEXT PRIMARY KEY, address TEXT, time INTEGER, tx TEXT)"
        )

    @classmethod
    def get_json(cls, url):
        network = Network.get_instance()
        proxy = network.proxy if network else None
        with make_aiohttp_session(proxy) as session:
            with session.get(url) as response:
                response.raise_for_status()
                # set content_type to None to disable checking MIME type
                return response.json(content_type=None)

    @staticmethod
    def get_web3():
        return PyWalib.web3

    @staticmethod
    def set_server(info):
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
        cache_config = cls.server_config

        try:
            cls.set_server(config)
            yield
        finally:
            if cls.server_config.get("id") == config.get("id"):
                cls.set_server(cache_config)

    @classmethod
    def get_rpc_info(cls) -> dict:
        rpc, chain_id = None, None

        for config in cls.server_config.get("Provider") or ():
            if cls.chain_type in config:
                rpc = config[cls.chain_type]
                chain_id = int(config["chainid"])
                break

        assert rpc is not None
        assert chain_id is not None

        return {
            "rpc": rpc,
            "chain_id": chain_id,
        }

    @classmethod
    def get_chain_code(cls) -> str:
        chain_code = cls.server_config["id"]

        if cls.chain_type == "testnet":
            chain_code = f"t{chain_code}"

        return chain_code

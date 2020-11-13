import time
from .pywalib import PyWalib, get_abi_json


class Eth_Contract:
    """Abstraction over ERC20 tokens"""

    # fitcoin_address = '0x19896cB57Bc5B4cb92dbC7D389DBa6290AF505Ce'
    # binancecoin_address = '0x64BBF67A8251F7482330C33E65b08B835125e018'
    # my_address = '0xc3519C4560BcfE3Ac0b137f1067d1655ed65FEa4'
    # metamask_address = '0xAAD533eb7Fe7F2657960AC7703F87E10c73ae73b'

    def __init__(self, symbol, address):
        """
        Constructor
        :param address: contract address or ESN name
        :type address: string
        """
        self.address = address
        self.symbol = symbol
        self.w3 = PyWalib.get_web3()
        import json
        self.contract = self.w3.eth.contract(address=address, abi=get_abi_json())
        #self.contract = self.w3.eth.contract(address=address, abi=abi)
        self.contract_decimals = self.contract.functions.decimals().call()

    def get_balance(self, wallet_address):
        """
        Get wallet's ballance of self.contract
        :param wallet_address: this wallet address
        :type wallet_address: string
        :return: balance as decimal number
        """
        #return self.contract.functions.balanceOf(wallet_address).call() / (10 ** self.contract_decimals)
        return self.contract.functions.balanceOf(wallet_address).call()

    def get_address(self):
        return self.address

    def get_symbol(self):
        return self.symbol

    def get_decimals(self):
        """
        Returns the number of decimals
        :return: integer
        """
        return self.contract_decimals

    def get_erc20_contract(self):
        """
        Returns w3.eth.contract instance
        :return:
        """
        return self.contract

from electrum import bitcoin
from electrum_gui.common.wallet.data import AddressValidation
from electrum_gui.common.wallet.interfaces import WalletAdapterInterface


class BTCWalletAdapter(WalletAdapterInterface):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid, address_format = False, None

        if bitcoin.is_segwit_address(address):
            is_valid, address_format = True, "segwit"
        elif bitcoin.is_b58_address(address):
            is_valid, address_format = True, "b58"

        return AddressValidation(is_valid, address_format)

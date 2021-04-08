from electrum import bitcoin
from electrum_gui.common.provider.data import AddressValidation
from electrum_gui.common.provider.interfaces import ProviderInterface


class BTCProvider(ProviderInterface):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid, encoding = False, None

        if bitcoin.is_segwit_address(address):
            is_valid, encoding = True, "segwit"
        elif bitcoin.is_b58_address(address):
            is_valid, encoding = True, "b58"

        return AddressValidation(is_valid=is_valid, encoding=encoding)

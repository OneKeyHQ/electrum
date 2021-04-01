import eth_utils

from electrum_gui.common.provider.data import AddressValidation
from electrum_gui.common.provider.interfaces import ProviderInterface


class ETHProvider(ProviderInterface):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid, address_format = False, None

        if eth_utils.is_checksum_formatted_address(address) and eth_utils.is_checksum_address(address):
            is_valid, address_format = True, "checksum"
        elif eth_utils.is_hex_address(address):
            is_valid, address_format = True, "hex"

        return AddressValidation(is_valid=is_valid, format=address_format)

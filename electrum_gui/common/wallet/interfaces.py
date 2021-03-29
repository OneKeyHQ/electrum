from abc import ABC, abstractmethod

from electrum_gui.common.wallet.data import AddressValidation


class WalletAdapterInterface(ABC):
    @abstractmethod
    def verify_address(self, address: str) -> AddressValidation:
        pass

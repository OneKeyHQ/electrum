from typing import Dict, Tuple

from electrum import bitcoin
from electrum_gui.common.provider.data import AddressValidation, SignedTx, UnsignedTx
from electrum_gui.common.provider.interfaces import ProviderInterface
from electrum_gui.common.secret.interfaces import SignerInterface, VerifierInterface


class BTCProvider(ProviderInterface):
    def verify_address(self, address: str) -> AddressValidation:
        is_valid, encoding = False, None

        if bitcoin.is_segwit_address(address):
            is_valid, encoding = True, "segwit"
        elif bitcoin.is_b58_address(address):
            is_valid, encoding = True, "b58"

        return AddressValidation(is_valid=is_valid, encoding=encoding)

    def pubkey_to_address(self, verifier: VerifierInterface, encoding: str = None) -> str:
        raise NotImplementedError()

    def filling_unsigned_tx(self, unsigned_tx: UnsignedTx) -> UnsignedTx:
        raise NotImplementedError()

    def sign_transaction(self, unsigned_tx: UnsignedTx, key_mapping: Dict[str, SignerInterface]) -> SignedTx:
        raise NotImplementedError()

    def get_token_info_by_address(self, token_address: str) -> Tuple[str, str, int]:
        raise NotImplementedError()

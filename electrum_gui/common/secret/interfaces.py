from abc import ABC, abstractmethod
from typing import Tuple

from electrum_gui.common.basic.functional.require import require


class VerifierInterface(ABC):
    @abstractmethod
    def get_pubkey(self, compressed=True) -> bytes:
        """
        Get pubkey
        :param compressed: compressed or uncompressed
        :return: pubkey as bytes
        """

    @abstractmethod
    def verify(self, digest: bytes, signature: bytes) -> bool:
        """
        Verify signature base on digest
        :param digest: digest
        :param signature: signature of digest
        :return: verify succeed or not
        """


class SignerInterface(VerifierInterface, ABC):
    @abstractmethod
    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        """
        Sign by digest
        :param digest: digest
        :return: signature with rec_id
        """


class KeyInterface(SignerInterface, ABC):
    def __init__(self, prvkey: bytes = None, pubkey: bytes = None):
        require((prvkey is not None) ^ (pubkey is not None), "Require one of 'prvkey' or 'pubkey' only")

    @classmethod
    def from_key(cls, prvkey: bytes = None, pubkey: bytes = None) -> "KeyInterface":
        return cls(prvkey=prvkey, pubkey=pubkey)

    @abstractmethod
    def has_prvkey(self) -> bool:
        pass

    @abstractmethod
    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        require(self.has_prvkey(), "Private key not found")
        return bytes(), 0

    def as_pubkey_version(self) -> "KeyInterface":
        return self.__class__(pubkey=self.get_pubkey())

    def __str__(self):
        pubkey = self.get_pubkey()
        pubkey_desc = f"pubkey<{pubkey.hex()}>"

        if self.has_prvkey():
            return f"private key of {pubkey_desc}"
        else:
            return pubkey_desc

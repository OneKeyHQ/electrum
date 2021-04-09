from abc import ABC, abstractmethod
from typing import Tuple


class VerifierInterface(ABC):
    @abstractmethod
    def get_pubkey(self, compressed=True) -> bytes:
        pass


class SignerInterface(VerifierInterface, ABC):
    @abstractmethod
    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        pass

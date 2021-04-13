import hashlib
import struct
from abc import ABC, abstractmethod
from typing import Iterable, Tuple, Type, Union

from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.secret import utils


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


class BIP32Interface(ABC):
    """
    refer to slip-0010, bip-0032 and pycoin
    """

    BIP32_PRIME: int = 0x80000000
    bip32_salt: bytes = None
    key_class: Type[KeyInterface] = None

    def __init__(
        self,
        prvkey: bytes = None,
        pubkey: bytes = None,
        chain_code: bytes = None,
        depth: int = 0,
        parent_fingerprint: bytes = bytes([0]) * 4,
        child_index: int = 0,
    ):
        require(self.key_class is not None, f"Please specify 'key_class' for <{self.__class__}>")
        require((prvkey is not None) ^ (pubkey is not None), "Require one of 'prvkey' or 'pubkey' only")

        self._prvkey = prvkey
        self._pubkey = pubkey or self.key_class(prvkey=self._prvkey).get_pubkey(compressed=True)
        self.chain_code = chain_code
        self.depth = depth
        self.parent_fingerprint = parent_fingerprint
        self.child_index = child_index
        self._lookup_cache = {}

    @classmethod
    def from_master_seed(cls, master_seed: bytes) -> "BIP32Interface":
        require(cls.bip32_salt is not None)

        digest = utils.hmac_oneshot(key=cls.bip32_salt, msg=master_seed, digest=hashlib.sha512)
        return cls(prvkey=digest[:32], chain_code=digest[32:])

    @classmethod
    def from_hd_wif(cls, wif: str) -> "BIP32Interface":
        data = utils.decode_base58_check(wif)
        require(len(data) == 78)
        return cls.deserialize(data)

    @classmethod
    @abstractmethod
    def deserialize(cls, data: bytes) -> "BIP32Interface":
        pass

    def get_hd_wif(self, as_private: bool = False) -> str:
        if as_private:
            prefix = b"\x04\x88\xAD\xE4"
        else:
            prefix = b"\x04\x88\xB2\x1E"

        data = self.serialize(as_private=as_private)
        require(len(data) == 74)
        return utils.encode_base58_check(prefix + data)

    def serialize(self, as_private=False) -> bytes:
        if as_private:
            require(self.has_prvkey(), "Private key not found")

        buffer = bytearray()
        buffer.append(self.depth)
        buffer.extend(self.parent_fingerprint)
        buffer.extend(struct.pack(">L", self.child_index))
        buffer.extend(self.chain_code)

        if as_private:
            buffer.extend(bytes([0]) + self._prvkey)
        else:
            compressed_pubkey = self.key_class(pubkey=self._pubkey).get_pubkey(True)
            if len(compressed_pubkey) == 32:
                buffer.append(1)  # padded with b'\x01', refer to trezor bip32 impl

            buffer.extend(compressed_pubkey)

        return bytes(buffer)

    def derive_path(self, path: Union[str, Iterable[int]]) -> "BIP32Interface":
        if isinstance(path, str):
            path = utils.decode_bip32_path(path)

        if not path:
            return self

        key = self
        for chain_index in path:
            is_hardened = bool(chain_index & self.BIP32_PRIME)
            key = key.derive(chain_index, is_hardened=is_hardened, as_private=key.has_prvkey())

        return key

    @property
    def fingerprint(self) -> bytes:
        return utils.hash_160(self._pubkey)[:4]

    def derive(self, child_index: int, is_hardened: bool, as_private: bool) -> "BIP32Interface":
        if is_hardened:
            require(
                child_index >= self.BIP32_PRIME,
                f"Illegal hardened child number. child_number: {child_index}",
            )
        else:
            require(
                0 <= child_index < self.BIP32_PRIME,
                f"Illegal no-hardened child number. child_number: {child_index}",
            )

        if as_private:
            require(self.has_prvkey(), "Private key not found")
        else:
            require(not is_hardened, "is_hardened is only supported on private key")

        lookup = (child_index, is_hardened, as_private)
        if lookup not in self._lookup_cache:
            self._lookup_cache[lookup] = self._derive(child_index, is_hardened, as_private)

        return self._lookup_cache[lookup]

    @abstractmethod
    def _derive(self, child_index: int, is_hardened: bool, as_private: bool) -> "BIP32Interface":
        pass

    @property
    def prvkey_interface(self) -> KeyInterface:
        require(self.has_prvkey(), "Private key not found")
        return self.key_class(prvkey=self._prvkey)

    @property
    def pubkey_interface(self) -> KeyInterface:
        return self.key_class(pubkey=self._pubkey)

    def has_prvkey(self) -> bool:
        return self._prvkey is not None

    def __str__(self):
        xpub_desc = f"HD WIF<{self.get_hd_wif(as_private=False)}>"

        if self.has_prvkey():
            return f"private for {xpub_desc}"
        return xpub_desc

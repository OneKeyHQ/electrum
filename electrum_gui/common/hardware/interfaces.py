import abc
from typing import Any


class HardwareCallbackInterface(abc.ABC):
    @abc.abstractmethod
    def button_request(self, code: int) -> None:
        pass

    @abc.abstractmethod
    def get_pin(self, code: int = None) -> str:
        pass

    @abc.abstractmethod
    def get_passphrase(self, available_on_device: bool) -> str:
        pass


class HardwareClientInterface(abc.ABC):
    @abc.abstractmethod
    def call(self, *args, **kwargs) -> Any:
        pass

    @abc.abstractmethod
    def open(self) -> None:
        pass

    @abc.abstractmethod
    def close(self) -> None:
        pass

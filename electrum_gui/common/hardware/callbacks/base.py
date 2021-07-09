import abc
import logging

from electrum_gui.common.hardware import exceptions, interfaces
from electrum_gui.common.hardware.callbacks import helper

logger = logging.getLogger("app.hardware")


class BaseCallback(interfaces.HardwareCallbackInterface, abc.ABC):
    @abc.abstractmethod
    def notify_handler(self, code: int):
        pass

    def button_request(self, code: int):
        code = int(code)
        self.notify_handler(code + 100)

    def get_pin(self, code: int = None) -> str:
        code = int(code)

        with helper.require_specific_value_of_agent("pin", code) as waiting_input:
            self.notify_handler(code)
            pin = waiting_input()

            if not pin:
                raise exceptions.CancelledFromHardware()

            return pin

    def get_passphrase(self, available_on_device: bool) -> str:
        is_creating_wallet = helper.get_value_of_agent("is_creating_wallet", False)
        is_bypass = helper.get_value_of_agent("pass_state", 0)

        helper.set_value_to_agent("is_creating_wallet", False)
        helper.set_value_to_agent("pass_state", 0)

        if is_bypass == 0:
            return ""

        code = 6 if is_creating_wallet else 3
        with helper.require_specific_value_of_agent("passphrase", code) as waiting_input:
            self.notify_handler(code)
            passphrase = waiting_input()

            if passphrase is None:
                raise exceptions.CancelledFromHardware()

            return passphrase

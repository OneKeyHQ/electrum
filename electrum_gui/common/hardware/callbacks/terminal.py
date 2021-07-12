from trezorlib import ui as trezor_ui

from electrum_gui.common.hardware import interfaces
from electrum_gui.common.hardware.callbacks import helper


class TerminalCallback(interfaces.HardwareCallbackInterface):
    def __init__(self, always_prompt: bool = False, passphrase_on_host: bool = False):
        self.impl = trezor_ui.ClickUI(always_prompt, passphrase_on_host)

    def button_request(self, code: int) -> None:
        return self.impl.button_request(code)

    def get_pin(self, code: int = None) -> str:
        if not helper.get_value_of_agent("is_changing_pin", False):
            return self.impl.get_pin(code)
        else:
            current_pin = self.impl.get_pin(code)
            next_pin = self.impl.get_pin(trezor_ui.PIN_NEW)
            return current_pin.ljust(9, "0") + next_pin.ljust(9, "0")

    def get_passphrase(self, available_on_device: bool) -> str:
        return self.impl.get_passphrase(available_on_device)

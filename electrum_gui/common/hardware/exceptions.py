from trezorlib.exceptions import Cancelled as CancelledFromHardware  # noqa


class GeneralHardwareException(Exception):
    pass


class NoAvailableDevice(GeneralHardwareException):
    pass


class CancelledFromUser(GeneralHardwareException):
    def __init__(self):
        super(CancelledFromUser, self).__init__("user cancel")


class CallbackTimeout(GeneralHardwareException):
    def __init__(self):
        super(CallbackTimeout, self).__init__("timeout")

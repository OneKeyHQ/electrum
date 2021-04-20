import os

import requests
from trezorlib import customer_ui, transport
from trezorlib.transport import bridge

from electrum import constants
from electrum_gui.android import console

# Start monkey patches


class _Handler(object):
    @staticmethod
    def sendEmptyMessage(code):
        print(f"\nGot Custom UI empty message: {code}\n")
        if code == 1:
            customer_ui.CustomerUI.set_pin("000000000")


customer_ui.CustomerUI.handler = _Handler()
transport.all_transports = lambda: {bridge.BridgeTransport}

# End monkey patches


def list_connected_devices():
    try:
        requests.get(bridge.TREZORD_HOST)
    except requests.ConnectionError:
        print("Trezord not started, hardware debug would not be available...")
    else:
        devices = bridge.BridgeTransport.enumerate()
        if devices:
            print("Connected devices:\n    - ", end="", flush=True)
            print("\n    -".join(str(device) for device in devices))
        else:
            print("No devices connected.")


class FakeCallbackIntent(object):
    def onCallback(self, *args, **kwargs):
        print(f"callback invoked, args: {args}, kwargs: {kwargs}")


_NETWORK_SET_FUNC = {
    'sim': constants.set_simnet,
    'test': constants.set_testnet,
    'reg': constants.set_regtest,
}


# By default using main network
set_network_func = _NETWORK_SET_FUNC.get(os.environ.get('USE_NETWORK'), None)
if set_network_func is not None:
    set_network_func()

if 'AUTOSTART' in os.environ:
    commands = console.AndroidCommands(os.environ['ANDROID_ID'], callback=FakeCallbackIntent())


print("\n    *** Welcome ***\n")
print("- Use list_connected_devices() to list connected hardwares.")
if 'AUTOSTART' in os.environ:
    print("- Type help(commands) to get the usage of commands.")

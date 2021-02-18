import hashlib
import json
import time
import threading
from typing import Any, Optional

# TODO: all these trezorlib imports should be moved into the lower level
# trezorclient, perhaps except for the module trezor_exceptions. So that this
# module only communicates with the trezor plugin, no lower level functions
# would be directly called from this module.
from trezorlib import customer_ui as trezorlib_customer_ui
from trezorlib import exceptions as trezor_exceptions
from trezorlib import firmware as trezor_firmware
from trezorlib import messages as trezor_messages
from trezorlib import protobuf as trezor_protobuf
from trezorlib.cli import trezorctl

from electrum import plugin as electrum_plugin
from electrum.i18n import _
from electrum.plugins.trezor import clientbase as trezor_clientbase
from electrum_gui.android import firmware_sign_nordic_dfu

TREZOR_PLUGIN_NAME = 'trezor'


def _reboot_to_bootloader(client: trezor_clientbase.TrezorClientBase, dry_run: bool = False) -> None:
    if client.features.bootloader_mode:
        return

    resp = client.client.call(trezor_messages.BixinReboot())
    if isinstance(resp, trezor_messages.Success) or dry_run:
        time.sleep(2)
        client.client.init_device()
    else:
        raise RuntimeError("Failed turn into bootloader")

    if dry_run or client.features.bootloader_mode:
        return

    raise BaseException(_("Switch the device to bootloader mode."))


def _do_firmware_update(
    client: trezor_clientbase.TrezorClientBase, data: bytes, type: str, dry_run: bool = False
) -> None:
    if dry_run:
        print("Dry run. Not uploading firmware to device.")
        return

    confirm_needed = client.features.major_version == 1 and client.features.firmware_present
    if confirm_needed:
        # Trezor One does not send ButtonRequest
        print("Please confirm the action on your Trezor device")

    try:
        return trezor_firmware.update(client.client, data, type=type)
    except trezor_exceptions.Cancelled:
        print("Update aborted on device.")
    except trezor_exceptions.TrezorException as e:
        raise BaseException(_("Update failed: {}".format(e)))


class TrezorManager(object):
    """Interact with hardwares via electrum's trezor plugin."""

    exposed_commands = (
        'hardware_verify',
        'backup_wallet',
        'se_proxy',
        'bixin_backup_device',
        'bixin_load_device',
        'recovery_wallet_hw',
        'bx_inquire_whitelist',
        'bx_add_or_delete_whitelist',
        'apply_setting',
        'init',
        'reset_pin',
        'wipe_device',
        'get_passphrase_status',
        'get_feature',
        'firmware_update',
    )

    def __init__(self, plugin_manager: electrum_plugin.Plugins) -> None:
        self.plugin = plugin_manager.get_plugin(TREZOR_PLUGIN_NAME)
        # TODO: check whether multiple clients are really needed.
        # With the latest commit(d68b538058d), only one client is allowed.
        # However, it seems that the client can be overwritten and thus
        # reinitialization of the client can happen many times.
        # See d68b538058d/electrum/plugins/trezor/trezor.py#L188,L203
        # To avoid redundant works, use a dict to store the needed clients.
        self.clients = dict()
        # NOTE: this lock is only used in get_feature(), should be removed
        # when no longer needed.
        self.lock = threading.RLock()

    def _get_client(self, path: str) -> trezor_clientbase.TrezorClientBase:
        # The meaning of 'path' is obsecure, it is used in the plugin to
        # select a device.
        # dict.setdefault() is not short-circuiting.
        client = self.clients.get(path)
        client = client or self.clients.setdefault(
            path,
            self.plugin.get_client(
                path=path,
                ui=trezorlib_customer_ui.CustomerUI()
                # TODO: this ui parameter is not used and should be removed.
            ),
        )
        return client

    def _bridge(self, path: str, method: str, *args, **kwargs) -> Any:
        # Bridge calls to the trezor client.
        # TODO: this bridge method is only used for raising the BaseException
        # because the caller might still expect a BaseException. Once we catch
        # the excetpions in the upper call itself (and raise generic
        # exceptions there), this should be removed.
        try:
            return getattr(self._get_client(path), method)(*args, **kwargs)
        except Exception as e:
            raise BaseException(e)

    def _json_bridge(self, path: str, method: str, *args, **kwargs) -> Any:
        return json.dumps(self._bridge(path, method, *args, **kwargs))

    # === Begin helper methods used in console.AndroidCommands ===

    def ensure_client(self, path: str) -> None:
        self._get_client(path)

    def get_device_id(self, path: str) -> str:
        client = self._get_client(path)
        return client.features.device_id

    def get_xpub(self, path: str, derivation: str, _type: str, creating: bool) -> str:
        return self._bridge(path, 'get_xpub', derivation, _type, creating=creating)

    def get_eth_xpub(self, path: str, derivation: str) -> str:
        return self._bridge(path, 'get_eth_xpub', derivation)

    # === End helper methods used in console.AndroidCommands ===

    # Below are exposed methods, would be directly called from the upper users.

    def hardware_verify(self, msg: str, path: str = "android_usb") -> str:
        """
        Anti-counterfeiting verification, used by hardware
        :param msg: msg as str
        :param path: NFC/android_usb/bluetooth as str
        :return: json like
            {'serialno': 'Bixin20051500293',
             'is_bixinkey': 'True',
             'is_verified': 'True',
             'last_check_time': 1611904981}
        """
        digest = hashlib.sha256(msg.encode('utf-8')).digest()
        cert, signature = self._bridge(path, 'se_verify', digest)
        params = {"data": msg, "cert": cert, "signature": signature}
        verify_url = "https://key.bixin.com/lengqian.bo/"
        import requests

        try:
            res = requests.post(verify_url, data=params, timeout=30)
        except Exception as e:
            # TODO: check this translation is not needed and use
            # RestfulRequest to do the whole verification job.
            raise BaseException(_(str(e))) from e
        else:
            return res.text

    def backup_wallet(self, path: str = "android_usb") -> str:
        """
        Backup wallet by se
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :return:
        """
        return self._bridge(path, 'backup')

    def se_proxy(self, message: str, path: str = "android_usb") -> str:
        return self._bridge(path, 'se_proxy', message)

    def bixin_backup_device(self, path: str = "android_usb") -> str:
        """
        Export seed, used by hardware
        :param path: NFC/android_usb/bluetooth as str
        :return: as string
        """
        return self._bridge(path, 'bixin_backup_device')

    def bixin_load_device(
        self,
        path: str = "android_usb",
        mnemonics: Optional[str] = None,
        language: str = "en-US",
        label: str = "BIXIN KEY",
    ) -> str:
        """
        Import seed, used by hardware
        :param mnemonics: as string
        :param path: NFC/android_usb/bluetooth as str
        :return: raise except if error
        """
        return self._bridge(path, 'bixin_load_device', mnemonics=mnemonics, language=language, label=label)

    def recovery_wallet_hw(self, path: str = "android_usb", *args) -> str:
        """
        Recovery wallet by encryption
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param args: encryption data as str
        :return:
        """
        return self._bridge(path, 'recovery', *args)

    def bx_inquire_whitelist(self, path: str = "android_usb", **kwargs) -> str:
        """
        Inquire
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param kwargs:
            type:2=inquire
            addr_in: addreess as str
        :return:
        """
        return self._json_bridge(path, 'bx_inquire_whitelist', **kwargs)

    def bx_add_or_delete_whitelist(self, path: str = "android_usb", **kwargs) -> str:
        """
        Add and delete whitelist
        :param path: NFC/android_usb/bluetooth as str, used by hardware
        :param kwargs:
            type:0=add 1=delete
            addr_in: addreess as str
        :return:
        """
        return self._json_bridge(path, 'bx_add_or_delete_whitelist', **kwargs)

    def apply_setting(self, path: str = "nfc", **kwargs) -> int:
        """
        Set the hardware function, used by hardware
        :param path: NFC/android_usb/bluetooth as str
        :param kwargs:
            label="wangls"
            language="chinese/english"
            use_passphrase=true/false
            auto_lock_delay_ms="600"
            use_ble=true/false
            use_se=true/false
            is_bixinapp=true/false
        :return:0/1
        """
        resp = self._bridge(path, 'apply_settings', **kwargs)
        return 1 if resp == "Settings applied" else 0

    def init(
        self,
        path: str = "android_usb",
        label: str = "BIXIN KEY",
        language: str = "english",
        stronger_mnemonic: Optional[str] = None,
        use_se: bool = False,
    ) -> int:
        """
        Activate the device, used by hardware
        :param stronger_mnemonic: if not None 256  else 128
        :param path: NFC/android_usb/bluetooth as str
        :param label: name as string
        :param language: as string
        :param use_se: as bool
        :return:0/1
        """
        if use_se:
            self.apply_settings(path, use_se=True)
        strength = 256 if stronger_mnemonic else 128
        response = self._bridge(
            path, 'reset_device', language=language, passphrase_protection=True, label=label, strength=strength
        )
        return 1 if response == "Device successfully initialized" else 0

    def reset_pin(self, path: str = "android_usb") -> int:
        """
        Reset pin, used by hardware
        :param path:NFC/android_usb/bluetooth as str
        :return:0/1
        """
        try:
            # TODO: not using self._bridge() because we need to catch low-level
            # exceptions.
            client = self._get_client(path)
            client.set_pin(False)
        except (trezor_exceptions.PinException, RuntimeError):
            return 0
        except Exception:
            raise BaseException("user cancel")

        return 1

    def wipe_device(self, path: str = "android_usb") -> int:
        """
        Reset device, used by hardware
        :param path: NFC/android_usb/bluetooth as str
        :return:0/1
        """
        try:
            # TODO: not using self._bridge() because we need to catch low-level
            # exceptions.
            client = self._get_client(path)
            resp = client.wipe_device()
        except (trezor_exceptions.PinException, RuntimeError):
            return 0
        except Exception as e:
            raise BaseException(str(e)) from e

        return 1 if resp == "Device wiped" else 0

    def get_passphrase_status(self, path: str = "android_usb") -> bool:
        client = self._get_client(path=path)
        return client.features.passphrase_protection

    def get_feature(self, path: str = "android_usb") -> str:
        """
        Get hardware information, used by hardware
        :param path: NFC/android_usb/bluetooth as str
        :return: dict like:
            {capabilities: List[EnumTypeCapability] = None,
            vendor: str = None,
            major_version: int = None,  主版本号
            minor_version: int = None,  次版本号
            patch_version: int = None
                修订号，即硬件的软件版本(俗称固件，在2.0.1 之前使用)
            bootloader_mode: bool = None,  设备当时是不是在bootloader模式
            device_id: str = None,  设备唯一标识，设备恢复出厂设置这个值会变
            pin_protection: bool = None, 是否开启了PIN码保护，
            passphrase_protection: bool = None
                是否开启了passphrase功能，这个用来支持创建隐藏钱包
            language: str = None,
            label: str = None,  激活钱包时，使用的名字
            initialized: bool = None, 当时设备是否激活
            revision: bytes = None,
            bootloader_hash: bytes = None,
            imported: bool = None,
            unlocked: bool = None,
            firmware_present: bool = None,
            needs_backup: bool = None,
            flags: int = None,
            model: str = None,
            fw_major: int = None,
            fw_minor: int = None,
            fw_patch: int = None,
            fw_vendor: str = None,
            fw_vendor_keys: bytes = None,
            unfinished_backup: bool = None,
            no_backup: bool = None,
            recovery_mode: bool = None,
            backup_type: EnumTypeBackupType = None,
            sd_card_present: bool = None,
            sd_protection: bool = None,
            wipe_code_protection: bool = None,
            session_id: bytes = None,
            passphrase_always_on_device: bool = None,
            safety_checks: EnumTypeSafetyCheckLevel = None,
            auto_lock_delay_ms: int = None, 自动关机时间
            display_rotation: int = None,
            experimental_features: bool = None,
            offset: int = None,  升级时断点续传使用的字段
            ble_name: str = None,
            ble_ver: str = None,  蓝牙固件版本
            ble_enable: bool = None,
            se_enable: bool = None,
            se_ver: str = None,  se的版本
            backup_only: bool = None
                是否是特殊设备，只用来备份，没有额外功能支持
            onekey_version: str = None
                硬件的软件版本（俗称固件），仅供APP使用（从2.0.1开始加入）}
        """
        with self.lock:
            # TODO: why is this lock needed
            self.plugin.clean()
            self.clients.pop(path, None)
            client = self._get_client(path=path)
        return json.dumps(trezor_protobuf.to_dict(client.features))

    def firmware_update(
        self,
        filename: str,
        path: str,
        type: str = "",
        fingerprint: Optional[str] = None,
        skip_check: bool = True,
        raw: bool = False,
        dry_run: bool = False,
    ) -> None:
        """
        Upload new firmware to device.used by hardware
        Note : Device must be in bootloader mode.
        :param filename: full path to local upgrade file
        :param path: NFC/android_usb/bluetooth as str
        :return: None
        """
        if not filename:
            raise BaseException(("Please give the file name"))

        try:
            if type:
                data = firmware_sign_nordic_dfu.parse(filename)
            else:
                with open(filename, "rb") as datafile:
                    data = datafile.read()
        except Exception as e:
            raise BaseException(e)

        client = self._get_client(path)
        _reboot_to_bootloader(client, dry_run=dry_run)

        if raw:
            return _do_firmware_update(client, data, type, dry_run=dry_run)

        bootloader_onev2 = client.features.major_version == 1 and client.features.minor_version >= 8
        embedded = bootloader_onev2 and data[:4] == b'TRZR' and data[256:260] == b'TRZF'
        if embedded:
            print("Extracting embedded firmware image " "(fingerprint may change).")
            data = data[256:]

        if skip_check:
            return _do_firmware_update(client, data, type, dry_run=dry_run)

        try:
            version, fw = trezor_firmware.parse(data)
        except Exception as e:
            raise BaseException(e)

        version_too_low = (
            bootloader_onev2 and version == trezor_firmware.FirmwareFormat.TREZOR_ONE and not fw.embedded_onev2
        )
        need_upgrade = bootloader_onev2 and version == trezor_firmware.FirmwareFormat.TREZOR_ONE_V2

        if version_too_low:
            raise BaseException(_("Your device's firmware version is too low. Aborting."))
        elif need_upgrade:
            raise BaseException(_("You need to upgrade the bootloader immediately."))

        compatible_versions = trezorctl.ALLOWED_FIRMWARE_FORMATS.get(client.features.major_version)
        if compatible_versions is None:
            raise BaseException(_("Trezorctl doesn't know your device version. Aborting."))
        elif version not in compatible_versions:
            raise BaseException(_("Firmware not compatible with your equipment, Aborting."))

        trezorctl.validate_firmware(version, fw, fingerprint)
        return _do_firmware_update(client, data, type, dry_run=dry_run)

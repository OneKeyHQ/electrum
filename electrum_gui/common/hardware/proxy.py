import hashlib
import logging
import time
from typing import Any

from trezorlib import client as trezor_client
from trezorlib import device as trezor_device
from trezorlib import exceptions as trezor_exceptions
from trezorlib import firmware as trezor_firmware
from trezorlib import protobuf as trezor_protobuf
from trezorlib import transport as trezor_transport
from trezorlib.transport import bridge as trezor_bridge

from electrum_gui.common.basic.request.restful import RestfulRequest
from electrum_gui.common.hardware import exceptions, interfaces
from electrum_gui.common.hardware.callbacks import helper

logger = logging.getLogger("app.hardware")


def _force_release_old_session_as_need(device: trezor_transport.Transport):
    if not isinstance(device, trezor_bridge.BridgeTransport):
        return

    splits = device.get_path().split(":")
    target_path = splits[1] if len(splits) == 2 else None
    if not target_path:
        return

    try:
        enumerates_by_bridge = trezor_bridge.call_bridge("enumerate").json()
        target = [i for i in enumerates_by_bridge if i.get("path") == target_path and i.get("session")]
        if target:
            old_session = target[0].get("session")
            trezor_bridge.call_bridge(f"release/{old_session}")
    except Exception as e:
        logger.exception(
            f"Error in enumerating or releasing specific device. device_path: {device.get_path()}, error: {e}"
        )


class HardwareProxyClient(interfaces.HardwareClientInterface):
    def __init__(self, device: trezor_transport.Transport, callback: interfaces.HardwareCallbackInterface):
        _force_release_old_session_as_need(device)
        self._client = trezor_client.TrezorClient(device, ui=callback)
        self._device_path = device.get_path()
        self._is_migrating_applied = False

    def ensure_device(self):
        _force_release_old_session_as_need(self._client.transport)
        self._client.init_device()

        if not self._is_migrating_applied:
            self._is_migrating_applied = True
            self.apply_migrating_settings()

    def apply_migrating_settings(self):
        try:
            features = self.get_feature()
            if not features.get("bootloader_mode") and (
                features.get("onekey_version")
                or features.get("major_version") > 1
                or (features.get("minor_version") == 9 and features.get("patch_version") >= 7)
            ):  # Fixme Trick point, no one knows the reason
                self.apply_settings({"is_bixinapp": True})
        except Exception as e:
            logger.exception(f"Error in apply 'is_bixinapp' setting. error: {e}")

    def call(self, *args, **kwargs) -> Any:
        return self._client.call(*args, **kwargs)

    def open(self) -> None:
        return self._client.open()

    def close(self) -> None:
        return self._client.close()

    def ping(self, message: str) -> str:
        return self._client.ping(message)

    def get_feature(self, force_refresh: bool = False) -> dict:
        if force_refresh:
            self._client.refresh_features()

        return trezor_protobuf.to_dict(self._client.features)

    def verify_secure_element(self, message: str) -> dict:
        digest = hashlib.sha256(message.encode("utf-8")).digest()
        signed_by_se = trezor_device.se_verify(self._client, digest)

        restful = RestfulRequest("https://key.bixin.com")
        return restful.post(
            "/lengqian.bo/",
            data={
                "data": message,
                "cert": signed_by_se.cert.hex(),
                "signature": signed_by_se.signature.hex(),
            },
        )

    def backup_mnemonic_on_device(self) -> str:
        return trezor_device.backup(self._client)

    def import_mnemonic_to_device(
        self,
        mnemonic: str,
        language: str = "english",
        label: str = "OneKey",
    ) -> str:
        return trezor_device.bixin_load_device(
            self._client,
            mnemonics=mnemonic,
            language=language,
            label=label,
        )

    def apply_settings(self, settings: dict) -> bool:
        result = trezor_device.apply_settings(self._client, **settings)
        return result == "Settings applied"

    def reset_device(
        self,
        language: str = "english",
        label: str = "OneKey",
        mnemonic_strength: int = 128,
    ) -> bool:
        result = trezor_device.reset(
            self._client,
            language=language,
            label=label,
            strength=mnemonic_strength,
        )
        return result == "Device successfully initialized"

    def change_pin(self) -> bool:
        try:
            helper.set_value_to_agent("is_changing_pin", True)
            result = trezor_device.change_pin(self._client, False)
            return result == "PIN changed"
        except (trezor_exceptions.PinException, RuntimeError):
            return False
        except Exception:
            raise exceptions.CancelledFromHardware()
        finally:
            helper.set_value_to_agent("is_changing_pin", False)

    def wipe_device(self) -> bool:
        try:
            result = trezor_device.wipe(self._client)
            return result == "Device wiped"
        except (trezor_exceptions.PinException, RuntimeError):
            return False
        except Exception as e:
            raise BaseException(str(e)) from e

    def reboot_to_bootloader(self) -> bool:
        if not self.get_feature().get("bootloader_mode"):
            trezor_device.reboot(self._client)
            time.sleep(2)
            self.ensure_device()

        return self.get_feature().get("bootloader_mode", False)

    def update_firmware(
        self,
        filename: str,
        is_raw_data_only: bool = False,
        dry_run: bool = False,
    ) -> None:
        updating_stream = open(filename, "rb").read()

        try:
            is_bootloader = self.reboot_to_bootloader()
        except Exception as e:
            raise exceptions.GeneralHardwareException("There was a problem rebooting to the bootloader") from e
        else:
            if not is_bootloader:
                raise exceptions.GeneralHardwareException("Unable to reboot into the bootloader")

        if not is_raw_data_only:
            features = self.get_feature()
            is_bootloader_one_v2 = features.get("major_version") == 1 and features.get("minor_version") >= 8
            has_embedded = (
                is_bootloader_one_v2 and updating_stream[:4] == b"TRZR" and updating_stream[256:260] == b"TRZF"
            )
            if has_embedded:
                updating_stream = updating_stream[256:]

        if dry_run:
            logger.debug("Dry run mode. Not uploading firmware to device. ")
            return

        try:
            trezor_firmware.update(self._client, updating_stream)
        except trezor_exceptions.Cancelled as e:
            logger.info("Updating process aborted on device.")
            raise e

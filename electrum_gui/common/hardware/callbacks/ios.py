from electrum_gui.common.basic.functional.require import require_not_none
from electrum_gui.common.hardware.callbacks import base


class IOSCallback(base.BaseCallback):
    def __init__(self):
        from rubicon.objc import ObjCClass  # noqa

        self.ios_handler = require_not_none(
            ObjCClass("OKBlueManager").sharedInstance().getNotificationCenter(),
            "Failed to init NotificationCenter for iOS",
        )

    def notify_handler(self, code: int):
        self.ios_handler.postNotificationName_object_("HardwareNotifications", str(code))

package org.haobtc.onekey.constant;

/**
 * other constant
 */
public final class Constant {

    public static final String WAY_MODE_BLE = "BLE";
    public static final String WAY_MODE_NFC = "NFC";
    public static final String WAY_MODE_USB = "USB";

    //ble name start with
    public static final String BLE_NAME_PREFIX = "BixinKey";

    public static final String SEARCH_DEVICE_MODE = "search_device_mode";

    public static class SearchDeviceMode{
        /**
         * 通过硬件恢复 HD 钱包
         */
        public static final int MODE_RECOVERY_WALLET_BY_COLD = 1;
        /**
         * 备份到硬件钱包
         */
        public static final int MODE_BACKUP_WALLET_TO_COLD = 2;
        /**
         * 配对硬件钱包
         */
        public static final int MODE_PAIR_WALLET_TO_COLD = 3;
        /**
         * 已经激活的设备克隆到另一台设备
         */
        public static final int MODE_CLONE_TO_OTHER_COLD = 4;
        /**
         * 绑定共管人
         */
        public static final int MODE_BIND_ADMIN_PERSON = 5;

    }
}

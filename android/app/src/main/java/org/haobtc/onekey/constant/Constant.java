package org.haobtc.onekey.constant;

/**
 * local storage key and others
 */
public final class Constant {
    /**
     * 是否是第一次启动
     * */
    public static final String FIRST_RUN = "is_first_run";
    /**
     * 当前系统语言
     * */
    public static final String LANGUAGE = "language";
    /**
     * 当前使用的通讯方式
     * */
    public static final String WAY = "way";

    /**
     * 是否支持NFC
     * */
    public static final String NFC_SUPPORT = "nfc_support";
    /**
     * 是否创建过免密钱包
     * */
    public static final String PASS_FREE_ALLOW = "haveCreateNopass";
    /**
     * 蓝牙标志
     * */
    public static final String WAY_MODE_BLE = "bluetooth";
    /**
     * NFC标志
     * */
    public static final String WAY_MODE_NFC = "nfc";
    /**
     * usb标志
     * */
    public static final String WAY_MODE_USB = "usb";
    /**
     * 链接过得硬件设备信息
     * */
    public static final String DEVICES = "devices";

    public static final String WALLETS = "wallets";

    /**
     * 蓝牙名称匹规则
     * */
    public static final String PATTERN = "(^(?i)BixinKey\\d{10})|(^K\\d{4})";

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
    /**
     * 激活方式
     * */
    public static final String ACTIVE_MODE = "mode";
    /**
     * 由硬件生成种子
     * */
    public static final int ACTIVE_MODE_NEW = 0;
    /**
     * 导入种子到设备
     * */
    public static final int ACTIVE_MODE_IMPORT = 1;

    public static final int ACTIVE_MODE_LOCAL_BACKUP = 2;
    /**
     * 助记词个数
     */
    public static final int MNEMONIC_SIZE = 12;

    public static final int LENGTH_FILED_START_OFFSET = 10;
    public static final int LENGTH_FILED_END_OFFSET = 18;
    public static final int HEAD_LENGTH = 9;

    public static final String COIN_TYPE_BTC = "BTC";

    public static final String COIN_TYPE_ETH = "ETH";

    public static final String COIN_TYPE_EOS = "EOS";


}

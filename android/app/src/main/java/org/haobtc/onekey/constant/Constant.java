package org.haobtc.onekey.constant;

/**
 * local storage key and others
 * @author liyan
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
    /**
     * 钱包
     * */
    public static final String WALLETS = "wallets";

    public static final String SELECTED_WALLET = "loadWalletName";

    /**
     * 硬件label
     * */
    public static final String  TAG_LABEL = "label";

    /**
     * 蓝牙名称
     * */
    public static final String TAG_BLE_NAME = "ble_name";

    /**
     * 当前固件版本
     * */
    public static final String TAG_FIRMWARE_VERSION = "firmware_version";

    /**
     * 最新固件版本
     * */
    public static final String TAG_FIRMWARE_VERSION_NEW = "firmware_version_new";

    /**
     * 新固件下载地址
     * */
    public static final String TAG_FIRMWARE_DOWNLOAD_URL = "firmware_url";

    /**
     * 新固件更新说明
     * */
    public static final String TAG_FIRMWARE_UPDATE_DES = "firmware_changelog";


    /**
     * 当前蓝牙固件版本
     * */
    public static final String TAG_NRF_VERSION = "nrf_version";

    /**
     * 最新蓝牙固件版本
     * */
    public static final String TAG_NRF_VERSION_NEW = "nrf_version_new";

    /**
     * 蓝牙新固件下载地址
     * */
    public static final String TAG_NRF_DOWNLOAD_URL= "nrf_url";

    /**
     * 新蓝牙固件更新说明
     * */
    public static final String TAG_NRF_UPDATE_DES = "nrf_changelog";
    /**
     * 当前Pin
     * */
    public static final String PIN_ORIGIN = "pin_origin";
    /**
     * 新Pin
     * */
    public static final String PIN_NEW = "pin_new";
    /**
     * 配对过的蓝牙信息
     * */
    public static final String BLE_INFO = "ble_info";
    /**
     * 蓝牙名称匹规则
     * */
    public static final String PATTERN = "(^(?i)BixinKey\\d{10})|(^K\\d{4})";
    /**
     * 助记词
     * */
    public static final String MNEMONICS = "mnemonics";
    /**
     * 搜索模式
     * */
    public static final String SEARCH_DEVICE_MODE = "search_device_mode";
    /**
     * 设备id
     * */
    public static final String DEVICE_ID = "device_id";
    /**
     * 自动关机时间
     * */
    public static final String AUTO_SHUT_DOWN_TIME = "shut_down_time";
   /**
    * 蓝牙Mac地址
    * */
    public static final String BLE_MAC = "mac";
    /**
     * todo: 临时的
     * */
    public static final String WALLET_TYPE_HARDWARE = "btc-hw-derived-1-1";
    /**
     * 固件升级文件后缀
     * */
    public static final String FIRMWARE_UPDATE_FILE_SUFFIX = ".bin";

    /**
     * 蓝牙固件升级文件后缀
     * */
    public static final String NRF_UPDATE_FILE_SUFFIX = ".zip";
    /**
     * 升级文件名称
     * */
    public static final String UPDATE_FILE_NAME = "onekey-";
    public static class SearchDeviceMode{
        /**
         * 通过硬件恢复 HD 钱包
         */
        public static final int MODE_RECOVERY_WALLET_BY_COLD = 1;
        /**
         * 备份本地创建的HD钱包到硬件钱包
         */
        public static final int MODE_BACKUP_HD_WALLET_TO_DEVICE = 2;
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
        /**
         * 个人签名
         * */
        public static final int MODE_SIGN_TX = 6;
    }
    public static final String OPERATE_TYPE = "operate_type";

    public static final String EXPORT_DESTINATIONS = "export2_hardware";

    public static final String RECOVERY_TYPE = "recovery_from_hardware";
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
    public static final String EXTEND_PUBLIC_KEY = "xpub";
    public static final String UPGRADE_INFO = "upgrade_info";
    public static final String CURRENT_SELECTED_WALLET = "current_selected_wallet";

}

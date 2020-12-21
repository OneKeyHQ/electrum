package org.haobtc.onekey.constant;

/**
 * local storage key、intent tags and others
 *
 * @author liyan
 */
public final class Constant {
    //=======================sp constant===============
    /**
     * 是否是第一次启动
     */
    public static final String FIRST_RUN = "is_first_run";
    /**
     * 当前系统语言
     */
    public static final String LANGUAGE = "language";
    /**
     * 当前使用的通讯方式
     */
    public static final String WAY = "way";

    /**
     * 是否支持NFC
     */
    public static final String NFC_SUPPORT = "nfc_support";
    /**
     * 是否创建过免密钱包
     */
    public static final String PASS_FREE_ALLOW = "haveCreateNopass";
    /**
     * 蓝牙标志
     */
    public static final String WAY_MODE_BLE = "bluetooth";
    /**
     * NFC标志
     */
    public static final String WAY_MODE_NFC = "nfc";
    /**
     * usb标志
     */
    public static final String WAY_MODE_USB = "usb";
    /**
     * 链接过得硬件设备信息
     */
    public static final String DEVICES = "devices";
    /**
     * 钱包
     */
    public static final String WALLETS = "wallets";

    /**
     * 设备验证
     */
    public static final String TAG_HARDWARE_VERIFY = "is_verify";

    /**
     * 配对过的蓝牙信息
     */
    public static final String BLE_INFO = "ble_info";

    /**
     * 版本更新信息key
     */
    public static final String UPGRADE_INFO = "upgrade_info";

    /**
     * 当前选择钱包名称
     */
    public static final String CURRENT_SELECTED_WALLET_NAME = "current_selected_wallet_name";
    /**
     * 当前选择钱包类型
     */
    public static final String CURRENT_SELECTED_WALLET_TYPE = "current_selected_wallet_type";
    /**
     * appHD钱包使用的密码类型
     */
    public static final String SOFT_HD_PASS_TYPE = "shortOrLongPass";
    /**
     * 是否需要弹出备份提醒
     */
    public static final String NEED_POP_BACKUP_DIALOG = "pop_backup_dialog";
    /**
     * 长密码
     */
    public static final String SOFT_HD_PASS_TYPE_LONG = "long";
    /**
     * 短密码
     */
    public static final String SOFT_HD_PASS_TYPE_SHORT = "short";
    // ======================= Intent Tag ===================

    /**
     * 硬件label
     */
    public static final String TAG_LABEL = "label";
    /**
     * 是否是特殊设备
     */
    public static final String TAG_IS_BACKUP_ONLY = "backup_only";
    /**
     * 蓝牙名称
     */
    public static final String TAG_BLE_NAME = "ble_name";

    /**
     * 当前固件版本
     */
    public static final String TAG_FIRMWARE_VERSION = "firmware_version";

    /**
     * 最新固件版本
     */
    public static final String TAG_FIRMWARE_VERSION_NEW = "firmware_version_new";

    /**
     * 新固件下载地址
     */
    public static final String TAG_FIRMWARE_DOWNLOAD_URL = "firmware_url";

    /**
     * 新固件更新说明
     */
    public static final String TAG_FIRMWARE_UPDATE_DES = "firmware_changelog";

    /**
     * 当前蓝牙固件版本
     */
    public static final String TAG_NRF_VERSION = "nrf_version";

    /**
     * 最新蓝牙固件版本
     */
    public static final String TAG_NRF_VERSION_NEW = "nrf_version_new";

    /**
     * 蓝牙新固件下载地址
     */
    public static final String TAG_NRF_DOWNLOAD_URL = "nrf_url";

    /**
     * 新蓝牙固件更新说明
     */
    public static final String TAG_NRF_UPDATE_DES = "nrf_changelog";
    /**
     * 当前Pin
     */
    public static final String PIN_ORIGIN = "pin_origin";
    /**
     * 新Pin
     */
    public static final String PIN_NEW = "pin_new";

    /**
     * 蓝牙名称匹规则(?i 忽略大小写)
     */
    public static final String PATTERN = "(^(?i)BixinKey\\d{10}$)|(^K\\d{4}$)";
    /**
     * 交易体积
     */
    public static final String TAG_TX_SIZE = "tx_size";
    /**
     * 自定义费率的最小值
     */
    public static final String CUSTOMIZE_FEE_RATE_MIN = "min_fee_rate";
    /**
     * 自定义费率的最大值
     */
    public static final String CUSTOMIZE_FEE_RATE_MAX = "max_fee_rate";
    /**
     * 助记词
     */
    public static final String MNEMONICS = "mnemonics";
    /**
     * 搜索模式
     */
    public static final String SEARCH_DEVICE_MODE = "search_device_mode";
    /**
     * 设备id
     */
    public static final String DEVICE_ID = "device_id";
    /**
     * 自动关机时间
     */
    public static final String AUTO_SHUT_DOWN_TIME = "shut_down_time";
    /**
     * 蓝牙Mac地址
     */
    public static final String BLE_MAC = "mac";
    /**
     * 通过硬件创建
     */
    public static final String WALLET_TYPE_HARDWARE = "btc-hw-derived-1-1";
    /**
     * 软件hd
     */
    public static final String WALLET_TYPE_LOCAL_HD = "btc-hd-standard";
    /**
     * 固件升级文件后缀
     */
    public static final String FIRMWARE_UPDATE_FILE_SUFFIX = ".bin";

    /**
     * 蓝牙固件升级文件后缀
     */
    public static final String NRF_UPDATE_FILE_SUFFIX = ".zip";
    /**
     * 升级文件名称
     */
    public static final String UPDATE_FILE_NAME = "onekey-";
    /**
     * 钱包类型TAG
     */
    public static final String WALLET_TYPE = "wallet_type";

    public static final String TAG_HARDWARE_TYPE_PROMOTE_ID = "update_promote_id";

    public static final String DEVICE_NOT_BOND = "DFU DEVICE NOT BONDED";
    /**
     * 交易发送方
     */
    public static final String TRANSACTION_SENDER = "tx_sender";
    /**
     * 交易接收方
     */
    public static final String TRANSACTION_RECEIVER = "tx_receiver";
    /**
     * 交易手续费
     */
    public static final String TRANSACTION_FEE = "tx_fee";
    /**
     * 交易额
     */
    public static final String TRANSACTION_AMOUNT = "tx_amount";

    public static final String RAW_MESSAGE = "raw_message";
    public static final String SIGNATURE = "message_signature";
    public static final String FORCE_UPDATE = "force_update";

    public static final String DEVICE_DEFAULT_LABEL = "oneKey";
    /**
     * 本地是否存在软件hd
     */
    public static final String HAS_LOCAL_HD = "has_local_hd";
    /**
     * 校验PIN方式
     * */
    public static final String PIN_VERIFY_ON_HARDWARE = "pin_verify_on_hardware";

    public static class SearchDeviceMode {
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
         */
        public static final int MODE_PREPARE = 6;
    }
    /**
     * 这个值意味着要在硬件上输入PIN, 注意 '0' 并不是合法的可选PIN值
     * */
    public static final String PIN_INVALID = "000000";
    /**
     * 操作类型
     * */
    public static final String OPERATE_TYPE = "operate_type";
    /**
     * 备份hd到未激活硬件设备
     * */
    public static final String EXPORT_DESTINATIONS = "export2_hardware";

    public static final String RECOVERY_TYPE = "recovery_from_hardware";
    /**
     * 激活方式tag
     */
    public static final String ACTIVE_MODE = "mode";
    /**
     * 由硬件生成种子激活
     */
    public static final int ACTIVE_MODE_NEW = 0;
    /**
     * 导入种子到设备激活
     */
    public static final int ACTIVE_MODE_IMPORT = 1;
    /**
     * 通过本地备份激活
     */
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
    public static final String COIN_TYPE = "coin_type";
    public static final String EXTEND_PUBLIC_KEY = "xpub";

    /**
     * 由软件创建
     */
    public static final int WALLET_TYPE_SOFTWARE = 0;
    /**
     * 由OneKey创建单签
     */
    public static final int WALLET_TYPE_HARDWARE_PERSONAL = 1;
    /**
     * 由OneKey创建多签
     */
    public static final int WALLET_TYPE_HARDWARE_MULTI = 2;

    /**
     * 主网
     */
    public static final String BITCOIN_NETWORK_TYPE_0 = "mainnet";
    /**
     * 回归测试网(私链)
     */
    public static final String BITCOIN_NETWORK_TYPE_1 = "regnet";
    /**
     * 公共测试网
     */
    public static final String BITCOIN_NETWORK_TYPE_2 = "testnet";
    /**
     * onekey官网
     * https://onekey.so/
     */
    public static final String ONE_KEY_WEBSITE = "https://key.bixin.com/";

    /**
     * 钱包可见名字
     */
    public static final String WALLET_LABEL = "wallet_label";
    /**
     * 钱包余额
     */
    public static final String WALLET_BALANCE = "balance";

    /**
     * 比特币计量单位BTC
     */
    public static final String BTC_UNIT_BTC = "BTC";
    /**
     * 比特币计量单位1 BTC = 1000 mBTC
     */
    public static final String BTC_UNIT_M_BTC = "mBTC";
    /**
     * 比特币计量单位 1 BTC = 10^6 bits
     */
    public static final String BTC_UNIT_M_BITS = "bits";

    public static final String VERIFY_DETAIL = "verify_detail";
    /**
     * 当前发币的货币符号
     */
    public static final String CURRENT_CURRENCY_SYMBOL = "currency_symbol";

    /**
     * 当前发币的货币图形符号
     */
    public static final String CURRENT_CURRENCY_GRAPHIC_SYMBOL = "currency_graphic_symbol";

    public static final String English = "English";

    public static final String Chinese = "Chinese";

    public static final String En_UK = "en_UK";

    public static final String Zh_CN = "zh_CN";
    /**
     * 收款与转账未备份提示
     */
    public static final String UN_BACKUP_TIP = "un_backup_tip";
}

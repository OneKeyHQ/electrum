package org.haobtc.onekey.passageway;

public final class CommandMethod {

    private CommandMethod(){}

    /**
     * 激活设备
     */
    public static final String INIT = "init";
    /**
     * 获取硬件详情
     */
    public static final String GET_FEATURE = "get_feature";
    /**
     * 导入助记词
     */
    public static final String BIXIN_LOAD_DEVICE = "bixin_load_device";
    /**
     * 导出助记词
     */
    public static final String BIXIN_BACKUP_DEVICE = "bixin_backup_device";
    /**
     * 获取硬件xpub
     */
    public static final String GET_XPUB_FROM_HW = "get_xpub_from_hw";
    /**
     * 获取派生公钥
     */
    public static final String CREATE_BTC_HW_DERIVED_WALLET = "create_btc_hw_derived_wallet";
    /**
     * 通过派生公钥创建钱包
     */
    public static final String IMPORT_CREATE_HW_WALLET = "import_create_hw_wallet";
    /**
     * 从已激活的硬件恢复钱包
     */
    public static final String RECOVERY_HD_DERIVED_WALLET_FROM_HW = "recovery_hd_derived_wallet_from_hw";
    /**
     * 设置语言/关机时间/
     */
    public static final String APPLY_SETTING = "apply_setting";
    /**
     * 升级固件
     */
    public static final String FIRMWARE_UPDATE = "firmware_update";
    /**
     * 防伪验证
     */
    public static final String HARDWARE_VERIFY = "hardware_verify";
    /**
     * 修改pin码
     */
    public static final String RESET_PIN = "reset_pin";
    /**
     * 恢复出厂设置
     */
    public static final String WIPE_DEVICE = "wipe_device";

    public static final String NOTIFY = "notify";


}

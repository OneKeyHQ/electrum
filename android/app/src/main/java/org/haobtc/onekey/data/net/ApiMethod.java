package org.haobtc.onekey.data.net;

public final class ApiMethod {

    private ApiMethod(){}

    /**
     * 创建HD钱包/恢复HD钱包
     */
    public static final String CREATE_HD_WALLET = "create_hd_wallet";
    /**
     * 创建HD派生钱包
     */
    public static final String CREATE_BTC_DERIVED_WALLET = "create_btc_derived_wallet";
    /**
     * 确认需要恢复的钱包
     */
    public static final String RECOVERY_CONFIRMED = "recovery_confirmed";
    /**
     * 通过硬件创建HD钱包/创建共管钱包
     */
    public static final String IMPORT_CREATE_HW_WALLET = "import_create_hw_wallet";
    /**
     * 删除钱包
     */
    public static final String DELETE_WALLET = "delete_wallet";
    /**
     * 创建单币种钱包/导入seed创建钱包/导入私钥创建钱包/导入地址创建钱包/导入xpub创建钱包
     */
    public static final String CREATE = "create";
    /**
     * 加载钱包们
     */
    public static final String LOAD_ALL_WALLET = "load_all_wallet";
    /**
     * 获取钱包列表信息
     */
    public static final String LIST_WALLETS = "list_wallets";
    /**
     * 选择指定钱包
     */
    public static final String SELECT_WALLET = "select_wallet";
    /**
     * 获取默认费率
     */
    public static final String GET_DEFAULT_FEE_STATUS = "get_default_fee_status";
    /**
     * 输入地址和转账额度获取fee
     */
    public static final String GET_FEE_BY_FEERATE = "get_fee_by_feerate";
    /**
     * 创建交易
     */
    public static final String MKTX = "mktx";
    /**
     * 获取交易状态（是否可以删除)
     */
    public static final String GET_REMOVE_FLAG = "get_remove_flag";
    /**
     * 签名交易
     */
    public static final String SIGN_TX = "sign_tx";
    /**
     * 广播交易
     */
    public static final String BROADCAST_TX = "broadcast_tx";
    /**
     * 签名信息
     */
    public static final String SIGN_MESSAGE = "sign_message";
    /**
     * 消息验签
     */
    public static final String VERIFY_MESSAGE = "verify_message";
    /**
     * 删除交易
     */
    public static final String REMOVE_LOCAL_TX = "remove_local_tx";
    /**
     * 法币和BTC单位互换
     */
    public static final String GET_EXCHANGE_CURRENCY = "get_exchange_currency";
    /**
     * 收款
     */
    public static final String GET_WALLET_ADDRESS_SHOW_UIQR_DTA = "get_wallet_address_show_UIqr_dta";
    /**
     * 硬件钱包校验地址
     */
    public static final String SHOW_ADDRESS = "show_address";
    /**
     * 获取交易记录
     */
    public static final String GET_ALL_TX_LIST = "get_all_tx_list";
    /**
     * 获取指定交易详情
     */
    public static final String GET_TX_INFO = "get_tx_info";

    /**
     * 修改密码
     */
    public static final String UPDATE_PASSWORD = "update_password";
    /**
     * 导出助记词
     */
    public static final String EXPORT_SEED = "export_seed";
    /**
     * 获取法币列表
     */
    public static final String GET_CURRENCIES = "get_currencies";
    /**
     * 设置法币单位
     */
    public static final String SET_CURRENCY = "set_currency";
    /**
     * 设置比特币单位
     */
    public static final String SET_BASE_UINT = "set_base_uint";
    /**
     * 设置同步服务器
     */
    public static final String SET_SYNC_SERVER_HOST = "set_sync_server_host";
    /**
     * 获取行情服务器列表
     */
    public static final String GET_EXCHANGES = "get_exchanges";
    /**
     * 设置行情服务器
     */
    public static final String SET_EXCHANGE = "set_exchange";
    /**
     * 获取默认节点  ETH
     */
    public static final String GET_DEFAULT_SERVER = "get_default_server";
    /**
     * 设置服务器
     */
    public static final String SET_SERVER = "set_server";
    /**
     * 获取服务器列表
     */
    public static final String GET_SERVER_LIST = "get_server_list";


}

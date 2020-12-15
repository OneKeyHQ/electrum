package org.haobtc.onekey.constant;

/**
 * python constant
 *
 * @author liyan
 */
public final class PyConstant {
    /**
     * python modules and attributes
     * */
    public static final String ELECTRUM_CONSTANTS_MODULE = "electrum.constants";
    public static final String TREZORLIB_TRANSPORT_NFC = "trezorlib.transport.nfc";
    public static final String TREZORLIB_TRANSPORT_BLUETOOTH = "trezorlib.transport.bluetooth";
    public static final String TREZORLIB_TRANSPORT_ANDROID_USB = "trezorlib.transport.android_usb";
    public static final String TREZORLIB_TRANSPORT_PROTOCOL = "trezorlib.transport.protocol";
    public static final String TREZORLIB_CUSTOMER_UI = "trezorlib.customer_ui";
    public static final String UI_HANDLER = "handler";
    public static final String BLUETOOTH_HANDLER = "BlueToothHandler";
    public static final String BLUETOOTH_TRANSPORT = "BlueToothTransport";

    public static final String NFC_HANDLE = "NFCHandle";
    public static final String NFC_TRANSPORT = "NFCTransport";

    public static final String CUSTOMER_UI = "CustomerUI";
    public static final String ANDROID_USB_TRANSPORT = "AndroidUsbTransport";

    public static final String WRITE_SUCCESS = "WRITE_SUCCESS";
    public static final String RESPONSE = "RESPONSE";
    public static final String ENABLED = "ENABLED";
    public static final String BLE = "BLE";
    public static final String BLE_DEVICE = "BLE_DEVICE";
    public static final String CALL_BACK = "CALL_BACK";
    public static final String ANDROID_COMMANDS = "AndroidCommands";
    public static final String ANDROID_ID = "android_id";
    public static final String CALLBACK = "callback";
    public static final String CALLBACK_SERVER_STATUS = "set_server_status";
    public static final String CALLBACK_HISTORY = "update_history";
    public static final String CALLBACK_STATUS = "update_status";
    public static final String ELECTRUM_GUI_ANDROID_CONSOLE = "electrum_gui.android.console";
    public static final String USER_CANCEL = "user_cancel";
    public static final String PIN = "pin";
    public static final String NOTIFICATION = "notify";
    public static final String IS_CANCEL = "IS_CANCEL";
    public static final String SET_TEST_NETWORK = "set_testnet";
    public static final String SET_REG_TEST_NETWORK = "set_regtest";
    public static final String PROCESS_REPORTER = "PROCESS_REPORTER";
    /**
     * 断点续传标记
     */
    public static final String HTTP = "HTTP";
    /**
     * 断点续传偏移量
     */
    public static final String OFFSET = "OFFSET";
    public static final String TAG = "BLE";

    public static final int BUTTON_REQUEST_9 = 9;
    /**
     * 按键确认
     */
    public static final int BUTTON_REQUEST_7 = 7;
    /**
     * 发币确认
     */
    public static final int BUTTON_REQUEST_8 = 8;
    /**
     * 恢复出厂设置
     */
    public static final int BUTTON_REQUEST_6 = 6;
    /**
     * 验证当前PIN
     */
    public static final int PIN_CURRENT = 1;
    /**
     * 设置新PIN
     */
    public static final int PIN_NEW_FIRST = 2;
    /**
     * 设置passphrass
     */
    public static final int PASS_NEW_PASSPHRASS = 6;
    /**
     * 验证passphrass
     */
    public static final int PASS_PASSPHRASS = 3;

    /**
     * 校验地址确认
     */
    public static final int VERIFY_ADDRESS_CONFIRM = 10;
    /**
     * xpub 类型
     */
    public static final String XPUB_P2WPKH = "p2wpkh";

// ============================= Python api name======================

    /**
     * 加载所有创建的钱包
     */
    public static final String LOAD_ALL_WALLET = "load_all_wallet";
    /**
     * 通过xpub创建钱包接口 import_create_hw_wallet(self, name, m, n, xpubs, hide_type=False, hd=False, derived=False)
     */
    public static final String CREATE_WALLET_BY_XPUB = "import_create_hw_wallet";
    /**
     * 获取硬件钱包信息接口
     */
    public static final String GET_FEATURE = "get_feature";
    /**
     * 加载本地钱包详情
     */
    public static final String GET_WALLETS_INFO = "list_wallets";

    /**
     * 查看钱包余额
     */
    public static final String GET_BALANCE = "select_wallet";

    /**
     * 查看当前钱包的备份状态
     */
    public static final String HAS_BACKUP = "get_backup_info";

    /**
     * 校验扩展公钥合法性
     */
    public static final String VALIDATE_XPUB = "is_valid_xpub";

    /**
     * 通过助记词创建HD钱包
     */
    public static final String CREATE_HD_WALLET = "create_hd_wallet";

    /**
     * 通过助记词创建HD钱包
     */
    public static final String RECOVERY_CONFIRM = "recovery_confirmed";
    /**
     * 固件升级
     * */
    public static final String FIRMWARE_UPDATE = "firmware_update";
    /**
     * 解析交易
     * */
    public static final String ANALYZE_TX = "get_tx_info_from_raw";
    /**
     * 获取当前费率信息
     * */
    public static final String GET_DEFAULT_FEE_DETAILS = "get_default_fee_info";
    /**
     * 计算当前交易的手续费
     * */
    public static final String CALCULATE_FEE = "get_fee_by_feerate";
    /**
     * 通过临时交易构建最终交易
     * */
    public static final String MAKE_TX = "mktx";
    /**
     * 检验地址合法性
     * */
    public static final String VERIFY_ADDRESS = "verify_address";
    /**
     * 比特币-> 现金 转换
     * */
    public static final String EXCHANGE_RATE_CONVERSION = "get_exchange_currency";
    /**
     * 广播交易
     * */
    public static final String BROADCAST_TX = "broadcast_tx";
    /**
     * 签名交易
     * */
    public static final String SIGN_TX = "sign_tx";
    /**
     * 获取当前选择钱包的地址信息
     * */
    public static final String ADDRESS_INFO = "get_wallet_address_show_UI";
    /**
     * 校验签名信息
     * */
    public static final String VERIFY_MESSAGE_SIGNATURE = "verify_message";
    /**
     * 删除钱包备份状态
     * */
    public static final String CLEAR_BACK_FLAGS = "delete_backup_info";
}

package org.haobtc.onekey.constant;

/**
 * python constant
 *
 * @author liyan
 */
public final class PyConstant {

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

    public static final String TAG = "BLE";

    public static final int BUTTON_REQUEST_9 = 9;
    /**
     * 按键确认
     */
    public static final int BUTTON_REQUEST_7 = 7;

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
     * 输入passphrass
     */
    public static final int PASS_NEW_PASSPHRASS = 6;

    public static final int PASS_PASSPHRASS = 3;
    /**
     * xpub 类型
     * */
    public static final String XPUB_P2WPKH = "p2wpkh";
// ============================= Python api name======================

    /**
     * 加载所有创建的钱包
     * */
    public static final String LOAD_ALL_WALLET = "load_all_wallet";
    /**
     * 通过xpub创建钱包接口 import_create_hw_wallet(self, name, m, n, xpubs, hide_type=False, hd=False, derived=False)
     */
    public static final String CREATE_WALLET_BY_XPUB = "import_create_hw_wallet";
    /**
     * 获取硬件钱包信息接口
     * */
    public static final String GET_FEATURE = "get_feature";
    /**
     * 加载本地钱包详情
     * */
    public static final String GET_WALLETS_INFO = "list_wallets";

    /**
     * 查看钱包余额
     * */
    public static final String GET_BALANCE = "select_wallet";

    /**
     * 查看当前钱包的备份状态
     * */
    public static final String HAS_BACKUP = "get_backup_info";

    /**
     * 校验扩展公钥合法性
     * */
    public static final String VALIDATE_XPUB = "is_valid_xpub";

    /**
     * 通过助记词创建HD钱包
     * */
    public static final String CREATE_HD_WALLET = "create_hd_wallet";

    /**
     * 通过助记词创建HD钱包
     * */
    public static final String RECOVERY_CONFIRM = "recovery_confirmed";

}

package org.haobtc.onekey.manager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.data.prefs.PreferencesManager;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

/**
 * @author liyan
 */
public final class PyEnv {

    private static final String TAG = PyEnv.class.getSimpleName();

    public static PyObject sBle, sCustomerUI, sNfc, sUsb, sBleHandler, sNfcHandler, sBleTransport,
            sNfcTransport, sUsbTransport, sProtocol, sCommands;
    public static FutureTask<PyObject> futureTask;
    private static ExecutorService mexecutorService = Executors.newSingleThreadExecutor();


    static {
        sNfc = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_NFC);
        sBle = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_BLUETOOTH);
        sUsb = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_ANDROID_USB);
        sProtocol = Global.py.getModule(PyConstant.TREZORLIB_TRANSPORT_PROTOCOL);
        sBleHandler = sBle.get(PyConstant.BLUETOOTH_HANDLER);
        sNfcHandler = sNfc.get(PyConstant.NFC_HANDLE);
        sUsbTransport = sUsb.get(PyConstant.ANDROID_USB_TRANSPORT);
        sNfcTransport = sNfc.get(PyConstant.NFC_TRANSPORT);
        sBleTransport = sBle.get(PyConstant.BLUETOOTH_TRANSPORT);
        sCustomerUI = Global.py.getModule(PyConstant.TREZORLIB_CUSTOMER_UI).get(PyConstant.CUSTOMER_UI);
    }

    public static void init(@NonNull Context context) {
        if (BuildConfig.net_type.equals(context.getString(R.string.TestNet))) {
            setTestNet();
        } else if (BuildConfig.net_type.equals(context.getString(R.string.RegTest))) {
            setRegNet();
        }
        Daemon.initCommands();
        sCommands = Daemon.commands;
        // 加载钱包信息
//        loadLocalWalletInfo(context);
    }

    /**
     * 设置硬件回调句柄
     */
    public static void setHandle(HardwareCallbackHandler handle) {
        sCustomerUI.put(PyConstant.UI_HANDLER, handle);
    }

    /**
     * 设置当前网络类型为公有测试网络
     */
    private static void setTestNet() {
        setNetType(PyConstant.SET_TEST_NETWORK);
    }

    /**
     * 设置当前网络类型为回归测试网络(私链)
     */
    private static void setRegNet() {
        setNetType(PyConstant.SET_REG_TEST_NETWORK);
    }

    private static void setNetType(String type) {
        Global.py.getModule(PyConstant.ELECTRUM_CONSTANTS_MODULE).callAttr(type);
    }

    /**
     * 提醒后台任务线程结束等待
     */
    public static void sNotify() {
        sProtocol.callAttr(PyConstant.NOTIFICATION);
    }


    public static void bleCancel() {
        sBle.put(PyConstant.IS_CANCEL, true);
    }

    public static void nfcCancel() {
        sNfc.put(PyConstant.IS_CANCEL, true);
    }

    public static void usbCancel() {
        sUsb.put(PyConstant.IS_CANCEL, true);
    }

    public static void bleReWriteResponse(String response) {
        sBleHandler.put(PyConstant.RESPONSE, response);
    }

    /**
     * 启用蓝牙，并做相关初始化准备
     */
    public static void bleEnable(BleDevice device, BleWriteCallback<BleDevice> mWriteCallBack) {
        sBleTransport.put(PyConstant.ENABLED, true);
        sNfcTransport.put(PyConstant.ENABLED, false);
        sUsbTransport.put(PyConstant.ENABLED, false);
        sBleHandler.put(PyConstant.BLE, Ble.getInstance());
        sBleHandler.put(PyConstant.BLE_DEVICE, device);
        sBleHandler.put(PyConstant.CALL_BACK, mWriteCallBack);
        sBle.put(PyConstant.WRITE_SUCCESS, true);
    }

    /**
     * 启用NFC，并做相关初始化准备
     */
    public static void nfcEnable() {
        sNfcTransport.put(PyConstant.ENABLED, true);
        sBleTransport.put(PyConstant.ENABLED, false);
        sUsbTransport.put(PyConstant.ENABLED, false);
    }

    /**
     * 启用USB，并做相关初始化准备
     */
    public static void usbEnable() {
        sUsbTransport.put(PyConstant.ENABLED, true);
        sBleTransport.put(PyConstant.ENABLED, false);
        sNfcTransport.put(PyConstant.ENABLED, false);
    }

    /**
     * 撤销正在执行的通信
     */
    public static void cancelAll() {
        sNotify();
        bleCancel();
        nfcCancel();
        usbCancel();
    }

    /**
     * 回传PIN码
     */
    public static void setPin(String pin) {
        sCustomerUI.put(PyConstant.PIN, pin);
    }

    /**
     * 获取硬件设备信息
     */
    @NonNull
    public static HardwareFeatures getFeature(Context context) throws Exception {
        String feature;
        try {
            futureTask = new FutureTask<>(() -> Daemon.commands.callAttr(PyConstant.GET_FEATURE, MyApplication.getInstance().getDeviceWay()));
            mexecutorService.submit(futureTask);
            feature = futureTask.get(5, TimeUnit.SECONDS).toString();
            return dealWithConnectedDevice(context, HardwareFeatures.objectFromData(feature));
        } catch (Exception e) {
            if (sBle != null) {
              cancelAll();
            }
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 处理当前链接硬件的设备信息并保存
     * 1. 已激活，并且有备份信息的不能直接覆盖
     * 2. 除上中情形，直接覆盖原有信息
     */
    private static HardwareFeatures dealWithConnectedDevice(Context context, HardwareFeatures features) {

        if (features.isInitialized()) {
            HardwareFeatures old;
            String backupMessage = "";
            if (PreferencesManager.contains(context, Constant.DEVICES, features.getDeviceId())) {
                old = HardwareFeatures.objectFromData((String) PreferencesManager.get(context, Constant.DEVICES, features.getDeviceId(), ""));
                backupMessage = old.getBackupMessage();
            }
            if (!Strings.isNullOrEmpty(backupMessage)) {
                features.setBackupMessage(backupMessage);
            }
        }
//        else {
//            // modify this value manual to support unfinished combined init
//            features.setNeedsBackup(true);
//        }
        PreferencesManager.put(context, Constant.DEVICES, features.getDeviceId(), features.toString());
        return features;
    }

    /**
     * 通过xpub创建钱包
     */
    public static void createWallet(BaseActivity activity, String walletName, int m, int n, String xPubs) {

        mexecutorService.execute(() -> {
            try {
                sCommands.callAttr(PyConstant.CREATE_WALLET_BY_XPUB, walletName, m, n, xPubs);
                EventBus.getDefault().post(new CreateSuccessEvent());
            } catch (Exception e) {
                e.printStackTrace();
                String message = e.getMessage();
                assert message != null;
                if (HardWareExceptions.WALLET_ALREADY_EXIST.getMessage().equals(message)) {
                    activity.showToast(R.string.changewalletname);
                } else {
                    if (message.contains(HardWareExceptions.WALLET_ALREADY_EXIST_1.getMessage())) {
                        String haveWalletName = message.substring(message.indexOf("name=") + 5);
                        activity.showToast(activity.getString(R.string.xpub_have_wallet) + haveWalletName);
                    }
                }
                activity.finish();
            }
        });
    }

    /**
     * 加载本地钱包信息
     */
    public static void loadLocalWalletInfo(Context context) {

        try {
            sCommands.callAttr(PyConstant.LOAD_ALL_WALLET);
            String walletsInfo = sCommands.callAttr(PyConstant.GET_WALLETS_INFO).toString();
            if (!Strings.isNullOrEmpty(walletsInfo)) {
                JsonArray wallets = JsonParser.parseString(walletsInfo).getAsJsonArray();
                wallets.forEach((wallet) -> {
                    wallet.getAsJsonObject().keySet().forEach((walletName) -> PreferencesManager.put(context, Constant.WALLETS, walletName,
                            wallet.getAsJsonObject().get(walletName)));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择要使用的钱包
     */
    public static BalanceInfo selectWallet(@NonNull String name) {
        try {
            String info = sCommands.callAttr(PyConstant.GET_BALANCE, name).toString();
            return BalanceInfo.objectFromData(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看当前钱包的备份状态
     */
    public static boolean hasBackup() {
        try {
            return sCommands.callAttr(PyConstant.HAS_BACKUP).toBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 校验扩展公钥格式
     * */
    public static boolean validateXpub(String xpub) {
        if (Global.guiConsole != null) {
          return  Global.guiConsole.callAttr(PyConstant.VALIDATE_XPUB, xpub).toBoolean();
        }
        return false;
    }
    public static List<BalanceInfo> createLocalHd(String passwd, String mnemonics) {
        List<BalanceInfo> infos = new ArrayList<>();
        try {
        String  walletsInfo  = sCommands.callAttr(PyConstant.CREATE_HD_WALLET, passwd, mnemonics).toString();
            System.out.println("============" + walletsInfo);
            if (!Strings.isNullOrEmpty(walletsInfo)) {
                JsonArray wallets = JsonParser.parseString(walletsInfo).getAsJsonArray();
                wallets.forEach((wallet) -> {
                   infos.add(BalanceInfo.objectFromData(wallet.toString()));
                });
            }
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean recoveryConfirm(List<String> nameList) {
        try {
            sCommands.callAttr(PyConstant.RECOVERY_CONFIRM, new Gson().toJson(nameList.toString()), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

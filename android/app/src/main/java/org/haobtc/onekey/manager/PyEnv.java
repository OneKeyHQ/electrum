package org.haobtc.onekey.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import com.alibaba.fastjson.JSON;
import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;
import dr.android.utils.LogUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.AllWalletBalanceInfoDTO;
import org.haobtc.onekey.bean.BalanceCoinInfo;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.CurrentAddressDetail;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.bean.CurrentWalletBalanceBean;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.MakeTxResponseBean;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.RPCInfoBean;
import org.haobtc.onekey.bean.SignTxResponseBean;
import org.haobtc.onekey.bean.SwitchWalletBean;
import org.haobtc.onekey.bean.TemporaryTxInfo;
import org.haobtc.onekey.bean.TokenList;
import org.haobtc.onekey.bean.TransactionInfoBean;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.RefreshEvent;
import org.haobtc.onekey.exception.AccountException;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.exception.PyEnvException;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.Global;
import org.jetbrains.annotations.NotNull;

/** @author liyan */
public final class PyEnv {

    public static PyObject sBle,
            sCustomerUI,
            sNfc,
            sUsb,
            sBleHandler,
            sNfcHandler,
            sBleTransport,
            sNfcTransport,
            sUsbTransport,
            sProtocol,
            sCommands;
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(
                    2,
                    new ThreadFactoryBuilder().setNameFormat("PyEnv-schedule-%d").build(),
                    new ThreadPoolExecutor.DiscardPolicy());
    public static ListeningScheduledExecutorService mExecutorService =
            MoreExecutors.listeningDecorator(scheduledThreadPoolExecutor);

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
        sCustomerUI =
                Global.py.getModule(PyConstant.TREZORLIB_CUSTOMER_UI).get(PyConstant.CUSTOMER_UI);
    }

    public static HardwareFeatures currentHwFeatures;
    public static Semaphore semaphore = new Semaphore(1);

    public static void init(@NonNull Context context) {
        if (BuildConfig.net_type.equals(context.getString(R.string.TestNet))) {
            setTestNet();
        } else if (BuildConfig.net_type.equals(context.getString(R.string.RegTest))) {
            setRegNet();
        }
        Daemon.initCommands();
        sCommands = Daemon.commands;
        // 加载钱包信息
        sCommands.callAttr(PyConstant.LOAD_ALL_WALLET);
        loadLocalWalletInfo(context);
    }

    public static void cancelPinInput() {
        sCustomerUI.callAttr(PyConstant.USER_CANCEL);
    }

    /** 设置硬件回调句柄 */
    public static void setHandle(HardwareCallbackHandler handle) {
        sCustomerUI.put(PyConstant.UI_HANDLER, handle);
    }

    /** 设置当前网络类型为公有测试网络 */
    private static void setTestNet() {
        setNetType(PyConstant.SET_TEST_NETWORK);
    }

    /** 设置当前网络类型为回归测试网络(私链) */
    private static void setRegNet() {
        setNetType(PyConstant.SET_REG_TEST_NETWORK);
    }

    private static void setNetType(String type) {
        Global.py.getModule(PyConstant.ELECTRUM_CONSTANTS_MODULE).callAttr(type);
    }

    /** 提醒后台任务线程结束等待 */
    public static void sNotify() {
        sProtocol.callAttr(PyConstant.NOTIFICATION);
    }

    /** 结束当前的蓝牙通信 */
    public static void bleCancel() {
        sBleHandler.callAttr(PyConstant.CANCEL_CURRENT_COMM);
    }

    public static void nfcCancel() {
        sNfc.put(PyConstant.IS_CANCEL, true);
    }

    public static void usbCancel() {
        sUsb.put(PyConstant.IS_CANCEL, true);
    }

    /**
     * 给Python回写蓝牙返回数据
     *
     * @param response 蓝牙回调的数据
     */
    public static void bleReWriteResponse(String response) {
        sBleHandler.callAttr(PyConstant.SET_BLE_RESPONSE, response);
    }

    /** 通知Python蓝牙数据已发送成功，可以继续 */
    public static void notifyWriteSuccess() {
        sBleHandler.callAttr(PyConstant.SET_BLE_WRITE_SUCCESS_FLAG);
    }

    /** 启用蓝牙，并做相关初始化准备 */
    public static void bleEnable(BleDevice device, BleWriteCallback<BleDevice> mWriteCallBack) {
        sBleTransport.put(PyConstant.ENABLED, true);
        sNfcTransport.put(PyConstant.ENABLED, false);
        sUsbTransport.put(PyConstant.ENABLED, false);
        sBleHandler.put(PyConstant.BLE, Ble.getInstance());
        sBleHandler.put(PyConstant.BLE_DEVICE, device);
        sBleHandler.put(PyConstant.CALL_BACK, mWriteCallBack);
        sBle.put(PyConstant.WRITE_SUCCESS, true);
    }

    /** 启用NFC，并做相关初始化准备 */
    public static void nfcEnable() {
        sNfcTransport.put(PyConstant.ENABLED, true);
        sBleTransport.put(PyConstant.ENABLED, false);
        sUsbTransport.put(PyConstant.ENABLED, false);
    }

    /** 启用USB，并做相关初始化准备 */
    public static void usbEnable() {
        sUsbTransport.put(PyConstant.ENABLED, true);
        sBleTransport.put(PyConstant.ENABLED, false);
        sNfcTransport.put(PyConstant.ENABLED, false);
    }

    /** 撤销正在执行的通信 */
    public static void cancelAll() {
        bleCancel();
        nfcCancel();
        usbCancel();
        sNotify();
    }

    /** 回传PIN码 */
    public static void setPin(String pin) {
        sCustomerUI.callAttr(PyConstant.SET_PIN, pin);
    }

    public static void cancelRecovery() {
        sCommands.callAttr(PyConstant.CANCEL_RECOVERY);
    }

    /** 获取硬件设备信息 */
    public static void getFeature(
            Context context, Consumer<PyResponse<HardwareFeatures>> callback) {
        Logger.d("get feature");
        if (!tryAcquire()) {
            Logger.d("concurrent get feature");
            return;
        }
        PyResponse<HardwareFeatures> response = new PyResponse<>();
        ListenableFuture<String> listenableFuture =
                Futures.withTimeout(
                        mExecutorService.submit(
                                () ->
                                        Daemon.commands
                                                .callAttr(
                                                        PyConstant.GET_FEATURE,
                                                        MyApplication.getInstance().getDeviceWay())
                                                .toString()),
                        5,
                        TimeUnit.SECONDS,
                        mExecutorService);
        Futures.addCallback(
                listenableFuture,
                new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        release();
                        Logger.json(result);
                        HardwareFeatures features =
                                dealWithConnectedDevice(
                                        context, HardwareFeatures.objectFromData(result));
                        response.setResult(features);
                        callback.accept(response);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        release();
                        Logger.e(t.getMessage());
                        if (sBle != null) {
                            cancelAll();
                        }
                        if (t instanceof TimeoutException) {
                            response.setErrors(context.getString(R.string.get_hard_msg_error));
                        } else {
                            response.setErrors(t.getMessage());
                        }
                        callback.accept(response);
                    }
                },
                mExecutorService);
    }

    /** 处理当前链接硬件的设备信息并保存 1. 已激活，并且有备份信息或已验证的不能直接覆盖 2. 除上中情形，直接覆盖原有信息 */
    private static HardwareFeatures dealWithConnectedDevice(
            Context context, HardwareFeatures features) {
        String deviceIdentifier =
                Optional.ofNullable(features.getSerialNum()).orElse(features.getDeviceId());
        if (features.isInitialized()) {
            HardwareFeatures old;
            String backupMessage = "";
            boolean isVerified = false;
            boolean onceUsed =
                    PreferencesManager.contains(context, Constant.DEVICES, deviceIdentifier);
            if (onceUsed) {
                old =
                        HardwareFeatures.objectFromData(
                                (String)
                                        PreferencesManager.get(
                                                context, Constant.DEVICES, deviceIdentifier, ""));
                backupMessage = old.getBackupMessage();
                isVerified = old.isVerify();
            }
            if (isVerified) {
                features.setVerify(true);
            }
            if (!Strings.isNullOrEmpty(backupMessage)) {
                features.setBackupMessage(backupMessage);
            }
        } else if (features.isBootloaderMode()) {
            features.setBleName(BleManager.currentBleName);
            currentHwFeatures = features;
            return features;
        }
        Optional.ofNullable(features.getSerialNum())
                .ifPresent(
                        (serial) -> {
                            PreferencesManager.put(
                                    context, Constant.DEVICES, serial, features.toString());
                        });
        currentHwFeatures = features;
        return features;
    }

    /** 通过xpub创建钱包 */
    public static String createWallet(
            BaseActivity activity, String walletName, int m, int n, String xPubs) {
        String walletInfo = null;
        try {
            walletInfo =
                    sCommands
                            .callAttr(PyConstant.CREATE_WALLET_BY_XPUB, walletName, m, n, xPubs)
                            .toString();
            Logger.json(walletInfo);
            String name =
                    CreateWalletBean.objectFromData(walletInfo).getWalletInfo().get(0).getName();
            EventBus.getDefault().post(new CreateSuccessEvent(name));
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            assert message != null;
            if (HardWareExceptions.WALLET_ALREADY_EXIST.getMessage().equals(message)) {
                activity.showToast(R.string.changewalletname);
            } else {
                if (message.contains(HardWareExceptions.WALLET_ALREADY_EXIST_1.getMessage())) {
                    String haveWalletName = message.substring(message.indexOf("name=") + 5);
                    activity.showToast(
                            activity.getString(R.string.xpub_have_wallet) + haveWalletName);
                }
            }
            activity.finish();
        }
        return null;
    }

    /** 通过xpub创建钱包 */
    public static PyResponse<String> createWalletNew(
            String walletName, int m, int n, String xPubs, String coinType) {
        PyResponse<String> response = new PyResponse<>();
        String walletInfo = null;
        try {
            walletInfo =
                    sCommands
                            .callAttr(
                                    PyConstant.CREATE_WALLET_BY_XPUB,
                                    walletName,
                                    m,
                                    n,
                                    xPubs,
                                    new Kwarg("coin", coinType))
                            .toString();
            String name =
                    CreateWalletBean.objectFromData(walletInfo).getWalletInfo().get(0).getName();
            EventBus.getDefault().post(new CreateSuccessEvent(name));
            response.setResult(name);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            assert message != null;
            if (HardWareExceptions.WALLET_ALREADY_EXIST.getMessage().equals(message)) {
                response.setErrors(
                        MyApplication.getInstance().getString(R.string.changewalletname));
            } else {
                if (message.contains(HardWareExceptions.WALLET_ALREADY_EXIST_1.getMessage())) {
                    String haveWalletName = message.substring(message.indexOf("name=") + 5);
                    response.setErrors(
                            MyApplication.getInstance().getString(R.string.xpub_have_wallet)
                                    + haveWalletName);
                } else {
                    Exception exception = HardWareExceptions.exceptionConvert(e);
                    response.setErrors(exception.getMessage());
                }
            }
        }
        return response;
    }

    /**
     * 通过xpub恢复钱包
     *
     * @return 找到的钱包
     */
    @WorkerThread
    public static List<BalanceInfoDTO> recoveryWallet(
            BaseActivity activity, String xPubs, boolean hd) throws Exception {
        List<BalanceInfoDTO> infos = new ArrayList<>();
        try {
            String walletsInfo = null;
            try {
                walletsInfo =
                        sCommands
                                .callAttr(
                                        PyConstant.CREATE_WALLET_BY_XPUB,
                                        "",
                                        1,
                                        1,
                                        xPubs,
                                        new Kwarg("hd", hd))
                                .toString();
                Logger.json("通过xpub恢复钱包:" + walletsInfo);
            } catch (Exception ignored) {
                return infos;
            }
            CreateWalletBean.objectFromData(walletsInfo)
                    .getDerivedInfo()
                    .forEach(
                            derivedInfoBean -> {
                                BalanceInfoDTO info = new BalanceInfoDTO();
                                info.setLabel(derivedInfoBean.getLabel());
                                info.setName(derivedInfoBean.getName());
                                ArrayList<BalanceCoinInfo> coinInfos = new ArrayList<>();
                                BalanceCoinInfo balanceCoinInfo = new BalanceCoinInfo();
                                balanceCoinInfo.setCoin(derivedInfoBean.getCoin());
                                String blance = "0.00 BTC";
                                String blanceFiat = "0.00 CNY";
                                try {
                                    String balanceStr = derivedInfoBean.getBlance();
                                    blance =
                                            balanceStr.substring(0, balanceStr.indexOf("(")).trim();
                                    blanceFiat =
                                            balanceStr
                                                    .substring(balanceStr.indexOf("("))
                                                    .replace(")", "")
                                                    .trim();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                balanceCoinInfo.setBalance(blance);
                                balanceCoinInfo.setFiat(blanceFiat);

                                coinInfos.add(balanceCoinInfo);
                                info.setWallets(coinInfos);

                                // 去除本地存在的钱包
                                String wallet =
                                        PreferencesManager.get(
                                                        activity,
                                                        Constant.WALLETS,
                                                        derivedInfoBean.getName(),
                                                        "")
                                                .toString();
                                if (Strings.isNullOrEmpty(wallet)) {
                                    infos.add(info);
                                }
                            });

        } catch (Exception e) {
            throw HardWareExceptions.exceptionConvert(e);
        }
        return infos;
    }

    public static PyResponse<CreateWalletBean> recoveryXpubWallet(String xPubs, boolean hd)
            throws Exception {
        PyResponse<CreateWalletBean> response = new PyResponse<>();
        try {
            String walletsInfo =
                    sCommands
                            .callAttr(
                                    PyConstant.CREATE_WALLET_BY_XPUB,
                                    "",
                                    1,
                                    1,
                                    xPubs,
                                    new Kwarg("hd", hd))
                            .toString();
            Logger.json(walletsInfo);
            CreateWalletBean walletBean = CreateWalletBean.objectFromData(walletsInfo);
            response.setResult(walletBean);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            String message = exception.getMessage();
            if (message.startsWith(HardWareExceptions.PAIR_USER_CANCEL.getMessage())) {
                message =
                        message.replaceFirst(HardWareExceptions.PAIR_USER_CANCEL.getMessage(), "");
            }
            if (message.startsWith(HardWareExceptions.OPERATION_CANCEL.getMessage())) {
                message =
                        message.replaceFirst(HardWareExceptions.OPERATION_CANCEL.getMessage(), "");
            }
            response.setErrors(message);
            e.printStackTrace();
        }
        return response;
    }

    /** 加载本地钱包信息 */
    public static void loadLocalWalletInfo(Context context) {
        try {
            String walletsInfo = sCommands.callAttr(PyConstant.GET_WALLETS_INFO).toString();
            LogUtil.d("oneKey", "----->" + walletsInfo);
            if (!Strings.isNullOrEmpty(walletsInfo)) {
                JsonArray wallets = JsonParser.parseString(walletsInfo).getAsJsonArray();
                wallets.forEach(
                        (wallet) -> {
                            wallet.getAsJsonObject()
                                    .keySet()
                                    .forEach(
                                            (walletName) ->
                                                    PreferencesManager.put(
                                                            context,
                                                            Constant.WALLETS,
                                                            walletName,
                                                            wallet.getAsJsonObject()
                                                                    .get(walletName)));
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择要使用的钱包
     *
     * @param name 钱包名称
     */
    @Nullable
    public static BalanceInfoDTO selectWallet(@NonNull String name) {
        try {
            String info = sCommands.callAttr(PyConstant.GET_BALANCE, name).toString();
            return BalanceInfoDTO.objectFromData(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 获取所有钱包的余额 */
    @Nullable
    public static PyResponse<AllWalletBalanceInfoDTO> getAllWalletBalance() {
        PyResponse<AllWalletBalanceInfoDTO> pyResponse = new PyResponse<>();
        try {
            String info = sCommands.callAttr("get_all_wallet_balance").toString();
            AllWalletBalanceInfoDTO allWalletBalanceInfoDTO =
                    new Gson().fromJson(info, AllWalletBalanceInfoDTO.class);
            pyResponse.setResult(allWalletBalanceInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            pyResponse.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return pyResponse;
    }

    /** 查看当前钱包的备份状态 */
    public static boolean hasBackup(Context context) {
        String keyName =
                PreferencesManager.get(
                                context, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "")
                        .toString();
        try {
            return sCommands.callAttr(PyConstant.HAS_BACKUP, keyName).toBoolean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验扩展公钥格式
     *
     * @param xpub 待校验的扩展公钥
     */
    public static boolean validateXpub(String xpub) {
        if (Global.guiConsole != null) {
            return Global.guiConsole.callAttr(PyConstant.VALIDATE_XPUB, xpub).toBoolean();
        }
        return false;
    }

    /**
     * 校验地址格式
     *
     * @param address 待校验的地址
     */
    public static boolean verifyAddress(String address) {
        if (Global.guiConsole != null) {
            return Global.guiConsole.callAttr(PyConstant.VERIFY_ADDRESS, address).toBoolean();
        }
        return false;
    }

    /**
     * 助记词恢复钱包
     *
     * @param password 密码
     * @param mnemonics 助记词
     * @return 钱包信息
     * @throws Exception 错误的提示信息
     */
    @WorkerThread
    public static List<BalanceInfoDTO> restoreLocalHDWallet(
            @NotNull String password, @NotNull String mnemonics) throws Exception {
        try {
            List<BalanceInfoDTO> infos = new ArrayList<>();
            String walletsInfo =
                    sCommands
                            .callAttr(
                                    PyConstant.CREATE_HD_WALLET,
                                    password,
                                    mnemonics,
                                    new Kwarg(Constant.Purpose, 49))
                            .toString();
            CreateWalletBean walletBean = CreateWalletBean.objectFromData(walletsInfo);
            Logger.e("====" + walletsInfo);
            // HD 根钱包
            Optional.ofNullable(walletBean.getWalletInfo())
                    .ifPresent(
                            (walletInfos -> {
                                for (CreateWalletBean.WalletInfoBean walletInfo : walletInfos) {
                                    String name = walletInfo.getName();
                                    BalanceInfoDTO info = PyEnv.selectWallet(name);
                                    if (info == null) {
                                        continue;
                                    }
                                    EventBus.getDefault().post(new CreateSuccessEvent(name));
                                    if (walletInfo.getCoinType().contains("btc")) {
                                        // 现在的 BTC HD 钱包的 Label 是 BTC-1
                                        info.setLabel("BTC-1");
                                    } else if (walletInfo.getCoinType().contains("eth")) {
                                        // 现在的 ETH HD 钱包的 Label 是 ETH-1
                                        info.setLabel("ETH-1");
                                    }
                                    infos.add(info);
                                }
                            }));
            walletBean
                    .getDerivedInfo()
                    .forEach(
                            derivedInfoBean -> {
                                BalanceInfoDTO info = new BalanceInfoDTO();
                                info.setLabel(derivedInfoBean.getLabel());
                                info.setName(derivedInfoBean.getName());
                                ArrayList<BalanceCoinInfo> coinInfos = new ArrayList<>();
                                BalanceCoinInfo balanceCoinInfo = new BalanceCoinInfo();
                                balanceCoinInfo.setCoin(derivedInfoBean.getCoin());
                                String blance = "0.00 BTC";
                                String blanceFiat = "0.00 CNY";
                                try {
                                    String blanceStr = derivedInfoBean.getBlance();
                                    blance = blanceStr.substring(0, blanceStr.indexOf("(")).trim();
                                    blanceFiat =
                                            blanceStr
                                                    .substring(blanceStr.indexOf("("))
                                                    .replace(")", "")
                                                    .trim();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                balanceCoinInfo.setBalance(blance);
                                balanceCoinInfo.setFiat(blanceFiat);

                                coinInfos.add(balanceCoinInfo);
                                info.setWallets(coinInfos);
                                infos.add(info);
                            });
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
            throw HardWareExceptions.exceptionConvert(e);
        }
    }

    /**
     * 助记词恢复钱包
     *
     * @param password 密码
     * @param mnemonics seed
     * @return
     */
    public static PyResponse<CreateWalletBean> restoreLocalWallet(
            @NotNull String password, @NotNull String mnemonics) {
        PyResponse<CreateWalletBean> pyResponse = new PyResponse<>();
        try {
            String walletsInfo =
                    sCommands
                            .callAttr(
                                    PyConstant.CREATE_HD_WALLET,
                                    password,
                                    mnemonics,
                                    new Kwarg(Constant.Purpose, 49))
                            .toString();
            CreateWalletBean walletBean = CreateWalletBean.objectFromData(walletsInfo);
            pyResponse.setResult(walletBean);
            Logger.json(walletsInfo);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            String message = exception.getMessage();
            if (message.startsWith(HardWareExceptions.PAIR_USER_CANCEL.getMessage())) {
                message =
                        message.replaceFirst(HardWareExceptions.PAIR_USER_CANCEL.getMessage(), "");
            }
            if (message.startsWith(HardWareExceptions.OPERATION_CANCEL.getMessage())) {
                message =
                        message.replaceFirst(HardWareExceptions.OPERATION_CANCEL.getMessage(), "");
            }
            pyResponse.setErrors(message);
            e.printStackTrace();
        }
        return pyResponse;
    }

    @WorkerThread
    public static List<BalanceInfoDTO> createLocalHDWallet(
            String password, List<Vm.CoinType> coinTypes) {

        JsonArray coins = new JsonArray();
        for (Vm.CoinType coinType : coinTypes) {
            coins.add(coinType.callFlag);
        }
        String walletsInfo =
                sCommands
                        .callAttr(
                                PyConstant.CREATE_HD_WALLET,
                                password,
                                new Kwarg(Constant.Purpose, 49),
                                new Kwarg("create_coin", coins.toString()))
                        .toString();
        CreateWalletBean walletBean = CreateWalletBean.objectFromData(walletsInfo);
        EventBus.getDefault()
                .post(new CreateSuccessEvent(walletBean.getWalletInfo().get(0).getName()));
        EventBus.getDefault().post(new RefreshEvent());
        // HD 根钱包
        List<BalanceInfoDTO> infos = new ArrayList<>();
        Optional.ofNullable(walletBean.getWalletInfo())
                .ifPresent(
                        (walletInfos -> {
                            for (CreateWalletBean.WalletInfoBean walletInfo : walletInfos) {
                                String name = walletInfo.getName();
                                BalanceInfoDTO info = PyEnv.selectWallet(name);
                                if (info == null) {
                                    continue;
                                }
                                //                                EventBus.getDefault().post(new
                                // CreateSuccessEvent(name));
                                //                                if
                                // (walletInfo.getCoinType().contains("btc")) {
                                //                                    // 现在的 BTC HD 钱包的 Label 是
                                // BTC-1
                                //                                    info.setLabel("BTC-1");
                                //                                } else if
                                // (walletInfo.getCoinType().contains("eth")) {
                                //                                    // 现在的 ETH HD 钱包的 Label 是
                                // ETH-1
                                //                                    info.setLabel("ETH-1");
                                //                                }
                                infos.add(info);
                            }
                        }));
        return infos;
    }

    /**
     * 确认恢复
     *
     * @param nameList 要恢复钱包的名称列表
     */
    public static boolean recoveryConfirm(List<String> nameList, boolean isHw) {
        try {
            sCommands.callAttr(PyConstant.RECOVERY_CONFIRM, new Gson().toJson(nameList), isHw);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 设置固件升级进度的推送句柄 */
    public static void setProgressReporter(AsyncTask<String, Object, Void> task) {
        sProtocol.put(PyConstant.PROCESS_REPORTER, task);
    }

    /** 重置固件断点续传的状态 */
    public static void clearUpdateStatus() {
        sProtocol.put(PyConstant.HTTP, false);
        sProtocol.put(PyConstant.OFFSET, 0);
        sProtocol.put(PyConstant.PROCESS_REPORTER, null);
    }

    /**
     * 固件升级接口
     *
     * @param path 升级文件路径
     */
    public static PyResponse<Void> firmwareUpdate(String path) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(
                    PyConstant.FIRMWARE_UPDATE, path, MyApplication.getInstance().getDeviceWay());
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 获取交易详情
     *
     * @param rawTx
     */
    public static PyResponse<String> analysisRawTx(String rawTx) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String txData = sCommands.callAttr(PyConstant.ANALYZE_TX, rawTx).toString();
            response.setResult(txData);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * @param type ："btc","eth"
     * @description 获取费率详情
     */
    public static PyResponse<String> getFeeInfo(
            @Nullable String type,
            String receiver,
            String amount,
            String feeRate,
            String contractAddress) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String info;
            if (!Strings.isNullOrEmpty(receiver)) {
                Map<String, String> map = new HashMap<>();
                map.put("to_address", receiver);
                if (!Strings.isNullOrEmpty(amount)) {
                    map.put("value", amount);
                }
                if (!Strings.isNullOrEmpty(contractAddress)) {
                    map.put("contract_address", contractAddress);
                }
                info =
                        sCommands
                                .callAttr(
                                        PyConstant.GET_DEFAULT_FEE_DETAILS,
                                        new Kwarg("coin", type),
                                        new Kwarg("eth_tx_info", new Gson().toJson(map)))
                                .toString();
            } else {
                info =
                        sCommands
                                .callAttr(
                                        PyConstant.GET_DEFAULT_FEE_DETAILS, new Kwarg("coin", type))
                                .toString();
            }
            Logger.json(info);
            response.setResult(info);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /** 获取自定义费率详情 */
    public static PyResponse<String> getCustomFeeInfo(String value) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String info =
                    sCommands
                            .callAttr(
                                    PyConstant.GET_DEFAULT_FEE_DETAILS, new Kwarg("feerate", value))
                            .toString();
            Logger.d(info);
            response.setResult(info);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 获取交易费
     *
     * @param coin "btc","eth"
     * @param receiver 接收方
     * @param amount 发送金额
     * @param feeRate 当前费率
     * @return 临时交易
     */
    public static PyResponse<TemporaryTxInfo> getBtcFeeByFeeRate(
            @Nullable String coin,
            @NonNull String receiver,
            @NonNull String amount,
            @NonNull String feeRate) {
        PyResponse<TemporaryTxInfo> response = new PyResponse<>();
        try {
            ArrayList<Map<String, String>> arrayList = new ArrayList<>();
            Map<String, String> params = new HashMap<>(1);
            params.put(receiver, amount);
            arrayList.add(params);
            String result =
                    Daemon.commands
                            .callAttr(
                                    PyConstant.CALCULATE_FEE,
                                    coin,
                                    new Gson().toJson(arrayList),
                                    "",
                                    feeRate)
                            .toString();
            Logger.json(result);
            TemporaryTxInfo temporaryTxInfo = TemporaryTxInfo.objectFromData(result);
            response.setResult(temporaryTxInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 获取交易费
     *
     * @param coin "btc","eth"
     * @param receiver 接收方
     * @param amount 发送金额
     * @param feeRate 当前费率
     * @param gasLimit 当前gasLimit
     * @return PyResponse
     */
    public static PyResponse<TemporaryTxInfo> getEthFeeByFeeRate(
            @Nullable String coin,
            @NonNull String receiver,
            @NonNull String amount,
            @NonNull String feeRate,
            String gasLimit) {
        PyResponse<TemporaryTxInfo> response = new PyResponse<>();
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        if (!Strings.isNullOrEmpty(receiver)) {
            params.put("to_address", receiver);
        }
        if (!Strings.isNullOrEmpty(amount)) {
            params.put("value", amount);
        }
        params.put("gas_price", feeRate);
        params.put("gas_limit", String.valueOf(gasLimit));
        try {
            String result =
                    Daemon.commands
                            .callAttr(
                                    PyConstant.CALCULATE_FEE,
                                    coin,
                                    new Kwarg("eth_tx_info", new Gson().toJson(params)))
                            .toString();
            Logger.json(result);
            TemporaryTxInfo temporaryTxInfo = TemporaryTxInfo.objectFromData(result);
            response.setResult(temporaryTxInfo);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 构建交易
     *
     * @param tempTx 临时交易
     * @return rawTx 待签名的交易
     */
    public static PyResponse<String> makeTx(String tempTx) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.MAKE_TX, tempTx).toString();
            String rawTx = MakeTxResponseBean.objectFromData(result).getTx();
            response.setResult(rawTx);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 汇率转换
     *
     * @param value value in btc
     * @return string value in cash
     */
    public static PyResponse<String> exchange(String value) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result =
                    sCommands
                            .callAttr(PyConstant.EXCHANGE_RATE_CONVERSION, "base", value)
                            .toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 广播交易
     *
     * @param signedTx 已签名交易
     */
    public static PyResponse<Void> broadcast(String signedTx) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.BROADCAST_TX, signedTx);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 签名交易
     *
     * @param rawTx 未签名的交易
     * @return 签名后的交易详情
     */
    public static PyResponse<TransactionInfoBean> signTx(String rawTx, String password) {
        PyResponse<TransactionInfoBean> response = new PyResponse<>();
        try {
            String result =
                    sCommands
                            .callAttr(PyConstant.SIGN_TX, rawTx, new Kwarg("password", password))
                            .toString();
            TransactionInfoBean txInfo = TransactionInfoBean.objectFromData(result);
            response.setResult(txInfo);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /** 获取当前钱包的详细信息 */
    public static PyResponse<CurrentAddressDetail> getCurrentAddressInfo() {
        PyResponse<CurrentAddressDetail> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.ADDRESS_INFO).toString();
            CurrentAddressDetail txInfo = CurrentAddressDetail.objectFromData(result);
            response.setResult(txInfo);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 校验签名
     *
     * @param address 地址
     * @param message 原始信息
     * @param signature 签名
     */
    public static void verifySignature(
            String address,
            String message,
            String signature,
            Consumer<PyResponse<Boolean>> callback) {
        PyResponse<Boolean> response = new PyResponse<>();
        if (tryAcquire()) {
            mExecutorService.submit(
                    () -> {
                        try {
                            boolean verified =
                                    sCommands
                                            .callAttr(
                                                    PyConstant.VERIFY_MESSAGE_SIGNATURE,
                                                    address,
                                                    message,
                                                    signature,
                                                    new Kwarg(
                                                            "path",
                                                            MyApplication.getInstance()
                                                                    .getDeviceWay()))
                                            .toBoolean();
                            response.setResult(verified);
                        } catch (Exception e) {
                            Exception exception = HardWareExceptions.exceptionConvert(e);
                            response.setErrors(exception.getMessage());
                            e.printStackTrace();
                        } finally {
                            callback.accept(response);
                            release();
                        }
                    });
        } else {
            Logger.d("concurrent signature verify");
            callback.accept(null);
        }
    }

    /**
     * 删除hd钱包的备份标记
     *
     * @param name 钱包名称
     */
    public static PyResponse<Void> clearHdBackupFlags(String name) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.CLEAR_BACK_FLAGS, name);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 校验APP主密码
     *
     * @param passwd APP主密码
     */
    public static PyResponse<Void> verifySoftPass(String passwd) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.VERIFY_SOFT_PASS, passwd);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 修改APP主密码
     *
     * @param passwdOrigin APP原主密码
     * @param passwdNew APP新主密码
     */
    public static PyResponse<Void> changeSoftPass(String passwdOrigin, String passwdNew) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.CHANGE_SOFT_PASS, passwdOrigin, passwdNew);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 导出助记词
     *
     * @param password APP主密码
     */
    public static PyResponse<String> exportMnemonics(String password, String name) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result =
                    sCommands
                            .callAttr(
                                    PyConstant.EXPORT_MNEMONICS, password, new Kwarg("name", name))
                            .toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 创建钱包
     *
     * @param walletName 钱包名称
     * @param password APP主密码
     * @param coinType 钱包类型
     * @param privateKey 私钥 optional
     * @param mnemonics 助记词 optional
     * @param purpose 地址类型 optional 可以传 -1
     * @return 创建的钱包
     */
    public static CreateWalletBean createWallet(
            String walletName,
            String password,
            Vm.CoinType coinType,
            String privateKey,
            String mnemonics,
            String keyStore,
            String keyStorePass,
            int purpose)
            throws AccountException {
        try {
            List<Kwarg> argList = new LinkedList<>();
            argList.add(new Kwarg("name", walletName));
            argList.add(new Kwarg("password", password));
            argList.add(new Kwarg("coin", coinType.callFlag));

            if (!TextUtils.isEmpty(privateKey)) {
                argList.add(new Kwarg("privkeys", privateKey));
            }
            if (!TextUtils.isEmpty(mnemonics)) {
                argList.add(new Kwarg("seed", mnemonics));
            }
            if (!TextUtils.isEmpty(keyStore) && !TextUtils.isEmpty(keyStorePass)) {
                argList.add(new Kwarg("keystores", keyStore.replace("'", "\"")));
                argList.add(new Kwarg("keystore_password", keyStorePass));
            }
            if (purpose != 0 || purpose != -1) {
                argList.add(new Kwarg("purpose", purpose));
            }

            String result =
                    sCommands
                            .callAttr(PyConstant.CREATE_WALLET, argList.toArray(new Object[0]))
                            .toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(result);
            EventBus.getDefault()
                    .post(
                            new CreateSuccessEvent(
                                    createWalletBean.getWalletInfo().get(0).getName()));
            return createWalletBean;
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            String message = "";
            if (exception.getMessage() != null) {
                message = exception.getMessage();
            }
            if (message.contains("文件已存在") || message.contains("File already exists.")) {
                throw new AccountException.WalletAlreadyExistsException(message);
            }
            if (!TextUtils.isEmpty(message) || message.contains(StringConstant.REPLACE_ERROR)) {
                String watchName = message.substring(message.indexOf(":") + 1);
                throw new AccountException.WalletWatchAlreadyExistsException(message, watchName);
            }
            e.printStackTrace();
            throw new AccountException.CreateException(message);
        }
    }

    /**
     * 创建钱包
     *
     * @param context
     * @param password APP主密码
     * @param walletName 钱包名称
     * @param mnemonics 助记词 optional
     * @param privateKey 私钥 optional
     */
    @Deprecated
    public static void createWallet(
            Context context,
            String walletName,
            String password,
            String privateKey,
            String mnemonics,
            int purpose) {
        try {
            String result = "";
            if (purpose != -1) {
                result =
                        sCommands
                                .callAttr(
                                        PyConstant.CREATE_WALLET,
                                        walletName,
                                        password,
                                        Strings.isNullOrEmpty(privateKey)
                                                ? new Kwarg("seed", mnemonics)
                                                : new Kwarg("privkeys", privateKey),
                                        new Kwarg(Constant.Purpose, purpose))
                                .toString();
            } else {
                result =
                        sCommands
                                .callAttr(
                                        PyConstant.CREATE_WALLET,
                                        walletName,
                                        password,
                                        Strings.isNullOrEmpty(privateKey)
                                                ? new Kwarg("seed", mnemonics)
                                                : new Kwarg("privkeys", privateKey))
                                .toString();
            }
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(result);
            EventBus.getDefault()
                    .post(
                            new CreateSuccessEvent(
                                    createWalletBean.getWalletInfo().get(0).getName()));
            context.startActivity(new Intent(context, HomeOneKeyActivity.class));
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            ExitEvent exitEvent = new ExitEvent();
            exitEvent.message = exception.getMessage();
            EventBus.getDefault().post(exitEvent);
            if (exception.getMessage() != null) {
                if (!exception.getMessage().contains(StringConstant.REPLACE_ERROR)) {
                    MyApplication.getInstance().toastErr(exception);
                }
            }
            e.printStackTrace();
        }
    }

    /**
     * 删除指定钱包
     *
     * @param password APP 主密码
     * @param walletName 要删除的钱包名称
     */
    public static PyResponse<Void> deleteWallet(
            String password, String walletName, boolean allDelete) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            if (Strings.isNullOrEmpty(password)) {
                sCommands.callAttr(PyConstant.DELETE_WALLET, new Kwarg("name", walletName));
            } else {
                if (allDelete) {
                    sCommands.callAttr(
                            PyConstant.DELETE_WALLET,
                            password,
                            new Kwarg("name", walletName),
                            new Kwarg("hd", true));
                } else {
                    sCommands.callAttr(
                            PyConstant.DELETE_WALLET, password, new Kwarg("name", walletName));
                }
            }
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 创建派生钱包
     *
     * @param walletName 钱包名称
     * @param password APP 主密码
     * @param currencyType 币种类型
     */
    @Deprecated
    public static PyResponse<Void> createDerivedWallet(
            String walletName, String password, String currencyType, int purpose) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            String result =
                    sCommands
                            .callAttr(
                                    "create_derived_wallet",
                                    walletName,
                                    password,
                                    currencyType,
                                    purpose)
                            .toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(result);
            EventBus.getDefault()
                    .post(
                            new CreateSuccessEvent(
                                    createWalletBean.getWalletInfo().get(0).getName()));
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 导出私钥
     *
     * @param password APP主密码
     */
    public static PyResponse<String> exportPrivateKey(String password) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.EXPORT_PRIVATE_KEY, password).toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 导出 ETH keyStore
     *
     * @param password APP主密码
     */
    public static PyResponse<String> exportKeystore(String password) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.EXPORT_KEYSTORE, password).toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 获取派生HD钱包的个数
     *
     * @param
     * @return
     */
    public static PyResponse<String> getDerivedNum(Vm.CoinType coinType) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String res = sCommands.callAttr(PyConstant.GET_DEVIRED_NUM, coinType.name()).toString();
            response.setResult(res);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 覆盖观察钱包
     *
     * @param replace true
     * @return
     */
    public static PyResponse<String> replaceWatchOnlyWallet(boolean replace) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String res = sCommands.callAttr(PyConstant.REP_WATCH_ONLY_WALLET, replace).toString();
            response.setResult(res);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 加载对应类型的钱包信息
     *
     * @param type "hd","btc","eth"，为空加载所有钱包类型
     */
    public static PyResponse<List<WalletInfo>> loadWalletByType(String type) {
        PyResponse<List<WalletInfo>> response = new PyResponse<>();
        try {
            String walletsInfo;
            if (Strings.isNullOrEmpty(type)) {
                walletsInfo = sCommands.callAttr(PyConstant.GET_WALLETS_INFO).toString();
            } else {
                walletsInfo = sCommands.callAttr(PyConstant.GET_WALLETS_INFO, type).toString();
            }
            List<WalletInfo> list = new ArrayList<>();
            if (!Strings.isNullOrEmpty(walletsInfo)) {
                JsonArray wallets = JsonParser.parseString(walletsInfo).getAsJsonArray();
                wallets.forEach(
                        (wallet) -> {
                            wallet.getAsJsonObject()
                                    .keySet()
                                    .forEach(
                                            (walletName) -> {
                                                JsonElement jsonElement =
                                                        wallet.getAsJsonObject().get(walletName);
                                                WalletInfo localWalletInfo =
                                                        JSON.parseObject(
                                                                jsonElement.toString(),
                                                                WalletInfo.class);
                                                list.add(localWalletInfo);
                                            });
                        });
                response.setResult(list);
            }
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 校验接口
     *
     * @param data string
     * @param flag seed/private/public/address/keystore as string
     * @param coinType btc/eth as string
     * @return
     */
    public static PyResponse<Void> VerifyLegality(String data, String flag, String coinType) {
        PyResponse<Void> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.VERIFY_LEGALITY, data, flag, coinType);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * eth 签名
     *
     * @param to_addr 发送的地址
     * @param value 数量
     * @param path NFC/android_usb/bluetooth as str, used by hardware，软件不需要此参数
     * @param pass 密码
     * @param gasPrice gas_price
     * @param gasLimit gas_limit
     * @return
     */
    public static PyResponse<String> signEthTX(
            @NotNull String to_addr,
            @NotNull String value,
            String path,
            @NotNull String pass,
            @NotNull String gasPrice,
            @NotNull String gasLimit,
            String contractAddress) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String res;
            if (Strings.isNullOrEmpty(path)) {
                if (!Strings.isNullOrEmpty(contractAddress)) {
                    res =
                            sCommands
                                    .callAttr(
                                            PyConstant.SIGN_ETH_TX,
                                            to_addr,
                                            value,
                                            new Kwarg("password", pass),
                                            new Kwarg("contract_addr", contractAddress),
                                            new Kwarg("gas_price", gasPrice),
                                            new Kwarg("gas_limit", gasLimit))
                                    .toString();
                } else {
                    res =
                            sCommands
                                    .callAttr(
                                            PyConstant.SIGN_ETH_TX,
                                            to_addr,
                                            value,
                                            new Kwarg("password", pass),
                                            new Kwarg("gas_price", gasPrice),
                                            new Kwarg("gas_limit", gasLimit))
                                    .toString();
                }
            } else {
                if (!Strings.isNullOrEmpty(contractAddress)) {
                    res =
                            sCommands
                                    .callAttr(
                                            PyConstant.SIGN_ETH_TX,
                                            to_addr,
                                            value,
                                            new Kwarg("path", path),
                                            new Kwarg("password", pass),
                                            new Kwarg("contract_addr", contractAddress),
                                            new Kwarg("gas_price", gasPrice),
                                            new Kwarg("gas_limit", gasLimit))
                                    .toString();
                } else {
                    res =
                            sCommands
                                    .callAttr(
                                            PyConstant.SIGN_ETH_TX,
                                            to_addr,
                                            value,
                                            new Kwarg("path", path),
                                            new Kwarg("password", pass),
                                            new Kwarg("gas_price", gasPrice),
                                            new Kwarg("gas_limit", gasLimit))
                                    .toString();
                }
            }
            Logger.json(res);
            response.setResult(res);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * eth 硬件签名
     *
     * @param to_addr 发送的地址
     * @param value 数量
     * @param path NFC/android_usb/bluetooth as str, used by hardware，软件不需要此参数
     * @param gasPrice gas_price
     * @param gasLimit gas_limit
     * @return
     */
    public static PyResponse<String> signHardWareEthTX(
            @NotNull String to_addr,
            @NotNull String value,
            String path,
            @NotNull String gasPrice,
            @NotNull String gasLimit,
            String tokenAddress) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String res =
                    sCommands
                            .callAttr(
                                    PyConstant.SIGN_ETH_TX,
                                    to_addr,
                                    value,
                                    new Kwarg("path", path),
                                    new Kwarg("gas_price", gasPrice),
                                    new Kwarg("contract_addr", tokenAddress),
                                    new Kwarg("gas_limit", gasLimit))
                            .toString();
            response.setResult(res);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 硬件创建Eth钱包
     *
     * @param path
     * @param coinType
     * @return
     */
    public static PyResponse<String> createEthHwDerivedWallet(
            String path, String coinType, String addressType) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = "";
            if (coinType.equals(Vm.CoinType.ETH.callFlag)) {
                result =
                        sCommands
                                .callAttr(
                                        PyConstant.Create_Hw_Derived_Wallet,
                                        path,
                                        new Kwarg("coin", coinType))
                                .toString();
            } else {
                result =
                        sCommands
                                .callAttr(
                                        PyConstant.Create_Hw_Derived_Wallet,
                                        path,
                                        addressType,
                                        new Kwarg("coin", coinType))
                                .toString();
            }
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /** 获取用于个人钱包的扩展公钥 */
    public static PyResponse<String> getXpubP2wpkh(String path, String type) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result =
                    sCommands.callAttr(PyConstant.Create_Hw_Derived_Wallet, path, type).toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 添加代币 token
     *
     * @param symbol
     * @param address
     * @return
     */
    public static PyResponse<String> addToken(String symbol, String address) {
        PyResponse<String> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.Add_Token, symbol, address);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 删除代币 token
     *
     * @param address
     * @return
     */
    public static PyResponse<String> deleteToken(String address) {
        PyResponse<String> response = new PyResponse<>();
        try {
            sCommands.callAttr(PyConstant.Delete_Token, address);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public static PyResponse<String> parseQrCode(String content) {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr("parse_pr", content).toString();
            response.setResult(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public static PyResponse<String> getAllTokenInfo() {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.GET_ALL_TOKEN).toString();
            response.setResult(result);
            Logger.json(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    public static SwitchWalletBean switchWallet(String walletId) throws Exception {
        try {
            String result = sCommands.callAttr("switch_wallet", walletId).toString();
            return new Gson().fromJson(result, SwitchWalletBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw HardWareExceptions.exceptionConvert(e);
        }
    }

    public static CurrentWalletBalanceBean getCurrentWallet() throws Exception {
        try {
            String result = sCommands.callAttr("get_wallet_balance").toString();
            return new Gson().fromJson(result, CurrentWalletBalanceBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw HardWareExceptions.exceptionConvert(e);
        }
    }

    public static PyResponse<List<TokenList.ERCToken>> getTokenList() {
        PyResponse<List<TokenList.ERCToken>> pyResponse = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.GET_CUS_LIST_INFO).toString();
            List<TokenList.ERCToken> list = JSON.parseArray(result, TokenList.ERCToken.class);
            pyResponse.setResult(list);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            pyResponse.setErrors(exception.getMessage());
            Logger.d(" 异常--->" + e.toString());
        }
        return pyResponse;
    }

    public static PyResponse<String> getAddTokenAddress() {
        PyResponse<String> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr(PyConstant.GET_CUR_TOKEN_ADDRESS).toString();
            response.setResult(result);
            Logger.json(result);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    /**
     * 获取用户自定义添加的Token
     *
     * @param address
     * @return
     */
    public static PyResponse<TokenList.ERCToken> getCustomerTokenInfo(String address) {
        PyResponse<TokenList.ERCToken> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr("get_customer_token_info", address).toString();
            Logger.json(result);
            TokenList.ERCToken token = JSON.parseObject(result, TokenList.ERCToken.class);
            response.setResult(token);
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    public static PyResponse<RPCInfoBean> getRpcInfo(Vm.CoinType coinType) {
        PyResponse<RPCInfoBean> response = new PyResponse<>();
        try {
            String result = sCommands.callAttr("dapp_eth_rpc_info").toString();
            RPCInfoBean token = new Gson().fromJson(result, RPCInfoBean.class);
            response.setResult(token);
        } catch (Exception e) {
            Exception exception = PyEnvException.convert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    public static PyResponse<SignTxResponseBean> signTx(
            Vm.CoinType coinType, JsonObject transaction, String pwd, String path) {
        PyResponse<SignTxResponseBean> response = new PyResponse<>();
        try {
            List<Kwarg> argList = new LinkedList<>();
            argList.add(new Kwarg("transaction", transaction.toString()));
            argList.add(new Kwarg("path", path));
            argList.add(new Kwarg("password", pwd));

            String result =
                    sCommands
                            .callAttr("dapp_eth_sign_tx", argList.toArray(new Object[0]))
                            .toString();
            SignTxResponseBean token = new Gson().fromJson(result, SignTxResponseBean.class);
            response.setResult(token);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = PyEnvException.convert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    public static PyResponse<String> signMessage(
            Vm.CoinType coinType, String fromAddress, String messageHex, String pwd, String path) {
        PyResponse<String> response = new PyResponse<>();
        List<Kwarg> argList = new LinkedList<>();
        argList.add(new Kwarg("address", fromAddress));
        argList.add(new Kwarg("message", messageHex));
        argList.add(new Kwarg("path", path));
        argList.add(new Kwarg("password", pwd));
        try {
            String result =
                    sCommands.callAttr("sign_message", argList.toArray(new Object[0])).toString();
            response.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = PyEnvException.convert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    public static PyResponse<CurrentFeeDetails> getDefFeeInfo(
            @NonNull Vm.CoinType coinType, String receiver, String amount, String data) {
        PyResponse<CurrentFeeDetails> response = new PyResponse<>();
        try {
            String info;
            if (!Strings.isNullOrEmpty(receiver)) {
                Map<String, String> map = new HashMap<>();
                map.put("to_address", receiver);
                if (!Strings.isNullOrEmpty(amount)) {
                    map.put("value", amount);
                }
                if (!Strings.isNullOrEmpty(data)) {
                    map.put("data", data);
                }
                info =
                        sCommands
                                .callAttr(
                                        PyConstant.GET_DEFAULT_FEE_DETAILS,
                                        new Kwarg("coin", coinType.callFlag),
                                        new Kwarg("eth_tx_info", new Gson().toJson(map)))
                                .toString();
            } else {
                info =
                        sCommands
                                .callAttr(
                                        PyConstant.GET_DEFAULT_FEE_DETAILS,
                                        new Kwarg("coin", coinType.callFlag))
                                .toString();
            }
            try {
                CurrentFeeDetails currentFeeDetails = CurrentFeeDetails.objectFromDate(info);
                Logger.json(info);
                response.setResult(currentFeeDetails);
            } catch (Exception e) {
                response.setErrors("error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Exception exception = PyEnvException.convert(e);
            response.setErrors(exception.getMessage());
        }
        return response;
    }

    @CheckResult
    public static boolean tryAcquire() {
        Logger.d("try acquire form>>>>>");
        return semaphore.tryAcquire();
    }

    public static void release() {
        Logger.d("try release form<<<<");
        semaphore.release();
    }
}

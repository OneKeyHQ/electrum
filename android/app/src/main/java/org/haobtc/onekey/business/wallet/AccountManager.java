package org.haobtc.onekey.business.wallet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.exception.AccountException;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.utils.Daemon;

import java.util.Map;
import java.util.Optional;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

/**
 * 账户管理类
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 2:13 PM
 */
public class AccountManager {
    private Context mContext;
    private SharedPreferences mPreferencesSharedPreferences;

    public AccountManager(Context context) {
        mContext = context.getApplicationContext();
        mPreferencesSharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
    }

    public Boolean existsWallets() {
        return PreferencesManager.hasWallet(mContext);
    }

    /**
     * 本地是否存在 HD 钱包
     *
     * @return true: 存在
     */
    public boolean existsLocalHD() {
        return mPreferencesSharedPreferences.getBoolean(Constant.HAS_LOCAL_HD, false);
    }

    /**
     * 设置本地是否存在 HD 钱包
     * 应该由软件进行逻辑判断，不应该人为设置。
     *
     * @param exists 是否存在
     */
    @Deprecated
    public void setExistsLocalHD(boolean exists) {
        mPreferencesSharedPreferences.edit().putBoolean(Constant.HAS_LOCAL_HD, exists).apply();
    }

    public String getCurrentWalletName() {
        return mPreferencesSharedPreferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
    }

    /**
     * 在本地存储的账户信息中查找钱包账户信息
     *
     * @param walletName 需要查找的钱包名称
     *
     * @return 返回钱包账户，如果找不到则返回 null
     */
    @WorkerThread
    @Nullable
    public LocalWalletInfo getLocalWalletByName(String walletName) {
        String str = PreferencesManager.get(mContext, org.haobtc.onekey.constant.Constant.WALLETS, walletName, "").toString();
        if (!Strings.isNullOrEmpty(str)) {
            return LocalWalletInfo.objectFromData(str);
        }
        return null;
    }

    /**
     * 选择一个钱包账户
     *
     * @return 钱包信息，如果没有钱包则返回 null。
     */
    @WorkerThread
    @Nullable
    public LocalWalletInfo selectWallet(String walletName) {
        LocalWalletInfo info = getLocalWalletByName(walletName);
        if (info == null) {
            return null;
        }
        mPreferencesSharedPreferences.edit()
                .putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, info.getName())
                .putString(CURRENT_SELECTED_WALLET_TYPE, info.getType())
                .apply();
        return info;
    }

    /**
     * 自动选择下一个可用的钱包账户
     *
     * @return 可用的钱包，如果没有钱包则返回 null。
     */
    @WorkerThread
    @Nullable
    public LocalWalletInfo autoSelectNextWallet() {
        Optional<? extends Map.Entry<String, ?>> entry = PreferencesManager.getAll(mContext, org.haobtc.onekey.constant.Constant.WALLETS).entrySet().stream().findFirst();
        if (!entry.isPresent()) {
            return null;
        }
        LocalWalletInfo info = LocalWalletInfo.objectFromData(entry.get().getValue().toString());
        if (info == null) {
            return null;
        }
        mPreferencesSharedPreferences.edit()
                .putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, info.getName())
                .putString(CURRENT_SELECTED_WALLET_TYPE, info.getType())
                .apply();
        return info;
    }

    /**
     * 获取当前钱包的类型  例如"btc-watch-standard"，"eth-derived-standard"
     *
     * @return
     */
    public String getCurrentWalletAccurateType() {
        return mPreferencesSharedPreferences.getString(Constant.CURRENT_SELECTED_WALLET_TYPE, "");
    }

    /**
     * 获取钱包的类型：例如"btc","eth"
     *
     * @return
     */
    public String getCurWalletType() {
        String string = mPreferencesSharedPreferences.getString(CURRENT_SELECTED_WALLET_TYPE, "");
        return string.substring(0, string.indexOf("-"));
    }

    /**
     * 判断当前钱包是否备份
     *
     * @return
     */
    public boolean getWalletBackup() {
        return PyEnv.hasBackup(mContext);
    }

    /**
     * 派生 HD 钱包
     *
     * @param coinType       钱包类型
     * @param walletName     钱包名称
     * @param walletPassword 钱包密码
     * @param purpose        派生规则
     *
     * @return
     */
    public CreateWalletBean deriveHdWallet(Vm.CoinType coinType, String walletName, String walletPassword, int purpose) throws AccountException.DeriveException {
        try {
            String result = Daemon.commands.callAttr("create_derived_wallet", walletName, walletPassword, coinType.coinName, purpose).toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(result);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            return createWalletBean;
        } catch (Exception e) {
            Exception exception = HardWareExceptions.exceptionConvert(e);
            e.printStackTrace();
            throw new AccountException.DeriveException(exception.getMessage());
        }
    }

    /**
     * 创建一个新的独立钱包
     *
     * @param coinType       钱包类型
     * @param walletName     钱包名称
     * @param walletPassword 钱包密码
     * @param purpose        派生规则
     *
     * @return 创建的钱包
     */
    public CreateWalletBean createNewSingleWallet(Vm.CoinType coinType, String walletName, String walletPassword, int purpose) throws AccountException.CreateException {
        return PyEnv.createWallet(walletName, walletPassword, coinType, null, null, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType       钱包类型
     * @param walletName     钱包名称
     * @param walletPassword 钱包密码
     * @param privateKey     私钥
     * @param purpose        派生规则
     *
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByPrivteKey(Vm.CoinType coinType, String walletName, String walletPassword, String privateKey, int purpose) throws AccountException.CreateException {
        return PyEnv.createWallet(walletName, walletPassword, coinType, privateKey, null, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType       钱包类型
     * @param walletName     钱包名称
     * @param walletPassword 钱包密码
     * @param mnemonics      助记词
     * @param purpose        派生规则
     *
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByMnemonic(Vm.CoinType coinType, String walletName, String walletPassword, String mnemonics, int purpose) throws AccountException.CreateException {
        return PyEnv.createWallet(walletName, walletPassword, coinType, null, mnemonics, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType       钱包类型
     * @param walletName     钱包名称
     * @param walletPassword 钱包密码
     * @param keystore       Keystore 文件
     * @param keystorePass   Keystore 文件密码
     * @param purpose        派生规则
     *
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByKeystore(Vm.CoinType coinType, String walletName, String walletPassword, String keystore, String keystorePass, int purpose) throws AccountException.CreateException {
        return PyEnv.createWallet(walletName, walletPassword, coinType, null, null, keystore, keystorePass, purpose);
    }

    /**
     * 创建观察钱包
     *
     * @param coinType   钱包类型
     * @param walletName 钱包名称
     * @param address    钱包地址
     *
     * @return 创建的观察钱包
     */
    public CreateWalletBean createWatchWallet(Vm.CoinType coinType, String walletName, String address) {
        PyObject pyObject = Daemon.commands.callAttr(PyConstant.CREATE_WALLET, walletName, new Kwarg("addresses", address), new Kwarg("coin", coinType.coinName));
        CreateWalletBean walletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
        EventBus.getDefault().post(new CreateSuccessEvent(walletBean.getWalletInfo().get(0).getName()));
        return walletBean;
    }
}

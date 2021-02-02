package org.haobtc.onekey.business.wallet;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;
import static org.haobtc.onekey.constant.Constant.HD;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.exception.AccountException;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.utils.Daemon;

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
        mPreferencesSharedPreferences =
                context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
    }

    public Boolean existsWallets() {
        return getAllWallet().size() > 0;
    }

    /**
     * 本地是否存在 HD 钱包
     *
     * @return true: 存在
     */
    public boolean existsLocalHD() {
        return getAllWalletByType(HD).size() > 0;
    }

    public String getCurrentWalletName() {
        return mPreferencesSharedPreferences.getString(
                org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
    }

    /**
     * 在本地存储的账户信息中查找钱包账户信息
     *
     * @param walletName 需要查找的钱包名称
     * @return 返回钱包账户，如果找不到则返回 null
     */
    @WorkerThread
    @Nullable
    public LocalWalletInfo getLocalWalletByName(String walletName) {
        List<WalletInfo> allWallet = getAllWallet();
        WalletInfo findWalletInfo = null;
        for (WalletInfo item : allWallet) {
            if (item.name.equals(walletName)) {
                findWalletInfo = item;
                break;
            }
        }

        if (findWalletInfo != null) {
            return new LocalWalletInfo(
                    findWalletInfo.type,
                    findWalletInfo.addr,
                    findWalletInfo.name,
                    findWalletInfo.label,
                    findWalletInfo.deviceId);
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
        mPreferencesSharedPreferences
                .edit()
                .putString(
                        org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME,
                        info.getName())
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
        List<WalletInfo> result = getAllWallet();
        if (result.size() <= 0) {
            return null;
        }
        WalletInfo walletInfo = result.get(result.size() - 1);

        LocalWalletInfo info =
                new LocalWalletInfo(
                        walletInfo.type,
                        walletInfo.addr,
                        walletInfo.name,
                        walletInfo.label,
                        walletInfo.deviceId);

        mPreferencesSharedPreferences
                .edit()
                .putString(
                        org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME,
                        info.getName())
                .putString(CURRENT_SELECTED_WALLET_TYPE, info.getType())
                .apply();
        return info;
    }

    /**
     * 获取已经存储的所有钱包
     *
     * @return 所有钱包
     */
    public List<WalletInfo> getAllWallet() {
        PyResponse<List<WalletInfo>> listPyResponse = PyEnv.loadWalletByType(null);
        if (Strings.isNullOrEmpty(listPyResponse.getErrors())) {
            return listPyResponse.getResult();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 获取已经存储的所有钱包
     *
     * @param type: None/hd/btc/eth
     * @return 所有钱包
     */
    public List<WalletInfo> getAllWalletByType(String type) {
        PyResponse<List<WalletInfo>> listPyResponse = PyEnv.loadWalletByType(type);
        if (Strings.isNullOrEmpty(listPyResponse.getErrors())) {
            return listPyResponse.getResult();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 获取当前钱包的类型 例如"btc-watch-standard"，"eth-derived-standard"
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
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param walletPassword 钱包密码
     * @param purpose 派生规则
     * @return
     */
    public CreateWalletBean deriveHdWallet(
            Vm.CoinType coinType, String walletName, String walletPassword, int purpose)
            throws AccountException {
        try {
            String result =
                    Daemon.commands
                            .callAttr(
                                    "create_derived_wallet",
                                    walletName,
                                    walletPassword,
                                    coinType.coinName,
                                    purpose)
                            .toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(result);
            EventBus.getDefault()
                    .post(
                            new CreateSuccessEvent(
                                    createWalletBean.getWalletInfo().get(0).getName()));
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
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param walletPassword 钱包密码
     * @param purpose 派生规则
     * @return 创建的钱包
     */
    public CreateWalletBean createNewSingleWallet(
            Vm.CoinType coinType, String walletName, String walletPassword, int purpose)
            throws AccountException {
        return PyEnv.createWallet(
                walletName, walletPassword, coinType, null, null, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param walletPassword 钱包密码
     * @param privateKey 私钥
     * @param purpose 派生规则
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByPrivteKey(
            Vm.CoinType coinType,
            String walletName,
            String walletPassword,
            String privateKey,
            int purpose)
            throws AccountException {
        return PyEnv.createWallet(
                walletName, walletPassword, coinType, privateKey, null, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param walletPassword 钱包密码
     * @param mnemonics 助记词
     * @param purpose 派生规则
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByMnemonic(
            Vm.CoinType coinType,
            String walletName,
            String walletPassword,
            String mnemonics,
            int purpose)
            throws AccountException {
        return PyEnv.createWallet(
                walletName, walletPassword, coinType, null, mnemonics, null, null, purpose);
    }

    /**
     * 创建独立钱包
     *
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param walletPassword 钱包密码
     * @param keystore Keystore 文件
     * @param keystorePass Keystore 文件密码
     * @param purpose 派生规则
     * @return 创建的钱包
     */
    public CreateWalletBean createSingleWalletByKeystore(
            Vm.CoinType coinType,
            String walletName,
            String walletPassword,
            String keystore,
            String keystorePass,
            int purpose)
            throws AccountException {
        return PyEnv.createWallet(
                walletName, walletPassword, coinType, null, null, keystore, keystorePass, purpose);
    }

    /**
     * 创建观察钱包
     *
     * @param coinType 钱包类型
     * @param walletName 钱包名称
     * @param address 钱包地址
     * @return 创建的观察钱包
     */
    public CreateWalletBean createWatchWallet(
            Vm.CoinType coinType, String walletName, String address) throws AccountException {
        try {
            PyObject pyObject =
                    Daemon.commands.callAttr(
                            PyConstant.CREATE_WALLET,
                            walletName,
                            new Kwarg("addresses", address),
                            new Kwarg("coin", coinType.coinName));
            CreateWalletBean walletBean =
                    new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault()
                    .post(new CreateSuccessEvent(walletBean.getWalletInfo().get(0).getName()));
            return walletBean;
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
     * 获取所有主钱包账户
     *
     * @return 主钱包账户
     */
    public List<WalletInfo> getAllMainWallet() {
        PyResponse<List<WalletInfo>> listPyResponse = PyEnv.loadWalletByType(HD);
        if (Strings.isNullOrEmpty(listPyResponse.getErrors())) {
            return listPyResponse.getResult();
        }
        return new ArrayList(0);
    }
}

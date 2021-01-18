package org.haobtc.onekey.business.wallet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.common.base.Strings;

import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;

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

    public String getCurrentWalletName() {
        return mPreferencesSharedPreferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
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
     * 获取当前钱包的类型
     *
     * @return
     */
    public String getCurrentWalletType() {
        return mPreferencesSharedPreferences.getString(Constant.CURRENT_SELECTED_WALLET_TYPE, "");
    }

    /**
     * 判断当前钱包是否备份
     *
     * @return
     */
    public boolean getWalletBackup() {
        return PyEnv.hasBackup(mContext);
    }
}

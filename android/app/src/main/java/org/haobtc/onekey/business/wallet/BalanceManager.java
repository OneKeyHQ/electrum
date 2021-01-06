package org.haobtc.onekey.business.wallet;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;

/**
 * 账户余额管理
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 12:01 PM
 */
public class BalanceManager {

    /**
     * 根据钱包名称获取钱包余额
     *
     * @param walletName 钱包名称
     * @return first 数字货币余额，second 法币余额
     */
    @Nullable
    public Pair<String, String> getBalanceByWalletName(String walletName) {
        BalanceInfo balanceInfo = PyEnv.selectWallet(walletName);
        if (balanceInfo == null) {
            return null;
        }
        String balanceStr = balanceInfo.getBalance();
        String balance = balanceStr.substring(0, balanceStr.indexOf(" "));

        String cnyStr = balanceStr.substring(balanceStr.indexOf("(") + 1, balanceStr.indexOf(")"));
        String cash = "0";
        if (cnyStr.contains(" ")) {
            cash = cnyStr.substring(0, cnyStr.indexOf(" "));
        }
        return new Pair<>(balance, cash);
    }

}

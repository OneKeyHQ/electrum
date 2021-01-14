package org.haobtc.onekey.business.wallet;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.Nullable;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.manager.PyEnv;
import org.json.JSONException;
import org.json.JSONObject;

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
     *
     * @return first 数字货币余额，second 法币余额
     */
    @Nullable
    public Pair<String, String> getBalanceByWalletName(String walletName) {
        BalanceInfo balanceInfo = PyEnv.selectWallet(walletName);
        if (balanceInfo == null) {
            return null;
        }
        String balanceStr = balanceInfo.getBalance();
        String balance = balanceStr == null ? "0" : balanceStr.substring(0, balanceStr.indexOf(" "));

        String cnyStr = balanceStr == null ? "CNY" : balanceStr.substring(balanceStr.indexOf("(") + 1, balanceStr.indexOf(")"));
        String cash = "0";
        if (cnyStr.contains(" ")) {
            cash = cnyStr.substring(0, cnyStr.indexOf(" "));
        }
        return new Pair<>(balance, cash);
    }

    /**
     * 解析 Python 轮训放回的信息，取出金额，法币金额。
     *
     * @param msgVote Python 推来的消息
     *
     * @return first 数字货币余额，second 法币余额
     */
    public Pair<String, String> decodePythonBalanceNotice(String msgVote) {
        String balance = null;
        String balanceFiat = null;
        try {
            if (!TextUtils.isEmpty(msgVote) && msgVote.length() != 2 && msgVote.contains("{")) {
                JSONObject jsonObject = new JSONObject(msgVote);
                if (msgVote.contains("fiat")) {
                    String fiat = jsonObject.getString("fiat");
                    if (jsonObject.has("balance")) {
                        String changeBalance = jsonObject.getString("balance");
                        if (!TextUtils.isEmpty(changeBalance)) {
                            balance = changeBalance;
                        }
                    }
                    if (!TextUtils.isEmpty(fiat)) {
                        String[] currencyArray = MyApplication.getInstance().getResources().getStringArray(R.array.currency);
                        for (String s : currencyArray) {
                            if (fiat.contains(s)) {
                                balanceFiat = fiat.substring(0, fiat.indexOf(" "));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<>(balance, balanceFiat);
    }
}

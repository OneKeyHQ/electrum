package org.haobtc.onekey.business.wallet;

import android.text.TextUtils;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.AllWalletBalanceBean;
import org.haobtc.onekey.bean.AllWalletBalanceInfoDTO;
import org.haobtc.onekey.bean.BalanceCoinInfo;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.business.wallet.bean.ETHTokenBalanceBean;
import org.haobtc.onekey.business.wallet.bean.TokenBalanceBean;
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean;
import org.haobtc.onekey.constant.Vm;
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
     * @return WalletBalanceBean
     */
    @Nullable
    public WalletBalanceBean getBalanceByWalletName(String walletName) {
        BalanceInfoDTO balanceInfoDTO = PyEnv.selectWallet(walletName);
        if (balanceInfoDTO == null) {
            return null;
        }
        return convert(balanceInfoDTO);
    }

    /**
     * 获取所有钱包的金额
     *
     * @return 钱包金额
     */
    public PyResponse<AllWalletBalanceBean> getAllWalletBalances() {
        PyResponse<AllWalletBalanceBean> data = new PyResponse<>();
        PyResponse<AllWalletBalanceInfoDTO> allWalletBalance = PyEnv.getAllWalletBalance();
        if (Strings.isNullOrEmpty(allWalletBalance.getErrors())) {
            AllWalletBalanceInfoDTO allWalletBalanceInfoDTO = allWalletBalance.getResult();
            List<WalletBalanceBean> walletBalanceBean =
                    new ArrayList<>(allWalletBalanceInfoDTO.getWalletInfo().size());
            allWalletBalanceInfoDTO
                    .getWalletInfo()
                    .forEach(
                            new Consumer<BalanceInfoDTO>() {
                                @Override
                                public void accept(BalanceInfoDTO balanceInfoDTO) {
                                    walletBalanceBean.add(convert(balanceInfoDTO));
                                }
                            });

            AllWalletBalanceBean allWallet =
                    new AllWalletBalanceBean(
                            allWalletBalanceInfoDTO.getAllBalance(), walletBalanceBean);
            data.setResult(allWallet);
        } else {
            data.setErrors(allWalletBalance.getErrors());
        }
        return data;
    }

    /**
     * 解析 Python 轮训放回的信息，取出金额，法币金额。
     *
     * @param msgVote Python 推来的消息
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
                        String[] currencyArray =
                                MyApplication.getInstance()
                                        .getResources()
                                        .getStringArray(R.array.currency);
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

    private WalletBalanceBean convert(BalanceInfoDTO balanceInfoDTO) {
        WalletBalanceBean walletBalanceBean = null;
        List<TokenBalanceBean> tokenBalanceBeans = new ArrayList<>();
        for (int i = 0; i < balanceInfoDTO.getWallets().size(); i++) {
            BalanceCoinInfo item = balanceInfoDTO.getWallets().get(i);
            String balance = item.getBalance().trim();
            String[] cnySplit = item.getFiat().trim().split(" ");
            String fiatBalance = null;
            String fiatUnit = null;
            try {
                fiatBalance = cnySplit[0].trim();
                fiatUnit = cnySplit[1].trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i == 0) {
                walletBalanceBean =
                        new WalletBalanceBean(
                                Vm.CoinType.convert(item.getCoin()),
                                balance,
                                fiatBalance,
                                fiatUnit,
                                new ArrayList<>(),
                                balanceInfoDTO.getName());
            } else {
                tokenBalanceBeans.add(
                        new ETHTokenBalanceBean(
                                "", item.getCoin(), balance, fiatBalance, fiatUnit));
            }
        }

        walletBalanceBean.getTokens().addAll(tokenBalanceBeans);
        return walletBalanceBean;
    }
}

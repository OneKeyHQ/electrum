package org.haobtc.onekey.business.wallet;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.haobtc.onekey.bean.AllWalletBalanceBean;
import org.haobtc.onekey.bean.AllWalletBalanceInfoDTO;
import org.haobtc.onekey.bean.BalanceBroadcastEventBean;
import org.haobtc.onekey.bean.BalanceCoinInfo;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.business.wallet.bean.ETHTokenBalanceBean;
import org.haobtc.onekey.business.wallet.bean.TokenBalanceBean;
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.manager.PyEnv;

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
    @Nullable
    public BalanceBroadcastEventBean decodePythonBalanceNotice(String msgVote) {
        try {
            if (!TextUtils.isEmpty(msgVote) && msgVote.length() != 2 && msgVote.contains("{")) {
                return new Gson().fromJson(msgVote, BalanceBroadcastEventBean.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                                balanceInfoDTO.getName(),
                                balanceInfoDTO.getLabel());
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

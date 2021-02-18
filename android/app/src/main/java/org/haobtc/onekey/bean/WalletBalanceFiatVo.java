package org.haobtc.onekey.bean;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import java.util.Objects;

/**
 * 数字钱包法币金额视图模型
 *
 * @author Onekey@QuincySx
 * @create 2021-01-08 10:47 AM
 */
public class WalletBalanceFiatVo {

    private final String balance;
    private final String unit;
    private final String symbol;

    public WalletBalanceFiatVo(
            @NonNull String balance, @NonNull String unit, @NonNull String symbol) {
        this.balance = balance;
        this.unit = unit;
        this.symbol = symbol;
    }

    public String getBalance() {
        return balance;
    }

    public String getBalanceFormat() {
        if (TextUtils.isEmpty(balance) || balance.equals("0")) {
            return "0.00";
        }
        return balance;
    }

    public String getUnit() {
        return unit;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletBalanceFiatVo that = (WalletBalanceFiatVo) o;
        return Objects.equals(balance, that.balance)
                && Objects.equals(unit, that.unit)
                && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, unit, symbol);
    }
}

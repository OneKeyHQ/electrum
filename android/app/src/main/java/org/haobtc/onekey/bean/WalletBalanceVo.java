package org.haobtc.onekey.bean;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * 钱包数字货币金额视图模型
 *
 * @author Onekey@QuincySx
 * @create 2021-01-08 10:47 AM
 */
public class WalletBalanceVo {
    private final String balance;
    private final String unit;

    public WalletBalanceVo(@NonNull String balance, @NonNull String unit) {
        this.balance = balance;
        this.unit = unit;
    }

    public String getBalance() {
        return balance;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletBalanceVo that = (WalletBalanceVo) o;
        return Objects.equals(balance, that.balance) &&
                Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, unit);
    }
}

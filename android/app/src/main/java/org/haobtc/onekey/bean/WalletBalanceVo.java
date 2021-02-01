package org.haobtc.onekey.bean;

import androidx.annotation.NonNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 钱包数字货币金额视图模型
 *
 * @author Onekey@QuincySx
 * @create 2021-01-08 10:47 AM
 */
public class WalletBalanceVo {

    private final BigDecimal balance;
    private final String unit;

    public WalletBalanceVo(@NonNull BigDecimal balance, @NonNull String unit) {
        this.balance = balance;
        this.unit = unit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getBalanceFormat(int scale) {
        // OPPO、ViVO 个别手机 balance 为 0 手动设置精度后，无法去掉后面的零。
        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return balance.setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WalletBalanceVo that = (WalletBalanceVo) o;
        return balance.compareTo(that.balance) == 0 && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, unit);
    }
}

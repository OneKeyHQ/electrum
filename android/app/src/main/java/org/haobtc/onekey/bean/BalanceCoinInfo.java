package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 币种金额
 *
 * @author Onekey@QuincySx
 * @create 2021-01-19 7:15 PM
 */
public class BalanceCoinInfo {
    @SerializedName("balance")
    private String balance = "0.00";
    @SerializedName("coin")
    private String coin = "btc";
    @SerializedName("fiat")
    private String fiat = "0.00 USD";

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getFiat() {
        return fiat;
    }

    public void setFiat(String fiat) {
        this.fiat = fiat;
    }
}

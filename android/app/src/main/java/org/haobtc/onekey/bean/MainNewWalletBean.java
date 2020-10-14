package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

public class MainNewWalletBean {
    /**
     * wallet_type : 2of2
     * balance : 0 mBTC
     * name : bbbbbbb
     */

    @SerializedName("wallet_type")
    private String walletType;
    @SerializedName("balance")
    private String balance;
    @SerializedName("name")
    private String name;

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

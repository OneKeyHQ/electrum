package org.haobtc.onekey.bean;
@Deprecated
public class WalletListCheckBean {

    /**
     * wallet_type : 2of2
     * balance : 0. mBTC
     * name : xiaomi
     */

    private String walletType;
    private String balance;
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

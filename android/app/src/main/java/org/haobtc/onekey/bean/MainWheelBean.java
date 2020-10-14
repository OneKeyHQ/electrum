package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
@Deprecated
public class MainWheelBean {


    @SerializedName("wallets")
    private List<WalletsBean> wallets;

    public List<WalletsBean> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletsBean> wallets) {
        this.wallets = wallets;
    }

    public static class WalletsBean {
        /**
         * wallet_type : 2of2
         * balance : 110. mBTC
         * name : 哦呃呃呃
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
}

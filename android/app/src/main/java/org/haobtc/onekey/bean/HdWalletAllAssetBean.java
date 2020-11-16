package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HdWalletAllAssetBean {

    /**
     * all_balance : 73600.26 CNY
     * wallet_info : [{"name":"duliwallet","btc":"90","fiat":"9,597.59 CNY"},{"name":"loiks","btc":"314","fiat":"33,484.91 CNY"},{"name":"watchs","btc":"286.176","fiat":"30,517.76 CNY"},{"name":"BTC-1","btc":"0","fiat":"0.00 CNY"}]
     */

    @SerializedName("all_balance")
    private String allBalance;
    @SerializedName("wallet_info")
    private List<WalletInfoBean> walletInfo;

    public String getAllBalance() {
        return allBalance;
    }

    public void setAllBalance(String allBalance) {
        this.allBalance = allBalance;
    }

    public List<WalletInfoBean> getWalletInfo() {
        return walletInfo;
    }

    public void setWalletInfo(List<WalletInfoBean> walletInfo) {
        this.walletInfo = walletInfo;
    }

    public static class WalletInfoBean {
        /**
         * name : duliwallet
         * btc : 90
         * fiat : 9,597.59 CNY
         */

        @SerializedName("name")
        private String name;
        @SerializedName("btc")
        private String btc;
        @SerializedName("fiat")
        private String fiat;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBtc() {
            return btc;
        }

        public void setBtc(String btc) {
            this.btc = btc;
        }

        public String getFiat() {
            return fiat;
        }

        public void setFiat(String fiat) {
            this.fiat = fiat;
        }
    }
}

package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecoveryWalletBean {

    /**
     * seed :
     * wallet_info : [{"coin_type":"btc","name":"6a2e85db92b2d44f77602a65f0d1cdfa564c22eb3baab8fb4ab8bddb1f8c96f5"}]
     * derived_info : [{"coin":"btc","blance":"0.004474 BTC (539.99 CNY)","name":"CX075EUy.unique.file","label":"btc_derived_1"},{"coin":"btc","blance":"0.00073 BTC (88.11 CNY)","name":"xTemnH9S.unique.file","label":"btc_derived_2"},{"coin":"btc","blance":"0.0008 BTC (96.56 CNY)","name":"vtcGAdzy.unique.file","label":"btc_derived_3"}]
     */

    @SerializedName("seed")
    private String seed;
    @SerializedName("wallet_info")
    private List<WalletInfoBean> walletInfo;
    @SerializedName("derived_info")
    private List<DerivedInfoBean> derivedInfo;

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public List<WalletInfoBean> getWalletInfo() {
        return walletInfo;
    }

    public void setWalletInfo(List<WalletInfoBean> walletInfo) {
        this.walletInfo = walletInfo;
    }

    public List<DerivedInfoBean> getDerivedInfo() {
        return derivedInfo;
    }

    public void setDerivedInfo(List<DerivedInfoBean> derivedInfo) {
        this.derivedInfo = derivedInfo;
    }

    public static class WalletInfoBean {
        /**
         * coin_type : btc
         * name : 6a2e85db92b2d44f77602a65f0d1cdfa564c22eb3baab8fb4ab8bddb1f8c96f5
         */

        @SerializedName("coin_type")
        private String coinType;
        @SerializedName("name")
        private String name;

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class DerivedInfoBean {
        /**
         * coin : btc
         * blance : 0.004474 BTC (539.99 CNY)
         * name : CX075EUy.unique.file
         * label : btc_derived_1
         */

        @SerializedName("coin")
        private String coin;
        @SerializedName("blance")
        private String blance;
        @SerializedName("name")
        private String name;
        @SerializedName("label")
        private String label;

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getBlance() {
            return blance;
        }

        public void setBlance(String blance) {
            this.blance = blance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}

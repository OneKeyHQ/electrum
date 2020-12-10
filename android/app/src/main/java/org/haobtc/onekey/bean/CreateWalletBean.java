package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liyan
 */
public class CreateWalletBean {

    /**
     * seed :
     * wallet_info : [{"coin_type":"btc","name":"6a2e85db92b2d44f77602a65f0d1cdfa564c22eb3baab8fb4ab8bddb1f8c96f5"}]
     * derived_info : [{"label":"btc_derived_1","blance":"0.004474 BTC (540.15 CNY)","coin":"sta","name":"PWQxSq2M.unique.file"},{"label":"btc_derived_2","blance":"0.00073 BTC (88.13 CNY)","coin":"sta","name":"Ff1ojIC4.unique.file"},{"label":"btc_derived_3","blance":"0.0008 BTC (96.59 CNY)","coin":"sta","name":"KBGqDbY8.unique.file"}]
     */

    @SerializedName("seed")
    private String seed;
    @SerializedName("wallet_info")
    private List<WalletInfoBean> walletInfo;
    @SerializedName("derived_info")
    private List<DerivedInfoBean> derivedInfo;

    public static CreateWalletBean objectFromData(String str) {

        return new Gson().fromJson(str, CreateWalletBean.class);
    }

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

        public static WalletInfoBean objectFromData(String str) {

            return new Gson().fromJson(str, WalletInfoBean.class);
        }

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
         * label : btc_derived_1
         * blance : 0.004474 BTC (540.15 CNY)
         * coin : sta
         * name : PWQxSq2M.unique.file
         */

        @SerializedName("label")
        private String label;
        @SerializedName("blance")
        private String blance;
        @SerializedName("coin")
        private String coin;
        @SerializedName("name")
        private String name;

        public static DerivedInfoBean objectFromData(String str) {

            return new Gson().fromJson(str, DerivedInfoBean.class);
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getBlance() {
            return blance;
        }

        public void setBlance(String blance) {
            this.blance = blance;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

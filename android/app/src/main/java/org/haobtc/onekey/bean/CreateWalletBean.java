package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateWalletBean {

    /**
     * seed : use clinic space useful object focus above destroy piece slight giraffe tunnel
     * wallet_info : [{"coin_type":"btc","name":"6798794fc5eff6e30868f36ccf336c9b721756f45a985a42d4bf7a5ee9880d7a"}]
     */

    @SerializedName("seed")
    private String seed;
    @SerializedName("wallet_info")
    private List<WalletInfoBean> walletInfo;

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

    public static class WalletInfoBean {
        /**
         * coin_type : btc
         * name : 6798794fc5eff6e30868f36ccf336c9b721756f45a985a42d4bf7a5ee9880d7a
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
}

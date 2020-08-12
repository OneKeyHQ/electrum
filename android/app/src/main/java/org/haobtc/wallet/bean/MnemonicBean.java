package org.haobtc.wallet.bean;

import com.google.gson.annotations.SerializedName;
@Deprecated
public class MnemonicBean {

    /**
     * p2wpkh : {"addr":"bcrt1qm7dsr9paqyvwexf6nlm60ma00u2nat0zqk7m8a","derivation":"m/84'/1'/0'"}
     * p2wpkh-p2sh : {"addr":"2NFZfD6vVyNsgKXUC3wmhMN6uHmmdL3qDKy","derivation":"m/49'/1'/0'"}
     * p2pkh : {"addr":"mmw27o6vR1DWXXetChT32a4o23LTrZBwsJ","derivation":"m/44'/1'/0'"}
     * electrum : {"addr":"bcrt1qlfv8648zlgl9qzrr03xaqa8p5ks2gvtulp83q4","derivation":""}
     */

    @SerializedName("p2wpkh")
    private P2wpkhBean p2wpkh;
    @SerializedName("p2wpkh-p2sh")
    private P2wpkhp2shBean p2wpkhp2sh;
    @SerializedName("p2pkh")
    private P2pkhBean p2pkh;
    @SerializedName("electrum")
    private ElectrumBean electrum;

    public P2wpkhBean getP2wpkh() {
        return p2wpkh;
    }

    public void setP2wpkh(P2wpkhBean p2wpkh) {
        this.p2wpkh = p2wpkh;
    }

    public P2wpkhp2shBean getP2wpkhp2sh() {
        return p2wpkhp2sh;
    }

    public void setP2wpkhp2sh(P2wpkhp2shBean p2wpkhp2sh) {
        this.p2wpkhp2sh = p2wpkhp2sh;
    }

    public P2pkhBean getP2pkh() {
        return p2pkh;
    }

    public void setP2pkh(P2pkhBean p2pkh) {
        this.p2pkh = p2pkh;
    }

    public ElectrumBean getElectrum() {
        return electrum;
    }

    public void setElectrum(ElectrumBean electrum) {
        this.electrum = electrum;
    }

    public static class P2wpkhBean {
        /**
         * addr : bcrt1qm7dsr9paqyvwexf6nlm60ma00u2nat0zqk7m8a
         * derivation : m/84'/1'/0'
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("derivation")
        private String derivation;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getDerivation() {
            return derivation;
        }

        public void setDerivation(String derivation) {
            this.derivation = derivation;
        }
    }

    public static class P2wpkhp2shBean {
        /**
         * addr : 2NFZfD6vVyNsgKXUC3wmhMN6uHmmdL3qDKy
         * derivation : m/49'/1'/0'
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("derivation")
        private String derivation;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getDerivation() {
            return derivation;
        }

        public void setDerivation(String derivation) {
            this.derivation = derivation;
        }
    }

    public static class P2pkhBean {
        /**
         * addr : mmw27o6vR1DWXXetChT32a4o23LTrZBwsJ
         * derivation : m/44'/1'/0'
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("derivation")
        private String derivation;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getDerivation() {
            return derivation;
        }

        public void setDerivation(String derivation) {
            this.derivation = derivation;
        }
    }

    public static class ElectrumBean {
        /**
         * addr : bcrt1qlfv8648zlgl9qzrr03xaqa8p5ks2gvtulp83q4
         * derivation :
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("derivation")
        private String derivation;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getDerivation() {
            return derivation;
        }

        public void setDerivation(String derivation) {
            this.derivation = derivation;
        }
    }
}

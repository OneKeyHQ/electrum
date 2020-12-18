package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyan
 */
public class TransactionInfoBean implements Serializable {

    /**
     * txid : 57ff90ae7f98727fd2782c95542c692e8aadcd6add156a52d505f8ab85d3543b
     * can_broadcast : false
     * amount : 0.0002 BTC (4.55 USD)
     * fee : unknown BTC (无数据 USD)
     * description :
     * tx_status : 527 个确认
     * sign_status : [1,1]
     * output_addr : [{"addr":"bcrt1qsnn9wl3f0wvvz8wvrsrp44sefe9fsn2cgucdwd","amount":"0.0002 BTC (4.55 USD)","is_change":false},{"addr":"bcrt1qlwc77qht4962vr993mjsq2l5kuts4txqp6qh3e","amount":"0.000152 BTC (3.46 USD)","is_change":true}]
     * input_addr : [{"prevout_hash":"f2813dc816c15c37100a6f1c5915791bd2ba6c1b981129617ddd38f69804ffd3","prevout_n":1,"address":""}]
     * height : 4313
     * cosigner : ["vpub5Y6t2r9bbCXUCcCBUZGgzeUYXvWtctcddPN8JhhXRkkeGtQY4yh7uSVbG64fEuyrCD86GAc9a4Zj1abgkh78FKfJ8n9VFHv4KGwSKbSbnHL"]
     * tx : 02000000000101d3ff0498f638dd7d612911981b6cbad21b7915591c6f0a10375cc116c83d81f20100000000fdffffff02204e00000000000016001484e6577e297b98c11dcc1c061ad6194e4a984d58603b000000000000160014fbb1ef02eba974a60ca58ee5002bf4b7170aacc00247304402207b105d607dff7b9ad703ae11a10e22d07c3a55b40a3d2b75c2efc9499ef896d7022069075e4cfdc71080aba1f559f4267cbf8a2937d83d27ff6f8a8d67d4eea5254d012102e598d7898b6f36aa0ddc226bc849e1f8e81d7bb9dde26df33155b4f9c0e2b643d8100000
     */
    public static TransactionInfoBean objectFromData(String s) {
        return new Gson().fromJson(s, TransactionInfoBean.class);
    }

    @SerializedName("txid")
    private String txid;
    @SerializedName("can_broadcast")
    private boolean canBroadcast;
    @SerializedName("amount")
    private String amount;
    @SerializedName("fee")
    private String fee;
    @SerializedName("description")
    private String description;
    @SerializedName("tx_status")
    private String txStatus;
    @SerializedName("height")
    private int height;
    @SerializedName("tx")
    private String tx;
    @SerializedName("sign_status")
    private List<Integer> signStatus;
    @SerializedName("output_addr")
    private ArrayList<TransactionInfoBean.OutputAddrBean> outputAddr;
    @SerializedName("input_addr")
    private List<TransactionInfoBean.InputAddrBean> inputAddr;
    @SerializedName("cosigner")
    private List<String> cosigner;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public boolean isCanBroadcast() {
        return canBroadcast;
    }

    public void setCanBroadcast(boolean canBroadcast) {
        this.canBroadcast = canBroadcast;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public List<Integer> getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(List<Integer> signStatus) {
        this.signStatus = signStatus;
    }

    public ArrayList<TransactionInfoBean.OutputAddrBean> getOutputAddr() {
        return outputAddr;
    }

    public void setOutputAddr(ArrayList<OutputAddrBean> outputAddr) {
        this.outputAddr = outputAddr;
    }

    public List<TransactionInfoBean.InputAddrBean> getInputAddr() {
        return inputAddr;
    }

    public void setInputAddr(List<TransactionInfoBean.InputAddrBean> inputAddr) {
        this.inputAddr = inputAddr;
    }

    public List<String> getCosigner() {
        return cosigner;
    }

    public void setCosigner(List<String> cosigner) {
        this.cosigner = cosigner;
    }

    public static class OutputAddrBean {
        /**
         * addr : bcrt1qsnn9wl3f0wvvz8wvrsrp44sefe9fsn2cgucdwd
         * amount : 0.0002 BTC (4.55 USD)
         * is_change : false
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("amount")
        private String amount;
        @SerializedName("is_change")
        private boolean isChange;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public boolean isIsChange() {
            return isChange;
        }

        public void setIsChange(boolean isChange) {
            this.isChange = isChange;
        }
    }

    public static class InputAddrBean {
        /**
         * prevout_hash : f2813dc816c15c37100a6f1c5915791bd2ba6c1b981129617ddd38f69804ffd3
         * prevout_n : 1
         * address :
         */

        @SerializedName("prevout_hash")
        private String prevoutHash;
        @SerializedName("prevout_n")
        private int prevoutN;
        @SerializedName("address")
        private String address;

        public String getPrevoutHash() {
            return prevoutHash;
        }

        public void setPrevoutHash(String prevoutHash) {
            this.prevoutHash = prevoutHash;
        }

        public int getPrevoutN() {
            return prevoutN;
        }

        public void setPrevoutN(int prevoutN) {
            this.prevoutN = prevoutN;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
package org.haobtc.wallet.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetnewcreatTrsactionListBean implements Serializable{


    /**
     * txid : a3099793e594bff4c70ca24259060c9c2478f433642eca8660e923cab7e2d2af
     * can_broadcast : false
     * amount : -0.0001 mBTC (-0.00 CNY)
     * fee : 0.2538 mBTC (12.09 CNY)
     * description : hello
     * tx_status : Unsigned
     * sign_status : [0,1]
     * output_addr : [{"addr":"bcrt1qcln8zm4wcp2ukfhfshllpwykyteceh4u7hpujg","amount":"0.0001 mBTC (0.00 CNY)"},{"addr":"bcrt1qn8mv7u8lsrts98zl756xz9h5cjp7sx6x0lna56","amount":"1.7461 mBTC (83.21 CNY)"}]
     * input_addr : [{"addr":"bcrt1qn8mv7u8lsrts98zl756xz9h5cjp7sx6x0lna56"}]
     * cosigner : ["vpub5ZqDEs4NETZbJ2tc45AwG1ZiQNAzcZHCqY2fxav5kvherpCaniMzXnD4zqaiZ8guR1UVDfBTHzo2cyvUWAShhboWi8a3KZfRjzDoXztypLA"]
     * tx : 70736274ff01007102000000016b032478b5af107c634b75e9a2a49125075ec364b3ac719ff3bbe57fe06fabbd0100000000fdffffff020a00000000000000160014c7e6716eaec055cb26e985fff0b89622f38cdebc12aa02000000000016001499f6cf70ff80d7029c5ff5346116f4c483e81b46000000000001011f400d03000000000016001499f6cf70ff80d7029c5ff5346116f4c483e81b462206036c9b8c0a67d90a6e2dd65d2945c5dde55a66789136e697d004fd4dce39abc55b0c39ac059c000000000000000000002202036c9b8c0a67d90a6e2dd65d2945c5dde55a66789136e697d004fd4dce39abc55b0c39ac059c000000000000000000
     */

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
    @SerializedName("tx")
    private String tx;
    @SerializedName("sign_status")
    private List<Integer> signStatus;
    @SerializedName("output_addr")
    private ArrayList<OutputAddrBean> outputAddr;
    @SerializedName("input_addr")
    private List<InputAddrBean> inputAddr;
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

    public ArrayList<OutputAddrBean> getOutputAddr() {
        return outputAddr;
    }

    public void setOutputAddr(ArrayList<OutputAddrBean> outputAddr) {
        this.outputAddr = outputAddr;
    }

    public List<InputAddrBean> getInputAddr() {
        return inputAddr;
    }

    public void setInputAddr(List<InputAddrBean> inputAddr) {
        this.inputAddr = inputAddr;
    }

    public List<String> getCosigner() {
        return cosigner;
    }

    public void setCosigner(List<String> cosigner) {
        this.cosigner = cosigner;
    }

    public static class OutputAddrBean implements Serializable{
        /**
         * addr : bcrt1qcln8zm4wcp2ukfhfshllpwykyteceh4u7hpujg
         * amount : 0.0001 mBTC (0.00 CNY)
         */

        @SerializedName("addr")
        private String addr;
        @SerializedName("amount")
        private String amount;
        @SerializedName("is_change")
        private Boolean is_change;

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

        public Boolean getIs_change() {
            return is_change;
        }

        public void setIs_change(Boolean is_change) {
            this.is_change = is_change;
        }
    }

    public static class InputAddrBean implements Serializable{
        /**
         * addr : bcrt1qn8mv7u8lsrts98zl756xz9h5cjp7sx6x0lna56
         */

        @SerializedName("addr")
        private String addr;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }
}
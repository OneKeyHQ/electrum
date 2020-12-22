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
     * txid : 4a5106a46cfabc31a56d6ff97bab8c4deff24bd726a8a4b6be31c9fe651bbb57
     * can_broadcast : false
     * amount : 0.271 BTC (42,544.19 CNY)
     * fee : unknown BTC (无数据 CNY)
     * description :
     * tx_status : 32 个确认
     * sign_status : [1,1]
     * output_addr : [{"addr":"bcrt1qpsca07809ukdx8rm44hhg48l8f9t3trd7y3mek","amount":"0.01836175 BTC (2,882.60 CNY)","is_change":false},{"addr":"bcrt1qhyv0sq62cm7juwz42gh296fshxengxctaj2a67","amount":"0.271 BTC (42,544.19 CNY)","is_change":true}]
     * input_addr : [{"address":""}]
     * height : 4906
     * cosigner : ["vpub5YNrBoSwK1FpupomyB3JqrbWy5Zk3TXAryyV1tLSZxhfrwNLuX7ytkHRjVjjjvqH8eJAveV1DB1nVSBKheu4xsiWn4bibdt2xTDaXi7BwvJ"]
     * tx : 0200000000010141491fe2216f46093813273ca9c2c07c6e02d5619f8b99341004cc2c417ce8700000000000feffffff028f041c00000000001600140c31d7f8ef2f2cd31c7bad6f7454ff3a4ab8ac6d60839d0100000000160014b918f8034ac6fd2e3855522ea2e930b9b3341b0b024730440220610e04ec77d9e2981bcac1ca7e7ff08e1e4ff8fe76cbe63c40d0b8e7111f50ca022019f4e76bf9bb4ac235463ddb4218c5fe0203e890e896f37cc2c0d514c141385401210283e89cc4a027cf780baf55779e738f44b1fdb73ca8e3c8597b8188f31c828be829130000
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

    public static class OutputAddrBean {
        /**
         * addr : bcrt1qpsca07809ukdx8rm44hhg48l8f9t3trd7y3mek
         * amount : 0.01836175 BTC (2,882.60 CNY)
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
         * address :
         */

        @SerializedName("address")
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
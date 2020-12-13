package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyan
 */
public class TransactionInfoBean implements Serializable{

    /**
     * txid : 54437025414b07c746596656f37d411be705d93a2faa467d2edf489eec3d6731
     * can_broadcast : false
     * amount : 0 mBTC (0.00 CNY)
     * fee : 0.508 mBTC (51.64 CNY)
     * description :
     * tx_status : Unsigned
     * sign_status : [0,1]
     * output_addr : [{"addr":"bcrt1qtsmu4zem4cyf6swrzye8as4cxl5fjlkh2zfkym","amount":"1 mBTC (101.66 CNY)","is_change":false},{"addr":"bcrt1qtsmu4zem4cyf6swrzye8as4cxl5fjlkh2zfkym","amount":"11.492 mBTC (1,168.31 CNY)","is_change":true}]
     * input_addr : [{"addr":"bcrt1qtsmu4zem4cyf6swrzye8as4cxl5fjlkh2zfkym"}]
     * height : -2
     * cosigner : ["vpub5ZnTu9psVLf4zPYfWQT9GGfmuNZEYNNGc4gRLUNYGU6STjtfzR89Dcck6De3y6yr2VPHTqezHZ4av8oHEQmiGksdodbHzv64q6zToR8Gvfq"]
     * tx : cHNidP8BAHECAAAAAVSrjEy1PY/rUd0HloZxnl0IhwgazJdpO+tOOdf8zqv6AAAAAAD9////AqCGAQAAAAAAFgAUXDfKizuuCJ1BwxEyfsK4N+iZftcQiREAAAAAABYAFFw3yos7rgidQcMRMn7CuDfomX7XAAAAAAABAP1yAQIAAAAAAQL3prCALdewsZNDVVoddwYqWt1j0ZiKaxgDcP0e8mfmiwEAAAAA/v///ywdfMjf3m/1HjxKUH/4/v/rXMi+5Jxe3Ac+SGZaQqz4AAAAAAD+////AiDWEwAAAAAAFgAUXDfKizuuCJ1BwxEyfsK4N+iZfteo5w8AAAAAABYAFLBc+/JfJHNu+0KGFlOhySxp8NXdAkcwRAIgNXgfEotUae+Cl+z8FDQ6IKI96ZUxSKxSP9HkETg7l+ICIDt+EZc7L8EijO/HVf+pJp3/lZTL/O3zbF3fZnSh3WdbASEDg6QHEBqU0ATHY9ExnSpVDt9g988wMCQn5o5elmGhmj0CRzBEAiB2GDvwObU/eZFKn5CXa5OrXwJ/V3UYImPEB9aWGWq6iQIgKKxc1txQqNQWxgeq/jh8YMsJU/2FnADaOFPAaS3+MXoBIQLBVV5LuZqkS6o3OSzcsnLAngM576IfK3Z1LFM1vBkXNQAAAAAiBgJRgWa5SzIDGfJjd8+xdX4R+DE4vz2hzCQ9Z+4/vclehwwQqA1BAAAAAAAAAAAAIgICUYFmuUsyAxnyY3fPsXV+EfgxOL89ocwkPWfuP73JXocMEKgNQQAAAAAAAAAAACICAlGBZrlLMgMZ8mN3z7F1fhH4MTi/PaHMJD1n7j+9yV6HDBCoDUEAAAAAAAAAAAA=
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
         * addr : bcrt1qtsmu4zem4cyf6swrzye8as4cxl5fjlkh2zfkym
         * amount : 1 mBTC (101.66 CNY)
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
         * addr : bcrt1qtsmu4zem4cyf6swrzye8as4cxl5fjlkh2zfkym
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
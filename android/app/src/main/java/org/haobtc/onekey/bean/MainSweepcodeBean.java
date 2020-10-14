package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

public class MainSweepcodeBean {


    /**
     * type : 1
     * data : {"amount":"0.005 BTC","message":"bcrt1qkhu5zfx7s5rms08efr0gfapty47h5dakl2twru3qn99yhck7g4hs0x3xrr","time":1591325822,"address":"bcrt1q82vs5lafxtr305utt62s8875s7df30vfnwnrjldtwsys8yjdtx2qslvf49","memo":"bcrt1qkhu5zfx7s5rms08efr0gfapty47h5dakl2twru3qn99yhck7g4hs0x3xrr"}
     */

    @SerializedName("type")
    private int type;
    @SerializedName("data")
    private DataBean data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * amount : 0.005 BTC
         * message : bcrt1qkhu5zfx7s5rms08efr0gfapty47h5dakl2twru3qn99yhck7g4hs0x3xrr
         * time : 1591325822
         * address : bcrt1q82vs5lafxtr305utt62s8875s7df30vfnwnrjldtwsys8yjdtx2qslvf49
         * memo : bcrt1qkhu5zfx7s5rms08efr0gfapty47h5dakl2twru3qn99yhck7g4hs0x3xrr
         */

        @SerializedName("amount")
        private String amount;
        @SerializedName("message")
        private String message;
        @SerializedName("time")
        private int time;
        @SerializedName("address")
        private String address;
        @SerializedName("memo")
        private String memo;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
}

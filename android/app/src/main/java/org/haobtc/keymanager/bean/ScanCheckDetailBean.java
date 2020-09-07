package org.haobtc.keymanager.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ScanCheckDetailBean implements Serializable {

    /**
     * type : 2
     * data : {"txid":"c1ef796131b8c3b788f5866cbc6fe1a3d32f53afb6537ad8a3ea6a0568dbf022","can_broadcast":false,"amount":"unknown mBTC (No data CNY)","fee":"0.499 mBTC (24.51 CNY)","description":"","tx_status":"Unsigned","sign_status":[0,1],"output_addr":[{"addr":"bcrt1qdrmh0nlkkw9hvdztaglqd8tp54fk5f3zv4nnqm","amount":"2 mBTC (98.25 CNY)"},{"addr":"bcrt1qxvf50hdmx34qp74qrkutm6ux04g5yjl84ypknt","amount":"4.501 mBTC (221.11 CNY)"}],"input_addr":[{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"},{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"},{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"}],"cosigner":["vpub5UMJom5zg1vX1C1fgWVwYViUkdbcrzKNB33UEMNAJZw5Kk57He2N8JRkHn2m9wsiLA45axMgXq8d82AA4t4c3jiWanafD4DbrcMvLPRFn2E"],"tx":"70736274ff0100c3020000000398a1c1eb8096df63472022b45c80509509bea8374d405fa36e1ae0be9cc65c110000000000fdffffff67af07075480580039a35cbd88395829b17179609c6656704bc80ae2fad0b0a00000000000fdffffffef99143e7cba589ca6e42b7cc9b39c342066b00d7e683910648e33f654be26cd0100000000fdffffff02400d03000000000016001468f777cff6b38b76344bea3e069d61a5536a262234de060000000000160014331347ddbb346a00faa01db8bdeb867d51424be7d93600000001011fa0860100000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f100000000000000000001011f20a10700000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f100000000000000000001011fa0860100000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f1000000000000000000220203088d555e00cea703149436f8add1f9c67f6af7299a57cc9ab3adb7b497f76cc50c20ef87f1000000000200000000220202ef80d2fcee593aae704dfd10ae237bf43e0251933a933b4e00b2fdb070b55c310c20ef87f1010000000d00000000"}
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
         * txid : c1ef796131b8c3b788f5866cbc6fe1a3d32f53afb6537ad8a3ea6a0568dbf022
         * can_broadcast : false
         * amount : unknown mBTC (No data CNY)
         * fee : 0.499 mBTC (24.51 CNY)
         * description :
         * tx_status : Unsigned
         * sign_status : [0,1]
         * output_addr : [{"addr":"bcrt1qdrmh0nlkkw9hvdztaglqd8tp54fk5f3zv4nnqm","amount":"2 mBTC (98.25 CNY)"},{"addr":"bcrt1qxvf50hdmx34qp74qrkutm6ux04g5yjl84ypknt","amount":"4.501 mBTC (221.11 CNY)"}]
         * input_addr : [{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"},{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"},{"addr":"bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p"}]
         * cosigner : ["vpub5UMJom5zg1vX1C1fgWVwYViUkdbcrzKNB33UEMNAJZw5Kk57He2N8JRkHn2m9wsiLA45axMgXq8d82AA4t4c3jiWanafD4DbrcMvLPRFn2E"]
         * tx : 70736274ff0100c3020000000398a1c1eb8096df63472022b45c80509509bea8374d405fa36e1ae0be9cc65c110000000000fdffffff67af07075480580039a35cbd88395829b17179609c6656704bc80ae2fad0b0a00000000000fdffffffef99143e7cba589ca6e42b7cc9b39c342066b00d7e683910648e33f654be26cd0100000000fdffffff02400d03000000000016001468f777cff6b38b76344bea3e069d61a5536a262234de060000000000160014331347ddbb346a00faa01db8bdeb867d51424be7d93600000001011fa0860100000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f100000000000000000001011f20a10700000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f100000000000000000001011fa0860100000000001600140522cb3b223339cc905b4367893c3e383b7446942206037f4e0eaf45aa391cd57b44cc48db5a26e0db66402a15a75c8ab2eb793a8823c40c20ef87f1000000000000000000220203088d555e00cea703149436f8add1f9c67f6af7299a57cc9ab3adb7b497f76cc50c20ef87f1000000000200000000220202ef80d2fcee593aae704dfd10ae237bf43e0251933a933b4e00b2fdb070b55c310c20ef87f1010000000d00000000
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
        private List<OutputAddrBean> outputAddr;
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

        public List<OutputAddrBean> getOutputAddr() {
            return outputAddr;
        }

        public void setOutputAddr(List<OutputAddrBean> outputAddr) {
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

        public static class OutputAddrBean implements Serializable {
            /**
             * addr : bcrt1qdrmh0nlkkw9hvdztaglqd8tp54fk5f3zv4nnqm
             * amount : 2 mBTC (98.25 CNY)
             */

            @SerializedName("addr")
            private String addr;
            @SerializedName("amount")
            private String amount;

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
        }

        public static class InputAddrBean implements Serializable {
            /**
             * addr : bcrt1qq53vkwezxvuueyzmgdncj0p78qahg355gd720p
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
}

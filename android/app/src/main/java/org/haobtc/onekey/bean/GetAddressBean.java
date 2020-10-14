package org.haobtc.onekey.bean;

public class GetAddressBean {

    /**
     * amount : 0
     * fee : 100000
     * tx : 45505446ff000200000000010120f4dd69233e0659b0fb786ad9f1b73c78da5feb89262cf633cc9c2352ad5c770100000000fdffffff02a0860100000000002200206e9af29c54a0a62d51dbb7860de1093134c59b1a7aad3255ef3683dfaa2a314900350c00000000002200209a2a629902aa526ba0313caf4eb43e8439a937d2b07e0a0af1735dad6c5d68fdfeffffffff40420f00000000000000040001ff01ffad524c53ff0257548301fb523f1d80000000d6675fe4c36997060b7ba4d10a0c21ddf6494802749299fcfaa9e6d94097993102b18d25f7351a107a73ed1101d4075451c8e19f2e7ede51ea50610a4034263b01010001004c53ff0257548301a525cc62800000001ae565474c80f78bb1a9213a17061c36dcfed41a40fb21b80104c9d1d8963e8703bd8432b0b0a755f6ef45696559b8999bd26e89835495f8ae96c676700052a4750100010052ae5e981800
     */

    private String amount;
    private String fee;
    private String tx;

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

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }
}

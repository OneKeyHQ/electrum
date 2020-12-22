package org.haobtc.onekey.event;

/**
 * @author
 * @date 12/11/20
 */

public class CustomizeFeeRateEvent {
    private String feeRate;
    private String fee;
    private String cash;
    private String time;

    public CustomizeFeeRateEvent(String feeRate, String fee, String cash, String time) {
        this.feeRate = feeRate;
        this.fee = fee;
        this.cash = cash;
        this.time = time;
    }

    public String getFeeRate() {
        return feeRate;
    }

    public String getFee() {
        return fee;
    }

    public String getCash() {
        return cash;
    }

    public String getTime() {
        return time;
    }
}
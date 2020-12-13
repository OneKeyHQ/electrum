package org.haobtc.onekey.event;

/**
 * @author
 * @date 12/11/20
 */

public class CustomizeFeeRateEvent {
    private String feeRate;

    public CustomizeFeeRateEvent(String feeRate) {
        this.feeRate = feeRate;
    }

    public String getFeeRate() {
        return feeRate;

    }
}
package org.haobtc.onekey.event;

/**
 * @author
 * @date 12/12/20
 */

public class GetFeeEvent {
    private String feeRate;
    public GetFeeEvent(String feeRate) {
        this.feeRate = feeRate;
    }

    public String getFeeRate() {
        return feeRate;
    }
}

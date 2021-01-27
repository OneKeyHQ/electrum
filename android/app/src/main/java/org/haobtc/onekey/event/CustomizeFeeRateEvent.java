package org.haobtc.onekey.event;

/**
 * @author 自定义费率事件
 * @date 12/11/20
 */

public class CustomizeFeeRateEvent {
    private String feeRate;
    private String fee;
    private String cash;
    private String time;
    private int gasLimit;
    private String gasPrice;

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

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

    public int getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(int gasLimit) {
        this.gasLimit = gasLimit;
    }
}

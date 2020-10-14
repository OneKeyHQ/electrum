package org.haobtc.onekey.event;

import java.io.Serializable;

public class SendMoreAddressEvent implements Serializable {
    private String inputAddress;
    private String inputAmount;
    private boolean is_change;

    public String getInputAddress() {
        return inputAddress;
    }

    public void setInputAddress(String inputAddress) {
        this.inputAddress = inputAddress;
    }

    public String getInputAmount() {
        return inputAmount;
    }

    public void setInputAmount(String inputAmount) {
        this.inputAmount = inputAmount;
    }

    public boolean isIs_change() {
        return is_change;
    }

    public void setIs_change(boolean is_change) {
        this.is_change = is_change;
    }
}

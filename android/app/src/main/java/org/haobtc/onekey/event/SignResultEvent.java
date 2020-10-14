package org.haobtc.onekey.event;

public class SignResultEvent {

    private String signedRaw;
    public SignResultEvent(String signedRaw) {
        this.signedRaw = signedRaw;
    }

    public String getSignedRaw() {
        return signedRaw;
    }

    public void setSignedRaw(String signedRaw) {
        this.signedRaw = signedRaw;
    }
}

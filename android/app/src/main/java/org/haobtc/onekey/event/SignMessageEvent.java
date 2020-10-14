package org.haobtc.onekey.event;

public class SignMessageEvent {

    private String signedRaw;
    public SignMessageEvent(String signedRaw) {
        this.signedRaw = signedRaw;
    }

    public String getSignedRaw() {
        return signedRaw;
    }

    public void setSignedRaw(String signedRaw) {
        this.signedRaw = signedRaw;
    }
}

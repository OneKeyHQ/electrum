package org.haobtc.keymanager.event;

public class SendXpubToSigwallet {
    String xpub;

    public SendXpubToSigwallet(String xpub) {
        this.xpub = xpub;
    }

    public String getXpub() {
        return xpub;
    }

    public void setXpub(String xpub) {
        this.xpub = xpub;
    }
}

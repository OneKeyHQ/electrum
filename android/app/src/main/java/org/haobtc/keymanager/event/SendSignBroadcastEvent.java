package org.haobtc.keymanager.event;

public class SendSignBroadcastEvent {
    String signTx;

    public SendSignBroadcastEvent(String signTx) {
        this.signTx = signTx;
    }

    public String getSignTx() {
        return signTx;
    }

    public void setSignTx(String signTx) {
        this.signTx = signTx;
    }
}

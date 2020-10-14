package org.haobtc.onekey.event;

public class MainpageWalletEvent {
    String status;
    int pos;

    public MainpageWalletEvent(String status, int pos) {
        this.status = status;
        this.pos = pos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}

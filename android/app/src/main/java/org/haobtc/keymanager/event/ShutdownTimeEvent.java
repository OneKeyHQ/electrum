package org.haobtc.keymanager.event;

public class ShutdownTimeEvent {
    String result;

    public ShutdownTimeEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}

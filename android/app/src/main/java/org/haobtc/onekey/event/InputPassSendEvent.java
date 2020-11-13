package org.haobtc.onekey.event;

public class InputPassSendEvent {
    private String pass;

    public InputPassSendEvent(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }
}

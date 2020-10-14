package org.haobtc.onekey.event;

public class SetKeyLanguageEvent {
    private String status;

    public SetKeyLanguageEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}

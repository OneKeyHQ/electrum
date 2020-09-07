package org.haobtc.keymanager.event;

public class FastPayEvent {
    String code;

    public FastPayEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

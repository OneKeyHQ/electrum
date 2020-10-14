package org.haobtc.onekey.event;

public class FixAllLabelnameEvent {
    String keyName;
    String code;

    public FixAllLabelnameEvent(String keyname, String code) {
        this.keyName = keyname;
        this.code = code;

    }

    public String getKeyName() {
        return keyName;
    }

    public String getCode() {
        return code;
    }
}

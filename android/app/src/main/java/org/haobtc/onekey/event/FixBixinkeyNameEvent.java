package org.haobtc.onekey.event;

public class FixBixinkeyNameEvent {
    String keyName;

    public FixBixinkeyNameEvent(String keyname) {
        this.keyName = keyname;
    }

    public String getKeyName() {
        return keyName;
    }
}

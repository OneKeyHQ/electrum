package org.haobtc.keymanager.event;

public class InputHistoryWalletEvent {
    private String name;
    private String type;
    private String xpubs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXpubs() {
        return xpubs;
    }

    public void setXpubs(String xpubs) {
        this.xpubs = xpubs;
    }
}

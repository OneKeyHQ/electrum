package org.haobtc.onekey.bean;

public class AddressEvent {
    private String name;
    private String type;
    private String amount;
    private String label;
    private String deviceId;

    public AddressEvent() {
    }

    public AddressEvent(String name) {
        this.name = name;
    }

    public AddressEvent(String name, String type, String amount) {
        this.name = name;
        this.type = type;
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

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
}

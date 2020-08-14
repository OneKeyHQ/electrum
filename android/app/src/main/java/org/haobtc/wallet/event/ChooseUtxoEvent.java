package org.haobtc.wallet.event;

public class ChooseUtxoEvent {
    private String prevout_hash;
    private String address;
    private String value;

    public String getHash() {
        return prevout_hash;
    }

    public void setHash(String prevout_hash) {
        this.prevout_hash = prevout_hash;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

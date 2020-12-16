package org.haobtc.onekey.event;

import com.google.gson.annotations.SerializedName;

public class WalletAddressEvent {

    /**
     * address : bcrt1q30m0kwf432jvvwq67kupes7wlh369x9svgcy3g
     * balance : 67 mBTC (4,340.95 CNY)
     */

    private String key;
    @SerializedName("address")
    private String address;
    @SerializedName("balance")
    private String balance;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}

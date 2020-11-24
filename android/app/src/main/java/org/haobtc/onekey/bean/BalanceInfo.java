package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 */
public class BalanceInfo {

    /**
     * balance : 0 ()
     * name : BTC-1
     */

    @SerializedName("balance")
    private String balance;
    @SerializedName("name")
    private String name;

    public static BalanceInfo objectFromData(String str) {
        return new Gson().fromJson(str, BalanceInfo.class);
    }
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

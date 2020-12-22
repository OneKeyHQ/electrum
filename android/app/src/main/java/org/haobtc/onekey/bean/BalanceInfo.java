package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author liyan
 */
public class BalanceInfo implements Serializable {

    /**
     * balance : 0 ()
     * name : BTC-1
     */
    private static final long serialVersionUID = 2L;

    @SerializedName("balance")
    private String balance;
    @SerializedName("name")
    private String name;
    @SerializedName("label")
    private String label;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

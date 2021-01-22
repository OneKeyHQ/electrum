package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author liyan
 */
public class BalanceInfoDTO implements Serializable {

    /**
     * balance : 0 ()
     * name : BTC-1
     */
    private static final long serialVersionUID = 2L;

    @SerializedName("wallets")
    private List<BalanceCoinInfo> wallets;
    @SerializedName("name")
    private String name;
    @SerializedName("label")
    private String label = "";

    public static BalanceInfoDTO objectFromData(String str) {
        return new Gson().fromJson(str, BalanceInfoDTO.class);
    }

    public List<BalanceCoinInfo> getWallets() {
        return wallets;
    }

    public void setWallets(List<BalanceCoinInfo> wallets) {
        this.wallets = wallets;
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

package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

public class AddspeedNewtrsactionBean {


    /**
     * new_tx : 70736274ff010052020000000116b6a47ce4090da62f514d18f752e630a556859c63bbafa4c09f52ecfc35429b0000000000fdffffff01eeba970000000000160014fa587d54e2fa3e5008637c4dd074e1a5a0a4317c000000000001011f8096980000000000160014fa587d54e2fa3e5008637c4dd074e1a5a0a4317c220602e6a8ecec4f2bcc86d0527942bb316fa92ba4d376c3e5c2f26c3c9f4f3e59f35e0c81f2ab5a000000000000000000220202e6a8ecec4f2bcc86d0527942bb316fa92ba4d376c3e5c2f26c3c9f4f3e59f35e0c81f2ab5a000000000000000000
     * fee : 56210
     */

    @SerializedName("new_tx")
    private String newTx;
    @SerializedName("fee")
    private int fee;

    public String getNewTx() {
        return newTx;
    }

    public void setNewTx(String newTx) {
        this.newTx = newTx;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }
}

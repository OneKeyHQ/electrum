package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 */
public class CurrentAddressDetail {

    /**
     * qr_data : bitcoin:tb1qnuh3qc9g6lwlqqvmf7hg05pzlujhua9emdqdty4znjstr5886paq6htvpe
     * addr : tb1qnuh3qc9g6lwlqqvmf7hg05pzlujhua9emdqdty4znjstr5886paq6htvpe
     */
    @SerializedName("qr_data")
    private String qrData;
    @SerializedName("addr")
    private String addr;
    public static CurrentAddressDetail objectFromData(String str) {
        return new Gson().fromJson(str, CurrentAddressDetail.class);
    }
    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}

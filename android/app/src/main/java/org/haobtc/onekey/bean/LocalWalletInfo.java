package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 * @date 11/23/20
 */

public class LocalWalletInfo {


    /**
     * type : btc-hw-1-1
     * addr : bcrt1qac609gecqut0k2m3key3df6k4le2pdqg8m466j
     */

    @SerializedName("type")
    private String type;
    @SerializedName("addr")
    private String addr;

    public static LocalWalletInfo objectFromData(String str) {

        return new Gson().fromJson(str, LocalWalletInfo.class);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}

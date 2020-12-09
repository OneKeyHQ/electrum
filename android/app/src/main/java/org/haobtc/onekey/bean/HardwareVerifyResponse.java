package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 * @date 12/8/20
 */

public class HardwareVerifyResponse {

    /**
     * serialno : b'Bixin20060900250'
     * is_bixinkey : true
     * is_verified : true
     * last_check_time : 1607409468
     */

    @SerializedName("serialno")
    private String serialno;
    @SerializedName("is_bixinkey")
    private boolean isBixinkey;
    @SerializedName("is_verified")
    private boolean isVerified;
    @SerializedName("last_check_time")
    private int lastCheckTime;

    public static HardwareVerifyResponse objectFromData(String str) {

        return new Gson().fromJson(str, HardwareVerifyResponse.class);
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public boolean isIsBixinkey() {
        return isBixinkey;
    }

    public void setIsBixinkey(boolean isBixinkey) {
        this.isBixinkey = isBixinkey;
    }

    public boolean isIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public int getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(int lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }
}

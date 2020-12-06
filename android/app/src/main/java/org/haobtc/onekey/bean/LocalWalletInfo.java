package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 * @date 11/23/20
 */

public class LocalWalletInfo {


    /**
     * type : btc-hw-derived-1-1
     * addr : bcrt1q5jdpq0nkyd2f9gn4nzd3lsj3lc8m2qykr0fxh0
     * name : 3dd510b6535968c0214165238468c750bed086955a11155fe3cff665fe00c7e5
     * label : The
     * device_id : "A9CCAA79760C69FC47089E12"
     */

    @SerializedName("type")
    private String type;
    @SerializedName("addr")
    private String addr;
    @SerializedName("name")
    private String name;
    @SerializedName("label")
    private String label;
    @SerializedName("device_id")
    private String deviceId;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

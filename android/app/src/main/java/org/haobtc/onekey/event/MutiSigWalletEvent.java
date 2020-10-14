package org.haobtc.onekey.event;

public class MutiSigWalletEvent {
    private String xpub;
    private String deviceId;
    private String lable;

    public MutiSigWalletEvent(String xpub, String device_id, String lable) {
        this.xpub = xpub;
        this.deviceId = device_id;
        this.lable = lable;
    }

    public String getXpub() {
        return xpub;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLable() {
        return lable;
    }
}

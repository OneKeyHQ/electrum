package org.haobtc.keymanager.event;

public class CheckHideWalletEvent {
    private String xpub;
    private String deviceId;

    public CheckHideWalletEvent(String xpub, String deviceId) {
        this.xpub = xpub;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getXpub() {
        return xpub;
    }
}

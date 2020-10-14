package org.haobtc.onekey.event;

public class ReceiveXpub {
    String xpub;
    String deviceId;
    Boolean needBackup;
    boolean showUI;

    public ReceiveXpub(String xpub, String id, Boolean needBackup, boolean show) {
        this.xpub = xpub;
        this.deviceId = id;
        this.needBackup = needBackup;
        this.showUI = show;
    }

    public String getXpub() {
        return xpub;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Boolean getNeedBackup() {
        return needBackup;
    }

    public void setShowUI(boolean showUI) {
        this.showUI = showUI;
    }

    public boolean isShowUI() {
        return showUI;
    }
}

package org.haobtc.onekey.event;

public class PersonalMutiSigEvent {
    private String xpub;
    private String deviceId;
    private String label;

    public PersonalMutiSigEvent(String xpub, String deviceId, String label) {
        this.xpub = xpub;
        this.deviceId = deviceId;
        this.label = label;
    }

    public String getXpub() {
        return xpub;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLabel() {
        return label;
    }
}

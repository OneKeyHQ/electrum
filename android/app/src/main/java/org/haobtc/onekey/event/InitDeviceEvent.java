package org.haobtc.onekey.event;

/**
 * @author liyan
 */
public class InitDeviceEvent {
    private boolean isNormal;
    private String mnemonics;

    public InitDeviceEvent(boolean isNormal, String mnemonics) {
        this.isNormal = isNormal;
        this.mnemonics = mnemonics;
    }
    public boolean getIsNormal() {
       return isNormal;
    }

    public String getMnemonics() {
        return mnemonics;
    }
}

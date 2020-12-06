package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/20/20
 */

public class MnemonicSizeSelectedEvent {
    boolean isNormal;
    public MnemonicSizeSelectedEvent(boolean isNormal) {
        this.isNormal = isNormal;
    }
    public boolean getNormal() {
        return isNormal;
    }
}

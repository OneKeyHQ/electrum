package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 12/4/20
 */

public class UpdateEvent {
    public static final int FIRMWARE = 0;
    public static final int BLE = 1;
    private int type;
    public UpdateEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

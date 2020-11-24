package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/21/20
 */

public class ButtonRequestEvent {
    private int type;
    public ButtonRequestEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
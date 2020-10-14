package org.haobtc.onekey.event;

public class DfuEvent {
    private int type;
    public static final int START_DFU = 0;
    public static final int DFU_SHOW_PROCESS = 1;

    public DfuEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

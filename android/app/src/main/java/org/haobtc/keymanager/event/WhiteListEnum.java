package org.haobtc.keymanager.event;

public enum WhiteListEnum {
    Add(0),//add -> 0
    Delete(1),//delete -> 1
    Inquire(2);//check -> 1

    private final int whiteListType;

    WhiteListEnum(int whiteListType) {
        this.whiteListType = whiteListType;
    }

    public int getWhiteListType() {
        return whiteListType;
    }
}

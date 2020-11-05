package org.haobtc.onekey.bean;

public class BackupWalletBean {

    private String mName;
    private long mCreateTime;
    private int mType;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }
}

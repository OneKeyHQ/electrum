package org.haobtc.onekey.bean;

public class MnemonicInfo {

    private int mIndex;
    private String mMnemonic;

    public MnemonicInfo(int mIndex, String mMnemonic) {
        this.mIndex = mIndex;
        this.mMnemonic = mMnemonic;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getMnemonic() {
        return mMnemonic;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public void setMnemonic(String mMnemonic) {
        this.mMnemonic = mMnemonic;
    }
}

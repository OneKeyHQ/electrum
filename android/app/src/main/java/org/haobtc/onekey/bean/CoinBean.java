package org.haobtc.onekey.bean;

import android.graphics.drawable.Drawable;

public class CoinBean {

    private Drawable mIcon;
    private String mName;
    private boolean mChecked = false;

    public CoinBean(Drawable mIcon, String mName) {
        this.mIcon = mIcon;
        this.mName = mName;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }


}

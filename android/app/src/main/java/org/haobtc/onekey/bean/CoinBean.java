package org.haobtc.onekey.bean;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * @author liyan
 */
public class CoinBean {

    private int mIconId;
    private int mNameId;

    public CoinBean(@DrawableRes int mIconId, @StringRes int mNameId) {
        this.mIconId = mIconId;
        this.mNameId = mNameId;
    }



    public int getIconId() {
        return mIconId;
    }


    public int getNameId() {
        return mNameId;
    }


}

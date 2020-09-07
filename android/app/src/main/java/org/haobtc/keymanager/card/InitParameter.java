package org.haobtc.keymanager.card;

import android.content.Context;
import android.nfc.Tag;

/**
* @author liyan
*/
@Deprecated
public class InitParameter {
    public Context mContext;
    public Tag mTag;

    public InitParameter(Context context, Tag tag) {
        this.mContext = context;
        this.mTag = tag;
    }
}
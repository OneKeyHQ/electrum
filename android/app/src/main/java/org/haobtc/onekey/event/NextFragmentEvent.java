package org.haobtc.onekey.event;

import androidx.annotation.LayoutRes;

/**
 * @author liyan
 * @date 11/23/20
 */

public class NextFragmentEvent {

    private int layoutId;

    public NextFragmentEvent(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public int getLayoutId() {
        return layoutId;
    }
}

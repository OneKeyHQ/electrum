package org.haobtc.onekey.event;

import androidx.annotation.LayoutRes;

/**
 * @author liyan
 * @date 11/23/20
 */

public class NextFragmentEvent {

    private int layoutId;
    private Object[] args;
    public NextFragmentEvent(@LayoutRes int layoutId, Object... args) {
        this.layoutId = layoutId;
        this.args = args;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public Object[] getArgs() {
        return args;
    }
}

package org.haobtc.onekey.event;

import androidx.annotation.StringRes;

/**
 * @author
 * @date 12/10/20
 */

public class RefreshViewEvent {
    private int id;
    public RefreshViewEvent(@StringRes int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}

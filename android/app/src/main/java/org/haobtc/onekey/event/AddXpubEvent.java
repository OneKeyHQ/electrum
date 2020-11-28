package org.haobtc.onekey.event;

import androidx.annotation.NonNull;

/**
 * @author liyan
 * @date 11/24/20
 */

public class AddXpubEvent {

    private String name;

    private String xpub;

    public AddXpubEvent(@NonNull String name, @NonNull String xpub) {
        this.name = name;
        this.xpub = xpub;
    }

    public String getName() {
        return name;
    }

    public String getXpub() {
        return xpub;
    }
}

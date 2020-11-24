package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/23/20
 */

public class CreateWalletEvent {
    private String name;

    public CreateWalletEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

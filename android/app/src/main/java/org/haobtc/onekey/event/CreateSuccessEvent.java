package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/23/20
 */

public class CreateSuccessEvent {
    private String name;
    public CreateSuccessEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

package org.haobtc.onekey.event;

/**
 * @author
 * @date 12/17/20
 */

public class DeleteSuccessEvent {
    private String name;
    public DeleteSuccessEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

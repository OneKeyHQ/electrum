package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/20/20
 */
//
public class NameSettedEvent {

    private String name;
    public int type;
    public NameSettedEvent(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}

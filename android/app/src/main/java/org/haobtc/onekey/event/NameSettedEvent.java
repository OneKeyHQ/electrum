package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/20/20
 */
@Deprecated
public class NameSettedEvent {
    @Deprecated
    private String name;
    @Deprecated
    public int addressPurpose;
    @Deprecated
    public String walletType;

    @Deprecated
    public NameSettedEvent(String name) {
        this.name = name;
    }

    @Deprecated
    public String getName() {
        return name;
    }
}

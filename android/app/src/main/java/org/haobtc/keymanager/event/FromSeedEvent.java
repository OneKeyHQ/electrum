package org.haobtc.keymanager.event;

public class FromSeedEvent {
    private String fromSeed;

    public FromSeedEvent(String fromSeed) {
        this.fromSeed = fromSeed;
    }

    public String getFromSeed() {
        return fromSeed;
    }
}

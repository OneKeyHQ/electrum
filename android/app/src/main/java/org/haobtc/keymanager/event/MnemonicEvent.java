package org.haobtc.keymanager.event;

public class MnemonicEvent {
    private String seed;

    public MnemonicEvent(String seed) {
        this.seed = seed;
    }

    public String getSeed() {
        return seed;
    }
}

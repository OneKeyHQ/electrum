package org.haobtc.onekey.event;

public class InitEvent {
    private String name;
    private boolean useSE;

    public InitEvent(String name, boolean useSE) {
        this.name = name;
        this.useSE = useSE;
    }

    public void setName(String task) {
        this.name = task;
    }

    public String getName() {
        return name;
    }

    public boolean isUseSE() {
        return useSE;
    }

    public void setUseSE(boolean useSE) {
        this.useSE = useSE;
    }
}

package org.haobtc.keymanager.event;

public class SetShutdownTimeEvent {
    String time;

    public SetShutdownTimeEvent(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}

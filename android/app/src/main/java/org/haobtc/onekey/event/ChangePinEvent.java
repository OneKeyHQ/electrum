package org.haobtc.onekey.event;

import androidx.annotation.NonNull;

public class ChangePinEvent {
    private String pinOrigin;
    private String pinNew;

    public ChangePinEvent(String pinNew, String pinOrigin) {
        this.pinOrigin = pinOrigin;
        this.pinNew = pinNew;
    }
    public void setPinNew(String pinNew) {
        this.pinNew = pinNew;
    }

    public String getPinNew() {
        return pinNew;
    }

    public void setPinOrigin(String pinOrigin) {
        this.pinOrigin = pinOrigin;
    }

    public String getPinOrigin() {
        return pinOrigin;
    }

    @NonNull
    @Override
    public String toString() {
        return pinOrigin + pinNew;
    }
}

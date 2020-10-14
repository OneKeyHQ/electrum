package org.haobtc.onekey.event;

public class SignFailedEvent {
    private Exception exception;

    public SignFailedEvent(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

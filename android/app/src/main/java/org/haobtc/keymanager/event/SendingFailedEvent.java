package org.haobtc.keymanager.event;

public class SendingFailedEvent {
    private Exception exception;

    public SendingFailedEvent(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

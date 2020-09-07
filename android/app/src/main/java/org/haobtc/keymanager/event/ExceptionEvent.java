package org.haobtc.keymanager.event;

public class ExceptionEvent {
    private String errorMessage;
    public ExceptionEvent(String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

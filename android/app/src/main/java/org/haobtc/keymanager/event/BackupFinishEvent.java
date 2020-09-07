package org.haobtc.keymanager.event;

//
// Created by  on 2020/6/17.
//
public class BackupFinishEvent {
    private String message;
    public BackupFinishEvent(String s) {
        this.message = s;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

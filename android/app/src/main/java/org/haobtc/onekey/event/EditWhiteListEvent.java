package org.haobtc.onekey.event;

public class EditWhiteListEvent {
    private String type;
    private String content;

    public EditWhiteListEvent(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}

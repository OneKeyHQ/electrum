package org.haobtc.onekey.event;

public class ResultEvent {

    private String result;
    public ResultEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

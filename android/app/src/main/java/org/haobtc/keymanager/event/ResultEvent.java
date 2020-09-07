package org.haobtc.keymanager.event;

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

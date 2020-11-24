package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/23/20
 */

public class GetXpubEvent {
    private String coinName;

    public GetXpubEvent(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinName() {
        return coinName;
    }
}

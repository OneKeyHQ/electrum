package org.haobtc.onekey.event;

/** Created by 小米粒 on 2019/4/12. */
public class FirstEvent {
    public static final String MSG_SET_BTC_BLOCK = "block_check";
    public static final String MSG_SET_ETH_BLOCK = "block_eth_check";
    // 11 --> update wallet list
    // 22 --> update transaction list
    // 33 --> Whether the custom node is added successfully
    private String mMsg;

    public FirstEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }
}

package org.haobtc.onekey.event;

/**
 * Created by 小米粒 on 2019/4/12.
 */

public class FirstEvent {
    //11 --> update wallet list
    //22 --> update transaction list
    //33 --> Whether the custom node is added successfully
    private String mMsg;
    public FirstEvent(String msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public String getMsg(){
        return mMsg;
    }

}

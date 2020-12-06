package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 12/2/20
 */

public class GotPassEvent {
    private String password;

    public GotPassEvent(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}

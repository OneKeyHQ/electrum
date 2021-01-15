package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 12/2/20
 */
@Deprecated
public class GotPassEvent {
    @Deprecated
    private String password;
    @Deprecated
    public int fromType;

    public GotPassEvent(String password) {
        this.password = password;
    }

    @Deprecated
    public String getPassword() {
        return password;
    }

}

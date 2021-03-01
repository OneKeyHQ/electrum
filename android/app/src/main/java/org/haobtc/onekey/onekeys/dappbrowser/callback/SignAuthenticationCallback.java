package org.haobtc.onekey.onekeys.dappbrowser.callback;

/**
 * @author Onekey@QuincySx
 * @create 2021-03-02 7:44 PM
 */
public interface SignAuthenticationCallback {

    void gotAuthorisation(String pwd, boolean gotAuth);

    void cancelAuthentication();
}

package org.haobtc.onekey.onekeys.dappbrowser.callback;

import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction;

/**
 * @author Onekey@QuincySx
 * @create 2021-03-02 7:43 PM
 */
public interface DappActionSheetCallback {

    void getAuthorisation(SignAuthenticationCallback callback);

    void dismissed(String txHash, long callbackId, boolean actionCompleted);

    void notifyConfirm(String mode);

    void signTransaction(String pwd, Web3Transaction tx);

    default void sendTransaction(String pwd, Web3Transaction tx) {}
}

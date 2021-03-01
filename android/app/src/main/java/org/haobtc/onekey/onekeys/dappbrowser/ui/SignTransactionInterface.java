package org.haobtc.onekey.onekeys.dappbrowser.ui;

import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction;

/**
 * @author Onekey@QuincySx
 * @create 2021-03-02 7:48 PM
 */
public interface SignTransactionInterface {

    void transactionSuccess(Web3Transaction web3Tx, String rawTx);

    void transactionError(long callbackId, Throwable error);
}

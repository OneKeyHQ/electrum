package org.haobtc.onekey.onekeys.dappbrowser.listener;

import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction;

public interface OnSignTransactionListener {

    void onSignTransaction(Web3Transaction transaction, String url);
}

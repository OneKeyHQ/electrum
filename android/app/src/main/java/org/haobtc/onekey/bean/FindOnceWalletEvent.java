package org.haobtc.onekey.bean;

import java.util.List;

/**
 * @author liyan
 * @date 11/27/20
 */

public class FindOnceWalletEvent<T> {
    private List<T> wallets;

    public FindOnceWalletEvent(List<T> wallets) {
        this.wallets = wallets;
    }

    public List<T> getWallets() {
        return wallets;
    }
}

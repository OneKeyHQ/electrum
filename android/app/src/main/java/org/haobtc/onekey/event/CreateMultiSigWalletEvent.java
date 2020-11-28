package org.haobtc.onekey.event;

import org.haobtc.onekey.bean.XpubItem;

import java.util.List;

/**
 * @author liyan
 * @date 11/23/20
 */

public class CreateMultiSigWalletEvent {
    private List<XpubItem> xpubItems;

    public CreateMultiSigWalletEvent(List<XpubItem> xpubItems) {
        this.xpubItems = xpubItems;
    }

    public List<XpubItem> getXpubItems() {
        return xpubItems;
    }
}

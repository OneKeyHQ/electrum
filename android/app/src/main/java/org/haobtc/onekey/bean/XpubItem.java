package org.haobtc.onekey.bean;

/**
 * @author liyan
 * @date 11/25/20
 */

public class XpubItem {
    private String name;
    private String xpub;

    public XpubItem(String name, String xpub) {
        this.name = name;
        this.xpub = xpub;
    }

    public String getName() {
        return name;
    }

    public String getXpub() {
        return xpub;
    }
}

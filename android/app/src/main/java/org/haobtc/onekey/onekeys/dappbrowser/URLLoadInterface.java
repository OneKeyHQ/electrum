package org.haobtc.onekey.onekeys.dappbrowser;

public interface URLLoadInterface {

    void onWebpageBeginLoad(String url, String title);

    void onWebpageLoaded(String url, String title);

    void onWebpageLoadComplete();

    void onWebpageLoadError();
}

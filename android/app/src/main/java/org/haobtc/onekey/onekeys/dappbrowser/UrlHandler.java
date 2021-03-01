package org.haobtc.onekey.onekeys.dappbrowser;

import android.net.Uri;

public interface UrlHandler {

    String getScheme();

    String handle(Uri uri);
}

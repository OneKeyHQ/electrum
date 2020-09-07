package org.haobtc.keymanager.utils.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 小米粒 on 2018/11/8.
 */

public class NetUtil {
    private static final boolean NETWORK_NONE = false;
    private static final boolean NETWORK_AVAILABLE = true;

    public static boolean getNetStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager
                .getActiveNetworkInfo() : null;
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return NETWORK_AVAILABLE;

        } else {
            return NETWORK_NONE;
        }

    }
}

package org.haobtc.onekey.utils;

import android.util.Log;

import org.haobtc.onekey.BuildConfig;

/***
 * log工具类
 * @author 一念间
 */
public class LogUtils {

    private static final String TAG = "tiger";

    public static void d(String info) {
        if (BuildConfig.IS_DEBUG) {
            Log.d(TAG, info);
        }
    }

    public static void e(String info) {
        if (BuildConfig.IS_DEBUG) {
            Log.e(TAG, info);
        }
    }

    public static void i(String info) {
        if (BuildConfig.IS_DEBUG) {
            Log.e(TAG, info);
        }
    }
}

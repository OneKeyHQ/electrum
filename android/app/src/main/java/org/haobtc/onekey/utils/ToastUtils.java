package org.haobtc.onekey.utils;

import android.widget.Toast;
import org.haobtc.onekey.activities.base.MyApplication;

/** @Description: ToastUtils @Author: peter Qin */
public class ToastUtils {

    public static void toast(String msg) {
        Toast toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void toastLong(String msg) {
        Toast toast = Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }
}

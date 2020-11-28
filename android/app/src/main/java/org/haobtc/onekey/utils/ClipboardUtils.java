package org.haobtc.onekey.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.haobtc.onekey.R;

/**
 * @author liyan
 * @date 11/26/20
 */

public class ClipboardUtils {

    public static void copyText(Context context, String s) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, s));
        }
        Toast.makeText(context, R.string.copysuccess, Toast.LENGTH_LONG).show();
    }

    public static String pasteText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData data = clipboard.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                return data.getItemAt(0).getText().toString();
            }
        }
        return "";
    }

}

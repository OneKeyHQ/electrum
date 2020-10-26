package org.haobtc.onekey.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.haobtc.onekey.R;

public class NfcUtils {
    //nfc
    public static NfcAdapter mNfcAdapter;
    public static IntentFilter[] mIntentFilter;
    public static PendingIntent mPendingIntent;
    public static String[][] mTechList;

    public static void nfc(Activity activity, boolean promote) {
        mNfcAdapter = nfcCheck(activity, promote);
        nfcInit(activity);
    }

    /**
     * check if NFC enabled
     */
    public static NfcAdapter nfcCheck(Activity activity, boolean promote) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            Toast.makeText(activity, activity.getString(R.string.dont_use_nfc), Toast.LENGTH_SHORT).show();
            return null;
        } else {
            if (!mNfcAdapter.isEnabled() && promote) {
                isToSet(activity);
            } else {
                Log.d("NFC", "NFC功能已打开!");
            }
        }
        return mNfcAdapter;
    }

    /**
     * init nfc
     */
    private static void nfcInit(Activity activity) {
        // tag tech_list
        mTechList = new String[][]{{Ndef.class.getName()}, {NfcV.class.getName()}, {NfcF.class.getName()}, {IsoDep.class.getName()}};

        // PendingIntent，the intent processing the coming tag
        mPendingIntent = PendingIntent.getActivity(activity, 0,
                new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            // filters to filter the nice tag
            mIntentFilter = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // if
    private static void isToSet(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("是否跳转到设置页面打开NFC功能");
//        builder.setTitle("提示");
        builder.setPositiveButton("确认", (dialog, which) -> {
            goToSet(activity);
            dialog.dismiss();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private static void goToSet(Activity activity) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        }

}
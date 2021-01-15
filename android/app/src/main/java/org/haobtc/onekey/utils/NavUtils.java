package org.haobtc.onekey.utils;
import android.content.Context;
import android.content.Intent;

import org.haobtc.onekey.activities.ResetAppActivity;
import org.haobtc.onekey.activities.base.LunchActivity;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportWalletSetNameActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateDeriveChooseTypeActivity;
import org.haobtc.onekey.onekeys.homepage.process.SoftWalletNameSettingActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;

/**
 * @Description: 页面跳转的管理类
 * @Author: peter Qin
 * @CreateDate: 2020/12/16$ 4:12 PM$
 */
public class NavUtils {
    // 跳转重置app 页面
    public static void gotoResetAppActivity (Context context) {
        ResetAppActivity.gotoResetAppActivity(context);
    }

    public static void gotoMainActivityTask (Context context, boolean isNewTask, boolean reset) {
        Intent intent = new Intent(context, HomeOneKeyActivity.class);
        if (reset) {
            intent.putExtra(HomeOneKeyActivity.EXT_RESTART, true);
        }
        if (isNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void gotoMainActivityTask (Context context, boolean isNewTask) {
        gotoMainActivityTask(context, isNewTask, false);
    }

    public static void gotoLunchActivity (Context context) {
        Intent intent = new Intent(context, LunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void reSetApp (Context mContext) {
        Intent intent = new Intent(mContext, LunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        System.exit(0);
    }

    public static void gotoCheckChainDetailWebActivity (Context context, String loadWhere, String loadUrl) {
        CheckChainDetailWebActivity.gotoCheckChainDetailWebActivity(context, loadWhere, loadUrl);
    }

    public static void gotoSoftWalletNameSettingActivity (Context context, int purpose,String type) {
        SoftWalletNameSettingActivity.gotoSoftWalletNameSettingActivity(context,purpose, type);
    }

    public static void gotoCreateDeriveChooseTypeActivity (Context context, boolean finish) {
        CreateDeriveChooseTypeActivity.gotoCreateDeriveChooseTypeActivity(context, finish);
    }

    /**
     * 跳转到导入私钥的设置名称页面
     *
     * @param context
     * @param purpose
     */
    public static void gotoImportWalletSetNameActivity (Context context, int purpose) {
        ImportWalletSetNameActivity.gotoImportWalletSetNameActivity(context, purpose);
    }

    public static void gotoSoftPassActivity (Context context,  int from) {
        SoftPassActivity.gotoSoftPassActivity(context,  from);
    }

}

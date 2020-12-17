package org.haobtc.onekey.utils;

import android.content.Context;
import android.content.Intent;

import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.activities.ResetAppActivity;
import org.haobtc.onekey.activities.base.LunchActivity;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;

/**
 * @Description: 页面跳转的管理类
 * @Author: peter Qin
 * @CreateDate: 2020/12/16$ 4:12 PM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/16$ 4:12 PM$
 * @UpdateRemark: 更新说明：
 */
public class NavUtils {
    // 跳转重置app 页面
    public static void gotoResetAppActivity (Context context) {
        ResetAppActivity.gotoResetAppActivity(context);
    }

    public static void gotoMainActivityTask (Context context) {
        Intent intent = new Intent(context, HomeOneKeyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void gotoLunchActivity (Context context) {
        Intent intent = new Intent(context, LunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void reStartApp (Context mContext) {
        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

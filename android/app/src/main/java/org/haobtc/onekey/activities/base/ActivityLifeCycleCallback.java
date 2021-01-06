package org.haobtc.onekey.activities.base;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.haobtc.onekey.utils.LanguageUtils;

import dr.android.utils.LogUtil;

/**
 * @Author: peter Qin
 */
public class ActivityLifeCycleCallback implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated (@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        LogUtil.d("OneKey", "页面 onCreate：" + activity.getClass().getName());
        LanguageUtils.changeLanguage(activity);
    }

    @Override
    public void onActivityStarted (@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed (@NonNull Activity activity) {
        LanguageUtils.changeLanguage(activity);
    }

    @Override
    public void onActivityPaused (@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped (@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState (@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed (@NonNull Activity activity) {
    }

}

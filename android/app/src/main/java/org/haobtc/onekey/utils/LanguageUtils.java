package org.haobtc.onekey.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import org.haobtc.onekey.constant.Constant;

import java.util.Locale;

public class LanguageUtils {
    private static final String TAG = "LanguageUtil";

    /**
     * @param context
     * @param newLanguage 想要切换的语言类型 比如 "en" ,"zh"
     */
    @SuppressWarnings("deprecation")
    public static void changeAppLanguage (Context context, String newLanguage) {
        if (TextUtils.isEmpty(newLanguage)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        //获取想要切换的语言类型
        Locale locale = getLocaleByLanguage(newLanguage);
        configuration.setLocale(locale);
        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    public static Locale getLocaleByLanguage (String language) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (language.equals(Constant.Chinese)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(Constant.English)) {
            locale = Locale.ENGLISH;
        }
        Log.d(TAG, "getLocaleByLanguage: " + locale.getDisplayName());
        return locale;
    }

    public static Context attachBaseContext (Context context, String language) {
        Log.d("TAG", "attachBaseContext: " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources (Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = LanguageUtils.getLocaleByLanguage(language);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }
}

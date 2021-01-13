package org.haobtc.onekey.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LanguageUtils {

    /**
     * 使用自定义语言的 Config
     *
     * @param context   系统上下文
     * @param newLocale 新语言地区
     * @return 系统上下文
     */
    public static Context userLanguageCustomConfig(Context context, Locale newLocale) {
        Locale.setDefault(newLocale);
        // Configuration.setLocale is added after 17 and Configuration.locale is deprecated after 24
        if (Build.VERSION.SDK_INT >= 17) {
            Configuration config = new Configuration();
            config.setLocale(newLocale);
            return context.createConfigurationContext(config);
        } else {
            Resources res = context.getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.locale = newLocale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }

    /**
     * 获取系统语言
     *
     * @return 系统语言
     */
    public static Locale getSysLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }
        return locale;
    }
}

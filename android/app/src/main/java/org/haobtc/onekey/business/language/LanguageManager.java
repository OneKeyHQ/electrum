package org.haobtc.onekey.business.language;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.FileNameConstant;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.LanguageUtils;

import java.util.Locale;

/**
 * 多语言管理类
 *
 * @author Onekey@QuincySx
 * @create 2021-01-13 10:34 AM
 */
public class LanguageManager {
    public static final String SAVE_LANGUAGE = Constant.LANGUAGE;
    private static final String TAG = LanguageManager.class.getSimpleName();
    private static volatile LanguageManager instance = null;

    public static LanguageManager getInstance() {
        if (instance == null) {
            synchronized (LanguageManager.class) {
                if (instance == null) {
                    instance = new LanguageManager();
                }
            }
        }
        return instance;
    }

    private LanguageManager() {
    }

    public Configuration updateConfigurationIfSupported(Configuration config) {
        // Configuration.getLocales is added after 24 and Configuration.locale is deprecated in 24
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.getLocales().isEmpty()) {
                return config;
            }
        } else {
            if (config.locale != null) {
                return config;
            }
        }

        Locale customLocale = getCurrentLocale(MyApplication.getInstance());
        if (customLocale != null) {
            // Configuration.setLocale is added after 17 and Configuration.locale is deprecated after 24
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(customLocale);
            } else {
                config.locale = customLocale;
            }
        }
        return config;
    }

    public Locale getCurrentLocale(Context context) {
        String language = getLocalLanguage(context);
        return getLocaleByLanguage(language);
    }

    public String getLocalLanguage(Context context) {
        return PreferencesManager.get(context, FileNameConstant.myPreferences, SAVE_LANGUAGE, "").toString();
    }

    /**
     * 切换语言
     *
     * @param language 新语言，跟随系统传 null
     */
    public void changeLanguage(Context context, @Nullable String language) throws Exception {
        try {
            String hardWareLanguage = convertHardWareLanguage(language);
            if (hardWareLanguage != null) {
                Daemon.commands.callAttr("set_language", hardWareLanguage);
            }
            PreferencesManager.put(context, FileNameConstant.myPreferences, SAVE_LANGUAGE, language == null ? "" : language);
        } catch (Exception e) {
            throw HardWareExceptions.exceptionConvert(e);
        }
    }

    /**
     * 转换成硬件识别的格式
     *
     * @param language App 语言标示
     * @return 硬件语言标示
     */
    @Nullable
    private String convertHardWareLanguage(@Nullable String language) {
        if (language == null) {
            return null;
        }
        if (language.equals(Constant.Chinese)) {
            return Constant.Zh_CN;
        } else if (language.equals(Constant.English)) {
            return Constant.En_UK;
        } else {
            return null;
        }
    }

    private Locale getLocaleByLanguage(String language) {
        Locale locale;
        if (language.equals(Constant.Chinese)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(Constant.English)) {
            locale = Locale.ENGLISH;
        } else {
            locale = LanguageUtils.getSysLocale();
        }
        Log.d(TAG, "getLocaleByLanguage: " + locale.getDisplayName());
        return locale;
    }
}

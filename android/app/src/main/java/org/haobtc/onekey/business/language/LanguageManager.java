package org.haobtc.onekey.business.language;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

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

    public Context attachBaseContext(Context context) {
        String language = getLocalLanguage(context);
        Locale newLocale = getLocaleByLanguage(language);
        return LanguageUtils.userLanguageCustomConfig(context, newLocale);
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

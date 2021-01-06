package org.haobtc.onekey.business.wallet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringDef;

import static org.haobtc.onekey.constant.Constant.NEED_POP_BACKUP_DIALOG;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;

/**
 * 系统配置管理
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 3:15 PM
 */
public class SystemConfigManager {
    private SharedPreferences mPreferencesSharedPreferences;

    public SystemConfigManager(Context context) {
        mPreferencesSharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
    }

    @StringDef({SoftHdPassType.LONG, SoftHdPassType.SHORT})
    public @interface SoftHdPassType {
        /**
         * 长密码
         */
        public static final String LONG = "long";

        /**
         * 短密码
         */
        public static final String SHORT = "short";
    }

    /**
     * 设置默认的密码输入模式。
     *
     * @param type 密码输入模式
     */
    public void setPassWordType(@SoftHdPassType String type) {
        mPreferencesSharedPreferences.edit()
                .putString(SOFT_HD_PASS_TYPE, type)
                .apply();
    }

    @SoftHdPassType
    public String getPassWordType() {
        return mPreferencesSharedPreferences.getString(SOFT_HD_PASS_TYPE, SoftHdPassType.SHORT);
    }

    /**
     * 设置备份钱包弹窗的开关
     *
     * @param bool true 下次弹窗
     */
    public void setNeedPopBackUpDialog(Boolean bool) {
        mPreferencesSharedPreferences.edit()
                .putBoolean(NEED_POP_BACKUP_DIALOG, bool)
                .apply();
    }

    /**
     * 获取备份钱包弹窗的开关状态
     *
     * @return bool 开关状态
     */
    public boolean getNeedPopBackUpDialog() {
        return mPreferencesSharedPreferences.getBoolean(NEED_POP_BACKUP_DIALOG, true);
    }
}

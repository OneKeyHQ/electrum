package org.haobtc.onekey.business.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.StringDef;

import org.haobtc.onekey.bean.FiatUnitSymbolBean;
import org.haobtc.onekey.utils.Daemon;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;
import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_SYMBOL;
import static org.haobtc.onekey.constant.Constant.NEED_POP_BACKUP_DIALOG;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;

/**
 * 系统配置管理
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 3:15 PM
 */
public class SystemConfigManager {
    private final SharedPreferences mPreferencesSharedPreferences;

    public SystemConfigManager(Context context) {
        mPreferencesSharedPreferences = context.getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
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

    /**
     * 获取当前法币的单位
     *
     * @return 法币单位
     */
    public String getCurrentFiatUnit() {
        return mPreferencesSharedPreferences.getString(CURRENT_CURRENCY_SYMBOL, "CNY");
    }

    /**
     * 获取当前法币的符号
     *
     * @return 法币符号
     */
    public String getCurrentFiatSymbol() {
        return mPreferencesSharedPreferences.getString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, "¥");
    }

    /**
     * 获取当前法币的符号与单位
     *
     * @return 法币符号与单位
     */
    public FiatUnitSymbolBean getCurrentFiatUnitSymbol() {
        String unit = getCurrentFiatUnit();
        String symbol = getCurrentFiatSymbol();
        return new FiatUnitSymbolBean(unit, symbol);
    }

    /**
     * 设置当前法币的符号与单位
     *
     * @param symbol 法币符号与单位
     * @return 是否保存成功
     */
    public boolean setCurrentFiatUnitSymbol(FiatUnitSymbolBean symbol) {
        if (TextUtils.isEmpty(symbol.getSymbol()) || TextUtils.isEmpty(symbol.getUnit())) {
            return false;
        }
        try {
            Daemon.commands.callAttr("set_currency", symbol.getUnit());
            mPreferencesSharedPreferences.edit()
                    .putString(CURRENT_CURRENCY_SYMBOL, symbol.getUnit())
                    .putString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, symbol.getSymbol())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取数字货币单位
     *
     * @return 数字货币单位
     */
    public String getCurrentBaseUnit() {
        return mPreferencesSharedPreferences.getString("base_unit", "BTC");
    }

    /**
     * 设置数字货币单位
     *
     * @param unit 数字货币单位
     * @return 是否存储成功
     */
    public boolean setCurrentBaseUnit(String unit) {
        if (TextUtils.isEmpty(unit)) {
            return false;
        }
        try {
            Daemon.commands.callAttr("set_base_uint", unit);
            mPreferencesSharedPreferences.edit().putString("base_unit", unit).apply();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

package org.haobtc.onekey.manager;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.constant.Constant;

/**
 * @Description: 用于管理安卓本地 SharePreference 管理类
 * @Author: peter Qin
 */
public class MySPManager {
    private static MySPManager mySPManager;

    public MySPManager () {
    }

    public static MySPManager getInstance () {
        if (mySPManager == null) {
            synchronized (MySPManager.class) {
                if (mySPManager == null) {
                    mySPManager = new MySPManager();
                }
            }
        }
        return mySPManager;
    }

    public void put (String key, Object value) {
        PreferencesManager.put(MyApplication.getInstance(), Constant.myPreferences, key, value);
    }

    public Object get (String key, Object defaultObject) {
        return PreferencesManager.get(MyApplication.getInstance(), Constant.myPreferences, key, defaultObject);
    }

    public void remove (String key) {
        PreferencesManager.remove(MyApplication.getInstance(), Constant.myPreferences, key);
    }

}

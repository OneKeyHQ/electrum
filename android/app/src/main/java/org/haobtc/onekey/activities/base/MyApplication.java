package org.haobtc.onekey.activities.base;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.MyEventBusIndex;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.FileNameConstant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.utils.Global;
import org.haobtc.onekey.utils.LanguageUtils;

import java.util.Locale;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;

/**
 * @author liyan
 */
public class MyApplication extends Application {

    private static volatile MyApplication mInstance;
    private static final String BUGLY_APPID = "91260a7fcb";
    private static final String PRIMARY_SERVICE = "00000001-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTERISTIC = "00000003-0000-1000-8000-00805f9b34fb";
    private static final Handler S_HANDLER = new Handler(Looper.myLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        // add application lifecycle observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
        // EventBus optimize
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        mInstance = this;
        initBle();
        initChaquo();
        registerLifeActivityCallbacks();
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, true);
    }

    private void registerLifeActivityCallbacks () {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated (@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted (@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed (@NonNull Activity activity) {
                String language = PreferencesManager.get(activity, FileNameConstant.myPreferences, Constant.LANGUAGE, "").toString();
                if (!TextUtils.isEmpty(language)) {
                    if (Constant.English.equals(language)) {
                        switchLanguage(1);
                    } else if (Constant.Chinese.equals(language)) {
                        switchLanguage(0);
                    }
                }
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
        });
    }

    public void switchLanguage (int mode) {
        if (mode == 0) {
            Locale.setDefault(Locale.CHINESE);
        } else if (mode == 1) {
            Locale.setDefault(Locale.ENGLISH);
        }
        Configuration config1 = getBaseContext().getResources().getConfiguration();
        if (mode == 0) {
            config1.locale = Locale.CHINESE;
        } else if (mode == 1) {
            config1.locale = Locale.ENGLISH;
        }
        getBaseContext().getResources().updateConfiguration(config1
                , getBaseContext().getResources().getDisplayMetrics());
    }

    public static MyApplication getInstance () {
        if (mInstance == null) {
            synchronized (MyApplication.class) {
                if (mInstance == null) {
                    mInstance = new MyApplication();
                }
            }
        }
        return mInstance;
    }

    // init ble
    private void initBle() {
        //Set whether to print Bluetooth log
        Ble.options().setLogBleEnable(false)
                /* Set whether to throw Bluetooth exception */
                .setThrowBleException(true)
                //Set global Bluetooth operation log TAG
                .setLogTAG("AndroidBLE")
                //Set whether to connect automatically
                .setAutoConnect(false)
                //Set whether to filter the founded devices
                .setFilterScan(true)
                //Set whether to filter the founded devices
                .setConnectFailedRetryCount(3)
                // Set the connection timeout
                .setConnectTimeout(10 * 1000)
                // Set the Scanning period
                .setScanPeriod(5 * 1000)
                .setServiceBindFailedRetryCount(3)
                .setUuidService(UUID.fromString(PRIMARY_SERVICE))
                .setUuidWriteCha(UUID.fromString(WRITE_CHARACTERISTIC))
                .setUuidNotify(UUID.fromString(READ_CHARACTERISTIC))
                .setUuidReadCha(UUID.fromString(READ_CHARACTERISTIC))
                .create(mInstance);
    }

    private void initChaquo() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(mInstance));
        }
        Global.py = Python.getInstance();
    }

    public String getDeviceWay() {
        return (String) PreferencesManager.get(getInstance(), "Preferences"
                , Constant.WAY, Constant.WAY_MODE_BLE);
    }


    public void toastErr(Exception e){
        if(e == null){
            return;
        }
        String info = e.toString();
        if(TextUtils.isEmpty(info)){
            return;
        }
        S_HANDLER.post(() -> Toast.makeText(MyApplication.this,info,Toast.LENGTH_SHORT).show());
    }

}

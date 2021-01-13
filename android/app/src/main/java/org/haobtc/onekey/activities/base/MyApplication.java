package org.haobtc.onekey.activities.base;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.MyEventBusIndex;
import org.haobtc.onekey.business.language.LanguageManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.utils.Global;

import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * @author liyan
 */
public class MyApplication extends Application implements ViewModelStoreOwner {

    private static volatile MyApplication mInstance;
    private static final String BUGLY_APPID = "91260a7fcb";
    private static final String PRIMARY_SERVICE = "00000001-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTERISTIC = "00000003-0000-1000-8000-00805f9b34fb";
    private final ViewModelStore mViewModelStore = new ViewModelStore();
    private final Handler S_HANDLER = new Handler(Looper.myLooper());

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.getInstance().attachBaseContext(base));
    }

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
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, true);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
        // Error message after unsubscribing,It crashes if you don't handle it.
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
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

    public Handler getHandler() {
        return S_HANDLER;
    }

    public void toastErr(Exception e){
        if(e == null || e.getMessage() == null){
            return;
        }
        String info;
        if (e.getMessage().contains("BaseException:")) {
            info = e.getMessage().replace("BaseException:", "");
        } else {
            info = e.toString();
        }
        if(TextUtils.isEmpty(info)){
            return;
        }
        S_HANDLER.post(() -> Toast.makeText(MyApplication.this,info,Toast.LENGTH_SHORT).show());
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }
}

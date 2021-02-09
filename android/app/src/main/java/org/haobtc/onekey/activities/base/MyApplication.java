package org.haobtc.onekey.activities.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import cn.com.heaton.blelibrary.ble.Ble;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import java.util.UUID;
import me.jessyan.autosize.AutoSizeConfig;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.MyEventBusIndex;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.utils.Global;
import zendesk.answerbot.AnswerBot;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

/** @author liyan */
public class MyApplication extends Application implements ViewModelStoreOwner {

    private static volatile MyApplication mInstance;
    private static final String BUGLY_APPID = "91260a7fcb";
    private static final String PRIMARY_SERVICE = "00000001-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTERISTIC = "00000003-0000-1000-8000-00805f9b34fb";
    private final ViewModelStore mViewModelStore = new ViewModelStore();
    private final Handler S_HANDLER = new Handler(Looper.myLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        AutoSizeConfig.getInstance().setExcludeFontScale(true);

        // add application lifecycle observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
        registerActivityLifecycleCallbacks(new ActivityLifeCycleCallback());
        // EventBus optimize
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        mInstance = this;
        initBle();
        initChaquo();
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, true);
        Logger.addLogAdapter(
                new AndroidLogAdapter() {
                    @Override
                    public boolean isLoggable(int priority, String tag) {
                        return BuildConfig.DEBUG;
                    }
                });
        // Error message after unsubscribing,It crashes if you don't handle it.
        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
        initZendesk();
    }

    private void initZendesk() {
        Zendesk.INSTANCE.init(
                this, StringConstant.Zen_Desk, StringConstant.Zen_ID, StringConstant.Zen_Oauth_ID);
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
        Support.INSTANCE.init(Zendesk.INSTANCE);
        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Support.INSTANCE);
    }

    public static MyApplication getInstance() {
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
        // Set whether to print Bluetooth log
        Ble.options()
                .setLogBleEnable(false)
                /* Set whether to throw Bluetooth exception */
                .setThrowBleException(true)
                // Set global Bluetooth operation log TAG
                .setLogTAG("AndroidBLE")
                // Set whether to connect automatically
                .setAutoConnect(true)
                // Set whether to filter the founded devices
                .setFilterScan(true)
                // Set connect retry count when failed
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
        return (String)
                PreferencesManager.get(
                        getInstance(), "Preferences", Constant.WAY, Constant.WAY_MODE_BLE);
    }

    public Handler getHandler() {
        return S_HANDLER;
    }

    public void toastErr(Exception e) {
        if (e == null || e.getMessage() == null) {
            return;
        }
        String info;
        if (e.getMessage().contains(":")) {
            info = e.getMessage().substring(e.getMessage().indexOf(":") + 1);
        } else {
            info = e.getMessage();
        }
        if (TextUtils.isEmpty(info)) {
            return;
        }
        S_HANDLER.post(
                () -> {
                    Toast toast = Toast.makeText(MyApplication.this, null, Toast.LENGTH_SHORT);
                    toast.setText(info);
                    toast.show();
                });
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }
}

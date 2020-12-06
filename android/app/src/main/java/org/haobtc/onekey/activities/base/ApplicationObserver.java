package org.haobtc.onekey.activities.base;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Optional;

import cn.com.heaton.blelibrary.ble.Ble;

/**
 * @author liyan
 */
public class ApplicationObserver implements LifecycleObserver {
    private final String TAG = this.getClass().getName();
    public static boolean tryUpdate;

    public ApplicationObserver() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        tryUpdate = true;
        Log.d(TAG, "Lifecycle.Event.ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.d(TAG, "Lifecycle.Event.ON_START");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.d(TAG, "Lifecycle.Event.ON_RESUME");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "Lifecycle.Event.ON_PAUSE");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Optional.ofNullable(Ble.getInstance().getConnetedDevices()).ifPresent((bleDevices) -> Ble.getInstance().disconnectAll());
        Log.d(TAG, "Lifecycle.Event.ON_STOP");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "Lifecycle.Event.ON_DESTROY");
    }
}
package org.haobtc.onekey.viewmodel;

import android.app.Application;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.utils.internet.NetBroadcastReceiver;

/**
 * 存放全局的网络状态，Application 生命周期的 ViewModel
 *
 * @author Onekey@QuincySx
 * @create 2021-01-04 11:52 AM
 */
public class NetworkViewModel extends ViewModel {
    private final MutableLiveData<Boolean> mHaveNet = new MutableLiveData<>(false);
    private final NetBroadcastReceiver mNetBroadcastReceiver = new NetBroadcastReceiver();
    private final LocalBroadcastManager mLocalBroadcastManager;

    public NetworkViewModel() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getInstance().getApplicationContext());
        mLocalBroadcastManager.registerReceiver(mNetBroadcastReceiver, new IntentFilter());
        mNetBroadcastReceiver.setStatusMonitor(mHaveNet::setValue);
    }

    public LiveData<Boolean> haveNet() {
        return mHaveNet;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLocalBroadcastManager.unregisterReceiver(mNetBroadcastReceiver);
    }
}

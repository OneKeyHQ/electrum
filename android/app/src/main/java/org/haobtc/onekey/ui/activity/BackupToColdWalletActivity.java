package org.haobtc.onekey.ui.activity;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.ui.fragment.BackupSuccessFragment;
import org.haobtc.onekey.ui.fragment.BackupWalletToColdWalletFragment;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.GiveNameFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePINFragment;
import org.haobtc.onekey.ui.listener.IBackupSuccessListener;
import org.haobtc.onekey.ui.listener.IBackupWalletToColdWalletListener;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

public class BackupToColdWalletActivity extends BaseActivity implements ISetDevicePassListener, IColdDeviceConfirmListener
        , IGiveNameListener, IBackupWalletToColdWalletListener, IBackupSuccessListener {


    @Override
    public void init() {
        startFragment(new SetDevicePINFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onSetDevicePassSuccess() {
        startFragment(new ColdDeviceConfirmFragment());
    }

    @Override
    public void toNext() {
        startFragment(new GiveNameFragment());
    }

    @Override
    public void onWalletInitSuccess() {

        startFragment(new BackupWalletToColdWalletFragment());
    }

    @Override
    public void onBackupSuccess() {

        startFragment(new BackupSuccessFragment());
    }

    @Override
    public void onToHome() {
        toActivity(HomeOnekeyActivity.class);
    }
}

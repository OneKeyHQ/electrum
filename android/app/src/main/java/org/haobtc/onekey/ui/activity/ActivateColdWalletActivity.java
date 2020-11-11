package org.haobtc.onekey.ui.activity;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.passageway.HandleCommands;
import org.haobtc.onekey.ui.fragment.BackupWalletFragment;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.CompleteBackupFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.GiveNameFragment;
import org.haobtc.onekey.ui.listener.IBackupWalletListener;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

/**
 * activate wallet
 */
public class ActivateColdWalletActivity extends BaseActivity implements View.OnClickListener
        , ISetDevicePassListener, IColdDeviceConfirmListener, IGiveNameListener, IBackupWalletListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new DevicePINFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                if (mCurrentFragment instanceof DevicePINFragment) {
                    HandleCommands.cancelPinUi();
                }
                finish();
                break;
        }
    }


    @Override
    public void toNext() {
        startFragment(new GiveNameFragment());
    }

    @Override
    public void onSetDevicePassSuccess() {
        startFragment(new ColdDeviceConfirmFragment());
    }

    @Override
    public void onWalletInitSuccess() {
        startFragment(new BackupWalletFragment());
    }

    @Override
    public void onReadyGo() {
        startFragment(new CompleteBackupFragment());
    }
}

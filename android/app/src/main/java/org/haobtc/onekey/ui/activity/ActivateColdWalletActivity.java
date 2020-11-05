package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.passageway.HandleCommands;
import org.haobtc.onekey.ui.fragment.BackupWalletFragment;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.CompleteBackupFragment;
import org.haobtc.onekey.ui.fragment.GiveNameFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePINFragment;
import org.haobtc.onekey.ui.listener.IBackupWalletListener;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

import butterknife.BindView;

/**
 * activate wallet
 */
public class ActivateColdWalletActivity extends BaseActivity implements View.OnClickListener
        , ISetDevicePassListener, IColdDeviceConfirmListener, IGiveNameListener, IBackupWalletListener {

    @BindView(R.id.title)
    protected TextView mTitle;

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new SetDevicePINFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                if (mCurrentFragment instanceof SetDevicePINFragment) {
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
    public void onResetPin() {
        HandleCommands.resetPin((HandleCommands.CallBack<String>) result -> {

        });
    }

    @Override
    public void onUpdateTitle(int title) {
        runOnUiThread(() -> mTitle.setText(title));
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

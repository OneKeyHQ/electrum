package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.ui.fragment.RecoveryWalletByColdWalletFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePINFragment;
import org.haobtc.onekey.ui.listener.IRecoveryWalletByColdWalletListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

import butterknife.BindView;

public class RecoveryWalletByColdWalletActivity extends BaseActivity implements ISetDevicePassListener
        , IRecoveryWalletByColdWalletListener, View.OnClickListener {

    @Override
    public void init() {
        mTitle.setText(R.string.recovery_hd_wallet);
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new SetDevicePINFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onSetDevicePassSuccess() {
        startFragment(new RecoveryWalletByColdWalletFragment());
    }

    @Override
    public void onRecoverySuccess() {
        toActivity(HomeOnekeyActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}

package org.haobtc.onekey.ui.activity;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;

public class FindNewDeviceActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.init_as_new_wallet).setOnClickListener(this);
        findViewById(R.id.init_as_new_wallet_hide).setOnClickListener(this);
        findViewById(R.id.import_seed).setOnClickListener(this);
        findViewById(R.id.import_seed_hide).setOnClickListener(this);
        findViewById(R.id.recovery_device).setOnClickListener(this);
        findViewById(R.id.recovery_device_hide).setOnClickListener(this);
        findViewById(R.id.multi_sig_wallet).setOnClickListener(this);
        findViewById(R.id.multi_sig_wallet_hide).setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_new_device;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.init_as_new_wallet:
            case R.id.init_as_new_wallet_hide:
                toActivity(ActivateColdWalletActivity.class);
                break;
            case R.id.import_seed:
            case R.id.import_seed_hide:
                toActivity(ImportMnemonicToDeviceActivity.class);
                break;
            case R.id.recovery_device:
            case R.id.recovery_device_hide:
                toActivity(RecoveryDeviceFromPhoneBackupActivity.class);
                break;
            case R.id.multi_sig_wallet:
            case R.id.multi_sig_wallet_hide:

                break;
        }
    }
}

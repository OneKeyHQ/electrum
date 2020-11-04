package org.haobtc.onekey.ui.activity;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;

public class FindDeviceNoBackupActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.add_new_wallet).setOnClickListener(this);
        findViewById(R.id.add_new_wallet_hide).setOnClickListener(this);
        findViewById(R.id.clone_to_other).setOnClickListener(this);
        findViewById(R.id.clone_to_other_hide).setOnClickListener(this);
        findViewById(R.id.recovery_used_wallet).setOnClickListener(this);
        findViewById(R.id.recovery_used_wallet_hide).setOnClickListener(this);
        findViewById(R.id.multi_sig_wallet).setOnClickListener(this);
        findViewById(R.id.multi_sig_wallet_hide).setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_device_no_backup;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.add_new_wallet:
            case R.id.add_new_wallet_hide:

                break;
            case R.id.clone_to_other:
            case R.id.clone_to_other_hide:

                break;
            case R.id.recovery_used_wallet:
            case R.id.recovery_used_wallet_hide:

                break;
            case R.id.multi_sig_wallet:
            case R.id.multi_sig_wallet_hide:

                break;
        }
    }
}

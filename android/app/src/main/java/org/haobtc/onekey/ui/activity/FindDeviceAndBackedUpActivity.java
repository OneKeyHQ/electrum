package org.haobtc.onekey.ui.activity;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;

public class FindDeviceAndBackedUpActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.recovery_hd_wallet).setOnClickListener(this);
        findViewById(R.id.recovery_hd_wallet_hide).setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_device_and_backed_up;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.recovery_hd_wallet:
            case R.id.recovery_hd_wallet_hide:

                break;

        }
    }
}

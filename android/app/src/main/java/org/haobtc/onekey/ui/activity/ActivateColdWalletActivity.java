package org.haobtc.onekey.ui.activity;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePassFragment;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

/**
 * activate wallet
 */
public class ActivateColdWalletActivity extends BaseActivity implements View.OnClickListener
        , ISetDevicePassListener, IColdDeviceConfirmListener {

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);

        startFragment(new SetDevicePassFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_activate_cold_wallet;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void toConfirmPass() {
        startFragment(new ColdDeviceConfirmFragment());
    }

    @Override
    public void toNext() {

    }
}

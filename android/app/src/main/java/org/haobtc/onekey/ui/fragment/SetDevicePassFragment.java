package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

public class SetDevicePassFragment extends BaseFragment<ISetDevicePassListener> {
    @Override
    public void init(View view) {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_set_device_pass;
    }
}

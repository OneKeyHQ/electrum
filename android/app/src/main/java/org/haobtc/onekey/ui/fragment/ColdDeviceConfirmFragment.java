package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;

public class ColdDeviceConfirmFragment extends BaseFragment  {

    @Override
    public void init(View view) {
//        view.findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_cold_device_confirm;
    }

}

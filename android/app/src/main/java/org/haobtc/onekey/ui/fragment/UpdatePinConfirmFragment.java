package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.IUpdatePinConfirmListener;

public class UpdatePinConfirmFragment extends BaseFragment<IUpdatePinConfirmListener> implements View.OnClickListener {


    @Override
    public void init(View view) {

        view.findViewById(R.id.back_device).setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_update_pin_confirm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_device:
                if (getListener() != null) {
                    getListener().onBackDevice();
                }
                break;
        }
    }
}

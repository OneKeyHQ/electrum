package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class UpdatePinConfirmFragment extends BaseFragment {


    @BindView(R.id.back_device)
    Button backDevice;

    @Override
    public void init(View view) {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_update_pin_confirm;
    }

    @OnClick(R.id.back_device)
    public void onViewClicked() {
        EventBus.getDefault().post(new NextFragmentEvent(R.layout.active_successful_fragment));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirm(ButtonRequestConfirmedEvent event) {
        backDevice.setEnabled(true);
    }
    @Override
    public boolean needEvents() {
        return true;
    }
}

package org.haobtc.onekey.ui.activity;

import android.view.View;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;

/**
 * @author liyan
 * @date 11/25/20
 */
public class VerifyPinActivity extends BaseActivity
        implements HardwarePinFragment.HardwareTitleChangeCallback,
                HardwarePinFragment.OnHardwarePinSuccessCallback {

    private String action;

    @Override
    public void init() {
        action = getIntent().getAction();
        if (action == null) {
            action = HardwarePinFragment.PinActionType.VERIFY_PIN;
        }
        HardwarePinFragment pinFragment = HardwarePinFragment.newInstance(action, null);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.view_container, pinFragment)
                .commit();
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_input_pin;
    }

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        PyEnv.cancelPinInput();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(ExitEvent exitEvent) {
        finish();
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @Override
    public void setTitle(String title) {
        runOnUiThread(
                () -> {
                    if (mTitle != null) {
                        mTitle.setText(title);
                    }
                });
    }

    @Override
    public void onSuccess(ChangePinEvent event) {
        EventBus.getDefault().post(event);
        if (!event.getAction().equalsIgnoreCase(HardwarePinFragment.PinActionType.NEW_PIN)) {
            finish();
        }
    }

    @Override
    public void onFinish() {
        finish();
    }
}

package org.haobtc.onekey.ui.activity;

import android.view.View;
import butterknife.OnClick;
import com.google.common.base.Strings;
import java.util.Optional;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;

/**
 * @author liyan
 * @date 11/25/20
 */
public class PinNewActivity extends BaseActivity
        implements HardwarePinFragment.HardwareTitleChangeCallback,
                HardwarePinFragment.OnHardwarePinSuccessCallback {

    private String pinOrigin;

    @Override
    public void init() {
        pinOrigin = Optional.ofNullable(getIntent().getStringExtra(Constant.PIN_ORIGIN)).orElse("");
        HardwarePinFragment pinFragment =
                HardwarePinFragment.newInstance(
                        HardwarePinFragment.PinActionType.NEW_PIN, pinOrigin);

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

    @OnClick(R.id.img_back)
    public void onClick(View view) {
        if (Strings.isNullOrEmpty(pinOrigin)) {
            PyEnv.cancelPinInput();
        }
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
    }

    @Override
    public void onFinish() {
        finish();
    }
}

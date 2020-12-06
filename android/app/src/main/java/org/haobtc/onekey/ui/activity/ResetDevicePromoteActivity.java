package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.WipeEvent;
import org.haobtc.onekey.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/26/20
 */

public class ResetDevicePromoteActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.reset_device)
    Button resetDevice;
    @BindView(R.id.checkbox)
    CheckBox checkbox;

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.restore_factory);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_recovery_set;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.reset_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.reset_device:
                EventBus.getDefault().post(new WipeEvent());
                break;
        }
    }
    @OnCheckedChanged(R.id.checkbox)
    public void  onChecked(boolean checked) {
           resetDevice.setEnabled(checked);
    }

    @Override
    public boolean needEvents() {
        return true;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(ExitEvent exitEvent) {
        finish();
    }
}

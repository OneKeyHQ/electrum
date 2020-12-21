package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class InputPinOnHardware extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.title)
    TextView title;

    /**
     * init
     */
    @Override
    public void init() {
        title.setText(R.string.verify_pin_onkey);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.input_on_hardware;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        if (hasWindowFocus()) {
            finish();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent event) {
        finish();
    }
    @Override
    public boolean needEvents() {
        return true;
    }
}

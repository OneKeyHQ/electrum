package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.PinInputComplete;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.widget.SuperTextView;

/**
 * @author liyan
 * @date 12/19/20
 */
public class InputPinOnHardware extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.confirm_btn)
    SuperTextView confirmBtn;

    /** init */
    @Override
    public void init() {
        title.setText(R.string.verify_pin_onkey);
        confirmBtn.setEnabled(false);
    }

    /**
     * * init layout
     *
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

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent event) {
        if (hasWindowFocus()) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinInputComplete(PinInputComplete pinInputComplete) {
        if (hasWindowFocus()) {
            confirmBtn.setEnabled(true);
            confirmBtn.setText(R.string.finish);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(HardWareExceptions exceptions) {
        if (hasWindowFocus()) {
            finish();
        }
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @SingleClick
    @OnClick({R.id.confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                finish();
                break;
        }
    }
}

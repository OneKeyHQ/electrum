package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.event.PinInputComplete;
import org.haobtc.onekey.ui.base.BaseActivity;

/**
 * @author liyan
 * @date 11/26/20
 */
public class ConfirmOnHardWareActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.next)
    Button next;

    @BindView(R.id.promote)
    TextView promote;

    private String action = "";

    /** init */
    @Override
    public void init() {
        action = getIntent().getAction();
        if (BusinessAsyncTask.WIPE_DEVICE.equals(action)) {
            updateTitle(R.string.restore_factory);
            promote.setText(getString(R.string.cold_device_confirm));
        } else {
            updateTitle(R.string.change_pin);
            next.setText(R.string.finish);
            next.setEnabled(false);
        }
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.confirm_on_hardware_activity;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.next:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinInputComplete(PinInputComplete pinInputComplete) {
        if (hasWindowFocus()) {
            next.setEnabled(true);
            next.setText(R.string.finish);
        }
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}

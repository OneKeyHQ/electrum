package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

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

    /**
     * init
     */
    @Override
    public void init() {
        action = getIntent().getAction();
        if (BusinessAsyncTask.WIPE_DEVICE.equals(action)) {
            updateTitle(R.string.restore_factory);
            promote.setText(getString(R.string.cold_device_confirm));
        } else {
            updateTitle(R.string.change_pin);
        }
    }

    /***
     * init layout
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
}

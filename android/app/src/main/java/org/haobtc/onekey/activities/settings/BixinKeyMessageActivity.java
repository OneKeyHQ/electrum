package org.haobtc.onekey.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author xiaomin
 */
public class BixinKeyMessageActivity extends BaseActivity {

    @BindView(R.id.tet_keyName)
    TextView tetKeyName;
    @BindView(R.id.tet_code)
    TextView tetCode;
    @BindView(R.id.tet_Bluetoose)
    TextView tetBluetoose;
    @BindView(R.id.text_systom_hardware)
    TextView textSystomHardware;
    @BindView(R.id.text_bluetooth_hardware)
    TextView textBluetoothHardware;
    private String bleName;
    private Intent intent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bixin_key_message;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        intent = getIntent();
        inits();
    }

    private void inits() {
        String label = intent.getStringExtra("label");
        bleName = intent.getStringExtra("bleName");
        String deviceId = intent.getStringExtra("device_id");
        String firmwareVersion = intent.getStringExtra("firmwareVersion");
        String bleVersion = intent.getStringExtra("bleVersion");
        textSystomHardware.setText(firmwareVersion);
        textBluetoothHardware.setText(bleVersion);
        tetKeyName.setText(label);
        tetCode.setText(deviceId);
        tetBluetoose.setText(bleName);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tet_keyName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_keyName:
                mIntent(FixBixinkeyNameActivity.class);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixBixinkeyNameEvent event) {
        tetKeyName.setText(event.getKeyName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

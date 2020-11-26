package org.haobtc.onekey.activities.settings.recovery_set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.HandlerEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.features;

public class ResetDeviceActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.reset_device)
    Button rest_device;
    public static final String TAG = "org.haobtc.onekey.activities.settings.recovery_set.RecoverySetActivity";
    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;

    private int img = 1;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recovery_set;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
//      mIntent(BackupRecoveryActivity.class);//备份bixinkey
        bleName = getIntent().getStringExtra("ble_name");
        checkboxOk.setOnCheckedChangeListener(this);
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.reset_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;

            case R.id.reset_device:
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        EventBus.getDefault().postSticky(new HandlerEvent());
                    }
                }
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (!features.isPinProtection()) {
            startActivity(new Intent(this, ResetDeviceSuccessActivity.class));
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            rest_device.setBackground(getDrawable(R.drawable.delete_wallet_yes));
            rest_device.setEnabled(true);
        } else {
            rest_device.setBackground(getDrawable(R.drawable.delete_wallet_no));
            rest_device.setEnabled(false);
        }
    }
}

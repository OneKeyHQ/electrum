package org.haobtc.keymanager.activities.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.SecondEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlueToothStatusActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.switchHideBluetooth)
    Switch switchHideBluetooth;
    private boolean bluetoothStatus;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_blue_tooth_status;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        bluetoothStatus = preferences.getBoolean("bluetoothStatus", false);
        edit = preferences.edit();
        switchHideBluetooth.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {
        if (bluetoothStatus) {
            switchHideBluetooth.setChecked(true);
        } else {
            switchHideBluetooth.setChecked(false);
        }
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            edit.putBoolean("bluetoothStatus", true);
        } else {
            edit.putBoolean("bluetoothStatus", false);
        }
        edit.apply();
        EventBus.getDefault().post(new SecondEvent("bluetooth_status"));
    }
}

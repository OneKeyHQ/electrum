package org.haobtc.keymanager.activities.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.HandlerEvent;
import org.haobtc.keymanager.event.SetBluetoothEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class BixinKeyBluetoothSettingActivity extends BaseActivity {

    public static final String TAG_TRUE = BixinKeyBluetoothSettingActivity.class.getSimpleName();
    public static final String TAG_FALSE = "TAG_FALSE_BLUETOOTH_CLOSE";
    private String bleName;
    @BindView(R.id.switchHideBluetooth)
    Switch switchHideBluetooth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bixin_key_bluetooth_setting;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

    }

    @Override
    public void initData() {
        bleName = getIntent().getStringExtra("ble_name");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SetBluetoothEvent updataHint) {

    }

    //Click the confirm button to indicate success
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventa(ButtonRequestEvent event) {
        mToast(getString(R.string.confirm_hardware_msg));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.img_back, R.id.text_open, R.id.text_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_open:
                nModeSelector(true);
                break;
            case R.id.text_close:
                nModeSelector(false);
                break;
        }
    }

    private void nModeSelector(boolean status) {
        if (Ble.getInstance().getConnetedDevices().size() != 0) {
            if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                EventBus.getDefault().postSticky(new HandlerEvent());
            }
        }
        if (status) {
            Intent intent = new Intent(BixinKeyBluetoothSettingActivity.this, CommunicationModeSelector.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra("tag", TAG_TRUE);
            startActivity(intent);
        } else {
            Intent intent = new Intent(BixinKeyBluetoothSettingActivity.this, CommunicationModeSelector.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra("tag", TAG_FALSE);
            startActivity(intent);
        }
    }

}

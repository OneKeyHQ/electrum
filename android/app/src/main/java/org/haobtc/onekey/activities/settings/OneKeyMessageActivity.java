package org.haobtc.onekey.activities.settings;

import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author xiaomin
 */
public class OneKeyMessageActivity extends BaseActivity {

    @BindView(R.id.tet_keyName)
    TextView tetKeyName;
    @BindView(R.id.tet_code)
    TextView tetCode;
    @BindView(R.id.tet_Bluetoose)
    TextView tetBluetooth;
    @BindView(R.id.text_systom_hardware)
    TextView textSystomHardware;
    @BindView(R.id.text_bluetooth_hardware)
    TextView textBluetoothHardware;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bixin_key_message;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();
    }

    private void inits() {
        String label = getIntent().getStringExtra(Constant.TAG_LABEL);
        String bleName = getIntent().getStringExtra(Constant.TAG_BLE_NAME);
        String deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
        String firmwareVersion = getIntent().getStringExtra(Constant.TAG_FIRMWARE_VERSION);
        String nrfVersion = getIntent().getStringExtra(Constant.TAG_NRF_VERSION);
        textSystomHardware.setText(firmwareVersion);
        textBluetoothHardware.setText(nrfVersion);
        tetKeyName.setText(label);
        tetCode.setText(deviceId);
        tetBluetooth.setText(bleName);
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
                mlToast(getString(R.string.support_less_promote));
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

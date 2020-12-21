package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class PinVerifyWaySelector extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.on_hardware)
    CheckBox onHardware;
    @BindView(R.id.input_on_hardware)
    RelativeLayout inputOnHardware;
    @BindView(R.id.on_app)
    CheckBox onApp;
    @BindView(R.id.input_on_app)
    RelativeLayout inputOnApp;

    /**
     * init
     */
    @Override
    public void init() {
        boolean isVerifyOnHardware = (boolean)PreferencesManager.get(this, "Preferences", Constant.PIN_VERIFY_ON_HARDWARE, false);
        if (isVerifyOnHardware) {
            onHardware.setChecked(true);
            onApp.setChecked(false);
        } else {
            onApp.setChecked(true);
            onHardware.setChecked(false);
        }
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.pin_verify_way_selector_activity;
    }

    @OnClick({R.id.img_back, R.id.on_hardware, R.id.input_on_hardware, R.id.on_app, R.id.input_on_app})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.on_hardware:
            case R.id.input_on_hardware:
                onHardware.setChecked(true);
                onApp.setChecked(false);
                break;
            case R.id.on_app:
            case R.id.input_on_app:
                onApp.setChecked(true);
                onHardware.setChecked(false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferencesManager.put(this, "Preferences", Constant.PIN_VERIFY_ON_HARDWARE, onHardware.isChecked());
    }
}

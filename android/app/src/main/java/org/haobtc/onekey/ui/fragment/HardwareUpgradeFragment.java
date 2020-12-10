package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.UpdateEvent;
import org.haobtc.onekey.ui.activity.HardwareUpgradeActivity;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/3/20
 */

public class HardwareUpgradeFragment extends BaseFragment {


    @BindView(R.id.current_stm32_version_code)
    TextView currentStm32VersionCode;
    @BindView(R.id.current_nrf_version_code)
    TextView currentNrfVersionCode;
    @BindView(R.id.newer_stm32_version_name)
    TextView newerStm32VersionName;
    @BindView(R.id.stm32_update)
    Button stm32Update;
    @BindView(R.id.stm32_update_description)
    TextView stm32UpdateDescription;
    @BindView(R.id.stm32)
    LinearLayout stm32;
    @BindView(R.id.newer_nrf_version_name)
    TextView newerNrfVersionName;
    @BindView(R.id.nrf_update)
    Button nrfUpdate;
    @BindView(R.id.nrf_update_description)
    TextView nrfUpdateDescription;
    @BindView(R.id.ble)
    LinearLayout ble;
    @BindView(R.id.no_update_promote)
    TextView noUpdatePromote;

    /**
     * init views
     *
     * @param view
     */

    @Override
    public void init(View view) {
        currentStm32VersionCode.setText(HardwareUpgradeActivity.currentFirmwareVersion);
        currentNrfVersionCode.setText(HardwareUpgradeActivity.currentNrfVersion);
        if (Strings.isNullOrEmpty(HardwareUpgradeActivity.newFirmwareVersion) && Strings.isNullOrEmpty(HardwareUpgradeActivity.newNrfVersion)) {
            noUpdatePromote.setVisibility(View.VISIBLE);
        } else {
            if (!Strings.isNullOrEmpty(HardwareUpgradeActivity.newFirmwareVersion)) {
                stm32.setVisibility(View.VISIBLE);
                newerStm32VersionName.setText(HardwareUpgradeActivity.newFirmwareVersion);
                stm32UpdateDescription.setText(HardwareUpgradeActivity.firmwareChangelog);
            }
            if (!Strings.isNullOrEmpty(HardwareUpgradeActivity.newNrfVersion)) {
                ble.setVisibility(View.VISIBLE);
                newerNrfVersionName.setText(HardwareUpgradeActivity.newNrfVersion);
                nrfUpdateDescription.setText(HardwareUpgradeActivity.nrfChangelog);
            }
        }

    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.hardware_upgrade_fragment;
    }

    @OnClick({R.id.stm32_update, R.id.nrf_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stm32_update:
                EventBus.getDefault().post(new UpdateEvent(UpdateEvent.FIRMWARE));
                break;
            case R.id.nrf_update:
                EventBus.getDefault().post(new UpdateEvent(UpdateEvent.BLE));
                break;
        }
    }
}

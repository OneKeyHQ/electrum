package org.haobtc.keymanager.activities.settings;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.base.Strings;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.bean.HardwareFeatures;
import org.haobtc.keymanager.bean.UpdateInfo;
import org.haobtc.keymanager.entries.FsActivity;
import org.haobtc.keymanager.event.DfuEvent;
import org.haobtc.keymanager.event.ExceptionEvent;
import org.haobtc.keymanager.event.HandlerEvent;
import org.haobtc.keymanager.fragment.BleDeviceRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import dr.android.fileselector.FileSelectConstant;
import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isDfu;

public class VersionUpgradeActivity extends BaseActivity {

    @BindView(R.id.btn_toUpgrade)
    Button btnToUpgrade;
    @BindView(R.id.tet_firmware)
    TextView tetFirmware;
    @BindView(R.id.checkBox_firmware)
    CheckBox checkBoxFirmware;
    @BindView(R.id.tet_bluetooth)
    TextView tetBluetooth;
    @BindView(R.id.checkBox_bluetooth)
    CheckBox checkBoxBluetooth;
    public final static String TAG = VersionUpgradeActivity.class.getSimpleName();
    @BindView(R.id.stm32_version_tip)
    TextView stm32VersionTip;
    @BindView(R.id.stm32_version_detail)
    TextView stm32VersionDetail;
    @BindView(R.id.nrf_version_tip)
    TextView nrfVersionTip;
    @BindView(R.id.nrf_version_detail)
    TextView nrfVersionDetail;
    @BindView(R.id.test_file_load)
    TextView testFileLoad;
    @BindView(R.id.checkBox_hardware)
    CheckBox checkBoxHardware;

    private int checkWitch = 1;
    public static final String UPDATE_PROCESS = "org.haobtc.wallet.activities.settings.percent";
    private RxPermissions rxPermissions;
    public static String filePath;
    private Bundle bundle;
    public static boolean isDIY;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_version_upgrade;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);
        Intent intent = getIntent();
        String firmwareVersion = intent.getStringExtra("firmwareVersion");
        String bleVerson = intent.getStringExtra("bleVerson");
        bundle = intent.getExtras();
        if (bundle != null) {
            firmwareVersion = bundle.getString("stm32_version");
            bleVerson = bundle.getString("nrf_version");
            stm32VersionTip.setText(String.format("V%s " + getString(R.string.verson_updates), firmwareVersion));
            nrfVersionTip.setText(String.format("V%s " + getString(R.string.verson_updates), bleVerson));
            stm32VersionDetail.setText(bundle.getString("stm32_description"));
            nrfVersionDetail.setText(bundle.getString("nrf_description"));
            bleName = bundle.getString("ble_name", "");
        }
        tetFirmware.setText(String.format("v%s", firmwareVersion));
        tetBluetooth.setText(String.format("v%s", bleVerson));
        if (!TextUtils.isEmpty(filePath)){
            testFileLoad.setText(filePath);
            testFileLoad.setVisibility(View.VISIBLE);
        }else{
            testFileLoad.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        checkBoxClick();
        isDfu = false;
        EventBus.getDefault().register(this);
    }

    private void checkBoxClick() {
        checkBoxFirmware.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxBluetooth.setChecked(false);
                checkBoxHardware.setChecked(false);
                checkWitch = 1;
            } else {
                checkWitch = 0;
            }
        });
        checkBoxBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxFirmware.setChecked(false);
                checkBoxHardware.setChecked(false);
                checkWitch = 2;
            } else {
                checkWitch = 0;
            }
        });
        checkBoxHardware.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxFirmware.setChecked(false);
                checkBoxBluetooth.setChecked(false);
                checkWitch = 3;
                isDIY = true;
            } else {
                isDIY = false;
                checkWitch = 0;
            }
        });
    }

    @SingleClick(value = 1000)
    @OnClick({R.id.img_back, R.id.btn_toUpgrade, R.id.btn_import_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                isDfu = false;
                finish();
                break;
            case R.id.btn_toUpgrade:
                switch (checkWitch) {
                    case 0:
                        mToast(getString(R.string.please_choose_firmware));
                        break;
                    case 1:
                        upgrade("hardware", false);
                        break;
                    case 2:
                        upgrade("ble", true);
                        break;
                    case 3:
                        if (Strings.isNullOrEmpty(filePath)) {
                            mToast(getString(R.string.select_promote));
                        } else {
                            if (filePath.endsWith(".bin")) {
                                upgrade("hardware", false);
                            } else if (filePath.endsWith(".zip")) {
                                upgrade("ble", true);
                            } else {
                                mToast(getString(R.string.update_file_format_error));
                            }
                        }
                }
                break;
            case R.id.btn_import_file:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                Intent intent1 = new Intent();
                                intent1.setClass(getApplicationContext(), FsActivity.class);
                                intent1.putExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY, FileSelectConstant.SELECTOR_MODE_FILE);
                                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                                intent1.putExtra("keyFile", "1");
                                startActivityForResult(intent1, 1);

                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
        }
    }
    /**
     *
     * @param hardware which firmware to upgrade
     * @param b use ble ota or not
     * **/
    private void upgrade(String hardware, boolean b) {
        if (Ble.getInstance().getConnetedDevices().size() != 0) {
            if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                EventBus.getDefault().postSticky(new HandlerEvent());
            }
        }
        Intent intent = new Intent(VersionUpgradeActivity.this, CommunicationModeSelector.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tag", TAG);
        intent.putExtras(bundle);
        intent.putExtra("extras", hardware);
        startActivity(intent);
        isDfu = b;
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void doDfu(DfuEvent dfuEvent) {
        isDfu = false;
        if (dfuEvent.getType() == DfuEvent.START_DFU) {
            Intent intent = new Intent(this, UpgradeBixinKEYActivity.class);
            intent.putExtra("tag", 2);
            intent.putExtras(bundle);
            startActivity(intent);
            EventBus.getDefault().removeStickyEvent(DfuEvent.class);
            new Handler().postDelayed(() -> EventBus.getDefault().postSticky(new DfuEvent(1)), 2000);
        }
    }


    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDfuCompleted(@NonNull String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
            pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_ABORT);
            LocalBroadcastManager.getInstance(VersionUpgradeActivity.this).sendBroadcast(pauseAction);
            SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
            SharedPreferences upgradeInfo = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            String info = upgradeInfo.getString("upgrade_info", "");
            if (deviceAddress.equals(BleDeviceRecyclerViewAdapter.mBleDevice.getBleAddress())) {
                String features = devices.getString(BleDeviceRecyclerViewAdapter.mBleDevice.getBleName(), "");
                if (!Strings.isNullOrEmpty(features)) {
                    HardwareFeatures features1 = HardwareFeatures.objectFromData(features);
                    features1.setBleVer(UpdateInfo.objectFromData(info).getNrf().getVersion());
                    devices.edit().putString(BleDeviceRecyclerViewAdapter.mBleDevice.getBleName(), features1.toString()).apply();
                }
            }
            mIntent(UpgradeFinishedActivity.class);
        }

        @Override
        public void onDfuProcessStarted(@NonNull String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
        }


        @Override
        public void onDfuProcessStarting(@NonNull String deviceAddress) {
            super.onDfuProcessStarting(deviceAddress);
        }

        @Override
        public void onDfuAborted(@NonNull String deviceAddress) {
            super.onDfuAborted(deviceAddress);
//            Ble.getInstance().disconnectAll();
            EventBus.getDefault().post(new ExceptionEvent("abort"));
        }

        @Override
        public void onProgressChanged(@NonNull String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            Intent intent = new Intent();
            intent.setAction(UPDATE_PROCESS);
            intent.putExtra("process", percent);
            LocalBroadcastManager.getInstance(VersionUpgradeActivity.this).sendBroadcast(intent);
        }

        @Override
        public void onError(@NonNull String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
//            Ble.getInstance().disconnectAll();
            if ("DFU DEVICE NOT BONDED".equals(message)) {
                BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress).createBond();
            } else {
                EventBus.getDefault().post(new ExceptionEvent(message));
            }
        }

        @Override
        public void onDeviceConnected(@NonNull String deviceAddress) {
            super.onDeviceConnected(deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(@NonNull String deviceAddress) {
            super.onEnablingDfuMode(deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(@NonNull String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //import file
            assert data != null;
            ArrayList<String> listExtra = data.getStringArrayListExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS);
            assert listExtra != null;
            String str = listExtra.toString();
            String substring = str.substring(1);
            filePath = substring.substring(0, substring.length() - 1);
            if (!TextUtils.isEmpty(filePath)){
                testFileLoad.setText(filePath);
                testFileLoad.setVisibility(View.VISIBLE);
            }else{
                testFileLoad.setVisibility(View.GONE);
            }
        }
    }
}

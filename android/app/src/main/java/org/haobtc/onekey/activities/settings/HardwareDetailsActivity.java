package org.haobtc.onekey.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.base.Strings;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.HardwareVerifyResponse;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ConnectedEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;
import org.haobtc.onekey.event.GotVerifyInfoEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.event.PostVerifyInfoEvent;
import org.haobtc.onekey.event.VerifyFailedEvent;
import org.haobtc.onekey.event.VerifySuccessEvent;
import org.haobtc.onekey.event.WipeEvent;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.activity.CheckXpubActivity;
import org.haobtc.onekey.ui.activity.ConfirmOnHardWareActivity;
import org.haobtc.onekey.ui.activity.HardwareUpgradeActivity;
import org.haobtc.onekey.ui.activity.PinNewActivity;
import org.haobtc.onekey.ui.activity.ResetDevicePromoteActivity;
import org.haobtc.onekey.ui.activity.VerifyHardwareActivity;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.DeleteLocalDeviceDialog;
import org.haobtc.onekey.ui.dialog.InvalidDeviceIdWarningDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class HardwareDetailsActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    public static final String TAG = "org.haobtc.onekey.activities.settings.HardwareDetailsActivity";
    public static final String TAG_VERIFICATION = "VERIFICATION";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_keyName)
    TextView tetKeyName;
    @BindView(R.id.lin_OnckOne)
    RelativeLayout linOnckOne;
    @BindView(R.id.tet_code)
    TextView tetCode;
    @BindView(R.id.lin_OnckTwo)
    LinearLayout linOnckTwo;
    @BindView(R.id.change_pin)
    LinearLayout changePin;
    @BindView(R.id.wipe_device)
    LinearLayout wipeDevice;
    @BindView(R.id.verified)
    TextView verified;
    @BindView(R.id.check_xpub)
    TextView checkXpub;
    @BindView(R.id.hide_wallet)
    LinearLayout hideWalletLayout;
    public static boolean dismiss;
    private String bleName;
    private String deviceId;
    private String label;
    private String bleMac;
    private String firmwareVersion;
    private String nrfVersion;
    private String currentMethod;

    @SingleClick
    @OnClick({R.id.img_back, R.id.lin_OnckOne, R.id.lin_OnckTwo, R.id.change_pin, R.id.wipe_device, R.id.tetBuckup, R.id.tet_deleteWallet, R.id.tetVerification, R.id.check_xpub, R.id.text_hide_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                PyEnv.cancelPinInput();
                finish();
                break;
            case R.id.lin_OnckOne:
                Intent intent = new Intent(HardwareDetailsActivity.this, OneKeyMessageActivity.class);
                intent.putExtra(Constant.TAG_BLE_NAME, bleName);
                intent.putExtra(Constant.TAG_LABEL, label);
                intent.putExtra(Constant.DEVICE_ID, deviceId);
                intent.putExtra(Constant.TAG_FIRMWARE_VERSION, firmwareVersion);
                intent.putExtra(Constant.TAG_NRF_VERSION, nrfVersion);
                startActivity(intent);
                break;
            case R.id.lin_OnckTwo:
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast(getString(R.string.unknown_device));
                    return;
                }
                currentMethod = PyConstant.FIRMWARE_UPDATE;
                initBle();
                break;
            case R.id.change_pin:
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast(getString(R.string.unknown_device));
                    return;
                }
                currentMethod = BusinessAsyncTask.CHANGE_PIN;
                initBle();
                break;
            case R.id.wipe_device:
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast(getString(R.string.unknown_device));
                    return;
                }
                currentMethod = BusinessAsyncTask.WIPE_DEVICE;
                initBle();
                break;
            case R.id.tetBuckup:
                Intent intent7 = new Intent(this, BackupRecoveryActivity.class);
                intent7.putExtra("ble_name", bleName);
                startActivity(intent7);
                break;
            case R.id.tet_deleteWallet:
                new DeleteLocalDeviceDialog(deviceId).show(getSupportFragmentManager(), "");
                break;
            case R.id.tetVerification:
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast(getString(R.string.unknown_device));
                    return;
                }
                currentMethod = BusinessAsyncTask.COUNTER_VERIFICATION;
                initBle();
                break;
            case R.id.check_xpub:
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast(getString(R.string.unknown_device));
                    return;
                }
                currentMethod = BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY;
                initBle();
                break;
            case R.id.text_hide_wallet:
                showToast(R.string.support_less_promote);
                break;
            default:
        }
    }

    private void verifyHardware() {
        String strRandom = UUID.randomUUID().toString().replaceAll("-", "");
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.COUNTER_VERIFICATION,
                strRandom,
                MyApplication.getInstance().getDeviceWay());
    }

    private void initBle() {
        BleManager bleManager = BleManager.getInstance(this);
        bleManager.initBle();
        bleManager.connDevByMac(bleMac);
    }

    private void changePin() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.CHANGE_PIN,
                MyApplication.getInstance().getDeviceWay());
    }

    private void wipeDevice() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.WIPE_DEVICE,
                MyApplication.getInstance().getDeviceWay());
    }

    private void getXpub() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY,
                MyApplication.getInstance().getDeviceWay());
    }

    private void getUpdateInfo() {
        String urlPrefix = "https://onekey.so/";
        String locate = PreferencesManager.get(this, "Preferences", Constant.LANGUAGE, "").toString();
        String info = PreferencesManager.get(this, "Preferences", Constant.UPGRADE_INFO, "").toString();
        if (Strings.isNullOrEmpty(info)) {
            showToast(R.string.get_update_info_failed);
            return;
        }
        Bundle bundle = getBundle(urlPrefix, locate, info);
        Intent intentVersion = new Intent(this, HardwareUpgradeActivity.class);
        intentVersion.putExtras(bundle);
        startActivity(intentVersion);
    }

    @NonNull
    private Bundle getBundle(String urlPrefix, String locate, String info) {

        UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
        String urlNrf = updateInfo.getNrf().getUrl();
        String urlStm32 = updateInfo.getStm32().getUrl();
        String versionNrf = updateInfo.getNrf().getVersion();
        String versionStm32 = updateInfo.getStm32().getVersion().toString().replace(",", ".");
        versionStm32 = versionStm32.substring(1, versionStm32.length() - 1).replaceAll("\\s+", "");
        String descriptionNrf = "English".equals(locate) ? updateInfo.getNrf().getChangelogEn() : updateInfo.getNrf().getChangelogCn();
        String descriptionStm32 = "English".equals(locate) ? updateInfo.getStm32().getChangelogEn() : updateInfo.getStm32().getChangelogCn();
        if (urlNrf.startsWith("https") || urlStm32.startsWith("https")) {
            urlPrefix = "";
        }
        Bundle bundle = new Bundle();
        List<Integer> firmwareCurrentVersion = new ArrayList<>();
        Arrays.asList(firmwareVersion.split("\\.")).forEach((s) -> {
            firmwareCurrentVersion.add(Integer.valueOf(s));
        });
        List<Integer> firmwareNewVersion = updateInfo.getStm32().getVersion();
        if (firmwareNewVersion.get(0) > firmwareCurrentVersion.get(0) ||
                 firmwareNewVersion.get(1) > firmwareCurrentVersion.get(1) ||
            firmwareNewVersion.get(2) > firmwareCurrentVersion.get(2)) {
            bundle.putString(Constant.TAG_FIRMWARE_DOWNLOAD_URL, urlPrefix + urlStm32);
            bundle.putString(Constant.TAG_FIRMWARE_VERSION_NEW, versionStm32);
            bundle.putString(Constant.TAG_FIRMWARE_UPDATE_DES, descriptionStm32);
        }
        if (versionNrf.compareTo(nrfVersion) > 0) {
            bundle.putString(Constant.TAG_NRF_DOWNLOAD_URL, urlPrefix + urlNrf);
            bundle.putString(Constant.TAG_NRF_VERSION_NEW, versionNrf);
            bundle.putString(Constant.TAG_NRF_UPDATE_DES, descriptionNrf);
        }
        bundle.putString(Constant.TAG_BLE_NAME, bleName);
        bundle.putString(Constant.TAG_FIRMWARE_VERSION, firmwareVersion);
        bundle.putString(Constant.TAG_NRF_VERSION, nrfVersion);
        bundle.putString(Constant.BLE_MAC, bleMac);
        bundle.putString(Constant.TAG_LABEL, label);
        bundle.putString(Constant.DEVICE_ID, deviceId);
        return bundle;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadyBle(NotifySuccessfulEvent event) {
        HardwareFeatures features;
        PyResponse<HardwareFeatures> response = PyEnv.getFeature(this);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            features = response.getResult();
            if (!deviceId.equals(features.getDeviceId())) {
                new InvalidDeviceIdWarningDialog().show(getSupportFragmentManager(), "");
                return;
            }
        } else {
            showToast(R.string.get_hard_msg_error);
            return;
        }
        switch (currentMethod) {
            case BusinessAsyncTask.CHANGE_PIN:
                if (features.isInitialized()) {
                    changePin();
                } else {
                   showToast(R.string.un_init_device_support_less);
                }
                break;
            case BusinessAsyncTask.WIPE_DEVICE:
                startActivity(new Intent(this, ResetDevicePromoteActivity.class));
                break;
            case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
                if (features.isInitialized()) {
                    getXpub();
                } else {
                    showToast(R.string.un_init_device_support_less);
                }
                break;
            case BusinessAsyncTask.COUNTER_VERIFICATION:
                Intent intent1 = new Intent(this, VerifyHardwareActivity.class);
                intent1.putExtra(Constant.BLE_INFO, Optional.of(label).orElse(bleName));
                startActivity(intent1);
                new Handler().postDelayed(() -> {
                    EventBus.getDefault().post(new ConnectedEvent());
                }, 1000);
                new Handler().postDelayed(this::verifyHardware, 2000);
                break;
            case PyConstant.FIRMWARE_UPDATE:
                getUpdateInfo();
                break;
            default:
        }
    }

    @Subscribe
    public void onConnectionTimeout(BleConnectionEx connectionEx) {
        if (connectionEx == BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT) {
            EventBus.getDefault().post(new ExitEvent());
            Toast.makeText(this, R.string.ble_connect_timeout, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        // 回写PIN码
        PyEnv.setPin(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        switch (event.getType()) {
            case PyConstant.PIN_CURRENT:
                Intent intent = new Intent(this, VerifyPinActivity.class);
                if (BusinessAsyncTask.CHANGE_PIN.equals(currentMethod)) {
                    intent.setAction(BusinessAsyncTask.CHANGE_PIN);
                }
                startActivity(intent);
                break;
            case PyConstant.BUTTON_REQUEST_7:
                if (hasWindowFocus()) {
                    showToast(R.string.confirm_hardware_msg);
                } else {
                    if (BusinessAsyncTask.CHANGE_PIN.equals(currentMethod)) {
                        PyEnv.cancelAll();
                        startActivity(new Intent(this, ConfirmOnHardWareActivity.class));
                        EventBus.getDefault().post(new ExitEvent());
                    }
                }
                break;
            case PyConstant.BUTTON_REQUEST_6:
            if (BusinessAsyncTask.WIPE_DEVICE.equals(currentMethod)) {
                PyEnv.cancelAll();
                Intent intent1 = new Intent(this, ConfirmOnHardWareActivity.class);
                intent1.setAction(BusinessAsyncTask.WIPE_DEVICE);
                startActivity(intent1);
            } else {
                startActivity(new Intent(this, ConfirmOnHardWareActivity.class));
            }
            EventBus.getDefault().post(new ExitEvent());
                break;
            case PyConstant.PIN_NEW_FIRST:
                startActivity(new Intent(this, PinNewActivity.class));
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWipeDevice(WipeEvent event) {
        wipeDevice();
    }

    private void verification(String result) {
        HashMap<String, String> pramas = new HashMap<>(5);
        try {
            JSONObject jsonObject = new JSONObject(result);
            pramas.put("data", jsonObject.getString("data"));
            pramas.put("signature", jsonObject.getString("signature"));
            pramas.put("cert", jsonObject.getString("cert"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new PostVerifyInfoEvent());
        OkHttpUtils.post().url("https://key.bixin.com/lengqian.bo/")
                .params(pramas)
                .build()
                .connTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        EventBus.getDefault().post(new VerifyFailedEvent(VerifyFailedEvent.FailedReason.NETWORK_ERROR));
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            HardwareVerifyResponse verifyResponse = HardwareVerifyResponse.objectFromData(response);
                            if (verifyResponse.isIsVerified()) {
                                verified.setVisibility(View.VISIBLE);
                                EventBus.getDefault().post(new VerifySuccessEvent());
                                // 修改本地设备信息
                                String str = PreferencesManager.get(HardwareDetailsActivity.this, Constant.DEVICES, deviceId, "").toString();
                                HardwareFeatures features = HardwareFeatures.objectFromData(str);
                                features.setVerify(true);
                                PreferencesManager.put(HardwareDetailsActivity.this, Constant.DEVICES, deviceId, features.toString());
                            } else {
                                EventBus.getDefault().post(new VerifyFailedEvent(VerifyFailedEvent.FailedReason.VERIFY_FAILED));
                            }
                        } catch (Exception e) {
                            EventBus.getDefault().post(new VerifyFailedEvent(VerifyFailedEvent.FailedReason.NETWORK_ERROR));
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixBixinkeyNameEvent event) {
        tetKeyName.setText(event.getKeyName());
    }


    /**
     * init
     */
    @Override
    public void init() {
        Intent intent = getIntent();
        boolean isBackupOnly = intent.getBooleanExtra(Constant.TAG_IS_BACKUP_ONLY, false);
        if (isBackupOnly) {
            checkXpub.setVisibility(View.GONE);
            hideWalletLayout.setVisibility(View.GONE);
        }
        bleName = intent.getStringExtra(Constant.TAG_BLE_NAME);
        deviceId = intent.getStringExtra(Constant.DEVICE_ID);
        label = intent.getStringExtra(Constant.TAG_LABEL);
        firmwareVersion = getIntent().getStringExtra(Constant.TAG_FIRMWARE_VERSION);
        nrfVersion = getIntent().getStringExtra(Constant.TAG_NRF_VERSION);
        tetKeyName.setText(label);
        boolean isVerified = intent.getBooleanExtra(Constant.TAG_HARDWARE_VERIFY, false);
        verified.setVisibility(isVerified ? View.VISIBLE : View.GONE);
        bleMac = PreferencesManager.get(this, Constant.BLE_INFO, bleName, "").toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {
        switch (currentMethod) {
            case BusinessAsyncTask.CHANGE_PIN:
            case BusinessAsyncTask.WIPE_DEVICE:
            case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
                if (HardWareExceptions.PIN_INVALID.getMessage().equals(e.getMessage())) {
                    showToast(R.string.pin_wrong);
                } else {
                    showToast(R.string.fail);
                }
                EventBus.getDefault().post(new ExitEvent());
                break;
            case BusinessAsyncTask.COUNTER_VERIFICATION:
                EventBus.getDefault().post(new VerifyFailedEvent(VerifyFailedEvent.FailedReason.GOT_CERT_FAILED));
                break;
        }
    }

    @Override
    public void onResult(String s) {
        switch (currentMethod) {
            case BusinessAsyncTask.CHANGE_PIN:
            case BusinessAsyncTask.WIPE_DEVICE:
                if ("0".equals(s)) {
                    showToast(R.string.pin_wrong);
                    EventBus.getDefault().post(new ExitEvent());
                }
                break;
            case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
                Intent intent = new Intent(this, CheckXpubActivity.class);
                intent.putExtra(Constant.EXTEND_PUBLIC_KEY, s);
                startActivity(intent);
                EventBus.getDefault().post(new ExitEvent());
                break;
            case BusinessAsyncTask.COUNTER_VERIFICATION:
                EventBus.getDefault().post(new GotVerifyInfoEvent());
                verification(s);
                break;
            default:
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {
        currentMethod = methodName;
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_somemore;
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}

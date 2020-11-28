package org.haobtc.onekey.activities.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.haobtc.onekey.activities.settings.recovery_set.FixHardwareLanguageActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.data.prefs.PreferencesManager;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ConnectedEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;
import org.haobtc.onekey.event.GotVerifyInfoEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.event.PostVerifyInfoEvent;
import org.haobtc.onekey.event.SetShutdownTimeEvent;
import org.haobtc.onekey.event.VerifyFailedEvent;
import org.haobtc.onekey.event.VerifySuccessEvent;
import org.haobtc.onekey.event.WipeEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.activity.CheckXpubActivity;
import org.haobtc.onekey.ui.activity.ConfirmOnHardWareActivity;
import org.haobtc.onekey.ui.activity.ResetDevicePromoteActivity;
import org.haobtc.onekey.ui.activity.VerifyHardwareActivity;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.dialog.DeleteLocalDeviceDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

/**
 * @author liyan
 */
public class HardwareDetailsActivity extends BaseActivity implements BusinessAsyncTask.Helper{

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
    @BindView(R.id.lin_OnckFour)
    LinearLayout linOnckFour;
    @BindView(R.id.wipe_device)
    LinearLayout wipeDevice;
    public static boolean dismiss;
    @BindView(R.id.test_shutdown_time)
    TextView testShutdownTime;
    private String bleName;
    private String deviceId;
    private String label;
    private String bleMac;
    private String currentMethod;

    @SingleClick
    @OnClick({R.id.img_back, R.id.lin_OnckOne, R.id.lin_OnckTwo, R.id.change_pin, R.id.lin_OnckFour, R.id.wipe_device, R.id.linear_shutdown_time, R.id.tetBuckup, R.id.tet_deleteWallet, R.id.test_set_key_language, R.id.tetVerification, R.id.check_xpub, R.id.text_hide_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_OnckOne:
                String firmwareVersion = getIntent().getStringExtra("firmwareVersion");
                String bleVersion = getIntent().getStringExtra("bleVerson");
                Intent intent = new Intent(HardwareDetailsActivity.this, BixinKeyMessageActivity.class);
                intent.putExtra("bleName", bleName);
                intent.putExtra("label", label);
                intent.putExtra("device_id", deviceId);
                intent.putExtra("firmwareVersion", firmwareVersion);
                intent.putExtra("bleVersion", bleVersion);
                startActivity(intent);
                break;
            case R.id.lin_OnckTwo:
                getUpdateInfo();
                break;
            case R.id.change_pin:
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                       changePin();
                       return;
                    }
                }
                if (Strings.isNullOrEmpty(bleMac)) {
                        showToast("未知设备！！！");
                }
                currentMethod = BusinessAsyncTask.CHANGE_PIN;
                initBle();
                break;
            case R.id.lin_OnckFour:
                Intent intent4 = new Intent(this, ConfidentialPaymentSettings.class);
                intent4.putExtra("ble_name", bleName);
                startActivity(intent4);
                break;
            case R.id.wipe_device:
                startActivity(new Intent(this, ResetDevicePromoteActivity.class));
                break;
            case R.id.linear_shutdown_time:
                Intent intent2 = new Intent(this, SetShutdownTimeActivity.class);
                intent2.putExtra("device_id", deviceId);
                intent2.putExtra("ble_name", bleName);
                startActivity(intent2);
                break;
            case R.id.tetBuckup:
                Intent intent7 = new Intent(this, BackupRecoveryActivity.class);
                intent7.putExtra("ble_name", bleName);
                startActivity(intent7);
                break;
            case R.id.tet_deleteWallet:
                new DeleteLocalDeviceDialog(this, deviceId).show(getSupportFragmentManager(), "");
                break;
            case R.id.test_set_key_language:
                Intent intent3 = new Intent(HardwareDetailsActivity.this, FixHardwareLanguageActivity.class);
                intent3.putExtra("ble_name", bleName);
                startActivity(intent3);
                break;
            case R.id.tetVerification:
                Intent intent1 = new Intent(this, VerifyHardwareActivity.class);
                intent1.putExtra(Constant.BLE_INFO, Optional.of(label).orElse(bleName));
                startActivity(intent1);
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        EventBus.getDefault().post(new ConnectedEvent());
                        verifyHardware();
                        return;
                    }
                }
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast("未知设备！！！");
                }
                currentMethod = BusinessAsyncTask.COUNTER_VERIFICATION;
                initBle();
                break;
            case R.id.check_xpub:
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        getXpub();
                        return;
                    }
                }
                if (Strings.isNullOrEmpty(bleMac)) {
                    showToast("未知设备！！！");
                }
                currentMethod = BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY;
                initBle();
                break;
            case R.id.text_hide_wallet:
                showToast("暂不支持，敬请期待！！");
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
        String urlPrefix = "https://key.bixin.com/";
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String locate = preferences.getString("language", "");
        String info = preferences.getString("upgrade_info", "");
        UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
        String urlNrf = updateInfo.getNrf().getUrl();
        String urlStm32 = updateInfo.getStm32().getUrl();
        String versionNrf = updateInfo.getNrf().getVersion();
        String versionStm32 = updateInfo.getStm32().getVersion().toString().replace(",", ".");
        versionStm32 = versionStm32.substring(1, versionStm32.length() - 1).replaceAll("\\s+", "");
        String descriptionNrf = "English".equals(locate) ? updateInfo.getNrf().getChangelogEn() : updateInfo.getNrf().getChangelogCn();
        String descriptionStm32 = "English".equals(locate) ? updateInfo.getStm32().getChangelogEn() : updateInfo.getStm32().getChangelogCn();
        Bundle bundle = new Bundle();
        if (urlNrf.startsWith("https") || urlStm32.startsWith("https")) {
            urlPrefix = "";
        }
        bundle.putString("nrf_url", urlPrefix + urlNrf);
        bundle.putString("stm32_url", urlPrefix + urlStm32);
        bundle.putString("nrf_version", versionNrf);
        bundle.putString("stm32_version", versionStm32);
        bundle.putString("nrf_description", descriptionNrf);
        bundle.putString("stm32_description", descriptionStm32);
        bundle.putString("ble_name", bleName);
        Intent intentVersion = new Intent(this, VersionUpgradeActivity.class);
        intentVersion.putExtras(bundle);
        startActivity(intentVersion);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadyBle(NotifySuccessfulEvent event) {
       switch (currentMethod) {
           case BusinessAsyncTask.CHANGE_PIN:
               changePin();
               break;
           case BusinessAsyncTask.WIPE_DEVICE:
               wipeDevice();
               break;
           case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
               getXpub();
           case BusinessAsyncTask.COUNTER_VERIFICATION:
               EventBus.getDefault().post(new ConnectedEvent());
               verifyHardware();
               break;
           default:
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
                    showToast("请在硬件上确认您的操作");
                } else {
                    switch (currentMethod) {
                        case BusinessAsyncTask.CHANGE_PIN:
                            PyEnv.cancelAll();
                            startActivity(new Intent(this, ConfirmOnHardWareActivity.class));
                            EventBus.getDefault().post(new ExitEvent());
                            break;
                        case BusinessAsyncTask.WIPE_DEVICE:
                            PyEnv.cancelAll();
                            Intent intent1 = new Intent(this, ConfirmOnHardWareActivity.class);
                            intent1.setAction(BusinessAsyncTask.WIPE_DEVICE);
                            startActivity(intent1);
                            break;
                        default:
                    }

                }
                    break;
                case PyConstant.BUTTON_REQUEST_6:
                    startActivity(new Intent(this, ConfirmOnHardWareActivity.class));
                    EventBus.getDefault().post(new ExitEvent());
            default:

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWipeDevice(WipeEvent event) {
        if (Ble.getInstance().getConnetedDevices().size() != 0) {
            if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                wipeDevice();
                return;
            }
        }
        if (Strings.isNullOrEmpty(bleMac)) {
            showToast("未知设备！！！");
        }
        currentMethod = BusinessAsyncTask.CHANGE_PIN;
        initBle();
    }
    private void verification(String result) {
        HashMap<String, String> pramas = new HashMap<>();
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
                        Log.i("strVerification", "onError: ---- " + e.getMessage());
                        EventBus.getDefault().post(new VerifyFailedEvent());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.i("strVerification", "onResponse:------- " + response);
                        if (response.contains("is_verified")) {
                            EventBus.getDefault().post(new VerifySuccessEvent());
                        }
                    }
                });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixBixinkeyNameEvent event) {
        tetKeyName.setText(event.getKeyName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showtime(SetShutdownTimeEvent event) {
        testShutdownTime.setText(String.format("%s%s", event.getTime(), getString(R.string.second)));
    }

    /**
     * init
     */
    @Override
    public void init() {
        Intent intent = getIntent();
        bleName = intent.getStringExtra("bleName");
        deviceId = intent.getStringExtra("device_id");
        label = intent.getStringExtra("label");
        if (!TextUtils.isEmpty(label)) {
            tetKeyName.setText(label);
        } else {
            tetKeyName.setText(String.format("%s", "BixinKEY"));
        }
        testShutdownTime.setText(String.format("%s%s", "600", getString(R.string.second)));
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
                break;
            case BusinessAsyncTask.WIPE_DEVICE:
                break;
            case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
                break;
            case BusinessAsyncTask.COUNTER_VERIFICATION:
                EventBus.getDefault().post(new VerifyFailedEvent());
                break;
        }
    }

    @Override
    public void onResult(String s) {
        switch (currentMethod) {
            case BusinessAsyncTask.CHANGE_PIN:
                System.out.println("change pin success=========");
                break;
            case BusinessAsyncTask.WIPE_DEVICE:
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

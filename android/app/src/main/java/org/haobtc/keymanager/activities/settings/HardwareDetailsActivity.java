package org.haobtc.keymanager.activities.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.activities.service.NfcNotifyHelper;
import org.haobtc.keymanager.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.keymanager.activities.settings.recovery_set.FixHardwareLanguageActivity;
import org.haobtc.keymanager.activities.settings.recovery_set.ResetDeviceActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.bean.UpdateInfo;
import org.haobtc.keymanager.event.AddKeyEvent;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.ExitEvent;
import org.haobtc.keymanager.event.FixBixinkeyNameEvent;
import org.haobtc.keymanager.event.HandlerEvent;
import org.haobtc.keymanager.event.SetShutdownTimeEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.features;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;

public class HardwareDetailsActivity extends BaseActivity {

    public static final String TAG = "org.haobtc.wallet.activities.settings.HardwareDetailsActivity";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_keyName)
    TextView tetKeyName;
    @BindView(R.id.lin_OnckOne)
    LinearLayout linOnckOne;
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
    @BindView(R.id.tetBuckup)
    TextView tetBuckup;
    @BindView(R.id.linear_serial)
    LinearLayout linearSerial;
    @BindView(R.id.view_OnckFour)
    View viewOnckFour;
    @BindView(R.id.lin_key)
    LinearLayout linKey;
    @BindView(R.id.lin_communication)
    LinearLayout linCommunication;
    @BindView(R.id.card_delete)
    CardView cardDelete;
    @BindView(R.id.tet_serial)
    TextView tetSerial;
    private String bleName;
    private String deviceId;
    private String label;
    private boolean isWipe;
    private SharedPreferences devices;
    private boolean whereIntent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_somemore;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();
    }

    private void inits() {
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        devices = getSharedPreferences("devices", MODE_PRIVATE);
        Intent intent = getIntent();
        bleName = intent.getStringExtra("bleName");
        whereIntent = intent.getBooleanExtra("whereIntent", false);
//        String firmwareVersion = intent.getStringExtra("firmwareVersion");
//        String bleVerson = intent.getStringExtra("bleVerson");
        deviceId = intent.getStringExtra("device_id");
        label = intent.getStringExtra("label");
        if (!TextUtils.isEmpty(label)) {
            tetKeyName.setText(label);
        } else {
            tetKeyName.setText(String.format("%s", "BixinKEY"));
        }
//        tetCode.setText(firmwareVersion);
        String shutdownTime = preferences.getString(deviceId, "600");
        testShutdownTime.setText(String.format("%s%s", shutdownTime, getString(R.string.second)));
        tetSerial.setText(deviceId);
        //key manage or app
        if (whereIntent) {
            tetBuckup.setVisibility(View.GONE);
            linearSerial.setVisibility(View.VISIBLE);
            linOnckFour.setVisibility(View.GONE);
            viewOnckFour.setVisibility(View.GONE);
            linKey.setVisibility(View.GONE);
            linCommunication.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initData() {
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.lin_OnckOne, R.id.lin_OnckTwo, R.id.change_pin, R.id.lin_OnckFour, R.id.wipe_device, R.id.tetBluetoothSet, R.id.linear_shutdown_time, R.id.tetBuckup, R.id.tet_deleteWallet, R.id.test_set_key_language, R.id.lin_communication})
    public void onViewClicked(View view) {
        isWipe = false;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_OnckOne:
                Intent intent = new Intent(HardwareDetailsActivity.this, BixinKeyMessageActivity.class);
                intent.putExtra("bleName", bleName);
                intent.putExtra("label", label);
                intent.putExtra("device_id", deviceId);
                startActivity(intent);
                break;
            case R.id.lin_OnckTwo:
                getUpdateInfo();
                break;
            case R.id.change_pin:
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        EventBus.getDefault().postSticky(new HandlerEvent());
                    }
                }
                Intent intent1 = new Intent(this, CommunicationModeSelector.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent1.putExtra("tag", TAG);
                startActivity(intent1);
                break;
            case R.id.lin_OnckFour:
                Intent intent4 = new Intent(this, ConfidentialPaymentSettings.class);
                intent4.putExtra("ble_name", bleName);
                startActivity(intent4);
                break;
            case R.id.wipe_device:
                isWipe = true;
                Intent intent5 = new Intent(this, ResetDeviceActivity.class);
                intent5.putExtra("ble_name", bleName);
                startActivity(intent5);
                break;
            case R.id.tetBluetoothSet:
                Intent intent6 = new Intent(this, BixinKeyBluetoothSettingActivity.class);
                intent6.putExtra("ble_name", bleName);
                startActivity(intent6);
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
                devices.edit().remove(deviceId).apply();
                EventBus.getDefault().post(new FixBixinkeyNameEvent(deviceId));
                EventBus.getDefault().post(new AddKeyEvent());
                mToast(getString(R.string.delete_succse));
                finish();
                break;
            case R.id.test_set_key_language:
                Intent intent3 = new Intent(HardwareDetailsActivity.this, FixHardwareLanguageActivity.class);
                intent3.putExtra("ble_name", bleName);
                startActivity(intent3);
                break;
            case R.id.lin_communication:
                mIntent(SelectorActivity.class);
                break;
            default:
        }
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
    public void onButtonRequest(ButtonRequestEvent event) {
        if (isNFC) {
            if (isWipe && !features.isPinProtection()) {
                EventBus.getDefault().post(new ExitEvent());
                return;
            }
            EventBus.getDefault().removeStickyEvent(event);
            Intent intent = new Intent(this, NfcNotifyHelper.class);
            intent.putExtra("is_button_request", true);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixBixinkeyNameEvent event) {
        tetKeyName.setText(event.getKeyName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showtime(SetShutdownTimeEvent event) {
        testShutdownTime.setText(String.format("%s%s", event.getTime(), getString(R.string.second)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

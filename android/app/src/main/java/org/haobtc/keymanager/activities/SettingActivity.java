package org.haobtc.keymanager.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azhon.appupdate.utils.ApkUtil;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.activities.service.NfcNotifyHelper;
import org.haobtc.keymanager.activities.settings.BixinKEYManageActivity;
import org.haobtc.keymanager.activities.settings.CurrencyActivity;
import org.haobtc.keymanager.activities.settings.SelectorActivity;
import org.haobtc.keymanager.activities.settings.VersionUpgradeActivity;
import org.haobtc.keymanager.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.bean.UpdateInfo;
import org.haobtc.keymanager.event.ButtonRequestEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.xpub;

public class SettingActivity extends BaseActivity {

    public static final String TAG = SettingActivity.class.getSimpleName();
    public static final String TAG_CHANGE_PIN = "SETTING_CHANGE_PIN";
    @BindView(R.id.backup)
    TextView backup;
    @BindView(R.id.tet_language)
    TextView tetLanguage;
    @BindView(R.id.tetSeverSet)
    TextView tetSeverSet;
    @BindView(R.id.tetTrsactionSet)
    TextView tetTransactionSet;
    @BindView(R.id.tetVerification)
    TextView tetVerification;
    @BindView(R.id.tetAbout)
    TextView tetAbout;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_bixinKey)
    TextView tetBixinKey;
    @BindView(R.id.tet_Faru)
    TextView tetFaru;
    @BindView(R.id.tet_verson)
    TextView tetVerson;
    @BindView(R.id.bluetooth_status)
    TextView bluetoothStatusText;
    @BindView(R.id.change_pin)
    LinearLayout changePin;
    @BindView(R.id.hardware_update)
    LinearLayout hardwareUpdate;
    @BindView(R.id.check_xpub)
    TextView checkXpub;
    public String pin = "";
    private boolean isChangePin;

    @Override
    public int getLayoutId() {
        return R.layout.setting;
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        String versionName = ApkUtil.getVersionName(this);
        tetVerson.setText(String.format("%s", versionName));
    }


    @SingleClick(value = 1000)
    @OnClick({R.id.backup, R.id.tet_language, R.id.tetSeverSet, R.id.tetTrsactionSet, R.id.tetVerification, R.id.tetAbout, R.id.img_back, R.id.tet_bixinKey, R.id.tet_Faru, R.id.bluetooth_set, R.id.change_pin, R.id.hardware_update, R.id.check_xpub})
    public void onViewClicked(View view) {
        isChangePin = false;
        switch (view.getId()) {
            case R.id.tet_bixinKey:
                mIntent(BixinKEYManageActivity.class);
                break;
            case R.id.backup:
                mIntent(BackupRecoveryActivity.class);
                break;
            case R.id.tet_language:
                mIntent(LanguageSettingActivity.class);
                break;
            case R.id.tetSeverSet:
                mIntent(ServerSettingActivity.class);
                break;
            case R.id.tetTrsactionSet:
                mIntent(TransactionsSettingActivity.class);
                break;
            case R.id.tetVerification:
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(runnable);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
            case R.id.tetAbout:
                mIntent(AboutActivity.class);
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_Faru:
                mIntent(CurrencyActivity.class);
                break;
            case R.id.bluetooth_set:
                mIntent(SelectorActivity.class);
                break;
            case R.id.hardware_update:
                getUpdateInfo();
                break;
            case R.id.change_pin:
                isChangePin = true;
                Intent intent1 = new Intent(this, CommunicationModeSelector.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("tag", TAG_CHANGE_PIN);
                startActivity(intent1);
                break;
            case R.id.check_xpub:
                Intent intent2 = new Intent(this, CommunicationModeSelector.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.putExtra("tag", "check_xpub");
                startActivity(intent2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void getUpdateInfo() {
        String urlPrefix = "https://key.bixin.com/";
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String locate = preferences.getString("language", "");
        String info = preferences.getString("upgrade_info", "");
        if (Strings.isNullOrEmpty(info)) {
            Toast.makeText(this, R.string.network_useless, Toast.LENGTH_SHORT).show();
            return;
        }
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
        Intent intentVersion = new Intent(SettingActivity.this, VersionUpgradeActivity.class);
        intentVersion.putExtras(bundle);
        startActivity(intentVersion);
    }

    private Runnable runnable = this::gotoConfirmVerification;

    private void gotoConfirmVerification() {
        Intent intentCon = new Intent(SettingActivity.this, VerificationKEYActivity.class);
        intentCon.putExtra("strVerification", xpub);
        startActivity(intentCon);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (isNFC && isChangePin) {
            EventBus.getDefault().removeStickyEvent(event);
            Intent intent = new Intent(this, NfcNotifyHelper.class);
            intent.putExtra("is_button_request", true);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

package org.haobtc.onekey.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.activities.settings.recovery_set.BackupMessageActivity;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.card.SmartCardHelper;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.PinEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.COMMUNICATION_MODE_BLE;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.customerUI;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.features;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;

/**
 * @author liyan
 * @date 2020/7/30
 */
//
public class BackupWaySelector extends BaseActivity implements BusinessAsyncTask.Helper{
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.local)
    Button local;
    @BindView(R.id.lite)
    Button lite;
    private boolean isLocal;
    public static final String TAG = BackupWaySelector.class.getSimpleName();

    @Override
    public int getLayoutId() {
        return R.layout.back_way_selector;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.local, R.id.lite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.local:
                isLocal = true;
                if (isNFC) {
                    Intent intent = new Intent(this, CommunicationModeSelector.class).setAction("backup");
                    intent.putExtra("get_feature", getIntent().getBooleanExtra("get_feature", false));
                    startActivity(intent);
                } else {
                    if (Ble.getInstance().getConnetedDevices().size() != 0) {
                        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.BACK_UP, COMMUNICATION_MODE_BLE);
                    } else {
                        Intent intent = new Intent(this, CommunicationModeSelector.class);
                        intent.putExtra("tag", TAG);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.lite:
                isLocal = false;
                if (!isNFC) {
                    Toast.makeText(this, R.string.nfc_only, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(this, SmartCardHelper.class);
                intent1.putExtra("extras", getIntent().getStringExtra("message"));
                intent1.setAction("backup");
                intent1.putExtra("step", 1);
                startActivity(intent1);
                break;
            default:
                throw new IllegalStateException("not satisfied id" + view.getId());

        }
    }

    @Subscribe
    public void onBackupFinish(BackupFinishEvent event) {
        SharedPreferences backup = getSharedPreferences("backup", Context.MODE_PRIVATE);
        SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
        backup.edit().putString(features.getDeviceId(), Strings.isNullOrEmpty(features.getLabel()) ?
                features.getBleName() + ":" + event.getMessage()
                : features.getLabel() + ":" + event.getMessage()).apply();
        features.setNeedsBackup(false);
        devices.edit().putString(features.getDeviceId(), features.toString()).apply();
        Intent intent = new Intent(this, BackupMessageActivity.class);
        intent.putExtra("label", Strings.isNullOrEmpty(features.getLabel()) ? features.getBleName() : features.getLabel());
        intent.putExtra("tag", "backup");
        intent.putExtra("message", event.getMessage());
        startActivity(intent);
        EventBus.getDefault().post(new FinishEvent());
        finish();
    }
    @Subscribe
    public void onFinish(FinishEvent event) {
        if (!isLocal) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onButtonRequest(ButtonRequestEvent event) {
        Intent intent = new Intent(this, ConfirmPINPrompt.class);
        startActivity(intent);
        finish();
//        }
    }
    @Subscribe
    public void setPin(PinEvent event) {
        if (!Strings.isNullOrEmpty(event.getPinCode())) {
            customerUI.put("pin", event.getPinCode());
        }
    }
    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onResult(String s) {
       onBackupFinish(new BackupFinishEvent(s));
    }

    @Override
    public void onCancelled() {

    }
}

package org.haobtc.onekey.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.NfcNotifyHelper;
import org.haobtc.onekey.activities.settings.recovery_set.BackupMessageActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.FinishEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.features;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;

/**
 * @author liyan
 */

public class ConfirmPINPrompt extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.backup)
    Button backup;

    @Override
    public int getLayoutId() {
        return R.layout.confirm_pin;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {

    }
    @SingleClick(value = 1000)
    @OnClick({R.id.img_back, R.id.backup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.backup:
                if (isNFC) {
                    Intent intent = new Intent(this, NfcNotifyHelper.class);
                    startActivity(intent);
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
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
}

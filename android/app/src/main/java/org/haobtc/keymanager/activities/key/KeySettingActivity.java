package org.haobtc.keymanager.activities.key;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.AboutActivity;
import org.haobtc.keymanager.activities.LanguageSettingActivity;
import org.haobtc.keymanager.activities.SettingActivity;
import org.haobtc.keymanager.activities.VerificationKEYActivity;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.xpub;

public class KeySettingActivity extends BaseActivity {

    public static final String TAG = KeySettingActivity.class.getSimpleName();

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_setting;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.test_language, R.id.test_about, R.id.test_verifying})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.test_language:
                Intent intent1 = new Intent(KeySettingActivity.this, LanguageSettingActivity.class);
                intent1.putExtra("whereIntent",true);
                startActivity(intent1);
                break;
            case R.id.test_about:
                mIntent(AboutActivity.class);
                break;
            case R.id.test_verifying:
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(runnable);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
        }
    }

    private Runnable runnable = this::gotoConfirmVerification;

    private void gotoConfirmVerification() {
        Intent intentCon = new Intent(KeySettingActivity.this, VerificationKEYActivity.class);
        intentCon.putExtra("strVerification", xpub);
        startActivity(intentCon);

    }
}

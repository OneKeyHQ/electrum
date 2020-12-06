package org.haobtc.onekey.activities.settings.recovery_set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.event.HandlerEvent;
import org.haobtc.onekey.event.SetKeyLanguageEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class FixHardwareLanguageActivity extends BaseActivity {

    public static final String TAG = FixHardwareLanguageActivity.class.getSimpleName();
    @BindView(R.id.chinese_easy)
    TextView chineseEasy;
    @BindView(R.id.test_english)
    TextView testEnglish;
    @BindView(R.id.img_chinese)
    ImageView imgChinese;
    @BindView(R.id.img_english)
    ImageView imgEnglish;
    private String keyLanguage = "Chinese";
    private SharedPreferences preferences;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fix_hardware_language;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);

    }

    @Override
    public void initData() {
        bleName = getIntent().getStringExtra("ble_name");
        if ("English".equals(preferences.getString("key_language", ""))) {
            imgChinese.setVisibility(View.GONE);
            imgEnglish.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.img_back, R.id.chinese_easy, R.id.test_english})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.chinese_easy:
                keyLanguage = "Chinese";
                imgChinese.setVisibility(View.VISIBLE);
                imgEnglish.setVisibility(View.GONE);
                break;
            case R.id.test_english:
                keyLanguage = "English";
                imgChinese.setVisibility(View.GONE);
                imgEnglish.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(SetKeyLanguageEvent event) {
        String status = event.getStatus();
        if ("1".equals(status)) {
            preferences.edit().putString("key_language", keyLanguage).apply();
            mToast(getString(R.string.set_success));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

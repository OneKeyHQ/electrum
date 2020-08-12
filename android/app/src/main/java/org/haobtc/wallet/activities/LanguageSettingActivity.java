package org.haobtc.wallet.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.wallet.MainActivity;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.base.LunchActivity;
import org.haobtc.wallet.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LanguageSettingActivity extends BaseActivity {

    @BindView(R.id.radio_chineseasy)
    TextView radioChineseasy;
    @BindView(R.id.radio_character)
    TextView radioCharacter;
    @BindView(R.id.radio_english)
    TextView radioEnglish;
    @BindView(R.id.radio_Korean)
    TextView radioKorean;
    @BindView(R.id.radio_Japanese)
    TextView radioJapanese;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.radio_system)
    TextView radioSystem;
    private SharedPreferences.Editor edit;
    private String language;

    @Override
    public int getLayoutId() {
        return R.layout.language_setting;
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        language = preferences.getString("language", "");
        edit = preferences.edit();

    }

    @Override
    public void initData() {
        if ("English".equals(language)) {
            radioSystem.setTextColor(getColor(R.color.text_color1));
            radioEnglish.setTextColor(getColor(R.color.button_bk_disableok));
            radioChineseasy.setTextColor(getColor(R.color.text_color1));
        } else if ("Chinese".equals(language)) {
            radioSystem.setTextColor(getColor(R.color.text_color1));
            radioEnglish.setTextColor(getColor(R.color.text_color1));
            radioChineseasy.setTextColor(getColor(R.color.button_bk_disableok));
        } else {
            radioSystem.setTextColor(getColor(R.color.button_bk_disableok));
            radioEnglish.setTextColor(getColor(R.color.text_color1));
            radioChineseasy.setTextColor(getColor(R.color.text_color1));
        }

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.radio_chineseasy, R.id.radio_character, R.id.radio_english, R.id.radio_Korean, R.id.radio_Japanese, R.id.radio_system})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.radio_system:
                edit.putString("language", "");
                edit.commit();
                radioSystem.setTextColor(getColor(R.color.button_bk_disableok));
                radioEnglish.setTextColor(getColor(R.color.text_color1));
                radioChineseasy.setTextColor(getColor(R.color.text_color1));
                Intent intent = new Intent(this, LunchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                System.exit(0);
                break;
            case R.id.radio_chineseasy:
                mTextChinese();
                edit.putString("language", "Chinese");
                edit.apply();
                radioSystem.setTextColor(getColor(R.color.text_color1));
                radioChineseasy.setTextColor(getColor(R.color.button_bk_disableok));
                radioEnglish.setTextColor(getColor(R.color.text_color1));
                refreshSelf();
                break;
            case R.id.radio_character:
                break;
            case R.id.radio_english:
                mTextEnglish();
                edit.putString("language", "English");
                edit.apply();
                radioSystem.setTextColor(getColor(R.color.text_color1));
                radioChineseasy.setTextColor(getColor(R.color.text_color1));
                radioEnglish.setTextColor(getColor(R.color.button_bk_disableok));
                refreshSelf();
                break;
            case R.id.radio_Korean:
                break;
            case R.id.radio_Japanese:
                break;
            default:
        }
    }

    public void refreshSelf() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}

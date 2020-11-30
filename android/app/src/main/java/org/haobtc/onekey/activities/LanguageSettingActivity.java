package org.haobtc.onekey.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.base.LunchActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;

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
    @BindView(R.id.radio_system)
    TextView radioSystem;
    @BindView(R.id.img_chinese)
    ImageView imgChinese;
    @BindView(R.id.img_english)
    ImageView imgEnglish;
    @BindView(R.id.img_system)
    ImageView imgSystem;
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
            imgChinese.setVisibility(View.GONE);
            imgEnglish.setVisibility(View.VISIBLE);
            imgSystem.setVisibility(View.GONE);
        } else if ("Chinese".equals(language)) {
            imgChinese.setVisibility(View.VISIBLE);
            imgEnglish.setVisibility(View.GONE);
            imgSystem.setVisibility(View.GONE);
        } else {
            imgChinese.setVisibility(View.GONE);
            imgEnglish.setVisibility(View.GONE);
            imgSystem.setVisibility(View.VISIBLE);
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
                imgSystem.setVisibility(View.VISIBLE);
                imgChinese.setVisibility(View.GONE);
                imgEnglish.setVisibility(View.GONE);
                Intent intent = new Intent(this, LunchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                System.exit(0);
                break;
            case R.id.radio_chineseasy:
                mTextChinese();
                edit.putString("language", "Chinese");
                edit.apply();
                edit.apply();
                imgSystem.setVisibility(View.GONE);
                imgChinese.setVisibility(View.VISIBLE);
                imgEnglish.setVisibility(View.GONE);
                refreshSelf();
                break;
            case R.id.radio_character:
                break;
            case R.id.radio_english:
                mTextEnglish();
                edit.putString("language", "English");
                edit.apply();
                imgSystem.setVisibility(View.GONE);
                imgChinese.setVisibility(View.GONE);
                imgEnglish.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(this, HomeOneKeyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}

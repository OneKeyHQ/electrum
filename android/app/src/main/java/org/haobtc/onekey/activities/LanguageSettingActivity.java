package org.haobtc.onekey.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.NavUtils;

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
        language = preferences.getString("language", "Chinese");
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
            case R.id.radio_chineseasy:
                mTextChinese();
                setLanguage(Constant.Zh_CN);
                edit.putString(Constant.LANGUAGE, Constant.Chinese);
                edit.apply();
                imgSystem.setVisibility(View.GONE);
                imgChinese.setVisibility(View.VISIBLE);
                imgEnglish.setVisibility(View.GONE);
                changeLanguage(Constant.Chinese);
                break;
            case R.id.radio_system:
            case R.id.radio_english:
                mTextEnglish();
                setLanguage(Constant.En_UK);
                edit.putString(Constant.LANGUAGE, Constant.English);
                edit.apply();
                imgSystem.setVisibility(View.GONE);
                imgChinese.setVisibility(View.GONE);
                imgEnglish.setVisibility(View.VISIBLE);
                changeLanguage(Constant.English);
                break;
            default:
                break;
        }
    }

    private void changeLanguage (String language) {
        NavUtils.gotoMainActivityTask(LanguageSettingActivity.this, true);
    }

    private void setLanguage (String language) {
        try {
            Daemon.commands.callAttr("set_language", language);
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
        }
    }

}

package org.haobtc.onekey.activities;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.business.language.LanguageManager;
import org.haobtc.onekey.constant.Constant;
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

    @Override
    public int getLayoutId() {
        return R.layout.language_setting;
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        String language = LanguageManager.getInstance().getLocalLanguage(this);
        changeLanguageSelect(language);
    }

    private void changeLanguageSelect(@Nullable String language) {
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
                changeLanguage(Constant.Chinese);
                break;
            case R.id.radio_system:
                radioSystem.setTextColor(getColor(R.color.onekey));
                radioEnglish.setTextColor(getColor(R.color.text_color1));
                radioChineseasy.setTextColor(getColor(R.color.text_color1));
                changeLanguage(null);
                break;
            case R.id.radio_english:
                changeLanguage(Constant.English);
                break;
            default:
                break;
        }
    }

    private void changeLanguage(@Nullable String language) {
        try {
            changeLanguageSelect(language);
            LanguageManager.getInstance().changeLanguage(this, language);
            NavUtils.gotoMainActivityTask(LanguageSettingActivity.this, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
                mToast(e.getMessage());
            }
        }
    }
}

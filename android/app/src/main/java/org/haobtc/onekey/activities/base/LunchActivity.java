package org.haobtc.onekey.activities.base;

import android.content.Intent;
import android.text.TextUtils;

import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.data.prefs.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.GuidanceActivity;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.NfcUtils;

import java.util.Optional;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.COMMUNICATION_MODE_NFC;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.way;

/**
 * @author liyan
 */
public class LunchActivity extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_lunch;
    }

    @Override
    public void initView() {
        if (!Optional.ofNullable(NfcUtils.nfcCheck(this, false)).isPresent()) {
            PreferencesManager.put(this, "Preferences", Constant.WAY, Constant.WAY_MODE_BLE);
            PreferencesManager.put(this, "Preferences", Constant.NFC_SUPPORT, false);
        }
    }

    private void init() {
        String language =  PreferencesManager.get(this, "Preferences", Constant.LANGUAGE, "").toString();
        judgeLanguage(language);

        boolean firstRun = (boolean)PreferencesManager.get(this, "Preferences", Constant.FIRST_RUN, false);
        if (firstRun) {
            Intent intent = new Intent(LunchActivity.this, HomeOneKeyActivity.class);
            startActivity(intent);
            finish();

        } else {
            initGuide();

        }
        way =  PreferencesManager.get(this, "Preferences", Constant.WAY, Constant.WAY_MODE_BLE).toString();
        isNFC = COMMUNICATION_MODE_NFC.equals(way);
    }

    //switch language
    private void judgeLanguage(String language) {
        if (!TextUtils.isEmpty(language)) {
            if ("English".equals(language)) {
                mTextEnglish();
            } else if ("Chinese".equals(language)) {
                mTextChinese();
            }
        }
    }

    private void initGuide() {
        Intent intent = new Intent(LunchActivity.this, GuidanceActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void initData() {
        PyEnv.init(this);
        init();
        loadAllWallets();
    }
    private void loadAllWallets() {
        try {
            Daemon.commands.callAttr("load_all_wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

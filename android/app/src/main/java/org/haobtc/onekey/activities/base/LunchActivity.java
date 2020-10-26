package org.haobtc.onekey.activities.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.chaquo.python.Kwarg;

import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.CreateWalletActivity;
import org.haobtc.onekey.activities.GuideActivity;
import org.haobtc.onekey.onekeys.GuidanceActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.Global;
import org.haobtc.onekey.utils.NfcUtils;

import java.util.Optional;

import dr.android.utils.LogUtil;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.COMMUNICATION_MODE_NFC;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.way;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;

public class LunchActivity extends BaseActivity {

    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_lunch;
    }

    @Override
    public void initView() {
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        if (!Optional.ofNullable(NfcUtils.nfcCheck(this, false)).isPresent()) {
            edit.putString("way", "ble");
            edit.putBoolean("nfc_support", false);
        }
        edit.putBoolean("haveCreateNopass", false);//No password is required to create app wallet for the first time
        edit.apply();
    }

    private void init() {
        String language = preferences.getString("language", "");
        judgeLanguage(language);

        String firstRun = "is_first_run";
        if (preferences.getBoolean(firstRun, false)) {
            Intent intent = new Intent(LunchActivity.this, HomeOnekeyActivity.class);
            startActivity(intent);
            finish();

        } else {
            initGuide();

        }
        way = preferences.getString("way", "ble");
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
        if (BuildConfig.net_type.equals(getString(R.string.TestNet))) {
            Global.py.getModule("electrum.constants").callAttr("set_testnet");
        } else if (BuildConfig.net_type.equals(getString(R.string.RegTest))) {
            Global.py.getModule("electrum.constants").callAttr("set_regtest");
        }
        //Global.guiDaemon = Global.py.getModule("electrum_gui.android.daemon");
        Global.guiConsole = Global.py.getModule("electrum_gui.android.console");
        try {
            Daemon.commands = Global.guiConsole.callAttr("AndroidCommands",new Kwarg("android_id","123456"), new Kwarg("callback", Daemon.getInstance()));
        } catch (Exception ignored) {
            ignored.printStackTrace();
            LogUtil.d("exception : " + ignored);
        }
        //Daemon.commands.callAttr("start", Daemon.getInstance());
        //Daemon.commands.callAttr("set_callback_fun", Daemon.getInstance());
        init();
    }
}

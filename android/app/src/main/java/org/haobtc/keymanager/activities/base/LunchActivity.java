package org.haobtc.keymanager.activities.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.chaquo.python.Kwarg;

import org.haobtc.keymanager.BuildConfig;
import org.haobtc.keymanager.MainActivity;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.CreateWalletActivity;
import org.haobtc.keymanager.activities.GuideActivity;
import org.haobtc.keymanager.utils.Daemon;
import org.haobtc.keymanager.utils.Global;
import org.haobtc.keymanager.utils.NfcUtils;

import java.util.Optional;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.COMMUNICATION_MODE_NFC;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.way;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;


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

        boolean jumpOr = preferences.getBoolean("JumpOr", false);
        String firstRun = "is_first_run";
        if (preferences.getBoolean(firstRun, false)) {
            Intent intent = new Intent(LunchActivity.this, KeyManageActivity.class);
            startActivity(intent);
            finish();

        } else {
            if (jumpOr) {
                //CreatWallet
                initCreatWallet();
            } else {
                //splash
                initGuide();
            }

        }
        way = preferences.getString("way", COMMUNICATION_MODE_NFC);
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
        Intent intent = new Intent(LunchActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();

    }

    private void initCreatWallet() {
        Intent intent = new Intent(LunchActivity.this, KeyManageActivity.class);
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
            Daemon.commands = Global.guiConsole.callAttr("AndroidCommands", new Kwarg("callback", Daemon.getInstance()));
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        //Daemon.commands.callAttr("start", Daemon.getInstance());
        //Daemon.commands.callAttr("set_callback_fun", Daemon.getInstance());
        init();
    }
}

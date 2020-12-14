package org.haobtc.onekey.onekeys;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.ApkUtil;
import com.azhon.appupdate.utils.LogUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.LunchActivity;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.manager.HardwareCallbackHandler;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.homepage.DiscoverFragment;
import org.haobtc.onekey.onekeys.homepage.MindFragment;
import org.haobtc.onekey.onekeys.homepage.WalletFragment;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.AppUpdateDialog;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.haobtc.onekey.constant.Constant.BITCOIN_NETWORK_TYPE_0;
import static org.haobtc.onekey.constant.Constant.BITCOIN_NETWORK_TYPE_1;
import static org.haobtc.onekey.constant.Constant.BITCOIN_NETWORK_TYPE_2;
import static org.haobtc.onekey.constant.Constant.ONE_KEY_WEBSITE;

/**
 * @author liyan
 */
public class HomeOneKeyActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, OnDownloadListener {

    @BindView(R.id.container)
    FrameLayout linearMains;
    @BindView(R.id.sj_radiogroup)
    RadioGroup sjRadiogroup;
    private long firstTime = 0;
    private DownloadManager manager;
    private AppUpdateDialog updateDialog;
    private WalletFragment walletFragment;
    private MindFragment mindFragment;
    private DiscoverFragment discoverFragment;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_home_onekey;
    }


    @Override
    public boolean needEvents() {
        return true;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_one:
                startFragment(walletFragment);
                break;
            case R.id.radio_two:
                startFragment(discoverFragment);
                break;
            case R.id.radio_three:
                startFragment(mindFragment);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWalletSuccess(CreateSuccessEvent event) {
        PyEnv.loadLocalWalletInfo(this);
        PreferencesManager.put(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, event.getName());
    }

    /**
     * init
     */
    @Override
    public void init() {
        HardwareCallbackHandler callbackHandler = HardwareCallbackHandler.getInstance(this);
        PyEnv.setHandle(callbackHandler);
        // init as singleInstance to avoid some baffling issue
        walletFragment = new WalletFragment();
        mindFragment = new MindFragment();
        getUpdateInfo();
        refreshView();
    }

    private void refreshView() {
        // 默认让主页被选中
        startFragment(walletFragment);
        // radiobutton长度
        RadioButton[] radioButton = new RadioButton[sjRadiogroup.getChildCount()];
        for (int i = 0; i < radioButton.length; i++) {
            radioButton[i] = (RadioButton) sjRadiogroup.getChildAt(i);
        }
        radioButton[0].setChecked(true);
        sjRadiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(HomeOneKeyActivity.this, R.string.dowbke_to_exit, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getUpdateInfo() {
        // version_testnet.json version_regtest.json
        String appId = BuildConfig.APPLICATION_ID;
        String urlPrefix = ONE_KEY_WEBSITE;
        String url = "";
        if (appId.endsWith(BITCOIN_NETWORK_TYPE_0)) {
            url = urlPrefix + "version.json";
        } else if (appId.endsWith(BITCOIN_NETWORK_TYPE_2)) {
            url = urlPrefix + "version_testnet.json";
        } else if (appId.endsWith(BITCOIN_NETWORK_TYPE_1)) {
            url = urlPrefix + "version_regtest.json";
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
//        Toast.makeText(this, getString(R.string.updating_dialog), Toast.LENGTH_LONG).show();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("获取更新信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                if (response.code() != HTTP_OK) {
                    Log.e("Main", "获取更新信息失败===" + response.body().string());
                    return;
                }
                String locate = PreferencesManager.get(HomeOneKeyActivity.this, "Preferences", Constant.LANGUAGE, "").toString();
                String info = response.body().string();
                UpdateInfo updateInfo = null;
                try {
                    updateInfo = UpdateInfo.objectFromData(info);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof JsonSyntaxException) {
                        Log.e("Main", "获取到的更新信息格式错误");
                    }
                    return;
                }
                String oldInfo = PreferencesManager.get(HomeOneKeyActivity.this, "Preferences", Constant.UPGRADE_INFO, "").toString();
                if (!Strings.isNullOrEmpty(oldInfo)) {
                    UpdateInfo old = UpdateInfo.objectFromData(oldInfo);
                    if (!old.getStm32().getUrl().equals(updateInfo.getStm32().getUrl())) {
                        updateInfo.getStm32().setNeedUpload(true);
                    }
                    if (!old.getNrf().getUrl().equals(updateInfo.getNrf().getUrl())) {
                        updateInfo.getNrf().setNeedUpload(true);
                    }
                }
                PreferencesManager.put(HomeOneKeyActivity.this, "Preferences", Constant.UPGRADE_INFO, updateInfo.toString());
                String url = updateInfo.getAPK().getUrl();
                String versionName = updateInfo.getAPK().getVersionName();
                int versionCode = updateInfo.getAPK().getVersionCode();
                String size = updateInfo.getAPK().getSize().replace("M", "");
                String description = "English".equals(locate) ? updateInfo.getAPK().getChangelogEn() : updateInfo.getAPK().getChangelogCn();
                runOnUiThread(() -> attemptUpdate(url, versionCode, versionName, size, description));
            }
        });
    }

    private void attemptUpdate(String uri, int versionCode, String versionName, String size, String description) {
        int versionCodeLocal = ApkUtil.getVersionCode(this);
        if (versionCodeLocal >= versionCode) {
            return;
        }

        String url;
        if (uri.startsWith("https")) {
            url = uri;
        } else {
            url = ONE_KEY_WEBSITE + uri;
        }
        UpdateConfiguration configuration = new UpdateConfiguration()
                .setEnableLog(true)
                .setJumpInstallPage(true)
                .setShowNotification(true)
                .setForcedUpgrade(false)
                .setOnDownloadListener(this);

        manager = DownloadManager.getInstance(this);
        manager.setApkName("oneKey.apk")
                .setApkUrl(url)
                .setSmallIcon(R.drawable.logo_square)
                .setConfiguration(configuration);
        updateDialog = new AppUpdateDialog(manager, versionName, description);
        updateDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void start() {
        updateDialog.progressBar.setIndeterminate(false);
    }

    @Override
    public void downloading(int max, int progress) {
        updateDialog.progressBar.setProgress((int) (((float) progress / max) * 100));
    }

    @Override
    public void done(File apk) {
        updateDialog.dismiss();
        manager.release();
    }

    @Override
    public void cancel() {
    }

    @Override
    public void error(Exception e) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        refreshView();
    }
}
package org.haobtc.onekey.onekeys;

import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.ApkUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.HardwareCallbackHandler;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.onekeys.homepage.DiscoverFragment;
import org.haobtc.onekey.onekeys.homepage.MindFragment;
import org.haobtc.onekey.onekeys.homepage.WalletFragment;
import org.haobtc.onekey.ui.dialog.AppUpdateDialog;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_one:
                startFragment(new WalletFragment());
                break;
            case R.id.radio_two:
                startFragment(new DiscoverFragment());
                break;
            case R.id.radio_three:
                startFragment(new MindFragment());
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onCreateWalletSuccess(CreateSuccessEvent event) {
        PyEnv.loadLocalWalletInfo(this);
    }

    /**
     * init
     */
    @Override
    public void init() {
        HardwareCallbackHandler callbackHandler = HardwareCallbackHandler.getInstance(this);
        PyEnv.setHandle(callbackHandler);
        BleManager.getInstance(this);
        getUpdateInfo();
        // 默认让主页被选中
        startFragment(new WalletFragment());
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
    private void getUpdateInfo() {
        // version_testnet.json version_regtest.json
        String appId = BuildConfig.APPLICATION_ID;
        String urlPrefix = "https://key.bixin.com/";
        String url = "";
        if (appId.endsWith("mainnet")) {
            url = urlPrefix + "version.json";
        } else if (appId.endsWith("testnet")) {
            url = urlPrefix + "version_testnet.json";
        } else if (appId.endsWith("regnet")) {
            url = urlPrefix + "version_regtest.json";
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
//        Toast.makeText(this, getString(R.string.updating_dialog), Toast.LENGTH_LONG).show();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("获取更新信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
                String locate = preferences.getString("language", "");

                String info = response.body().string();
                try {
                    UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
                    String url = updateInfo.getAPK().getUrl();
                    String versionName = updateInfo.getAPK().getVersionName();
                    int versionCode = updateInfo.getAPK().getVersionCode();
                    String size = updateInfo.getAPK().getSize().replace("M", "");
                    String description = "English".equals(locate) ? updateInfo.getAPK().getChangelogEn() : updateInfo.getAPK().getChangelogCn();
                    runOnUiThread(() -> attemptUpdate(url, versionCode, versionName, size, description));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void attemptUpdate(String uri,  int versionCode, String versionName, String size, String description) {
        int versionCodeLocal  = ApkUtil.getVersionCode(this);
        if (versionCodeLocal >= versionCode) {
//            showToast("当前是最新版本");
            return;
        }

        String url;
        if (uri.startsWith("https")) {
            url = uri;
        } else {
            url = "https://key.bixin.com/" + uri;
        }
        UpdateConfiguration configuration = new UpdateConfiguration()
                .setEnableLog(true)
                .setJumpInstallPage(true)
                .setShowNotification(true)
                .setShowBgdToast(true)
                .setForcedUpgrade(false)
                .setOnDownloadListener(this);

        manager = DownloadManager.getInstance(this);
        manager.setApkName("oneKey.apk")
                .setApkUrl(url)
                .setSmallIcon(R.drawable.logo_square)
                .setConfiguration(configuration)
                .download();
        updateDialog = new AppUpdateDialog(manager, versionName, description);
        updateDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void start() {
        updateDialog.progressBar.setIndeterminate(false);
    }

    @Override
    public void downloading(int max, int progress) {
        updateDialog.progressBar.setProgress((int)((float)progress/max)*100);
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
}
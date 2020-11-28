package org.haobtc.onekey.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.ApkUtil;

import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.ui.dialog.AppUpdateDialog;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liyan
 */
public class AboutActivity extends BaseActivity implements OnDownloadListener {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.attempt_update)
    RelativeLayout update;
    @BindView(R.id.update_version)
    TextView updateVersion;
    @BindView(R.id.tet_s5)
    TextView tetS5;
    private DownloadManager manager;
    private AppUpdateDialog updateDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        String versionName = ApkUtil.getVersionName(this);
        updateVersion.setText(String.format("V%s", versionName));
    }

    @SingleClick(value = 5000)
    @OnClick({R.id.img_back, R.id.attempt_update, R.id.tet_s5})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.attempt_update:
                getUpdateInfo();
                break;
            case R.id.tet_s5:
//                mIntent(UserAgreementActivity.class);
                Intent intent = new Intent(AboutActivity.this, CheckChainDetailWebActivity.class);
                intent.putExtra("loadWhere", "userAgreement");
                intent.putExtra("loadUrl","https://onekey.zendesk.com/hc/articles/360002014776");
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
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
        Toast.makeText(this, getString(R.string.updating_dialog), Toast.LENGTH_LONG).show();
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
                UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
                String url = updateInfo.getAPK().getUrl();
                String versionName = updateInfo.getAPK().getVersionName();
                int versionCode = updateInfo.getAPK().getVersionCode();
                String size = updateInfo.getAPK().getSize().replace("M", "");
                String description = "English".equals(locate) ? updateInfo.getAPK().getChangelogEn() : updateInfo.getAPK().getChangelogCn();
                runOnUiThread(() -> attemptUpdate(url, versionCode, versionName, size, description));
            }
        });
    }

    private void attemptUpdate(String uri,  int versionCode, String versionName, String size, String description) {
        int versionCodeLocal  = ApkUtil.getVersionCode(AboutActivity.this);
        if (versionCodeLocal >= versionCode) {
            mToast("当前是最新版本");
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

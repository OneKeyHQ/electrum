package org.haobtc.onekey.business.update;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.ApkUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.logger.Logger;

import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.business.language.LanguageManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.dialog.AppUpdateDialog;

import java.io.File;
import java.io.IOException;

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
 * 检查更新，并自动更新
 *
 * @author Onekey@QuincySx
 * @create 2021-01-20 5:21 PM
 */
public class AutoCheckUpdate implements OnDownloadListener {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final DownloadManager manager;
    private AppUpdateDialog updateDialog;
    private final Context mContext;
    private final UpdateConfiguration mConfiguration;
    private volatile static AutoCheckUpdate AUTOCHECKUPDATE;

    public static AutoCheckUpdate getInstance(Context context) {
        if (AUTOCHECKUPDATE == null) {
            synchronized (AutoCheckUpdate.class) {
                if (AUTOCHECKUPDATE == null) {
                    AUTOCHECKUPDATE = new AutoCheckUpdate(context.getApplicationContext());
                }
            }
        }
        return AUTOCHECKUPDATE;
    }

    private AutoCheckUpdate(Context context) {
        mContext = context;
        manager = DownloadManager.getInstance(mContext);
        mConfiguration = new UpdateConfiguration()
                .setEnableLog(true)
                .setJumpInstallPage(true)
                .setShowNotification(true)
                .setForcedUpgrade(false)
                .setOnDownloadListener(this);
    }

    public void checkUpdate(@NonNull FragmentManager manager, boolean showHint) {
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
        // Toast.makeText(this, getString(R.string.updating_dialog), Toast.LENGTH_LONG).show();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Logger.e("获取更新信息失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                if (response.code() != HTTP_OK) {
                    Logger.e(response.body().string());
                    return;
                }
                String locate = LanguageManager.getInstance().getLocalLanguage(mContext);
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
                String oldInfo = PreferencesManager.get(mContext, "Preferences", Constant.UPGRADE_INFO, "").toString();
                if (!Strings.isNullOrEmpty(oldInfo)) {
                    UpdateInfo old = UpdateInfo.objectFromData(oldInfo);
                    if (!old.getStm32().getUrl().equals(updateInfo.getStm32().getUrl())) {
                        updateInfo.getStm32().setNeedUpload(true);
                    }
                    if (!old.getNrf().getUrl().equals(updateInfo.getNrf().getUrl())) {
                        updateInfo.getNrf().setNeedUpload(true);
                    }
                }
                PreferencesManager.put(mContext, "Preferences", Constant.UPGRADE_INFO, updateInfo.toString());
                String url = updateInfo.getAPK().getUrl();
                String versionName = updateInfo.getAPK().getVersionName();
                int versionCode = updateInfo.getAPK().getVersionCode();
                String size = updateInfo.getAPK().getSize().replace("M", "");
                String description = "English".equals(locate) ? updateInfo.getAPK().getChangelogEn() : updateInfo.getAPK().getChangelogCn();
                mHandler.post(() -> attemptUpdate(manager, url, versionCode, versionName, size, description, showHint));
            }
        });
    }

    private void attemptUpdate(@NonNull FragmentManager fragmentManager, String uri, int versionCode, String versionName, String size, String description, boolean showHint) {
        int versionCodeLocal = ApkUtil.getVersionCode(mContext);
        if (versionCodeLocal >= versionCode) {
            if (showHint) {
                Toast.makeText(mContext, R.string.is_new, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String url;
        if (uri.startsWith("https")) {
            url = uri;
        } else {
            url = ONE_KEY_WEBSITE + uri;
        }
        if (!manager.isDownloading()) {
            manager.setApkName("oneKey.apk")
                    .setApkUrl(url)
                    .setSmallIcon(R.drawable.logo_square)
                    .setConfiguration(mConfiguration);
        }
        if (updateDialog != null) {
            updateDialog.dismiss();
        }
        updateDialog = new AppUpdateDialog(manager, versionName, description);
        try{
            updateDialog.show(fragmentManager, "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (updateDialog.progressBar != null) {
            updateDialog.progressBar.setIndeterminate(false);
        }
    }

    @Override
    public void downloading(int max, int progress) {
        if (updateDialog.progressBar != null) {
            updateDialog.progressBar.setProgress((int) (((float) progress / max) * 100));
        }
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

    public void onDestroy() {
        try {
            if (updateDialog != null) {
                updateDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

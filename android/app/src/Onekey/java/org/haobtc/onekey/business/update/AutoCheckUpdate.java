package org.haobtc.onekey.business.update;

import static org.haobtc.onekey.constant.Constant.ONE_KEY_WEBSITE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.ApkUtil;
import java.io.File;
import org.haobtc.onekey.R;
import org.haobtc.onekey.business.language.LanguageManager;
import org.haobtc.onekey.business.version.VersionManager;
import org.haobtc.onekey.ui.dialog.AppUpdateDialog;

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
    private static volatile AutoCheckUpdate AUTOCHECKUPDATE;
    private VersionManager mVersionManager;

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
        mConfiguration =
                new UpdateConfiguration()
                        .setEnableLog(true)
                        .setJumpInstallPage(true)
                        .setShowNotification(true)
                        .setForcedUpgrade(false)
                        .setOnDownloadListener(this);
        mVersionManager = new VersionManager();
    }

    public void checkUpdate(@NonNull FragmentManager manager, boolean showHint) {
        mVersionManager.getVersionData(
                updateInfo -> {
                    String locate = LanguageManager.getInstance().getLocalLanguage(mContext);

                    String url = updateInfo.getAPK().getUrl();
                    String versionName = updateInfo.getAPK().getVersionName();
                    int versionCode = updateInfo.getAPK().getVersionCode();
                    String size = updateInfo.getAPK().getSize().replace("M", "");
                    String description =
                            "English".equals(locate)
                                    ? updateInfo.getAPK().getChangelogEn()
                                    : updateInfo.getAPK().getChangelogCn();
                    mVersionManager.saveVersionInfo(mContext, updateInfo);
                    mHandler.post(
                            () ->
                                    attemptUpdate(
                                            manager,
                                            url,
                                            versionCode,
                                            versionName,
                                            size,
                                            description,
                                            showHint));
                    return null;
                });
    }

    private void attemptUpdate(
            @NonNull FragmentManager fragmentManager,
            String uri,
            int versionCode,
            String versionName,
            String size,
            String description,
            boolean showHint) {
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
        try {
            updateDialog.show(fragmentManager, "");
        } catch (Exception e) {
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
    public void cancel() {}

    @Override
    public void error(Exception e) {}

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

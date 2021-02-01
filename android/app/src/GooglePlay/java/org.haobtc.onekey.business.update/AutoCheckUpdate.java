package org.haobtc.onekey.business.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import org.haobtc.onekey.business.version.VersionManager;

/**
 * 检查更新跳转至 Google Play
 *
 * @author Onekey@QuincySx
 * @create 2021-01-20 5:21 PM
 */
public class AutoCheckUpdate {
    private static final String GOOGLE_PLAY = "com.android.vending";
    private static volatile AutoCheckUpdate AUTOCHECKUPDATE;
    private VersionManager mVersionManager;

    private Context mContext;

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
        mVersionManager = new VersionManager();
    }

    private String getPackageName() {
        return "com.bixin.wallet.mainnet";
    }

    public void checkUpdate(@NonNull FragmentManager manager, boolean showHint) {
        mVersionManager.getVersionData(
                updateInfo -> {
                    mVersionManager.saveVersionInfo(mContext, updateInfo);
                    return null;
                });
        if (!showHint) {
            return;
        }
        if (!jumpGoogleMarket()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }
    }

    private boolean jumpGoogleMarket() {
        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
        intent.addCategory("android.intent.category.APP_MARKET");
        intent.setPackage(GOOGLE_PLAY);
        if (intent.resolveActivityInfo(
                        mContext.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY)
                != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    public void onDestroy() {}
}

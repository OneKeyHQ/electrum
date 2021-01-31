package org.haobtc.onekey.business.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.google.common.base.Strings;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;

/**
 * 检查更新跳转至 Google Play
 *
 * @author Onekey@QuincySx
 * @create 2021-01-20 5:21 PM
 */
public class AutoCheckUpdate {
    private static final String GOOGLE_PLAY = "com.android.vending";
    private static volatile AutoCheckUpdate AUTOCHECKUPDATE;

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
    }

    private String getPackageName() {
        return "com.bixin.wallet.mainnet";
    }

    public void checkUpdate(@NonNull FragmentManager manager, boolean showHint) {
        // version_testnet.json version_regtest.json
        String appId = BuildConfig.APPLICATION_ID;
        String urlPrefix = Constant.ONE_KEY_WEBSITE;
        String url = "";
        if (appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_0)) {
            url = urlPrefix + "version.json";
        } else if (appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_2)) {
            url = urlPrefix + "version_testnet.json";
        } else if (appId.endsWith(Constant.BITCOIN_NETWORK_TYPE_1)) {
            url = urlPrefix + "version_regtest.json";
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        // Toast.makeText(this, getString(R.string.updating_dialog), Toast.LENGTH_LONG).show();
        call.enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Logger.e("获取更新信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        assert response.body() != null;
                        if (response.code() != HttpURLConnection.HTTP_OK) {
                            Logger.e(response.body().string());
                            return;
                        }
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
                        String oldInfo =
                                PreferencesManager.get(
                                                mContext, "Preferences", Constant.UPGRADE_INFO, "")
                                        .toString();
                        if (!Strings.isNullOrEmpty(oldInfo)) {
                            UpdateInfo old = UpdateInfo.objectFromData(oldInfo);
                            if (!old.getStm32().getUrl().equals(updateInfo.getStm32().getUrl())) {
                                updateInfo.getStm32().setNeedUpload(true);
                            }
                            if (!old.getNrf().getUrl().equals(updateInfo.getNrf().getUrl())) {
                                updateInfo.getNrf().setNeedUpload(true);
                            }
                        }
                        PreferencesManager.put(
                                mContext,
                                "Preferences",
                                Constant.UPGRADE_INFO,
                                updateInfo.toString());
                    }
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

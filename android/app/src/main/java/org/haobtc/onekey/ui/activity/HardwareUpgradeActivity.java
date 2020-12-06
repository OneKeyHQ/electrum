package org.haobtc.onekey.ui.activity;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.dfu.service.DfuService;
import org.haobtc.onekey.event.ExceptionEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.event.UpdateEvent;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.fragment.BleDeviceRecyclerViewAdapter;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.HardwareUpgradeFragment;
import org.haobtc.onekey.ui.fragment.HardwareUpgradingFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author liyan
 * @date 12/3/20
 */

public class HardwareUpgradeActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    public static String currentFirmwareVersion;
    public static String currentNrfVersion;
    public static String newFirmwareVersion;
    public static String newNrfVersion;
    public static String firmwareChangelog;
    public static String nrfChangelog;
    private String firmwareUrl;
    private String nrfUrl;
    private String mac;
    private String label;
    private String bleName;
    private HardwareUpgradeFragment hardwareUpgradeFragment;
    protected static HardwareUpgradingFragment hardwareUpgradingFragment;
    private String cacheDir;
    private MyTask task;
    /**
     * dfu callback
     * */
    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDfuCompleted(@NonNull String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            cancelDfu();
            SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
            SharedPreferences upgradeInfo = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            String info = upgradeInfo.getString("upgrade_info", "");
            // 更新本地信息
//            if (deviceAddress.equals(BleDeviceRecyclerViewAdapter.mBleDevice.getBleAddress())) {
//                String features = devices.getString(BleDeviceRecyclerViewAdapter.mBleDevice.getBleName(), "");
//                if (!Strings.isNullOrEmpty(features)) {
//                    HardwareFeatures features1 = HardwareFeatures.objectFromData(features);
//                    features1.setBleVer(UpdateInfo.objectFromData(info).getNrf().getVersion());
//                    devices.edit().putString(BleDeviceRecyclerViewAdapter.mBleDevice.getBleName(), features1.toString()).apply();
//                }
//            }
        }

        @Override
        public void onDfuProcessStarted(@NonNull String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
        }


        @Override
        public void onDfuProcessStarting(@NonNull String deviceAddress) {
            super.onDfuProcessStarting(deviceAddress);
        }

        @Override
        public void onDfuAborted(@NonNull String deviceAddress) {
            super.onDfuAborted(deviceAddress);
            EventBus.getDefault().post(new ExceptionEvent("abort"));
        }

        @Override
        public void onProgressChanged(@NonNull String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            if (hardwareUpgradingFragment.getProgressBar().isIndeterminate()) {
                hardwareUpgradingFragment.getProgressBar().setIndeterminate(false);
            }
            hardwareUpgradingFragment.getProgressBar().setProgress(percent);
        }

        @Override
        public void onError(@NonNull String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
//            Ble.getInstance().disconnectAll();
            if ("DFU DEVICE NOT BONDED".equals(message)) {
                BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress).createBond();
            } else {
                EventBus.getDefault().post(new ExceptionEvent(message));
            }
        }

        @Override
        public void onDeviceConnected(@NonNull String deviceAddress) {
            super.onDeviceConnected(deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(@NonNull String deviceAddress) {
            super.onEnablingDfuMode(deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(@NonNull String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
        }
    };
    private void cancelDfu() {
        final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_ABORT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pauseAction);
    }

    static boolean updateFiles(String path, String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        File file = new File(path);
        byte[] buf = new byte[2048];
        int len = 0;
        try (Response response = call.execute(); InputStream is = response.body().byteStream(); FileOutputStream fos = new FileOutputStream(file)) {
            if (response.code() != HTTP_OK) {
                return false;
            }
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.verson_update);
        dealBundle(Objects.requireNonNull(getIntent().getExtras()));
        hardwareUpgradeFragment = new HardwareUpgradeFragment();
        startFragment(hardwareUpgradeFragment);
    }

    private void dealBundle(Bundle bundle) {
        currentFirmwareVersion = bundle.getString(Constant.TAG_FIRMWARE_VERSION);
        currentNrfVersion = bundle.getString(Constant.TAG_NRF_VERSION);
        newFirmwareVersion = bundle.getString(Constant.TAG_FIRMWARE_VERSION_NEW);
        newNrfVersion = bundle.getString(Constant.TAG_NRF_VERSION_NEW);
        firmwareChangelog = bundle.getString(Constant.TAG_FIRMWARE_UPDATE_DES);
        nrfChangelog = bundle.getString(Constant.TAG_NRF_UPDATE_DES);
        firmwareUrl = bundle.getString(Constant.TAG_FIRMWARE_DOWNLOAD_URL);
        nrfUrl = bundle.getString(Constant.TAG_NRF_DOWNLOAD_URL);
        mac = bundle.getString(Constant.BLE_MAC);
        label = bundle.getString(Constant.TAG_LABEL);
        bleName = bundle.getString(Constant.TAG_BLE_NAME);
        cacheDir = getExternalCacheDir().getPath();
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }
    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
        finish();
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadyBle(NotifySuccessfulEvent event) {
        hardwareUpgradingFragment = new HardwareUpgradingFragment();
        String path = String.format("%s%s%s%s", cacheDir, Constant.UPDATE_FILE_NAME, newNrfVersion, Constant.FIRMWARE_UPDATE_FILE_SUFFIX);
        new MyTask().execute(path, firmwareUrl);
        startFragment(hardwareUpgradingFragment);
    }

    private void initBle() {
        BleManager bleManager = BleManager.getInstance(this);
        bleManager.initBle();
        bleManager.connDevByMac(mac);
    }

    @Subscribe
    public void onUpdateEvent(UpdateEvent event) {
        switch (event.getType()) {
            case UpdateEvent.FIRMWARE:
                initBle();
                break;
            case UpdateEvent.BLE:
                String path = String.format("%s%s%s%s", cacheDir, Constant.UPDATE_FILE_NAME, newFirmwareVersion, Constant.NRF_UPDATE_FILE_SUFFIX);
                dfu(path, nrfUrl);
                break;
            default:
        }

    }

    private void showPromptMessage(@StringRes int id) {
        runOnUiThread(() -> {
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        });
    }

    private void dfu(String path, String url) {
        File file = new File(path);
        if (file.exists()) {
            beginDfu(path);
        } else {
            if (updateFiles(path, url)) {
                beginDfu(path);
            }
        }
    }

    private void beginDfu(String path) {
        final DfuServiceInitiator starter = new DfuServiceInitiator(mac);
        starter.setDeviceName(label);
        starter.setKeepBond(true);
        /*
           Call this method to put Nordic nrf52832 into bootloader mode
        */
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        starter.setZip(null, path);
        DfuServiceInitiator.createDfuNotificationChannel(this);
        starter.start(this, DfuService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hardwareUpgradingFragment = null;
        task = null;
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    public static class MyTask extends AsyncTask<String, Object, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            String path = params[0];
            String url = params[1];
            File file = new File(path);
            if (file.exists()) {
                doUpdate(path);
            } else {
                boolean uploadSuccessful = updateFiles(path, url);
                if (uploadSuccessful) {
                    doUpdate(path);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... progresses) {
            if (hardwareUpgradingFragment.getProgressBar().isIndeterminate()) {
                hardwareUpgradingFragment.getProgressBar().setIndeterminate(false);
            }
            hardwareUpgradingFragment.getProgressBar().setProgress(Integer.parseInt(((progresses[0]).toString())));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("升级成功");
        }

        @Override
        protected void onCancelled() {
            PyEnv.cancelAll();
        }
        // 升级stm32固件
        private void doUpdate(String path) {
            File file = new File(path);
            PyEnv.setProgressReporter(this);
            PyResponse<Void> response = PyEnv.firmwareUpdate(path);
            if (!Strings.isNullOrEmpty(response.getErrors())) {
                if (HardWareExceptions.FILE_FORMAT_ERROR.getMessage().equals(response.getErrors())) {
                    Optional.ofNullable(file).ifPresent(File::delete);
                }
                // clear state
                PyEnv.clearUpdateStatus();
                cancel(true);
            }
        }
    }
}

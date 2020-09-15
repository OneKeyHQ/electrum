package org.haobtc.wallet.activities.settings;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.bean.HardwareFeatures;
import org.haobtc.wallet.bean.UpdateInfo;
import org.haobtc.wallet.dfu.service.DfuService;
import org.haobtc.wallet.event.DfuEvent;
import org.haobtc.wallet.event.ExceptionEvent;
import org.haobtc.wallet.event.ExecuteEvent;
import org.haobtc.wallet.event.ExitEvent;
import org.haobtc.wallet.event.FinishEvent;
import org.haobtc.wallet.exception.BixinExceptions;
import org.haobtc.wallet.fragment.BleDeviceRecyclerViewAdapter;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.Global;
import org.haobtc.wallet.utils.NfcUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.haobtc.wallet.activities.service.CommunicationModeSelector.ble;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.bleTransport;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.executorService;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.features;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfcHandler;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.protocol;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.way;
import static org.haobtc.wallet.activities.settings.VersionUpgradeActivity.isDIY;


public class UpgradeBixinKEYActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_test)
    TextView tetTest;
    @BindView(R.id.progressUpgrade)
    ProgressBar progressUpgrade;
    @BindView(R.id.tetUpgradeTest)
    TextView tetUpgradeTest;
    @BindView(R.id.tetUpgradeNum)
    TextView tetUpgradeNum;
    @BindView(R.id.imgdhksjks)
    ImageView imgdhksjks;
    private MyTask mTask;
    private int tag;
    private String newNrfVersion;
    private String newLoaderVersion;
    private boolean isNew;
    private boolean isStm32Update;
    private boolean isNrfUpdate;
    private SharedPreferences preferences;
    private UpdateInfo updateInfo;
    private boolean isNeedBackup;
    private boolean uploadFileFailed;


    public static final String TAG = UpgradeBixinKEYActivity.class.getSimpleName();


    @SuppressLint("StaticFieldLeak")
    public class MyTask extends AsyncTask<String, Object, Void> {
        @Override
        protected void onPreExecute() {
            progressUpgrade.setIndeterminate(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (!isDIY) {
                    String nrfVersion = Optional.ofNullable(features.getBleVer()).orElse("0");
                    String loaderVersion = String.format("%s.%s.%s", features.getMajorVersion(), features.getMinorVersion(), features.getPatchVersion());
                    switch (tag) {
                        case 1:
                            if (features.isInitialized() && features.isNeedsBackup()) {
                                isNeedBackup = true;
                                cancel(true);
                                break;
                            }
                            assert newLoaderVersion != null;
                            if (newLoaderVersion.compareTo(loaderVersion) <= 0 && !features.isBootloaderMode()) {
                                isNew = true;
                                cancel(true);
                            } else {
                                File file = new File(String.format("%s/bixin.bin", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                                boolean success = true;
                                if (isStm32Update || !file.exists()) {
                                    runOnUiThread(() -> tetUpgradeTest.setText("正在下载升级文件"));
                                    success = updateFiles(Objects.requireNonNull(getIntent().getExtras()).getString("stm32_url"));
                                }
                                if (success) {
                                    showPromote();
                                    doUpdate(params[0]);
                                }
                            }
                            break;
                        case 2:
                            assert newNrfVersion != null;
                            if (newNrfVersion.compareTo(nrfVersion) <= 0 && !features.isBootloaderMode()) {
                                isNew = true;
                                cancel(true);
                            } else {
                                File file = new File(String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                                boolean success = true;
                                if (isNrfUpdate || !file.exists()) {
                                    runOnUiThread(() -> tetUpgradeTest.setText("正在下载升级文件"));
                                    success = updateFiles(Objects.requireNonNull(getIntent().getExtras()).getString("nrf_url"));
                                }
                                if (success) {
                                    showPromote();
                                    doUpdate(params[0]);
                                }
                            }
                            break;
                        default:
                    }
                } else {
                    if (features.isInitialized() && features.isNeedsBackup() && tag ==1) {
                        isNeedBackup = true;
                        cancel(true);
                    } else {
                        runOnUiThread(() -> tetUpgradeTest.setText("正在升级至自定义版本"));
                        doUpdate(params[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... progresses) {
            progressUpgrade.setIndeterminate(false);
            progressUpgrade.setProgress(Integer.parseInt(((progresses[0]).toString())));
            tetUpgradeNum.setText(String.format("%s%%", String.valueOf(progresses[0])));

        }

        private void doUpdate(String path) {
            PyObject protocol = Global.py.getModule("trezorlib.transport.protocol");
            File file = null;
            try {
                protocol.put("PROCESS_REPORTER", this);
                switch (tag) {
                    case 1:
                        if (!isDIY) {
                            file = new File(String.format("%s/bixin.bin", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                            if (!file.exists()) {
                                showPromptMessage(R.string.update_file_not_exist);
                                cancel(true);
                                return;
                            }
                        } else if (!VersionUpgradeActivity.filePath.endsWith(".bin")) {
                            showPromptMessage(R.string.update_file_format_error);
                            cancel(true);
                            return;
                        }
                        Daemon.commands.callAttr("firmware_update", !isDIY ? String.format("%s/bixin.bin", getExternalCacheDir().getPath()) : VersionUpgradeActivity.filePath, path);
                        break;
                    case 2:
                        if (!isDIY) {
                            file = new File(String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                            if (!file.exists()) {
                                showPromptMessage(R.string.update_file_not_exist);
                                cancel(true);
                                return;
                            }
                        } else if (!VersionUpgradeActivity.filePath.endsWith(".zip")) {
                            showPromptMessage(R.string.update_file_format_error);
                            cancel(true);
                            return;
                        }
                        Daemon.commands.callAttr("firmware_update", !isDIY ? String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()) : VersionUpgradeActivity.filePath, path, "ble_ota");
                        break;
                    default:
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (BixinExceptions.FILE_FORMAT_ERROR.getMessage().equals(e.getMessage())) {
                    Optional.ofNullable(file).ifPresent(File::delete);
                }
                // clear state
                protocol.put("HTTP", false);
                protocol.put("OFFSET", 0);
                protocol.put("PROCESS_REPORTER", null);
                cancel(true);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mIntent(UpgradeFinishedActivity.class);
            finishAffinity();
        }

        @Override
        protected void onCancelled() {
            tetUpgradeTest.setText(getString(R.string.Cancelled));
            EventBus.getDefault().post(new ExitEvent());
            if (isNew) {
                isNew = false;
                showPromptMessage(R.string.is_new);
            } else if (isNeedBackup) {
                isNeedBackup = false;
                showPromptMessage(R.string.need_backup);
            } else if (uploadFileFailed) {
                uploadFileFailed = false;
                showPromptMessage(R.string.upload_file_failed);
            } else {
                showPromptMessage(R.string.update_failed);
            }
            ble.put("IS_CANCEL", true);
            nfc.put("IS_CANCEL", true);
            protocol.callAttr("notify");
            finish();
        }

    }

    private boolean updateFiles(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        String name = "";
        if (tag == 1) {
            name = "bixin.bin";
        } else {
            name = "bixin.zip";
        }
        File file = new File(String.format("%s/%s", Objects.requireNonNull(getExternalCacheDir()).getPath(), name));
        byte[] buf = new byte[2048];
        int len = 0;
        try (Response response = call.execute(); InputStream is = response.body().byteStream(); FileOutputStream fos = new FileOutputStream(file)) {
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if (mTask != null) {
                uploadFileFailed = true;
                mTask.cancel(true);
            }
            return false;
        }
        showPromote();
        if (tag == 2 && "ble".equals(way)) {
            dfu();
        }
        return true;
    }

    private void showPromote() {
        if (tag == 1) {
            updateInfo.getStm32().setNeedUpload(false);
            runOnUiThread(() -> tetUpgradeTest.setText("正在升级至 v" + newLoaderVersion));
        } else {
            updateInfo.getNrf().setNeedUpload(false);
            runOnUiThread(() -> tetUpgradeTest.setText("正在升级至 v" + newNrfVersion));
        }
        preferences.edit().putString("update_info", updateInfo.toString()).apply();
    }

    private void showPromptMessage(@StringRes int id) {
        UpgradeBixinKEYActivity.this.runOnUiThread(() -> {
            Toast.makeText(UpgradeBixinKEYActivity.this, id, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_upgrade_bixin_key;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        NfcUtils.nfc(this, false);
        tag = getIntent().getIntExtra("tag", 1);
        newNrfVersion = Objects.requireNonNull(getIntent().getExtras()).getString("nrf_version");
        newLoaderVersion = getIntent().getExtras().getString("stm32_version");
        if (!isDIY) {
            switch (tag) {
                case 1:
                    tetUpgradeTest.setText("V" + newLoaderVersion);
                    break;
                case 2:
                    tetUpgradeTest.setText("V" + newNrfVersion);
            }
        }
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String info = preferences.getString("upgrade_info", null);
        Optional.ofNullable(info).ifPresent((infos) -> {
            updateInfo = UpdateInfo.objectFromData(infos);
            isStm32Update = updateInfo.getStm32().isNeedUpload();
            isNrfUpdate = updateInfo.getNrf().isNeedUpload();
        });
    }

    @Override
    public void initData() {
        mTask = new MyTask();
        if ("bluetooth".equals(getIntent().getStringExtra("way"))) {
            mTask.execute("bluetooth");
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (VersionUpgradeActivity.UPDATE_PROCESS.equals(intent.getAction())) {
                int percent = intent.getIntExtra("process", 0);
                progressUpgrade.setIndeterminate(false);
                progressUpgrade.setProgress(percent);
                tetUpgradeNum.setText(String.format(Locale.ENGLISH, "%d%%", percent));
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeTask(ExecuteEvent executeEvent) {
        EventBus.getDefault().removeStickyEvent(ExecuteEvent.class);
        if ("usb".equals(way)) {
           mTask.execute("android_usb");
        } else {
            mTask.execute("nfc");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDfuException(ExceptionEvent event) {
        tetUpgradeTest.setText(getString(R.string.Cancelled));
        showPromptMessage(R.string.update_failed);
        finish();
    }
    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
            if (mTask != null) {
                nfcTransport.put("ENABLED", false);
                bleTransport.put("ENABLED", false);
                protocol.callAttr("notify");
                mTask.cancel(true);
            }
           cancelDfu();
        }
    }
    private void cancelDfu() {
        final Intent pauseAction = new Intent(DfuBaseService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuBaseService.EXTRA_ACTION, DfuBaseService.ACTION_ABORT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pauseAction);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDfu(DfuEvent event) {
        if (event.getType() == DfuEvent.DFU_SHOW_PROCESS) {
            EventBus.getDefault().removeStickyEvent(DfuEvent.class);
            if (!isDIY) {
                SharedPreferences sharedPreferences = getSharedPreferences("devices", MODE_PRIVATE);
                String device = sharedPreferences.getString(BleDeviceRecyclerViewAdapter.mBleDevice.getBleName(), "");
                String nrfVersion;
                if (Strings.isNullOrEmpty(device)) {
                   nrfVersion = "0";
                } else {
                    HardwareFeatures features = new Gson().fromJson(device, HardwareFeatures.class);
                    nrfVersion = features.getBleVer();
                }

                if (newNrfVersion.compareTo(nrfVersion) <= 0 && !features.isBootloaderMode()) {
                    EventBus.getDefault().post(new ExitEvent());
                    showPromptMessage(R.string.is_new);
                    finish();
                } else {
                    File file = new File(String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                    if (isNrfUpdate || !file.exists()) {
                        runOnUiThread(() -> tetUpgradeTest.setText("正在下载升级文件"));
                        executorService.execute(() -> updateFiles(Objects.requireNonNull(getIntent().getExtras()).getString("nrf_url")));
                    } else {
                        dfu();
                    }
                }
            } else {
                runOnUiThread(() -> tetUpgradeTest.setText("正在升级至自定义版本"));
                dfu();
            }
        }
    }
    private void dfu() {
        BleDevice device = BleDeviceRecyclerViewAdapter.mBleDevice;
            final DfuServiceInitiator starter = new DfuServiceInitiator(device.getBleAddress());
            starter.setDeviceName(device.getBleName());
            starter.setKeepBond(true);
        /*
           Call this method to put Nordic nrf52832 into bootloader mode
        */
            starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
            if (!isDIY) {
                File file = new File(String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()));
                if (!file.exists()) {
                    EventBus.getDefault().post(new ExitEvent());
                    Toast.makeText(this, R.string.update_file_not_exist, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            } else if (!VersionUpgradeActivity.filePath.endsWith(".zip")) {
                EventBus.getDefault().post(new ExitEvent());
                Toast.makeText(this, R.string.update_file_format_error, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            starter.setZip(null, !isDIY ? String.format("%s/bixin.zip", Objects.requireNonNull(getExternalCacheDir()).getPath()) : VersionUpgradeActivity.filePath);
            DfuServiceInitiator.createDfuNotificationChannel(this);
            starter.start(this, DfuService.class);
    }
    @Subscribe
    public void doDFU(DfuEvent event) {
        if (event.getType() ==3) {
            EventBus.getDefault().removeStickyEvent(DfuEvent.class);
            dfu();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcHandler.put("device", tags);
            protocol.callAttr("notify");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
//            // enable nfc discovery for the app
//            Log.i("NFC", "为本App启用NFC感应");
//            NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
//        }
        if (tag == 2) {
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(VersionUpgradeActivity.UPDATE_PROCESS));
            progressUpgrade.setIndeterminate(true);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
//            // disable nfc discovery for the app
//            NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
//            Log.i("NFC", "禁用本App的NFC感应");
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (tag == 2) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        mTask = null;
    }

//    @Subscribe()
//    public void onFinish(ExitEvent event) {
//        finish();
//    }
}

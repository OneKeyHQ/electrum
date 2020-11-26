package org.haobtc.onekey.activities.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.usb.UsbDevice;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.chaquo.python.PyObject;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.BackupWaySelector;
import org.haobtc.onekey.activities.PinSettingActivity;
import org.haobtc.onekey.activities.ReceivedPageActivity;
import org.haobtc.onekey.activities.SendOne2ManyMainPageActivity;
import org.haobtc.onekey.activities.SendOne2OneMainPageActivity;
import org.haobtc.onekey.activities.SettingActivity;
import org.haobtc.onekey.activities.TransactionDetailsActivity;
import org.haobtc.onekey.activities.VerificationKEYActivity;
import org.haobtc.onekey.activities.WalletUnActivatedActivity;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.jointwallet.MultiSigWalletCreator;
import org.haobtc.onekey.activities.personalwallet.ChooseHistryWalletActivity;
import org.haobtc.onekey.activities.personalwallet.ImportHistoryWalletActivity;
import org.haobtc.onekey.activities.personalwallet.PersonalMultiSigWalletCreator;
import org.haobtc.onekey.activities.personalwallet.SingleSigWalletCreator;
import org.haobtc.onekey.activities.personalwallet.hidewallet.HideWalletSetPassActivity;
import org.haobtc.onekey.activities.settings.CheckXpubResultActivity;
import org.haobtc.onekey.activities.settings.ConfidentialPaymentSettings;
import org.haobtc.onekey.activities.settings.EditWhiteListActivity;
import org.haobtc.onekey.activities.settings.FixBixinkeyNameActivity;
import org.haobtc.onekey.activities.settings.HardwareDetailsActivity;
import org.haobtc.onekey.activities.settings.SetShutdownTimeActivity;
import org.haobtc.onekey.activities.settings.UpgradeBixinKEYActivity;
import org.haobtc.onekey.activities.settings.VersionUpgradeActivity;
import org.haobtc.onekey.activities.settings.recovery_set.BackupMessageActivity;
import org.haobtc.onekey.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.onekey.activities.settings.recovery_set.FixHardwareLanguageActivity;
import org.haobtc.onekey.activities.settings.recovery_set.RecoveryActivity;
import org.haobtc.onekey.activities.settings.recovery_set.RecoveryResult;
import org.haobtc.onekey.activities.settings.recovery_set.ResetDeviceActivity;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.event.BackupEvent;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CheckHideWalletEvent;
import org.haobtc.onekey.event.CheckReceiveAddress;
import org.haobtc.onekey.event.ConnectingEvent;
import org.haobtc.onekey.event.EditWhiteListEvent;
import org.haobtc.onekey.event.ExecuteEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FastPayEvent;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.FixAllLabelnameEvent;
import org.haobtc.onekey.event.HandlerEvent;
import org.haobtc.onekey.event.InitEvent;
import org.haobtc.onekey.event.MutiSigWalletEvent;
import org.haobtc.onekey.event.PersonalMutiSigEvent;
import org.haobtc.onekey.event.PinEvent;
import org.haobtc.onekey.event.ReadingEvent;
import org.haobtc.onekey.event.ReceiveXpub;
import org.haobtc.onekey.event.RefreshEvent;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.event.SendSignBroadcastEvent;
import org.haobtc.onekey.event.SendXpubToSigwallet;
import org.haobtc.onekey.event.SetBluetoothEvent;
import org.haobtc.onekey.event.SetKeyLanguageEvent;
import org.haobtc.onekey.event.ShutdownTimeEvent;
import org.haobtc.onekey.event.SignMessageEvent;
import org.haobtc.onekey.event.SignResultEvent;
import org.haobtc.onekey.event.WipeEvent;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.fragment.BleDeviceRecyclerViewAdapter;
import org.haobtc.onekey.fragment.BluetoothConnectingFragment;
import org.haobtc.onekey.fragment.BluetoothFragment;
import org.haobtc.onekey.fragment.ErrorDialogFragment;
import org.haobtc.onekey.fragment.NeedNewVersion;
import org.haobtc.onekey.fragment.ReadingOrSendingDialogFragment;
import org.haobtc.onekey.fragment.mainwheel.CheckHideWalletFragment;
import org.haobtc.onekey.utils.CustomerUsbManager;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.Global;
import org.haobtc.onekey.utils.NfcUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

import static org.haobtc.onekey.activities.sign.SignActivity.strinputAddress;

/**
 * the dialog style activity which is the main logic processor
 *
 * @author liyan
 */
public class CommunicationModeSelector extends BaseActivity implements View.OnClickListener, BusinessAsyncTask.Helper {

    public static final String TAG = "BLE";
    public static final int BUTTON_REQUEST = 9;
    public static final int PIN_CURRENT = 1;
    public static final int PIN_NEW_FIRST = 2;
    public static final int PASS_NEW_PASSPHRASS = 6;
    public static final int PASS_PASSPHRASS = 3;
    private static final int REQUEST_ENABLE_BT = 1;
    public static PyObject ble, customerUI, nfc, usb, bleHandler, nfcHandler, bleTransport, nfcTransport, usbTransport, protocol;
    public static MyHandler handler;
    public static FutureTask<PyObject> futureTask;
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static String xpub;
    private boolean isActive = false;
    private boolean isSign = false;
    public static volatile boolean isNFC;
    private BleDeviceRecyclerViewAdapter adapter;
    private RelativeLayout relativeLayout;
    private String tag;
    private String extras;
    public static List<Runnable> runnables = new ArrayList<>();
    private BluetoothConnectingFragment fragment;
    private Ble<BleDevice> mBle;
    private ReadingOrSendingDialogFragment dialogFragment;
    private BluetoothFragment bleFragment;
    public static volatile boolean isDfu;
    public static final String COMMUNICATION_MODE_BLE = "bluetooth";
    public static final String COMMUNICATION_MODE_NFC = "nfc";
    public static volatile String way;
    private CustomerUsbManager usbManager;
    private UsbDevice device;
    public static HardwareFeatures features;
    private boolean isChangePin;
    private boolean isGpsStatueChange;
    public static Tag nfcTag;
    private String action;
    // 是否显示钱包创建成功页面
    private boolean showUI = true;
    private boolean isPairFailed;
    private boolean isFastPay = true;
    private boolean isGetXpub;
    private boolean isErrorOccurred;
    public static boolean backupTip;
    private static String hideWalletReceive = "";
    private boolean isTimeout;
    public boolean isRecovery;
    private AnimationDrawable animationDrawable;

    static {
        nfc = Global.py.getModule("trezorlib.transport.nfc");
        ble = Global.py.getModule("trezorlib.transport.bluetooth");
        usb = Global.py.getModule("trezorlib.transport.android_usb");
        protocol = Global.py.getModule("trezorlib.transport.protocol");
        bleHandler = ble.get("BlueToothHandler");
        nfcHandler = nfc.get("NFCHandle");
        usbTransport = usb.get("AndroidUsbTransport");
        nfcTransport = nfc.get("NFCTransport");
        bleTransport = ble.get("BlueToothTransport");
        customerUI = Global.py.getModule("trezorlib.customer_ui").get("CustomerUI");
    }

    @Override
    public int getLayoutId() {
        return R.layout.bluetooth_nfc;
    }

    @Override
    public void initView() {
        ImageView imageViewCancel;
        EventBus.getDefault().post(new ExitEvent());
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        way = preferences.getString("way", "bluetooth");
        isNFC = COMMUNICATION_MODE_NFC.equals(way);
        ImageView imageView = findViewById(R.id.touch_nfc);
        TextView textView = findViewById(R.id.text_prompt);
        FrameLayout frameLayout = findViewById(R.id.ble_device);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        RadioButton radioBle = findViewById(R.id.radio_ble);
        RadioButton radioNfc = findViewById(R.id.radio_nfc);
        imageViewCancel = findViewById(R.id.img_cancel);
        TextView textViewInputByHand = findViewById(R.id.text_input_publickey_by_hand);
        relativeLayout = findViewById(R.id.input_layout);
        textViewInputByHand.setOnClickListener(this);
        imageViewCancel.setOnClickListener(this);
        tag = getIntent().getStringExtra("tag");
        action = getIntent().getAction();
        handler = MyHandler.getInstance(this);
        customerUI.put("handler", handler);
        extras = getIntent().getStringExtra("extras");
        if (!MultiSigWalletCreator.TAG.equals(tag)) {
            relativeLayout.setVisibility(View.GONE);
        }
        adapter = new BleDeviceRecyclerViewAdapter(this);
        bleFragment = new BluetoothFragment(adapter);
        if (COMMUNICATION_MODE_NFC.equals(way)) {
            NfcUtils.nfc(this, true);
            radioBle.setVisibility(View.GONE);
            usbTransport.put("ENABLED", false);
            bleTransport.put("ENABLED", false);
            nfcTransport.put("ENABLED", true);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
            animationDrawable.start();
            Optional.ofNullable(nfcTag).ifPresent((tags) -> {
                nfcHandler.put("device", tags);
                IsoDep isoDep = IsoDep.get(tags);
                try {
                    isoDep.connect();
                    isoDep.close();
                    animationDrawable.stop();
                    new Handler().postDelayed(() -> handlerEverything(true), 100);
                } catch (IOException e) {
                    Log.d("NFC", "try connect failed");
                    nfcTag = null;
                }
            });
        } else if ("ble".equals(way)) {
            usbTransport.put("ENABLED", false);
            bleTransport.put("ENABLED", true);
            nfcTransport.put("ENABLED", false);
            mBle = Ble.getInstance();
            radioNfc.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.ble_device, bleFragment).commitAllowingStateLoss();
            RxPermissions permissions = new RxPermissions(this);
            radioGroup.check(R.id.radio_ble);
            permissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).subscribe(
                    granted -> {
                        if (granted) {
                            turnOnBlueTooth();
                            onRefresh(new RefreshEvent());
                        } else {
                            Toast.makeText(this, getString(R.string.blurtooth_need_permission), Toast.LENGTH_LONG).show();
                        }
                    }
            ).dispose();
        } else {
            // usb init
            usbTransport.put("ENABLED", true);
            bleTransport.put("ENABLED", false);
            nfcTransport.put("ENABLED", false);
            radioGroup.setVisibility(View.GONE);
            textView.setText(getString(R.string.link_use));
            imageView.setImageResource(R.drawable.img_usb);
            usbManager = CustomerUsbManager.getInstance(this);
            // used to deal with later attach
            usbManager.register(this);
            // used to deal with prior attach
            device = usbManager.findBixinKEYDevice();
            if (device != null) {
                try {
                    usbManager.doBusiness(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.dnot_use), Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::finish, 1000);
            }
        }
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
    }

    private final BleScanCallback<BleDevice> scanCallback = new BleScanCallback<BleDevice>() {
        @Override
        public void onLeScan(final BleDevice device, int rssi, byte[] scanRecord) {
            synchronized (mBle.getLocker()) {
                Log.d(TAG, "BLE Device Find====" + device.getBleName());
                adapter.add(device);
            }
        }
    };

    private void turnOnBlueTooth() {
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBle.isBleEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
                isGpsStatueChange = true;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private LocationManager locationManager;

    @SuppressLint("MissingPermission")
    private void refreshDeviceList(boolean start) {
        locationManager = (LocationManager) Objects.requireNonNull(getSystemService(LOCATION_SERVICE));
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!ok) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.open_location_service)
                    .setMessage(R.string.promote_ble)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        Toast.makeText(this, getString(R.string.dont_use_bluetooth), Toast.LENGTH_SHORT).show();
                        locationManager.removeUpdates(locationListener);
                        dialog.dismiss();
                        finish();
                    })
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .create();
            alertDialog.show();
            //show center
            Window dialogWindow = alertDialog.getWindow();
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay();
            WindowManager.LayoutParams p = dialogWindow.getAttributes();
            p.width = (int) (d.getWidth() * 0.95);
            p.gravity = Gravity.CENTER;
            dialogWindow.setAttributes(p);
        } else {
            locationManager = null;
            if (mBle.isScanning()) {
                mBle.stopScan();
            }
            BleDeviceRecyclerViewAdapter.mValues.clear();
            // When the device is connected, it will stop broadcasting, so the device cannot be searched. Therefore, the device connected by this app will be added to the list
            BleDeviceRecyclerViewAdapter.mValues.addAll(Ble.getInstance().getConnetedDevices());
            adapter.notifyDataSetChanged();
            if (start) {
                mBle.startScan(scanCallback);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent event) {
        refreshDeviceList(true);
    }


    private final Runnable retry = new Runnable() {
        @Override
        public void run() {
            if (isNFC) {
                return;
            }
            if (Ble.getInstance().getConnetedDevices().size() != 0) {
                startService(new Intent(CommunicationModeSelector.this, BleService.class));
            } else {
                if (device != null) {
                    usbManager.doBusiness(device);
                }
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            animationDrawable.stop();
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcTransport.put("ENABLED", true);
            bleHandler.put("ENABLED", false);
            usbTransport.put("ENABLED", false);
            nfcHandler.put("device", tags);
            handlerEverything(true);
        }

    }

    @NonNull
    private HardwareFeatures getFeatures(boolean isNFC) throws Exception {
        String feature;
        try {
            futureTask = new FutureTask<>(() -> {
                Log.d("Features", String.format("method==get_feature===in thread===%d", Thread.currentThread().getId()));
                return Daemon.commands.callAttr("get_feature", isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
            });
            executorService.submit(futureTask);
            feature = futureTask.get(5, TimeUnit.SECONDS).toString();
            if (!futureTask.isDone()) {
                futureTask.cancel(true);
            }
            HardwareFeatures features = HardwareFeatures.objectFromData(feature);
            SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
            if (features.isInitialized()) {
                HardwareFeatures old;
                String backupMessage = "";
                if (devices.contains(features.getDeviceId())) {
                    old = HardwareFeatures.objectFromData(devices.getString(features.getDeviceId(), ""));
                    backupMessage = old.getBackupMessage();
                }
                if (!Strings.isNullOrEmpty(backupMessage)) {
                    features.setBackupMessage(backupMessage);
                    feature = features.toString();
//                    devices.edit().putString(features.getDeviceId(), features.toString()).apply();
                }
            } else {
                // modify this value manual to support unfinished combined init
                features.setNeedsBackup(true);
                feature = features.toString();
            }
            devices.edit().putString(features.getDeviceId(), feature).apply();
            return features;
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_message), Toast.LENGTH_SHORT).show();
            if (ble != null) {
                ble.put("IS_CANCEL", true);
                nfc.put("IS_CANCEL", true);
                protocol.callAttr("notify");
            }
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param isNFC
     */
    private void handlerEverything(boolean isNFC) {
        isSign = false;
        isActive = false;
        if (isRecovery && isNFC) {
            protocol.callAttr("notify");
            return;
        }
        // used for combined init logic only.
        if (isNFC) {
            if (combinedInit()) {
                return;
            }
        }
        try {
            features = getFeatures(isNFC);
        } catch (Exception e) {
            finish();
            return;
        }
        if (VersionUpgradeActivity.TAG.equals(tag)) {
            // stm32 firmware update by nfc or ble
            if ("hardware".equals(extras)) {
                stm32Upgrade(isNFC);
                // ble firmware update by nfc only
            } else if ("ble".equals(extras)) {
                nrfUpgradeWithoutDFU(isNFC);
            }
        } else if (HardwareDetailsActivity.TAG.equals(tag) || SettingActivity.TAG_CHANGE_PIN.equals(tag)) {
            dealWithChangePin(isNFC);
        } else if (ResetDeviceActivity.TAG.equals(tag)) {
            dealWithWipeDevice(isNFC);
        } else if (HardwareDetailsActivity.TAG_VERIFICATION.equals(tag)) {
            hardwareVerify(isNFC);
        } else {
            dealWithBusiness(isNFC);
        }
    }

    // only use in nfc, ble .etc by event bus
    private boolean combinedInit() {
        if ("init".equals(action)) {
            doInit(new InitEvent("Activate", getIntent().getBooleanExtra("use_se", false)));
            return true;
        }
        if ("change_pin".equals(action)) {
            changePin(new ChangePinEvent("", ""));
            return true;
        }
        if ("backup".equals(action)) {
            if (getIntent().getBooleanExtra("get_feature", false)) {
                try {
                    features = getFeatures(isNFC);
                } catch (Exception e) {
                    finish();
                    return true;
                }
            }
            doBackup(new BackupEvent());
            return true;
        }
        return false;
    }

    private void hardwareVerify(boolean isNFC) {
        String strRandom = UUID.randomUUID().toString().replaceAll("-", "");
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.COUNTER_VERIFICATION, strRandom, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        startActivity(new Intent(this, VerificationKEYActivity.class));
    }

    private void stm32Upgrade(boolean isNFC) {
        Intent intent = new Intent(CommunicationModeSelector.this, UpgradeBixinKEYActivity.class);
        intent.putExtra("way", isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
        intent.putExtra("tag", 1);
        startActivity(intent);
        if (isNFC) {
            new Handler().postDelayed(() -> EventBus.getDefault().postSticky(new ExecuteEvent()), 1000);
        }
    }

    private void nrfUpgradeWithoutDFU(boolean isNFC) {
        if (isNFC || "usb".equals(way)) {
            Intent intent = new Intent(CommunicationModeSelector.this, UpgradeBixinKEYActivity.class);
            intent.putExtra("way", COMMUNICATION_MODE_NFC);
            intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
            intent.putExtra("tag", 2);
            startActivity(intent);
            new Handler().postDelayed(() -> EventBus.getDefault().postSticky(new ExecuteEvent()), 1000);
        }
    }

    private void dealWithChangePin(boolean isNFC) {
        if (features.isBootloaderMode()) {
            Toast.makeText(this, R.string.bootloader_mode, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (features.isInitialized()) {
            isChangePin = true;
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.CHANGE_PIN, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        } else {
            Toast.makeText(this, R.string.wallet_un_activated_pin, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * deal with the wipe device logic
     *
     * @param isNFC which communication way we use
     */
    private void dealWithWipeDevice(boolean isNFC) {
        if (features.isBootloaderMode()) {
            Toast.makeText(this, R.string.bootloader_mode, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (features.isInitialized()) {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.WIPE_DEVICE, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        } else {
            Toast.makeText(this, R.string.wallet_un_activated, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * deal with the create wallet 、sign、init logic
     *
     * @param isNFC which communication way we use
     */
    @SuppressLint("CommitPrefEdits")
    private void dealWithBusiness(boolean isNFC) {
        if (features.isBootloaderMode()) {
            Toast.makeText(this, R.string.bootloader_mode, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        boolean isInit = features.isInitialized();//isInit -->  Judge whether it is activated
        if (isInit) {
            if (MultiSigWalletCreator.TAG.equals(tag) || SingleSigWalletCreator.TAG.equals(tag) || PersonalMultiSigWalletCreator.TAG.equals(tag) || CheckHideWalletFragment.TAG.equals(tag) || ImportHistoryWalletActivity.TAG.equals(tag) || "check_xpub".equals(tag)) {
                isGetXpub = true;
                if (SingleSigWalletCreator.TAG.equals(tag) || CheckHideWalletFragment.TAG.equals(tag)) {
                    if (CheckHideWalletFragment.TAG.equals(tag)) {
                        if (features.isPassphraseProtection()) {
                            customerUI.callAttr("set_pass_state", 1);
                        } else {
                            Toast.makeText(this, getString(R.string.dont_create), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY_SINGLE, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "p2wpkh");
                } else {
                    new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
                }
            } else if (TransactionDetailsActivity.TAG.equals(tag) || SignActivity.TAG.equals(tag) || SignActivity.TAG1.equals(tag) || SignActivity.TAG2.equals(tag) || SignActivity.TAG3.equals(tag) || SendOne2OneMainPageActivity.TAG.equals(tag) || SendOne2ManyMainPageActivity.TAG.equals(tag) || TransactionDetailsActivity.TAG_HIDE_WALLET.equals(tag)) {
                dealwithSign(isNFC);
            } else if (BackupRecoveryActivity.TAG.equals(tag) || RecoveryActivity.TAG.equals(tag) || BackupMessageActivity.TAG.equals(tag) || "recovery".equals(action) || BackupWaySelector.TAG.equals(tag)) {
                if (TextUtils.isEmpty(extras)) {
                    //No backup after backup
                    String contrastDeviceId = getIntent().getStringExtra("contrastDeviceId");
                    if (!TextUtils.isEmpty(contrastDeviceId)) {
                        if (contrastDeviceId.equals(features.getDeviceId())) {
                            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.BACK_UP, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
                        } else {
                            mToast(getString(R.string.differnce_deviceId));
                            finish();
                        }
                    } else {
                        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.BACK_UP, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
                    }
                } else {
                    mToast(getString(R.string.recovery_unsupport));
                    finish();
                }
            } else if (HardwareDetailsActivity.TAG_VERIFICATION.equals(tag)) {
                String strRandom = UUID.randomUUID().toString().replaceAll("-", "");
                //Anti counterfeiting verification
                new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.COUNTER_VERIFICATION, strRandom, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
            } else if (FixBixinkeyNameActivity.TAG.equals(tag)) {
                //modify hardware label
                modifyHardwareLabel(isNFC);
            } else if (ConfidentialPaymentSettings.TAG.equals(tag)) {
                //Fast pay details setting
                fastPay(isNFC);
            } else if (ConfidentialPaymentSettings.TAG_EDIT_WHITE_LIST.equals(tag)) {
                //check white list
                if (checkVersion()) {
                    checkWhiteList(isNFC);
                }
            } else if (EditWhiteListActivity.TAG_ADD_WHITE_LIST.equals(tag)) {
                //add white list
                if (checkVersion()) {
                    addWhiteList(isNFC);
                }
            } else if (EditWhiteListActivity.TAG_DELETE_WHITE_LIST.equals(tag)) {
                //delete white list
                if (checkVersion()) {
                    deleteWhiteList(isNFC);
                }
            } else if (SetShutdownTimeActivity.TAG.equals(tag)) {
                //modify hardware's automatic shutdown time
                shutdownSetting(isNFC);
            } else if (FixHardwareLanguageActivity.TAG.equals(tag)) {
                //modify hardware language
                hardwareLanguage(isNFC);
            } else if (ReceivedPageActivity.TAG.equals(tag)) {
                //contrast phone and hardware address
                contrastAddress();
            }
        } else {
            if (!TextUtils.isEmpty(extras) && (BackupMessageActivity.TAG.equals(tag) || RecoveryActivity.TAG.equals(tag)) || "recovery".equals(action)) {
                if (RecoveryActivity.TAG.equals(tag)) {
                    isRecovery = true;
                }
                recovery(isNFC);
                return;
            }
            isActive = true;
            Intent intent = new Intent(this, WalletUnActivatedActivity.class);
            if (SingleSigWalletCreator.TAG.equals(tag)) {
                intent.putExtra("tag_Xpub", tag);
            }
            startActivity(intent);
        }
    }

    private boolean checkVersion() {
        // if the version of stm32 is below 1.9.7, turn to upgrade page
//        if (features.getMajorVersion() <= 1 && features.getMinorVersion() <= 9 && features.getPatchVersion() < 7 && !VersionUpgradeActivity.TAG.equals(tag)) {
        if (features.getMajorVersion() <= 1 && features.getMinorVersion() <= 9 && features.getPatchVersion() < 7) {
            NeedNewVersion fragment = new NeedNewVersion(R.string.update2_new_version, R.string.old_version);
            fragment.setActivity(this);
            fragment.show(getSupportFragmentManager(), "");
            return false;
        }
        return true;
    }

    private void contrastAddress() {
        String contrastAddress = getIntent().getStringExtra("contrastAddress");
        hideWalletReceive = getIntent().getStringExtra("hideWalletReceive");
        String deviceId = features.getDeviceId();
        PyObject deviceInfo = Daemon.commands.callAttr("get_device_info");
        String strDeviceId = deviceInfo.toString();
        if (strDeviceId.contains(deviceId)) {
            if ("hideWalletReceive".equals(hideWalletReceive)) {
                customerUI.callAttr("set_pass_state", 1);
            }
            try {
                new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SHOW_ADDRESS, contrastAddress, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mToast(getString(R.string.mismatch_wallet));
        }

    }

    private void dealwithSign(boolean isNFC) {
        isSign = true;
        if (SignActivity.TAG1.equals(tag) || SignActivity.TAG3.equals(tag)) {
            if (SignActivity.TAG3.equals(tag)) {
                // means operation about a hide wallet
                customerUI.callAttr("set_pass_state", 1);
            }
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_MESSAGE, strinputAddress, extras, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        } else {
            if (SignActivity.TAG2.equals(tag) || TransactionDetailsActivity.TAG_HIDE_WALLET.equals(tag)) {
                // means operation about a hide wallet
                customerUI.callAttr("set_pass_state", 1);
            }
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_TX, extras, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        }
    }

    private void modifyHardwareLabel(boolean isNFC) {
        String fixNamed = getIntent().getStringExtra("fixName");
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.APPLY_SETTING, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "label", fixNamed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hardwareLanguage(boolean isNFC) {
        String keyLanguage = getIntent().getStringExtra("set_key_language");
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.APPLY_SETTING, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "fix_hardware_language", keyLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutdownSetting(boolean isNFC) {
        String shutdownTime = getIntent().getStringExtra("shutdown_time");
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.APPLY_SETTING, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "shutdown_time", shutdownTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fastPay(boolean isNFC) {
        String limit = getIntent().getStringExtra("limit");
        String times = getIntent().getStringExtra("times");
        String noPIN = getIntent().getStringExtra("noPIN");
        String noHard = getIntent().getStringExtra("noHard");
        Toast.makeText(this, getString(R.string.confirm_finish), Toast.LENGTH_LONG).show();
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.APPLY_SETTING, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "fastPay", limit, times, noPIN, noHard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWhiteList(boolean isNFC) {
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.EDIT_WHITE_LIST, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "Inquire");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addWhiteList(boolean isNFC) {
        String whiteAddress = getIntent().getStringExtra("whiteAddress");
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.ADD_AND_DELETE_WHITE_LIST, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "Add", whiteAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteWhiteList(boolean isNFC) {
        String whiteAddress = getIntent().getStringExtra("whiteAddress");
        try {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.ADD_AND_DELETE_WHITE_LIST, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "Delete", whiteAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recovery(boolean isNFC) {
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String locate = preferences.getString("language", "");
        String language = "English".equals(locate) ? "english" : "chinese";
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.RECOVER, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, extras, language);
    }

    private void showConnecting() {
        runOnUiThread(() -> {
            if (fragment == null) {
                fragment = new BluetoothConnectingFragment();
            }
            if (hasWindowFocus() && !isFinishing()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.ble_device, fragment, "connecting").commitNow();
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }

    public ReadingOrSendingDialogFragment showReadingDialog(int res) {
        getSupportFragmentManager().beginTransaction().replace(R.id.ble_device, bleFragment).commitAllowingStateLoss();
        ReadingOrSendingDialogFragment fragment = new ReadingOrSendingDialogFragment(res);
        fragment.show(getSupportFragmentManager(), "");
        return fragment;
    }

    //Activate interface
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doInit(InitEvent event) {
        isActive = true;
        if ("Activate".equals(event.getName())) {
            SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
            String locate = preferences.getString("language", "");
            String language = "English".equals(locate) ? "english" : "chinese";
            boolean useSe = Optional.of(event.isUseSE()).orElse(true);
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.INIT_DEVICE, isNFC ?
                    COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "BIXIN KEY", language, useSe ? "1" : "");
        }
    }

    //Activate success ,then,get xpub and to back
    @Subscribe
    public void backXpub(SendXpubToSigwallet event) {
        isActive = false;
        showUI = false;
        if ("get_xpub_and_send".equals(event.getXpub())) {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY_SINGLE, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE, "p2wpkh");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doWipe(WipeEvent event) {
        isActive = false;
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.WIPE_DEVICE, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED, sticky = true)
    public void doBusiness(HandlerEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (mBle != null && mBle.isScanning()) {
            mBle.stopScan();
        }
        handlerEverything(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connecting(ConnectingEvent event) {
        showConnecting();
    }

    @Subscribe
    public void setPin(PinEvent event) {
        if (!Strings.isNullOrEmpty(event.getPinCode())) {
            customerUI.put("pin", event.getPinCode());
        }
    }

    @Subscribe
    public void doBackup(BackupEvent backupEvent) {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.BACK_UP, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
    }

    @Subscribe
    public void changePin(ChangePinEvent event) {
        isActive = false;
        if (!Strings.isNullOrEmpty(event.toString())) {
            customerUI.put("pin", event.toString());
        } else {
            new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.CHANGE_PIN, isNFC ? COMMUNICATION_MODE_NFC : COMMUNICATION_MODE_BLE);
        }
    }

    @Subscribe
    public void setPassphrass(PinEvent event) {
        if (!Strings.isNullOrEmpty(event.getPassphrass())) {
            customerUI.put("passphrase", event.getPassphrass());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(ReadingEvent event) {
        dialogFragment = showReadingDialog(R.string.reading_dot);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (isSign) {
            isFastPay = false;
            synchronized (CommunicationModeSelector.class) {
                if (runnables.size() != 0) {
                    runOnUiThread(runnables.get(0));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exit(ExitEvent event) {
        finish();
    }

    public void onSignSuccessful(SignResultEvent resultEvent) {
        String signedRaw = resultEvent.getSignedRaw();
        if (!TextUtils.isEmpty(signedRaw)) {
            EventBus.getDefault().post(new SecondEvent("finish"));
            Intent intent1 = new Intent(this, TransactionDetailsActivity.class);
            intent1.putExtra("signed_raw_tx", signedRaw);
            intent1.putExtra("is_mine", true);
            startActivity(intent1);
            finish();
        }
    }

    public void onSignSuccessful(SendSignBroadcastEvent resultEvent) {
        EventBus.getDefault().removeStickyEvent(SendSignBroadcastEvent.class);
        String signedTx = resultEvent.getSignTx();
        String txHash;
        if (!TextUtils.isEmpty(signedTx)) {
            try {
                Gson gson = new Gson();
                GetnewcreatTrsactionListBean getnewcreatTrsactionListBean = gson.fromJson(signedTx, GetnewcreatTrsactionListBean.class);
                String tx = getnewcreatTrsactionListBean.getTx();
                txHash = getnewcreatTrsactionListBean.getTxid();
                Daemon.commands.callAttr("broadcast_tx", tx);
            } catch (Exception e) {
                e.printStackTrace();
                String message = e.getMessage();
                if (message.contains(".")) {
                    if (message.endsWith(".")) {
                        message = message.substring(0, message.length() - 1);
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, message.substring(message.lastIndexOf(".") + 1), Toast.LENGTH_SHORT).show();
                }

                return;
            }
            EventBus.getDefault().post(new SecondEvent("finish"));
            EventBus.getDefault().post(new FirstEvent("22"));
            EventBus.getDefault().postSticky(new SecondEvent("ActivateFinish"));
            Intent intent1 = new Intent(this, TransactionDetailsActivity.class);
            intent1.putExtra("listType", "history");
            intent1.putExtra("keyValue", "B");
            intent1.putExtra("tx_hash", txHash);
            intent1.putExtra("is_mine", true);
            intent1.putExtra("unConfirmStatus", "broadcast_complete");
            startActivity(intent1);
            finish();
        }
    }


    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cancel:
                if (mBle != null) {
                    refreshDeviceList(false);
                    mBle.disconnectAll();
                }
                finish();
                break;
            case R.id.text_input_publickey_by_hand:
                if (mBle != null) {
                    refreshDeviceList(false);
                }
                runOnUiThread(runnables.get(0));
                finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.open_bluetooth), Toast.LENGTH_LONG).show();
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            refreshDeviceList(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isNFC) {
            if (animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        }
        // if the below code was commented may make the callback(onException) doesn't work
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGpsStatueChange && "ble".equals(way)) {
            isGpsStatueChange = false;
            refreshDeviceList(true);
        }
        if (!isErrorOccurred) {
            if (isSign) {
                if (SignActivity.TAG1.equals(tag) || SignActivity.TAG3.equals(tag)) {
                    dialogFragment = showReadingDialog(R.string.message_loading);
                } else {
                    dialogFragment = showReadingDialog(R.string.transaction_loading);
                }
            } else if (isGetXpub) {
                dialogFragment = showReadingDialog(R.string.reading_dot);
            } else {
                if (isNFC) {
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
            }
        } else {
            if (isPairFailed) {
                isPairFailed = false;
                showErrorDialog(R.string.try_another_key, R.string.sign_failed_device);
            } else if (isSign && isTimeout) {
                showErrorDialog(R.string.timeout_error, R.string.read_pk_failed);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(CheckReceiveAddress event) {
        if ("checking".equals(event.getType()) || "finish_mode_select".equals(event.getType())) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyHandler.myHandler = null;
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        EventBus.getDefault().unregister(this);
        if ("usb".equals(way)) {
            CustomerUsbManager.getInstance(this).unRegister(this);
        }
    }

    @Override
    public void onPreExecute() {
        isErrorOccurred = false;
        isTimeout = false;
        isPairFailed = false;
        if (isActive) {
            return;
        }
        if (isSign) {
            if (SignActivity.TAG1.equals(tag) || SignActivity.TAG3.equals(tag)) {
                dialogFragment = showReadingDialog(R.string.message_loading);
            } else {
                dialogFragment = showReadingDialog(R.string.transaction_loading);
            }

        } else if (MultiSigWalletCreator.TAG.equals(tag) || SingleSigWalletCreator.TAG.equals(tag) || PersonalMultiSigWalletCreator.TAG.equals(tag) || CheckHideWalletFragment.TAG.equals(tag) || ImportHistoryWalletActivity.TAG.equals(tag)) {
            // loading page
            if (hasWindowFocus()) {
                dialogFragment = showReadingDialog(R.string.reading_dot);
            }
        }
    }

    /**
     * Note: the execute thread isn't the UI thread
     **/
    @Override
    public void onException(Exception e) {
        Log.i("Exceptionjinxiaoin", "onException:-- " + e.getMessage());
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
        isErrorOccurred = true;
        EventBus.getDefault().post(new FinishEvent());
        if (!isFinishing()) {
            if (HardWareExceptions.PIN_INVALID.getMessage().equals(e.getMessage())) {
                showErrorDialog(0, R.string.pin_wrong);
            } else if (HardWareExceptions.UN_PAIRABLE.getMessage().equals(e.getMessage())) {
                // state variable that can be useful with pin request
                isPairFailed = true;
                // can be useful without pin request
                showErrorDialog(R.string.try_another_key, R.string.sign_failed_device);
            } else if (HardWareExceptions.TRANSACTION_FORMAT_ERROR.getMessage().equals(e.getMessage())) {
                showErrorDialog(R.string.sign_failed, R.string.transaction_parse_error);
            } else if (HardWareExceptions.BLE_RESPONSE_READ_TIMEOUT.getMessage().equals(e.getMessage())) {
                isTimeout = true;
            } else if ("BaseException: ".equals(e.getMessage())) {
                runOnUiThread(() -> mToast(getString(R.string.canceled)));
            } else if (e.getMessage().contains("addres is existed")) {
                runOnUiThread(() -> mToast(getString(R.string.have_address)));
            } else {
                showErrorDialog(R.string.key_wrong_prompte, R.string.read_pk_failed);
            }
        }
    }


    public void showErrorDialog(int error, int title) {
        ErrorDialogFragment fragment = new ErrorDialogFragment(error, title);
        fragment.setRunnable(retry);
        fragment.setActivity(CommunicationModeSelector.this);
        fragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onResult(String s) {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
        if (isActive) {
            EventBus.getDefault().post(new ResultEvent(s));
            finish();
            return;
        }

        if (MultiSigWalletCreator.TAG.equals(tag) || SingleSigWalletCreator.TAG.equals(tag) || PersonalMultiSigWalletCreator.TAG.equals(tag) || CheckHideWalletFragment.TAG.equals(tag) || ImportHistoryWalletActivity.TAG.equals(tag) || "check_xpub".equals(tag)) {
            xpub = s;
            EventBus.getDefault().post(new FinishEvent());
            if (ImportHistoryWalletActivity.TAG.equals(tag)) {
                Intent intent1 = new Intent(this, ChooseHistryWalletActivity.class);
                intent1.putExtra("history_xpub", xpub);
                startActivity(intent1);
            } else if (SingleSigWalletCreator.TAG.equals(tag)) {
                EventBus.getDefault().post(new ReceiveXpub(xpub, features.getDeviceId(), features.isNeedsBackup(), showUI));
            } else if ("check_xpub".equals(tag)) {
                Intent intent = new Intent(this, CheckXpubResultActivity.class);
                intent.putExtra("label", Optional.ofNullable(features.getLabel()).orElse("BixinKEY"));
                intent.putExtra("xpub", s);
                startActivity(intent);
            } else if (CheckHideWalletFragment.TAG.equals(tag)) {
                EventBus.getDefault().post(new CheckHideWalletEvent(xpub, features.getDeviceId()));
            } else if (PersonalMultiSigWalletCreator.TAG.equals(tag)) {
                EventBus.getDefault().post(new PersonalMutiSigEvent(xpub, features.getDeviceId(), features.getLabel()));
            } else if (MultiSigWalletCreator.TAG.equals(tag)) {
                EventBus.getDefault().post(new MutiSigWalletEvent(xpub, features.getDeviceId(), features.getLabel()));
            } else {
                runOnUiThread(runnables.get(1));
            }
        } else if (isSign) {
            if (SignActivity.TAG1.equals(tag) || SignActivity.TAG3.equals(tag)) {
                EventBus.getDefault().post(new SignMessageEvent(s));
            } else if (SendOne2OneMainPageActivity.TAG.equals(tag) || SendOne2ManyMainPageActivity.TAG.equals(tag)) {
                if (isFastPay) {
                    onSignSuccessful(new SendSignBroadcastEvent(s));
                    return;
                }
                EventBus.getDefault().postSticky(new SendSignBroadcastEvent(s));
            } else {
                if (isFastPay) {
                    onSignSuccessful(new SignResultEvent(s));
                    return;
                }
                EventBus.getDefault().post(new SignResultEvent(s));
            }
        } else if (BackupRecoveryActivity.TAG.equals(tag) || BackupMessageActivity.TAG.equals(tag) || RecoveryActivity.TAG.equals(tag) || "backup".equals(action) || "recovery".equals(action) || BackupWaySelector.TAG.equals(tag)) {
            if (TextUtils.isEmpty(extras)) {
                SharedPreferences backup = getSharedPreferences("backup", Context.MODE_PRIVATE);
                SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
                backup.edit().putString(features.getDeviceId(), Strings.isNullOrEmpty(features.getLabel()) ? features.getBleName() + ":" + s : features.getLabel() + ":" + s).apply();
                features.setNeedsBackup(false);
                devices.edit().putString(features.getDeviceId(), features.toString()).apply();
                if (backupTip) {
                    backupTip = false;
                    EventBus.getDefault().post(new FinishEvent());
                }
                if ("backup".equals(action)) {
                    EventBus.getDefault().post(new BackupFinishEvent(s));
                } else {
                    Intent intent = new Intent(this, BackupMessageActivity.class);
                    intent.putExtra("label", Strings.isNullOrEmpty(features.getLabel()) ? features.getBleName() : features.getLabel());
                    intent.putExtra("tag", "backup");
                    intent.putExtra("message", s);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(this, RecoveryResult.class);
                intent.putExtra("tag", s);
                startActivity(intent);
            }
        } else if (ResetDeviceActivity.TAG.equals(tag) || HardwareDetailsActivity.TAG.equals(tag) || SettingActivity.TAG_CHANGE_PIN.equals(tag) || HardwareDetailsActivity.TAG_VERIFICATION.equals(tag)) {
            EventBus.getDefault().postSticky(new ResultEvent(s));
        } else if (ConfidentialPaymentSettings.TAG.equals(tag)) {
            EventBus.getDefault().post(new FastPayEvent(s));
        } else if (ConfidentialPaymentSettings.TAG_EDIT_WHITE_LIST.equals(tag)) {
            EventBus.getDefault().post(new EditWhiteListEvent("checkWhiteList", s));
        } else if (EditWhiteListActivity.TAG_ADD_WHITE_LIST.equals(tag)) {
            EventBus.getDefault().post(new EditWhiteListEvent("addWhiteList", s));
        } else if (EditWhiteListActivity.TAG_DELETE_WHITE_LIST.equals(tag)) {
            EventBus.getDefault().post(new EditWhiteListEvent("deleteWhiteList", s));
        } else if (FixBixinkeyNameActivity.TAG.equals(tag)) {
            String deviceid = features.getDeviceId();
            EventBus.getDefault().post(new FixAllLabelnameEvent(deviceid, s));
        } else if (SetShutdownTimeActivity.TAG.equals(tag)) {
            EventBus.getDefault().post(new ShutdownTimeEvent(s));
        } else if (FixHardwareLanguageActivity.TAG.equals(tag)) {
            EventBus.getDefault().post(new SetKeyLanguageEvent(s));
        }
        finish();
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public void currentMethod(String methodName) {

    }

    /**
     * DCL singleton used for hardware callback
     * works in UI thread
     */
    public static class MyHandler extends Handler {
        /**
         * handler used in python
         */
        private static volatile MyHandler myHandler;
        /**
         * the handler to start new activity
         */
        private FragmentActivity fragmentActivity;

        private MyHandler(FragmentActivity activity) {
            this.fragmentActivity = activity;
        }

        static MyHandler getInstance(FragmentActivity fragmentActivity) {
            if (myHandler == null) {
                synchronized (MyHandler.class) {
                    if (myHandler == null) {
                        myHandler = new MyHandler(fragmentActivity);
                    }
                }
            }
            return myHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String tag = ((CommunicationModeSelector) fragmentActivity).tag;
            boolean isSign = ((CommunicationModeSelector) fragmentActivity).isSign;
            boolean isChangePin = ((CommunicationModeSelector) fragmentActivity).isChangePin;
            switch (msg.what) {
                case PIN_CURRENT:
                    Intent intent = new Intent(fragmentActivity, PinSettingActivity.class);
                    if (!isSign) {
                        intent.putExtra("tag", tag);
                    }
                    if (isChangePin) {
                        intent.putExtra("pin_type", 3);
                    }
                    fragmentActivity.startActivity(intent);
                    break;
                case PIN_NEW_FIRST:
                    Intent intent1 = new Intent(fragmentActivity, PinSettingActivity.class);
                    intent1.putExtra("pin_type", PIN_NEW_FIRST);
                    intent1.putExtra("tag", tag);
                    fragmentActivity.startActivity(intent1);
                    break;
                case BUTTON_REQUEST:
                    EventBus.getDefault().postSticky(new ButtonRequestEvent(0));
                    break;
                case PASS_NEW_PASSPHRASS:
                case PASS_PASSPHRASS:
                    //Set password
                    Intent intent3 = new Intent(fragmentActivity, HideWalletSetPassActivity.class);
                    intent3.putExtra("hideWalletReceive", hideWalletReceive);
                    fragmentActivity.startActivity(intent3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    }
}

package org.haobtc.onekey.ui.activity;

import static cn.com.heaton.blelibrary.ble.Ble.REQUEST_ENABLE_BT;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.orhanobut.logger.Logger;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.business.language.LanguageManager;
import org.haobtc.onekey.business.version.VersionManager;
import org.haobtc.onekey.business.wallet.DeviceManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.BleScanStopEvent;
import org.haobtc.onekey.event.ConnectingEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.adapter.BleDeviceAdapter;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.ConnectingDialog;
import org.haobtc.onekey.ui.dialog.InvalidDeviceIdWarningDialog;
import org.haobtc.onekey.utils.ValueAnimatorUtil;

/** @author liyan */
public class SearchDevicesActivity extends BaseActivity
        implements BleDeviceAdapter.OnItemBleDeviceClick {

    public static final int REQUEST_ID = 65578;

    public static void startSearchADevice(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SearchDevicesActivity.class);
        intent.putExtra(
                org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE,
                org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.open_wallet_hide)
    protected TextView mOpenWalletHide;

    @BindView(R.id.device_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.device_list_layout)
    LinearLayout mDeviceListLayout;

    @BindView(R.id.container)
    LinearLayout mContainer;

    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.relode)
    TextView relode;

    @BindView(R.id.load_device)
    RelativeLayout mLoadingLayout;

    @BindView(R.id.title)
    TextView mTitle;

    private LinearLayout mLayoutView;
    private BleDeviceAdapter mBleAdapter;
    private int mSearchMode;
    private BleManager bleManager;
    private VersionManager mVersionManager;
    private CenterPopupView dialog;
    private String serialNum = "";

    @Override
    public void init() {
        mSearchMode =
                getIntent()
                        .getIntExtra(
                                Constant.SEARCH_DEVICE_MODE,
                                Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
        serialNum = getIntent().getStringExtra(Constant.SERIAL_NUM);
        addBleView();
        bleManager = BleManager.getInstance(this);
        if (mSearchMode != Constant.SearchDeviceMode.MODE_PREPARE) {
            initBle();
        }
        mVersionManager = new VersionManager();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_search_devices;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleScanDevice(BleDevice device) {
        if (mBleAdapter != null) {
            BleLog.d(
                    "SearchDevicesActivity",
                    String.format(
                            "find device ====%s=====%s",
                            device.getBleName(), device.getBleAddress()));
            mBleAdapter.add(device);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnecting(ConnectingEvent event) {
        dialog =
                (CenterPopupView)
                        new XPopup.Builder(mContext)
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .asCustom(new ConnectingDialog(mContext))
                                .show();
        dialog.postDelayed(
                () -> {
                    if (dialog != null && dialog.isShow()) {
                        dialog.dismiss();
                    }
                },
                10000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEx(BleConnectionEx connectionEx) {
        if (connectionEx == BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT) {
            Toast.makeText(this, R.string.ble_connect_timeout, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.ble_connect_error, Toast.LENGTH_SHORT).show();
        }
        if (dialog != null && dialog.isShow()) {
            dialog.dismiss();
            refreshBleList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleScanStop(BleScanStopEvent event) {
        if (mSearchMode == Constant.SearchDeviceMode.MODE_PREPARE) {
            return;
        }
        if (mLoadingLayout.getVisibility() != View.GONE) {
            mLoadingLayout.setVisibility(View.GONE);
        }
        if (mDeviceListLayout.getVisibility() != View.VISIBLE) {
            mDeviceListLayout.setVisibility(View.VISIBLE);
        }
        if (mOpenWalletHide.getVisibility() != View.VISIBLE) {
            mOpenWalletHide.postDelayed(() -> mOpenWalletHide.setVisibility(View.VISIBLE), 400);
        }
        // 加入当前已连接的设备
        if (!Ble.getInstance().getConnetedDevices().isEmpty()) {
            Ble.getInstance().getConnetedDevices().forEach(mBleAdapter::add);
        }
        mBleAdapter.notifyDataSetChanged();
        if (mLayoutView.getHeight() > 0) {
            mLayoutView.post(
                    () ->
                            ValueAnimatorUtil.animatorHeightLayout(
                                    mLayoutView, mLayoutView.getHeight(), 0));
        }
    }

    public void addBleView() {
        mLayoutView =
                (LinearLayout)
                        LayoutInflater.from(this).inflate(R.layout.layout_search_device_ble, null);

        TextView next = mLayoutView.findViewById(R.id.next_to_do);
        TextView hide = mLayoutView.findViewById(R.id.hind);
        TextView action = mLayoutView.findViewById(R.id.action);

        switch (mSearchMode) {
            case Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD:
                mTitle.setText(R.string.recovery_hd_wallet);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_BACKUP_HD_WALLET_TO_DEVICE:
                mTitle.setText(R.string.backup_wallet);
                next.setText(R.string.ready_unactive_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_CLONE_TO_OTHER_COLD:
                mTitle.setText(R.string.ready_unactive_wallet);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON:
                mTitle.setText(R.string.bind_admin_person);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            default:
                mTitle.setText(R.string.pair);
                next.setText(R.string.open_cold_wallet);
                hide.setText(R.string.support_device);
                if (action.getVisibility() == View.VISIBLE) {
                    action.setVisibility(View.GONE);
                }
                break;
        }
        mContainer.addView(mLayoutView, 1);
        mBleAdapter = new BleDeviceAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mBleAdapter);
    }

    @Override
    public void connectBle(BleDevice device) {
        PreferencesManager.put(
                this, Constant.BLE_INFO, device.getBleName(), device.getBleAddress());
        bleManager.connDev(device);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadyBle(NotifySuccessfulEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        toNextActivity();
    }

    private void toNextActivity() {
        PyEnv.getFeature(this, this::dealResponse);
    }

    private void dealResponse(PyResponse<HardwareFeatures> response) {
        HardwareFeatures features;
        String errors = response.getErrors();
        features = response.getResult();
        runOnUiThread(
                () -> {
                    if (Strings.isNullOrEmpty(errors) && Objects.nonNull(features)) {
                        if (DeviceManager.forceUpdate(features)) {
                            update(features);
                            finish();
                            return;
                        }
                    } else {
                        showToast(getString(R.string.get_hard_msg_error));
                        if (dialog != null && dialog.isShow()) {
                            dialog.dismiss();
                        }
                        if (Objects.equals(mSearchMode, Constant.SearchDeviceMode.MODE_PREPARE)) {
                            finish();
                        } else {
                            refreshBleList();
                        }
                        return;
                    }
                    switch (mSearchMode) {
                            // 通过备份提示过来
                        case Constant.SearchDeviceMode.MODE_BACKUP_HD_WALLET_TO_DEVICE:
                            if (features.isInitialized()) {
                                showToast(getString(R.string.hard_tip2));
                            } else {
                                Intent intent = new Intent(this, ActivateColdWalletActivity.class);
                                intent.putExtra(Constant.ACTIVE_MODE, Constant.ACTIVE_MODE_IMPORT);
                                intent.putExtra(
                                        Constant.MNEMONICS,
                                        getIntent().getStringExtra(Constant.MNEMONICS));
                                startActivity(intent);
                                finish();
                            }
                            break;
                            // 共管钱包
                        case Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON:
                            if (features.isInitialized() && !features.isBackupOnly()) {
                                EventBus.getDefault()
                                        .post(new GetXpubEvent(Constant.COIN_TYPE_BTC));
                            } else {
                                showToast(getString(R.string.hard_tip1));
                            }
                            finish();
                            break;
                            // 仅连接蓝牙
                        case Constant.SearchDeviceMode.MODE_PREPARE:
                            if (features.isInitialized() && !features.isBackupOnly()) {
                                if (!Strings.isNullOrEmpty(serialNum)
                                        && !Objects.equals(features.getSerialNum(), serialNum)) {
                                    new InvalidDeviceIdWarningDialog()
                                            .show(getSupportFragmentManager(), "");
                                    return;
                                }
                                setResult(Activity.RESULT_OK);
                                EventBus.getDefault().post(new BleConnectedEvent());
                            } else {
                                showToast(getString(R.string.hard_tip3));
                            }
                            finish();
                            break;
                            // 恢复 HD
                        case Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD:
                            if (features.isInitialized()) {
                                if (features.isBackupOnly()) {
                                    boolean hasLocalHd =
                                            (boolean)
                                                    PreferencesManager.get(
                                                            this,
                                                            "Preferences",
                                                            Constant.HAS_LOCAL_HD,
                                                            false);
                                    if (hasLocalHd) {
                                        showToast(R.string.already_has_local_hd);
                                    } else {
                                        startActivity(
                                                new Intent(
                                                        this, FindBackupOnlyDeviceActivity.class));
                                    }
                                } else {
                                    showToast(R.string.only_backup_only_device);
                                }
                            } else {
                                showToast(getString(R.string.hard_tip4));
                            }
                            finish();
                            break;
                        default:
                            // 配对过来的逻辑
                            if (!features.isInitialized()) {
                                Logger.e(features.toString());
                                startActivity(new Intent(this, FindUnInitDeviceActivity.class));
                                finish();
                            } else if (features.isBackupOnly()) {
                                boolean hasLocalHd =
                                        (boolean)
                                                PreferencesManager.get(
                                                        this,
                                                        "Preferences",
                                                        Constant.HAS_LOCAL_HD,
                                                        false);
                                if (hasLocalHd) {
                                    showToast(R.string.already_has_local_hd);
                                } else {
                                    startActivity(
                                            new Intent(this, FindBackupOnlyDeviceActivity.class));
                                }
                                finish();
                            } else {
                                startActivity(new Intent(this, FindNormalDeviceActivity.class));
                                finish();
                            }
                    }
                });
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.relode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.relode:
                refreshBleList();
                break;
        }
    }

    private void refreshBleList() {
        if (mLoadingLayout.getVisibility() != View.VISIBLE) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
        if (mDeviceListLayout.getVisibility() != View.GONE) {
            mDeviceListLayout.setVisibility(View.GONE);
        }
        mBleAdapter.clear();
        bleManager.refreshBleDeviceList();
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.open_bluetooth), Toast.LENGTH_LONG).show();
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            initBle();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bleManager.initBle();
            } else {
                showToast(R.string.blurtooth_need_permission);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bleManager != null) {
            if (bleManager.isGpsStatusChange()) {
                bleManager.setGpsStatusChange(false);
                initBle();
            }
        }
    }

    private void update(HardwareFeatures features) {
        UpdateInfo forceVersionInfo = mVersionManager.getForceLocalVersionInfo(this);

        if (forceVersionInfo == null) {
            showToast(R.string.get_update_info_failed);
            return;
        }
        String urlPrefix = "https://onekey.so/";
        String locate = LanguageManager.getInstance().getLocalLanguage(this);
        String info = forceVersionInfo.toString();

        String firmwareVersion = "";
        if (!features.isBootloaderMode()) {
            firmwareVersion =
                    features.getMajorVersion()
                            + "."
                            + features.getMinorVersion()
                            + "."
                            + features.getPatchVersion();
        }
        String nrfVersion = features.getBleVer();
        String bleName = features.getBleName();
        String label = features.getLabel();
        String serialNum = features.getSerialNum();
        String bleMac =
                PreferencesManager.get(
                                this, org.haobtc.onekey.constant.Constant.BLE_INFO, bleName, "")
                        .toString();
        Bundle bundle = getBundle(urlPrefix, locate, info, nrfVersion, features.isBootloaderMode());
        bundle.putString(Constant.TAG_FIRMWARE_VERSION, firmwareVersion);
        bundle.putString(Constant.TAG_NRF_VERSION, nrfVersion);
        bundle.putString(Constant.TAG_BLE_NAME, bleName);
        bundle.putString(Constant.BLE_MAC, bleMac);
        bundle.putString(Constant.TAG_LABEL, label);
        bundle.putString(Constant.SERIAL_NUM, serialNum);
        bundle.putBoolean(Constant.FORCE_UPDATE, true);
        Intent intent = new Intent(this, HardwareUpgradeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @NonNull
    private Bundle getBundle(
            String urlPrefix,
            String locate,
            String info,
            String curNrfVersion,
            boolean isBootloader) {

        UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
        String urlNrf = updateInfo.getNrf().getUrl();
        String urlStm32 = updateInfo.getStm32().getUrl();
        String versionNrf = updateInfo.getNrf().getVersion();
        String versionStm32 = updateInfo.getStm32().getVersion().toString().replace(",", ".");
        versionStm32 = versionStm32.substring(1, versionStm32.length() - 1).replaceAll("\\s+", "");
        String descriptionNrf =
                "English".equals(locate)
                        ? updateInfo.getNrf().getChangelogEn()
                        : updateInfo.getNrf().getChangelogCn();
        String descriptionStm32 =
                "English".equals(locate)
                        ? updateInfo.getStm32().getChangelogEn()
                        : updateInfo.getStm32().getChangelogCn();
        if (urlNrf.startsWith("https") || urlStm32.startsWith("https")) {
            urlPrefix = "";
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_FIRMWARE_DOWNLOAD_URL, urlPrefix + urlStm32);
        bundle.putString(Constant.TAG_FIRMWARE_VERSION_NEW, versionStm32);
        bundle.putString(Constant.TAG_FIRMWARE_UPDATE_DES, descriptionStm32);
        // todo: 此处的判定条件在版本号出现2位数以上时会有问题
        boolean show = getShowNrf(isBootloader, curNrfVersion, versionNrf);
        if (show) {
            bundle.putString(Constant.TAG_NRF_DOWNLOAD_URL, urlPrefix + urlNrf);
            bundle.putString(Constant.TAG_NRF_VERSION_NEW, versionNrf);
            bundle.putString(Constant.TAG_NRF_UPDATE_DES, descriptionNrf);
        }
        return bundle;
    }

    /**
     * @param isBootloader 如果是 Bootloader 模式就直接显示升级，否则去校验版本
     * @param versionNrf
     * @return
     */
    private boolean getShowNrf(boolean isBootloader, String curNrfVersion, String versionNrf) {
        if (isBootloader) {
            return true;
        } else {
            if (!Strings.isNullOrEmpty(curNrfVersion) && !Strings.isNullOrEmpty(versionNrf)) {
                return versionNrf.compareTo(curNrfVersion) > 0
                        || java.util.Objects.equals(curNrfVersion, Constant.BLE_OLDEST_VER);
            } else {
                return false;
            }
        }
    }

    /** 初始化蓝牙前检查权限，为后续在 BleManager 抽离 Activity 作准备，BleManager 持有 Activity 此处会发生内存泄漏。 */
    private void initBle() {
        bleManager.initBle();
    }
}

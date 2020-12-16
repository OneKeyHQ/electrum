package org.haobtc.onekey.ui.activity;

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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.UpdateInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.event.BleScanStopEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.adapter.BleDeviceAdapter;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.utils.ValueAnimatorUtil;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

import static cn.com.heaton.blelibrary.ble.Ble.REQUEST_ENABLE_BT;

/**
 * @author liyan
 */
public class SearchDevicesActivity extends BaseActivity implements BleDeviceAdapter.OnItemBleDeviceClick {

    public static final String MIN_SUPPORT_VERSION = "1.9.7";
    public static final int REQUEST_ID = 65578;
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

    @Override
    public void init() {
        mSearchMode = getIntent().getIntExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
        addBleView();
        bleManager = BleManager.getInstance(this);
        if (mSearchMode != Constant.SearchDeviceMode.MODE_PREPARE) {
            bleManager.initBle();
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_search_devices;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleScanDevice(BleDevice device) {
        if (mBleAdapter != null) {
            System.out.println("find device ====" + device.getBleName());
            mBleAdapter.add(device);
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
            mLayoutView.post(() -> ValueAnimatorUtil
                    .animatorHeightLayout(mLayoutView, mLayoutView.getHeight(), 0));
        }
    }

    public void addBleView() {
        mLayoutView = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_search_device_ble, null);

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
        PreferencesManager.put(this, Constant.BLE_INFO, device.getBleName(), device.getBleAddress());
        bleManager.connDev(device);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadyBle(NotifySuccessfulEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        toNextActivity();
    }

    private void toNextActivity() {
        HardwareFeatures features;
        try {
            features = PyEnv.getFeature(this);
            String firmwareVersion = features.getMajorVersion() + "." + features.getMinorVersion() + "." + features.getPatchVersion();
            if (firmwareVersion.compareTo(MIN_SUPPORT_VERSION) <= 0 ) {
                update(features);
                finish();
                return;
            }
        } catch (Exception e) {
            showToast(getString(R.string.get_hard_msg_error));
            e.printStackTrace();
            finish();
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
                    intent.putExtra(Constant.MNEMONICS, getIntent().getStringExtra(Constant.MNEMONICS));
                    startActivity(intent);
                    finish();
                }
                break;
                // 共管钱包
            case Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON:
                if (features.isInitialized() && !features.isBackupOnly()) {
                    EventBus.getDefault().post(new GetXpubEvent(Constant.COIN_TYPE_BTC));
                } else {
                    showToast(getString(R.string.hard_tip1));
                }
                finish();
                break;
                // 仅连接蓝牙
            case Constant.SearchDeviceMode.MODE_PREPARE:
                if (features.isInitialized() && !features.isBackupOnly()) {
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
                        boolean hasLocalHd = (boolean)PreferencesManager.get(this, "Preferences", Constant.HAS_LOCAL_HD, false);
                        if (hasLocalHd) {
                            showToast(R.string.already_has_local_hd);
                        } else {
                            startActivity(new Intent(this, FindBackupOnlyDeviceActivity.class));
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
                    startActivity(new Intent(this, FindUnInitDeviceActivity.class));
                    finish();
                } else if (features.isBackupOnly()) {
                    boolean hasLocalHd = (boolean)PreferencesManager.get(this, "Preferences", Constant.HAS_LOCAL_HD, false);
                    if (hasLocalHd) {
                        showToast(R.string.already_has_local_hd);
                    } else {
                        startActivity(new Intent(this, FindBackupOnlyDeviceActivity.class));
                    }
                    finish();
                } else {
                    Intent intent = new Intent(this, FindNormalDeviceActivity.class);
                    intent.putExtra(Constant.DEVICE_ID, features.getDeviceId());
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.relode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.relode:
                if (mLoadingLayout.getVisibility() != View.VISIBLE) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                }
                if (mDeviceListLayout.getVisibility() != View.GONE) {
                    mDeviceListLayout.setVisibility(View.GONE);
                }
                bleManager.refreshBleDeviceList();
                break;
        }
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
            bleManager.initBle();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID) {
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
                AlertDialog dialog = bleManager.getAlertDialog();
                if (dialog != null) {
                    dialog.dismiss();
                }
                bleManager.setGpsStatusChange(false);
                bleManager.initBle();
            }
        }
    }
    private void update(HardwareFeatures features) {
        String urlPrefix = "https://onekey.so/";
        String locate = PreferencesManager.get(this, "Preferences", Constant.LANGUAGE, "").toString();
        String info = PreferencesManager.get(this, "Preferences", Constant.UPGRADE_INFO, "").toString();
        if (Strings.isNullOrEmpty(info)) {
            showToast(R.string.get_update_info_failed);
            return;
        }
        String firmwareVersion = features.getMajorVersion() + "." + features.getMinorVersion() + "." + features.getPatchVersion();
        String nrfVersion = features.getBleVer();
        String bleName = features.getBleName();
        String label = features.getLabel();
        String deviceId = features.getDeviceId();
        String bleMac = PreferencesManager.get(this, org.haobtc.onekey.constant.Constant.BLE_INFO, bleName, "").toString();
        Bundle bundle = getBundle(urlPrefix, locate, info);
        bundle.putString(Constant.TAG_FIRMWARE_VERSION, firmwareVersion);
        bundle.putString(Constant.TAG_NRF_VERSION, nrfVersion);
        bundle.putString(Constant.TAG_BLE_NAME, bleName);
        bundle.putString(Constant.BLE_MAC, bleMac);
        bundle.putString(Constant.TAG_LABEL, label);
        bundle.putString(Constant.DEVICE_ID, deviceId);
        bundle.putBoolean(Constant.FORCE_UPDATE, true);
        Intent intent = new Intent(this, HardwareUpgradeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @NonNull
    private Bundle getBundle(String urlPrefix, String locate, String info) {

        UpdateInfo updateInfo = UpdateInfo.objectFromData(info);
        String urlNrf = updateInfo.getNrf().getUrl();
        String urlStm32 = updateInfo.getStm32().getUrl();
        String versionNrf = updateInfo.getNrf().getVersion();
        String versionStm32 = updateInfo.getStm32().getVersion().toString().replace(",", ".");
        versionStm32 = versionStm32.substring(1, versionStm32.length() - 1).replaceAll("\\s+", "");
        String descriptionNrf = "English".equals(locate) ? updateInfo.getNrf().getChangelogEn() : updateInfo.getNrf().getChangelogCn();
        String descriptionStm32 = "English".equals(locate) ? updateInfo.getStm32().getChangelogEn() : updateInfo.getStm32().getChangelogCn();
        if (urlNrf.startsWith("https") || urlStm32.startsWith("https")) {
            urlPrefix = "";
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TAG_FIRMWARE_DOWNLOAD_URL, urlPrefix + urlStm32);
        bundle.putString(Constant.TAG_FIRMWARE_VERSION_NEW, versionStm32);
        bundle.putString(Constant.TAG_FIRMWARE_UPDATE_DES, descriptionStm32);
        bundle.putString(Constant.TAG_NRF_DOWNLOAD_URL, urlPrefix + urlNrf);
        bundle.putString(Constant.TAG_NRF_VERSION_NEW, versionNrf);
        bundle.putString(Constant.TAG_NRF_UPDATE_DES, descriptionNrf);
        return bundle;
    }

}

package org.haobtc.onekey.manager;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.FragmentActivity;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleGlobalConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import com.lxj.xpopup.XPopup;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.disposables.Disposable;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.business.wallet.OnekeyBleConnectCallback;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.BleScanStopEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.ui.base.IBaseView;
import org.haobtc.onekey.ui.dialog.OpenLocationServiceDialog;
import org.haobtc.onekey.ui.dialog.RequestLocationPermissionsDialog;
import org.haobtc.onekey.utils.CommonUtils;

/**
 * ble
 *
 * @author liyan
 */
public final class BleManager {

    private Ble<BleDevice> mBle;
    private BleScanCallback<BleDevice> mBleScanCallBack;
    private static volatile BleManager sInstance;
    private FragmentActivity fragmentActivity;
    private volatile boolean connecting;
    private String currentAddress;
    public static String currentBleName;
    private HardwareFeatures mHardwareFeatures;
    private volatile boolean mShieldingGlobalCallback = false;

    private BleManager(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        Ble.getInstance().setGlobalConnectStatusCallback(mConnectCallback);
    }

    public static BleManager getInstance(FragmentActivity fragmentActivity) {
        if (sInstance == null) {
            synchronized (BleManager.class) {
                if (sInstance == null) {
                    sInstance = new BleManager(fragmentActivity);
                }
            }
        }
        if (fragmentActivity != sInstance.fragmentActivity) {
            sInstance.fragmentActivity = fragmentActivity;
        }
        return sInstance;
    }

    /** init ble */
    public void initBle() {
        if (mBle == null) {
            mBle = Ble.getInstance();
        }
        if (mBleScanCallBack == null) {
            mBleScanCallBack =
                    new BleScanCallback<BleDevice>() {
                        @Override
                        public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                            synchronized (mBle.getLocker()) {
                                if (device != null) {
                                    EventBus.getDefault().post(device);
                                }
                            }
                        }

                        @Override
                        public void onStop() {
                            super.onStop();
                            if (connecting) {
                                connecting = false;
                            }
                            EventBus.getDefault().post(new BleScanStopEvent());
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            super.onScanFailed(errorCode);
                            EventBus.getDefault().post(new BleScanStopEvent());
                        }
                    };
        }

        RxPermissions permissions = new RxPermissions(fragmentActivity);
        // 收不到回调，要手动监听
        Disposable subscribe =
                permissions
                        .requestEachCombined(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(
                                permission -> {
                                    if (permission.granted) {
                                        refreshBleDeviceList();
                                    } else if (permission.shouldShowRequestPermissionRationale) {
                                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask
                                        // again）,那么下次再次启动时，还会提示请求权限的对话框
                                        if (fragmentActivity != null) {
                                            ((IBaseView) fragmentActivity)
                                                    .showToast(R.string.blurtooth_need_permission);
                                            fragmentActivity.finish();
                                        }
                                    } else {
                                        new XPopup.Builder(fragmentActivity)
                                                .dismissOnTouchOutside(false)
                                                .asCustom(
                                                        new RequestLocationPermissionsDialog(
                                                                fragmentActivity))
                                                .show();
                                    }
                                });
    }

    /** jump to open bluetooth */
    private void openBle() {
        if (mBle != null) {
            mBle.turnOnBlueTooth(fragmentActivity);
        }
    }

    /** search ble devices */
    public void refreshBleDeviceList() {
        if (mBle == null) {
            return;
        }
        if (mBle.isBleEnable()) {
            if (checkGpsEnable()) {
                mBle.startScan(mBleScanCallBack);
            }
        } else {
            openBle();
        }
    }

    private final BleWriteCallback<BleDevice> mWriteCallBack =
            new BleWriteCallback<BleDevice>() {

                @Override
                public void onWriteSuccess(
                        BleDevice device, BluetoothGattCharacteristic characteristic) {
                    PyEnv.notifyWriteSuccess();
                }
            };

    /**
     * conn ble device
     *
     * @param device
     */
    public void connDev(BleDevice device) {
        if (connecting) {
            return;
        }
        synchronized (BleManager.class) {
            connecting = true;
            disconnectAllOther(device.getBleAddress());
            if (device.isConnected()) {
                EventBus.getDefault().post(new NotifySuccessfulEvent());
                connecting = false;
                return;
            } else if (device.isConnectting()) {
                return;
            }
            PyEnv.mExecutorService.execute(
                    () -> {
                        currentAddress = device.getBleAddress();
                        Ble.getInstance().connect(device, null);
                    });
        }
    }

    public void disconnectAllOther(String mac) {
        try {
            Ble.getInstance().getConnetedDevices().stream()
                    .filter(bleDevice -> !bleDevice.getBleAddress().equals(mac))
                    .forEach(bleDevice -> Ble.getInstance().disconnect(bleDevice));
        } catch (Exception ignored) {
        }
    }

    public void cancelConnect(String macAddress) {
        BleDevice bleDevice = Ble.getInstance().getBleDevice(macAddress);
        cancelConnect(bleDevice);
    }

    public void cancelConnect(BleDevice device) {
        try {
            Ble.getInstance().cancelConnectting(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    public void connDevByMac(String device, OnekeyBleConnectCallback callback) {
        connecting = true;
        synchronized (BleManager.class) {
            // targetDevice.isConnected() 状态不及时，有 1-2 秒的延迟。未来看情况删掉该判断
            BleDevice targetDevice = Ble.getInstance().getBleDevice(device);
            if (targetDevice != null && targetDevice.isConnected()) {
                EventBus.getDefault().postSticky(new NotifySuccessfulEvent());
                connecting = false;
                callback.onSuccess(targetDevice);
                return;
            }

            disconnectAllOther(device);
            mShieldingGlobalCallback = true;
            PyEnv.mExecutorService.execute(
                    () -> {
                        currentAddress = device;
                        Ble.getInstance()
                                .connect(
                                        device,
                                        new BleConnectCallback<BleDevice>() {

                                            @Override
                                            public void onReady(BleDevice device) {
                                                super.onReady(device);
                                                PyEnv.mExecutorService.execute(
                                                        () -> {
                                                            setNotify(
                                                                    device,
                                                                    new BleNotiftCallback<
                                                                            BleDevice>() {
                                                                        @Override
                                                                        public void onChanged(
                                                                                BleDevice device,
                                                                                BluetoothGattCharacteristic
                                                                                        characteristic) {}

                                                                        @Override
                                                                        public void onNotifySuccess(
                                                                                BleDevice device) {
                                                                            super.onNotifySuccess(
                                                                                    device);
                                                                            mShieldingGlobalCallback =
                                                                                    false;
                                                                            callback.onSuccess(
                                                                                    device);
                                                                        }

                                                                        @Override
                                                                        public void
                                                                                onNotifyCanceled(
                                                                                        BleDevice
                                                                                                device) {
                                                                            super.onNotifyCanceled(
                                                                                    device);
                                                                            mShieldingGlobalCallback =
                                                                                    false;
                                                                            callback
                                                                                    .onConnectCancel(
                                                                                            device);
                                                                        }
                                                                    });
                                                        });
                                                callback.onReady(device);
                                            }

                                            @Override
                                            public void onConnectException(
                                                    BleDevice device, int errorCode) {
                                                super.onConnectException(device, errorCode);
                                                connecting = false;
                                                if (Objects.equals(
                                                        currentAddress, device.getBleName())) {
                                                    EventBus.getDefault()
                                                            .post(
                                                                    BleConnectionEx
                                                                            .BLE_CONNECTION_EX_OTHERS);
                                                    mShieldingGlobalCallback = false;
                                                    EventBus.getDefault().post(new ExitEvent());
                                                    PyEnv.cancelAll();
                                                }
                                                callback.onConnectException(device, errorCode);
                                            }

                                            @Override
                                            public void onConnectTimeOut(BleDevice device) {
                                                super.onConnectTimeOut(device);
                                                connecting = false;
                                                EventBus.getDefault()
                                                        .post(
                                                                BleConnectionEx
                                                                        .BLE_CONNECTION_EX_TIMEOUT);
                                                mShieldingGlobalCallback = false;
                                                callback.onConnectTimeOut(device);
                                            }

                                            @Override
                                            public void onConnectCancel(BleDevice device) {
                                                super.onConnectCancel(device);
                                                mShieldingGlobalCallback = false;
                                                callback.onConnectCancel(device);
                                            }
                                        });
                    });
        }
    }

    /**
     * conn ble device by mac address
     *
     * @param device
     */
    public void connDevByMac(String device) {
        if (connecting) {
            return;
        }
        synchronized (BleManager.class) {
            connecting = true;
            disconnectAllOther(device);
            BleDevice targetDevice = Ble.getInstance().getBleDevice(device);
            if (Objects.nonNull(targetDevice)) {
                if (targetDevice.isConnected()) {
                    EventBus.getDefault().postSticky(new NotifySuccessfulEvent());
                    connecting = false;
                    return;
                } else if (targetDevice.isConnectting()) {
                    return;
                }
            }
            PyEnv.mExecutorService.execute(
                    () -> {
                        currentAddress = device;
                        Ble.getInstance().connect(device, null);
                    });
        }
    }

    /** ble call back */
    private final BleGlobalConnectCallback<BleDevice> mConnectCallback =
            new BleGlobalConnectCallback<BleDevice>() {
                @Override
                public void onConnectionChanged(BleDevice device) {
                    if (device.isDisconnected()
                            && Objects.equals(currentAddress, device.getBleAddress())) {
                        connecting = false;
                    }
                }

                @Override
                public void onReady(BleDevice device) {
                    super.onReady(device);
                    if (!mShieldingGlobalCallback) {
                        setNotify(device);
                    }
                }

                @Override
                public void onConnectException(BleDevice device, int errorCode) {
                    super.onConnectException(device, errorCode);
                    connecting = false;
                    if (Objects.equals(currentAddress, device.getBleName())) {
                        EventBus.getDefault().post(BleConnectionEx.BLE_CONNECTION_EX_OTHERS);
                        EventBus.getDefault().post(new ExitEvent());
                        PyEnv.cancelAll();
                    }
                }

                @Override
                public void onConnectTimeOut(BleDevice device) {
                    super.onConnectTimeOut(device);
                    connecting = false;
                    EventBus.getDefault().post(BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT);
                }
            };

    private void setNotify(BleDevice device, BleNotiftCallback<BleDevice> callback) {
        // buffered a proportion of response
        StringBuffer buffer = new StringBuffer();
        Ble.getInstance()
                .enableNotify(
                        device,
                        true,
                        new BleNotiftCallback<BleDevice>() {
                            @Override
                            public void onChanged(
                                    BleDevice device, BluetoothGattCharacteristic characteristic) {
                                dealBleResponse(characteristic, buffer);
                                callback.onChanged(device, characteristic);
                            }

                            @Override
                            public void onNotifySuccess(BleDevice device) {
                                super.onNotifySuccess(device);
                                Ble.getInstance()
                                        .setMTU(
                                                device.getBleAddress(),
                                                512,
                                                new BleMtuCallback<BleDevice>() {
                                                    @Override
                                                    public void onMtuChanged(
                                                            BleDevice device, int mtu, int status) {
                                                        super.onMtuChanged(device, mtu, status);
                                                        PyEnv.bleEnable(device, mWriteCallBack);
                                                        EventBus.getDefault()
                                                                .post(new NotifySuccessfulEvent());
                                                        connecting = false;
                                                        callback.onNotifySuccess(device);
                                                    }
                                                });
                                currentAddress = device.getBleAddress();
                                currentBleName = device.getBleName();
                            }

                            @Override
                            public void onNotifyCanceled(BleDevice device) {
                                super.onNotifyCanceled(device);
                                callback.onNotifyCanceled(device);
                            }
                        });
    }

    /**
     * setNotify set up notifications when connection is established
     *
     * @param device the object ble device to set enable notification
     */
    private void setNotify(BleDevice device) {
        // buffered a proportion of response
        StringBuffer buffer = new StringBuffer();
        Ble.getInstance()
                .enableNotify(
                        device,
                        true,
                        new BleNotiftCallback<BleDevice>() {
                            @Override
                            public void onChanged(
                                    BleDevice device, BluetoothGattCharacteristic characteristic) {
                                dealBleResponse(characteristic, buffer);
                            }

                            @Override
                            public void onNotifySuccess(BleDevice device) {
                                super.onNotifySuccess(device);
                                Ble.getInstance()
                                        .setMTU(
                                                device.getBleAddress(),
                                                512,
                                                new BleMtuCallback<BleDevice>() {
                                                    @Override
                                                    public void onMtuChanged(
                                                            BleDevice device, int mtu, int status) {
                                                        super.onMtuChanged(device, mtu, status);
                                                        PyEnv.bleEnable(device, mWriteCallBack);
                                                        EventBus.getDefault()
                                                                .post(new NotifySuccessfulEvent());
                                                        connecting = false;
                                                    }
                                                });
                                currentAddress = device.getBleAddress();
                                currentBleName = device.getBleName();
                            }

                            @Override
                            public void onNotifyCanceled(BleDevice device) {
                                super.onNotifyCanceled(device);
                            }
                        });
    }

    /** 整合数据超过MTU引起的的分包数据 */
    private void dealBleResponse(BluetoothGattCharacteristic characteristic, StringBuffer buffer) {
        buffer.append(CommonUtils.bytes2hex(characteristic.getValue()));
        // Log.e(TAG, "receive from hardware: " + CommonUtils.bytes2hex(characteristic.getValue()));
        // \
        // dispatcher it util all response returned
        if ((Integer.parseInt(
                                buffer.substring(
                                        Constant.LENGTH_FILED_START_OFFSET,
                                        Constant.LENGTH_FILED_END_OFFSET),
                                16)
                        + Constant.HEAD_LENGTH)
                == buffer.toString().length() / 2) {
            PyEnv.bleReWriteResponse(buffer.toString());
            // clear the buffer for reuse in one connection
            buffer.delete(0, buffer.length());
        }
    }

    /** 定位功能监控 */
    private final LocationListener locationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {}

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {
                    if (LocationManager.GPS_PROVIDER.equals(provider)) {
                        isGpsStatusChange = true;
                    }
                }

                @Override
                public void onProviderDisabled(String provider) {}
            };

    private LocationManager locationManager;
    private boolean isGpsStatusChange;

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public boolean isGpsStatusChange() {
        return isGpsStatusChange;
    }

    public void setGpsStatusChange(boolean gpsStatusChange) {
        isGpsStatusChange = gpsStatusChange;
    }

    @SuppressLint("MissingPermission")
    private boolean checkGpsEnable() {
        if (locationManager == null) {
            locationManager =
                    (LocationManager)
                            Objects.requireNonNull(
                                    fragmentActivity.getSystemService(LOCATION_SERVICE));
        }
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!ok) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, (float) 0, locationListener);
            new XPopup.Builder(fragmentActivity)
                    .dismissOnTouchOutside(false)
                    .asCustom(new OpenLocationServiceDialog(fragmentActivity))
                    .show();
        }
        return ok;
    }
}

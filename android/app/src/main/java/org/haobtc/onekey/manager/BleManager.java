package org.haobtc.onekey.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.BleScanStopEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.ui.base.IBaseView;
import org.haobtc.onekey.ui.dialog.OpenLocationServiceDialog;
import org.haobtc.onekey.ui.fragment.RequestLocationPermissionsDialog;
import org.haobtc.onekey.utils.CommonUtils;

import java.util.Objects;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import io.reactivex.disposables.Disposable;

import static android.content.Context.LOCATION_SERVICE;

/**
 * ble
 * @author liyan
 */
public final class BleManager {

    private Ble<BleDevice> mBle;
    private BleScanCallback<BleDevice> mBleScanCallBack;
    private static volatile BleManager sInstance;
    private FragmentActivity fragmentActivity;
    private static boolean connecting;


    private BleManager(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
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
    /**
     * init ble
     */
    public void initBle() {
        if (mBle == null) {
            mBle = Ble.getInstance();
        }
        if (mBleScanCallBack == null) {
            mBleScanCallBack = new BleScanCallback<BleDevice>() {
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
        Disposable subscribe = permissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(
                        permission -> {
                            if (permission.granted) {
                                refreshBleDeviceList();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                if (fragmentActivity != null) {
                                    ((IBaseView) fragmentActivity).showToast(R.string.blurtooth_need_permission);
                                    fragmentActivity.finish();
                                }
                            } else {
                                new RequestLocationPermissionsDialog().show(fragmentActivity.getSupportFragmentManager(), "");
                            }
                        }
                );
    }

    /**
     * jump to open bluetooth
     */
    private void openBle() {
        if (mBle != null) {
            mBle.turnOnBlueTooth(fragmentActivity);
        }
    }

    /**
     * search ble devices
     */
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
    private final BleWriteCallback<BleDevice> mWriteCallBack = new BleWriteCallback<BleDevice>() {

        @Override
        public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
            PyEnv.sBle.put(PyConstant.WRITE_SUCCESS, true);
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
            if (device.isConnected()) {
                connecting = false;
                EventBus.getDefault().post(new NotifySuccessfulEvent());
                return;
            }
            disconnectAllOther(device.getBleAddress());
            Ble.getInstance().connect(device, mConnectCallback);
        }
    }

    public void disconnectAllOther(String mac) {
        Ble.getInstance().getConnetedDevices().stream().filter(bleDevice -> !bleDevice.getBleAddress()
                .equals(mac)).forEach(bleDevice -> Ble.getInstance().disconnect(bleDevice));
    }
    /**
     * conn ble device by mac address
     *
     * @param device
     */
    public void connDevByMac(String device) {
        disconnectAllOther(device);
        if (Ble.getInstance().getConnetedDevices().isEmpty()) {
            Ble.getInstance().connect(device, mConnectCallback);
        } else {
            EventBus.getDefault().postSticky(new NotifySuccessfulEvent());
        }
    }

    /**
     * ble call back
     */
    private final BleConnectCallback<BleDevice> mConnectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {

        }

        @Override
        public void onReady(BleDevice device) {
            super.onReady(device);
            setNotify(device);
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            connecting = false;
            Toast.makeText(MyApplication.getInstance(), R.string.bluetooth_exception,Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(BleConnectionEx.BLE_CONNECTION_EX_OTHERS);
            EventBus.getDefault().post(new ExitEvent());
            PyEnv.cancelAll();
        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            connecting = false;
            EventBus.getDefault().post(BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT);
        }
    };


    /**
     * setNotify set up notifications when connection is established
     *
     * @param device the object ble device to set enable notification
     */
    private void setNotify(BleDevice device) {
        // buffered a proportion of response
        StringBuffer buffer = new StringBuffer();
        Ble.getInstance().enableNotify(device, true, new BleNotiftCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                dealBleResponse(characteristic, buffer);
            }

            @Override
            public void onNotifySuccess(BleDevice device) {
                super.onNotifySuccess(device);
                connecting = false;
                Ble.getInstance().setMTU(device.getBleAddress(), 512, new BleMtuCallback<BleDevice>() {
                    @Override
                    public void onMtuChanged(BleDevice device, int mtu, int status) {
                        super.onMtuChanged(device, mtu, status);
                        PyEnv.bleEnable(device, mWriteCallBack);
                        EventBus.getDefault().post(new NotifySuccessfulEvent());
                    }

                });
            }


            @Override
            public void onNotifyCanceled(BleDevice device) {
                super.onNotifyCanceled(device);
            }
        });
    }
    /**
     * 整合数据超过MTU引起的的分包数据
     * */
    private void dealBleResponse(BluetoothGattCharacteristic characteristic, StringBuffer buffer) {
        buffer.append(CommonUtils.bytes2hex(characteristic.getValue()));
        // Log.e(TAG, "receive from hardware: " + CommonUtils.bytes2hex(characteristic.getValue())); \
        // dispatcher it util all response returned
        if ((Integer.parseInt(buffer.substring(Constant.LENGTH_FILED_START_OFFSET, Constant.LENGTH_FILED_END_OFFSET), 16)
                + Constant.HEAD_LENGTH) == buffer.toString().length() / 2) {
            PyEnv.bleReWriteResponse(buffer.toString());
            // clear the buffer for reuse in one connection
            buffer.delete(0, buffer.length());
        }
    }
    /**
     * 定位功能监控
     * */
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
                isGpsStatusChange = true;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
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
            locationManager = (LocationManager) Objects.requireNonNull(fragmentActivity.getSystemService(LOCATION_SERVICE));
        }
        boolean ok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!ok) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float) 0, locationListener);
            new OpenLocationServiceDialog().show(fragmentActivity.getSupportFragmentManager(), "");
        }
        return ok;
    }
}

package org.haobtc.onekey.manager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.BleScanStopEvent;
import org.haobtc.onekey.event.NotifySuccessfulEvent;
import org.haobtc.onekey.mvp.base.IBaseView;
import org.haobtc.onekey.utils.CommonUtils;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

/**
 * ble
 * @author liyan
 */
public final class BleManager {

    private Ble<BleDevice> mBle;
    private BleDevice mBleDevice;
    private BleScanCallback<BleDevice> mBleScanCallBack;
    private static volatile BleManager sInstance;
    private FragmentActivity fragmentActivity;


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
        return sInstance;
    }
    /**
     * init ble
     */
    public void initBle() {
        mBle = Ble.getInstance();
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
        final RxPermissions permissions = new RxPermissions(fragmentActivity);
        permissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(
                        granted -> {
                            if (granted) {
                                if (mBle.isBleEnable()) {
                                    refreshBleDeviceList();
                                    return;
                                }
                                openBle();
                            } else {
                                if (fragmentActivity != null) {
                                    ((IBaseView)fragmentActivity).showToast(R.string.no_permission);
                                }
                            }
                        }
                ).dispose();
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
            mBle.startScan(mBleScanCallBack);
        } else {
           openBle();
        }

    }
    private final BleWriteCallback<BleDevice> mWriteCallBack = new BleWriteCallback<BleDevice>() {

        @Override
        public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
            // Log.d(TAG, "send successful:" + CommonUtils.bytes2hex(characteristic.getValue()));
            PyEnv.sBle.put(PyConstant.WRITE_SUCCESS, true);
        }
    };


    /**
     * conn ble device
     *
     * @param device
     */
    public void connDev(BleDevice device) {
        if (device.isConnected()) {
            EventBus.getDefault().post(new NotifySuccessfulEvent());
            return;
        }
        mBleDevice = device;
        BluetoothDevice mBluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mBleDevice.getBleAddress());
        Ble.getInstance().getConnetedDevices().stream().filter(bleDevice -> !bleDevice.getBleAddress()
                .equals(mBleDevice.getBleAddress())).forEach(bleDevice -> Ble.getInstance().disconnect(bleDevice));
        Ble.getInstance().connect(mBleDevice, mConnectCallback);
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

        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);

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

}

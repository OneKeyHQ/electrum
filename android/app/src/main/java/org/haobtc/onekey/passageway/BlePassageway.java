package org.haobtc.onekey.passageway;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.utils.CommonUtils;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

/**
 * ble
 */
public final class BlePassageway {

    private BleDevice mBleDevice;
    private BluetoothDevice mBluetoothDevice;
    private BleConnectCallBack mConnectCallBack;
    private static volatile BlePassageway sInstance;


    private BlePassageway() {
    }

    public static BlePassageway getInstance() {
        if (sInstance == null) {
            synchronized (BlePassageway.class) {
                if (sInstance == null) {
                    sInstance = new BlePassageway();
                }
            }
        }
        return sInstance;
    }

    private final BleWriteCallback<BleDevice> mWriteCallBack = new BleWriteCallback<BleDevice>() {

        @Override
        public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
            // Log.d(TAG, "send successful:" + CommonUtils.bytes2hex(characteristic.getValue()));
            HandleCommands.sBle.put(PyConstant.WRITE_SUCCESS, true);
        }
    };


    /**
     * conn ble device
     *
     * @param device
     */
    public void connDev(BleDevice device, BleConnectCallBack callBack) {
        if (device.isConnected()) {
            callBack.connectSucceeded();
            return;
        }
        mConnectCallBack = callBack;
        mBleDevice = device;
        mBluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mBleDevice.getBleAddress());
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
            if (mConnectCallBack != null) {
                mConnectCallBack.connectFailed();
            }
        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            if (mConnectCallBack != null) {
                mConnectCallBack.connectFailed();
            }
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
                buffer.append(CommonUtils.bytes2hex(characteristic.getValue()));
                // Log.e(TAG, "receive from hardware: " + CommonUtils.bytes2hex(characteristic.getValue()));
                // dispatcher it util all response returned
                if ((Integer.parseInt(buffer.substring(10, 18), 16) + 9) == buffer.toString().length() / 2) {
                    HandleCommands.sBleHandler.put(PyConstant.RESPONSE, buffer.toString());
                    // clear the buffer for reuse in one connection
                    buffer.delete(0, buffer.length());
                }
            }

            @Override
            public void onNotifySuccess(BleDevice device) {
                super.onNotifySuccess(device);
                Ble.getInstance().setMTU(device.getBleAddress(), 512, new BleMtuCallback<BleDevice>() {
                    @Override
                    public void onMtuChanged(BleDevice device, int mtu, int status) {
                        super.onMtuChanged(device, mtu, status);
//                        Log.d(TAG, "onNotifySuccess: " + device.getBleName());
                        HandleCommands.sBleTransport.put(PyConstant.ENABLED, true);
                        HandleCommands.sNfcTransport.put(PyConstant.ENABLED, false);
                        HandleCommands.sUsbTransport.put(PyConstant.ENABLED, false);
                        HandleCommands.sBleHandler.put(PyConstant.BLE, Ble.getInstance());
                        HandleCommands.sBleHandler.put(PyConstant.BLE_DEVICE, device);
                        HandleCommands.sBleHandler.put(PyConstant.CALL_BACK, mWriteCallBack);
                        HandleCommands.sBle.put(PyConstant.WRITE_SUCCESS, true);
                        if (mConnectCallBack != null) {
                            mConnectCallBack.connectSucceeded();
                        }
                    }

                });

            }


            @Override
            public void onNotifyCanceled(BleDevice device) {
                super.onNotifyCanceled(device);

            }
        });

    }


    public interface BleConnectCallBack {
        void connectSucceeded();

        void connectFailed();
    }

}

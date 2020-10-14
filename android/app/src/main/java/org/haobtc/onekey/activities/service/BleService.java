package org.haobtc.onekey.activities.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ConnectingEvent;
import org.haobtc.onekey.event.DfuEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.HandlerEvent;
import org.haobtc.onekey.fragment.BleDeviceRecyclerViewAdapter;
import org.haobtc.onekey.utils.CommonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.ble;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.bleHandler;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.bleTransport;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isDfu;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.usbTransport;

/**
 * BleService deal with ble connect
 * @author liyan
 */
public class BleService extends Service {
    private Ble<BleDevice> mBle;
    public static final String TAG = BleService.class.getSimpleName();
    private BluetoothDevice bluetoothDevice;
    private final IntentFilter bondFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
    private boolean isBonded;
    private BleDevice mBleDevice;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mBle = Ble.getInstance();
        registerReceiver(broadcastReceiver, bondFilter);

    }

    private final BleWriteCallback<BleDevice> writeCallBack = new BleWriteCallback<BleDevice>() {

        @Override
        public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
           // Log.d(TAG, "send successful:" + CommonUtils.bytes2hex(characteristic.getValue()));
            ble.put("WRITE_SUCCESS", true);
        }
    };


    /**
     * the ble callback
     * */
    private final BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            if (device.isConnectting()) {
                Log.i(TAG, "connecting---" + device.getBleName());
            }
            if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                return;
            }
            if (device.isConnected()) {
                Log.i(TAG, "connected---" + device.getBleName());
            }
        }

        @Override
        public void onServicesDiscovered(BleDevice device, List<BluetoothGattService> gattServices) {
            super.onServicesDiscovered(device, gattServices);
        }

        @Override
        public void onReady(BleDevice device) {
            super.onReady(device);
            setNotify(device);
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            Log.e(TAG, String.format("连接异常，异常状态码: %d", errorCode));
            switch (errorCode) {
                case 2521:
                 //   removeBond(bluetoothDevice);
                case 2523:
                case 133:
                case 8:
                case 59:
                default:
                    if (ble != null) {
                        ble.put("IS_CANCEL", true);
                    }
                    EventBus.getDefault().post(new ExitEvent());
                    Ble.getInstance().refreshDeviceCache(bluetoothDevice.getAddress());
            }
        }

        @Override
        public void onConnectCancel(BleDevice device) {
            super.onConnectCancel(device);
            Log.i(TAG, "正在取消连接");

        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            Log.e(TAG, String.format("连接设备==%s超时", device.getBleName()));
            EventBus.getDefault().post(new ExitEvent());
        }
    };
    /**
     * handler send notify to subscribers when everything satisfied
     * */
    private void handle() {
//        isNFC = false;
        EventBus.getDefault().post(new HandlerEvent());
    }
    /**
     * setNotify set up notifications when connection is established
     * @param device the object ble device to set enable notification
     * */
    private void setNotify(BleDevice device) {
       // buffered a proportion of response
        StringBuffer buffer = new StringBuffer();
        mBle.enableNotify(device, true, new BleNotiftCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                buffer.append(CommonUtils.bytes2hex(characteristic.getValue()));
                Log.e(TAG, "receive from hardware: " + CommonUtils.bytes2hex(characteristic.getValue()));
                // dispatcher it util all response returned
                if ((Integer.parseInt(buffer.substring(10, 18), 16) + 9) == buffer.toString().length()/2) {
                    bleHandler.put("RESPONSE", buffer.toString());
                    // clear the buffer for reuse in one connection
                    buffer.delete(0, buffer.length());
                }
            }
            @Override
            public void onNotifySuccess(BleDevice device) {
                super.onNotifySuccess(device);
                // request mtu to enable DLE(Data Length Extension)feature
                mBle.setMTU(device.getBleAddress(), 512, new BleMtuCallback<BleDevice>() {
                    @Override
                    public void onMtuChanged(BleDevice device, int mtu, int status) {
                        super.onMtuChanged(device, mtu, status);
                        Log.d(TAG, "onNotifySuccess: " + device.getBleName());
                        bleTransport.put("ENABLED", true);
                        nfcTransport.put("ENABLED", false);
                        usbTransport.put("ENABLED", false);
                        bleHandler.put("BLE", mBle);
                        bleHandler.put("BLE_DEVICE", device);
                        bleHandler.put("CALL_BACK", writeCallBack);
                        ble.put("WRITE_SUCCESS", true);
                        handle();
                    }
                });
            }

            @Override
            public void onNotifyCanceled(BleDevice device) {
                super.onNotifyCanceled(device);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            if (mBle.isScanning()) {
                mBle.stopScan();
            }
            mBleDevice = BleDeviceRecyclerViewAdapter.mBleDevice;
            bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mBleDevice.getBleAddress());
            // 禁止同时连接多个BiXinKEY设备
            mBle.getConnetedDevices().stream().filter(bleDevice -> !bleDevice.getBleAddress().equals(mBleDevice.getBleAddress()))
                    .forEach(bleDevice -> mBle.disconnect(bleDevice));
            if (isDfu) {
                mBle.disconnectAll();
                new Handler().postDelayed(() -> EventBus.getDefault().postSticky(new DfuEvent(DfuEvent.START_DFU)), 2000);
            } else {
                isBonded = false;
                switch (bluetoothDevice.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
                        if (mBleDevice.isConnected()) {
                            handle();
                        } else if (mBleDevice.isConnectting()) {
                            mBle.cancelConnectting(mBleDevice);
                            mBle.connect(mBleDevice, connectCallback);
                        } else if (mBleDevice.isDisconnected()) {
                            mBle.connect(mBleDevice, connectCallback);
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        boolean bond =  bluetoothDevice.createBond();
                        if (!bond) {
                            EventBus.getDefault().post(new ExitEvent());
                            Log.e("BLE", "无法绑定设备");
                            Toast.makeText(this, getString(R.string.dont_band), Toast.LENGTH_SHORT).show();
                        }
                        isBonded = true;
                        break;
                    default:
                }
            }

        return Service.START_NOT_STICKY;
    }
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           synchronized (this) {
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice  device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int previousState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    Log.d(TAG, "receive broadcast===" + action + "state===" + bluetoothDevice.getBondState() + "===previous===" + previousState + "===STATE" + state);
                    // In order to support pair and upgrade ble one time
                    if (state == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING && !isBonded) {
                        new Handler().postDelayed(() -> EventBus.getDefault().postSticky(new DfuEvent(3)), 2000);
                        return;
                    }
                    if (state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, String.format("设备==%s==,配对信息已清除", bluetoothDevice.getName()));
                    }
                    if (bluetoothDevice == null || device == null || !device.getAddress().equals(bluetoothDevice.getAddress())) {
                        return;
                    }
                    if (state == BluetoothDevice.BOND_BONDING && previousState == BluetoothDevice.BOND_NONE) {
                        EventBus.getDefault().post(new ConnectingEvent());
                        return;
                    }
                    if (state == BluetoothDevice.BOND_NONE && previousState == BluetoothDevice.BOND_BONDING) {
                        EventBus.getDefault().post(new ExitEvent());
                        Log.d(TAG, String.format("您已拒绝与设备==%s==配对", bluetoothDevice.getName()));
                        return;
                    }
                    if (state == BluetoothDevice.BOND_BONDED && previousState == BluetoothDevice.BOND_BONDING  && isBonded) {
                        if (mBleDevice.isConnected()) {
                            handle();
                        } else {
                            mBle.connect(mBleDevice, connectCallback);
                        }
                    }
                }
             }
        }
    };
    void removeBond(BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            return;
        }
        try {
            final Method removeBond = device.getClass().getMethod("removeBond");
            removeBond.invoke(device);
            } catch (final NoSuchMethodException ignored) {
            } catch (IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}

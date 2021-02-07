package cn.com.heaton.blelibrary.ble.proxy;

import android.util.Log;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
import cn.com.heaton.blelibrary.ble.callback.BleReadCallback;
import cn.com.heaton.blelibrary.ble.callback.BleReadRssiCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteEntityCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.EntityData;
import cn.com.heaton.blelibrary.ble.model.ScanRecord;
import cn.com.heaton.blelibrary.ble.request.ConnectRequest;
import cn.com.heaton.blelibrary.ble.request.MtuRequest;
import cn.com.heaton.blelibrary.ble.request.NotifyRequest;
import cn.com.heaton.blelibrary.ble.request.ReadRequest;
import cn.com.heaton.blelibrary.ble.request.ReadRssiRequest;
import cn.com.heaton.blelibrary.ble.request.Rproxy;
import cn.com.heaton.blelibrary.ble.request.ScanRequest;
import cn.com.heaton.blelibrary.ble.request.WriteRequest;
import java.util.UUID;

/** Created by LiuLei on 2017/10/30. */
public class RequestImpl<T extends BleDevice> implements RequestLisenter<T> {

    public static RequestImpl newRequestImpl() {
        return new RequestImpl();
    }

    @Override
    public void startScan(BleScanCallback<T> callback, long scanPeriod) {
        ScanRequest<T> request = Rproxy.getRequest(ScanRequest.class);
        request.startScan(callback, scanPeriod);
    }

    @Override
    public void stopScan() {
        ScanRequest request = Rproxy.getRequest(ScanRequest.class);
        request.stopScan();
    }

    @Override
    public boolean connect(T device, BleConnectCallback<T> callback) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        BleLog.BluetoothState("connect", device);
        return request.connect(device, callback);
    }

    @Override
    public boolean connect(String address, BleConnectCallback<T> callback) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        BleLog.BluetoothState("connect address " + address, null);
        return request.connect(address, callback);
    }

    @Override
    public void notify(T device, BleNotiftCallback<T> callback) {
        NotifyRequest<T> request = Rproxy.getRequest(NotifyRequest.class);
        BleLog.BluetoothState("notify", device);
        request.notify(device, true, callback);
    }

    @Override
    public void cancelNotify(T device, BleNotiftCallback<T> callback) {
        NotifyRequest<T> request = Rproxy.getRequest(NotifyRequest.class);
        BleLog.BluetoothState("cancelNotify", device);
        request.notify(device, false, callback);
    }

    @Override
    public void enableNotify(T device, boolean enable, BleNotiftCallback<T> callback) {
        NotifyRequest<T> request = Rproxy.getRequest(NotifyRequest.class);
        BleLog.BluetoothState("enableNotify", device);
        request.notify(device, enable, callback);
    }

    @Override
    public void enableNotifyByUuid(
            T device,
            boolean enable,
            UUID serviceUUID,
            UUID characteristicUUID,
            BleNotiftCallback<T> callback) {
        NotifyRequest<T> request = Rproxy.getRequest(NotifyRequest.class);
        BleLog.BluetoothState("enableNotifyByUuid", device);
        request.notifyByUuid(device, enable, serviceUUID, characteristicUUID, callback);
    }

    @Override
    public void disconnect(T device) {
        ConnectRequest request = Rproxy.getRequest(ConnectRequest.class);
        BleLog.BluetoothState("disconnect", device);
        Log.e(BleLog.TAG, Log.getStackTraceString(new Throwable()));
        request.disconnect(device);
    }

    @Override
    public void disconnect(T device, BleConnectCallback<T> callback) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        BleLog.BluetoothState("disconnect", device);
        Log.e(BleLog.TAG, Log.getStackTraceString(new Throwable()));
        request.disconnect(device, callback);
    }

    @Override
    public boolean read(T device, BleReadCallback<T> callback) {
        ReadRequest<T> request = Rproxy.getRequest(ReadRequest.class);
        BleLog.BluetoothState("read", device);
        return request.read(device, callback);
    }

    @Override
    public boolean readByUuid(
            T device, UUID serviceUUID, UUID characteristicUUID, BleReadCallback<T> callback) {
        ReadRequest<T> request = Rproxy.getRequest(ReadRequest.class);
        BleLog.BluetoothState("readByUuid", device);
        return request.readByUuid(device, serviceUUID, characteristicUUID, callback);
    }

    @Override
    public boolean readRssi(T device, BleReadRssiCallback<T> callback) {
        ReadRssiRequest<T> request = Rproxy.getRequest(ReadRssiRequest.class);
        BleLog.BluetoothState("readRssi", device);
        return request.readRssi(device, callback);
    }

    @Override
    public boolean write(T device, byte[] data, BleWriteCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        BleLog.BluetoothState("write data:" + ScanRecord.bytesToHex(data), device);
        return request.write(device, data, callback);
    }

    @Override
    public boolean writeByUuid(
            T device,
            byte[] data,
            UUID serviceUUID,
            UUID characteristicUUID,
            BleWriteCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        BleLog.BluetoothState("writeByUuid data:" + ScanRecord.bytesToHex(data), device);
        return request.writeByUuid(device, data, serviceUUID, characteristicUUID, callback);
    }

    @Override
    public void writeEntity(
            T device, byte[] data, int packLength, int delay, BleWriteEntityCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        BleLog.BluetoothState("writeEntity data:" + ScanRecord.bytesToHex(data), device);
        request.writeEntity(device, data, packLength, delay, callback);
    }

    @Override
    public void writeEntity(EntityData entityData, BleWriteEntityCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        request.writeEntity(entityData, callback);
    }

    @Override
    public void cancelWriteEntity() {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        request.cancelWriteEntity();
    }

    @Override
    public boolean setMtu(String address, int mtu, BleMtuCallback<T> callback) {
        MtuRequest<T> request = Rproxy.getRequest(MtuRequest.class);
        BleLog.BluetoothState("setMtu address:" + address + " mtu:" + mtu, null);
        return request.setMtu(address, mtu, callback);
    }
}

package org.haobtc.onekey.mvp.view;

import org.haobtc.onekey.mvp.base.IBaseView;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public interface ISearchDevicesView extends IBaseView {


    void onBleScanDevice(BleDevice device);

    void onBleScanStop();

    void addBleView();

    void addNfcView();

    void addUsbView();
}

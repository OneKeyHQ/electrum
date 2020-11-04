package org.haobtc.onekey.ui.listener;

import org.haobtc.onekey.mvp.base.IBaseListener;

public interface ISetDevicePassListener extends IBaseListener,IUpdateTitleListener {

    void onSetDevicePassSuccess();

    void onResetPin();

}

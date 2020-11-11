package org.haobtc.onekey.ui.listener;

import org.haobtc.onekey.mvp.base.IBaseListener;

public interface IVerifyConnListener extends IBaseListener, IUpdateTitleListener {

    void onVerifyComplete(boolean ret);
}

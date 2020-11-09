package org.haobtc.onekey.ui.listener;

import org.haobtc.onekey.bean.CoinBean;
import org.haobtc.onekey.mvp.base.IBaseListener;

public interface ISelectCoinListener extends IBaseListener, IUpdateTitleListener {

    void onCoinChoose(CoinBean bean);
}

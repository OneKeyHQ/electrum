package org.haobtc.onekey.ui.listener;

import org.haobtc.onekey.bean.MnemonicInfo;
import org.haobtc.onekey.mvp.base.IBaseListener;

import java.util.List;

public interface IImportMnemonicToDeviceListener extends IBaseListener {

    void onImport(List<MnemonicInfo> list);
}

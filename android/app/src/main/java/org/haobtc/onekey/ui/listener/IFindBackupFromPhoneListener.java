package org.haobtc.onekey.ui.listener;

import org.haobtc.onekey.bean.BackupWalletBean;
import org.haobtc.onekey.mvp.base.IBaseListener;

public interface IFindBackupFromPhoneListener extends IBaseListener,IUpdateTitleListener {

    void onBackupToRecovery(BackupWalletBean bean);
}

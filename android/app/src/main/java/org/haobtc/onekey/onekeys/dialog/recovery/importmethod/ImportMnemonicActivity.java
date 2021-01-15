package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;


import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportMnemonicFragment;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;
import org.haobtc.onekey.utils.NavUtils;


public class ImportMnemonicActivity extends BaseActivity implements ImportMnemonicFragment.OnImportMnemonicCallback {
    @Override
    public int getLayoutId() {
        return R.layout.activity_import_mnemonic;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }


    @Override
    public void onImportMnemonic(String mnemonic) {
        showSelectDialog(mnemonic);
    }

    private void showSelectDialog(String strNewseed) {
        new XPopup.Builder(mContext).asCustom(new SelectWalletTypeDialog(mContext, new SelectWalletTypeDialog.onClickListener() {
            @Override
            public void onClick(int purpose) {
                EventBus.getDefault().post(new ResultEvent(strNewseed));
                NavUtils.gotoImportWalletSetNameActivity(mContext, purpose);
                finish();
            }
        })).show();
    }

    @Override
    public boolean requireSecure() {
        return true;
    }

}

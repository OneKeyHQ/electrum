package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportPrivateKeyFragment.OnImportPrivateKeyCallback;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;
import org.haobtc.onekey.utils.NavUtils;

public class ImportPrivateKeyActivity extends BaseActivity implements OnImportPrivateKeyCallback, OnFinishViewCallBack {

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_private_key;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public boolean requireSecure() {
        return true;
    }

    private void showSelectDialog(String privateKey) {
        new XPopup.Builder(mContext).asCustom(new SelectWalletTypeDialog(mContext, new SelectWalletTypeDialog.onClickListener() {
            @Override
            public void onClick(int purpose) {
                EventBus.getDefault().post(new ResultEvent(privateKey));
                NavUtils.gotoImportWalletSetNameActivity(mContext, purpose);
                finish();
            }
        })).show();
    }

    @Override
    public void onImportPrivateKey(String privateKey) {
        showSelectDialog(privateKey);
    }

    @Override
    public void onFinishView() {
        finish();
    }
}

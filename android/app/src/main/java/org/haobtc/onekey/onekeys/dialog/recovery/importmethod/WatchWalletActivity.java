package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;


import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportWatchWalletFragment;

public class WatchWalletActivity extends BaseActivity implements OnFinishViewCallBack, ImportWatchWalletFragment.OnImportWatchAddressCallback {
    @Override
    public int getLayoutId() {
        return R.layout.activity_watch_wallet;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void onImportWatchAddress(String watchAddress) {
        EventBus.getDefault().post(new ResultEvent(watchAddress));
        Intent intent = new Intent(WatchWalletActivity.this, ImportWalletSetNameActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFinishView() {
        finish();
    }
}

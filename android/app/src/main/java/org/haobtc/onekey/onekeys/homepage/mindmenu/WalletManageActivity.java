package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletManageActivity extends BaseActivity {

    @BindView(R.id.text_safe)
    TextView textSafe;
    @BindView(R.id.rel_export_word)
    RelativeLayout relExportWord;
    private String deleteHdWalletName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manage;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.rel_export_word, R.id.rel_delete_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_export_word:
                Intent intent1 = new Intent(WalletManageActivity.this, SetHDWalletPassActivity.class);
                intent1.putExtra("importHdword", "importHdword");
                startActivity(intent1);
                break;
            case R.id.rel_delete_wallet:
                Intent intent = new Intent(WalletManageActivity.this, DeleteWalletActivity.class);
                intent.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent);
                break;
        }
    }

    @Subscribe
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
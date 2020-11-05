package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletManageActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manage;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
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
                intent1.putExtra("importHdword","importHdword");
                startActivity(intent1);
                break;
            case R.id.rel_delete_wallet:
                Intent intent = new Intent(WalletManageActivity.this, DeleteWalletActivity.class);
                startActivity(intent);
                break;
        }
    }
}
package org.haobtc.onekey.onekeys.dialog.recovery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportKeystoreActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportMnemonicActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportPrivateKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.WatchWalletActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseImportMethodActivity extends BaseActivity {

    @BindView(R.id.rel_import_keystore)
    RelativeLayout relImportKeystore;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_import_method;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String importType = getIntent().getStringExtra("importType");
        if ("BTC".equals(importType)) {
            relImportKeystore.setVisibility(View.GONE);
        }else{
            relImportKeystore.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.rel_import_private, R.id.rel_import_help, R.id.rel_import_keystore, R.id.rel_watch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_import_private:
                Intent intent = new Intent(ChooseImportMethodActivity.this, ImportPrivateKeyActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_import_help:
                Intent intent1 = new Intent(ChooseImportMethodActivity.this, ImportMnemonicActivity.class);
                startActivity(intent1);
                break;
            case R.id.rel_import_keystore:
                //eth
                Intent intent2 = new Intent(ChooseImportMethodActivity.this, ImportKeystoreActivity.class);
                startActivity(intent2);
                break;
            case R.id.rel_watch:
                Intent intent3 = new Intent(ChooseImportMethodActivity.this, WatchWalletActivity.class);
                startActivity(intent3);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
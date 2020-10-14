package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportPrivateKeyActivity extends BaseActivity {

    @BindView(R.id.edit_input_private)
    EditText editInputPrivate;
    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    @BindView(R.id.btn_import)
    Button btnImport;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_private_key;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.img_scan, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_scan:
                break;
            case R.id.btn_import:
                break;
        }
    }
}
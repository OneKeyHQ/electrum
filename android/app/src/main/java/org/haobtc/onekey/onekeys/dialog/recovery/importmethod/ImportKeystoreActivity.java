package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportKeystoreFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportKeystoreActivity extends BaseActivity implements ImportKeystoreFragment.OnImportKeystoreCallback, OnFinishViewCallBack {

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_keystore;
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

    @Override
    public void onImportKeystore(String keystore, String password) {

    }

    @Override
    public void onFinishView() {
        finish();
    }
}

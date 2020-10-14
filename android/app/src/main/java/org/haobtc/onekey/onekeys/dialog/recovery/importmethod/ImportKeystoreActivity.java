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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportKeystoreActivity extends BaseActivity {

    @BindView(R.id.edit_keystore_content)
    EditText editKeystoreContent;
    @BindView(R.id.edit_keystore_pass)
    EditText editKeystorePass;
    @BindView(R.id.img_eye_yes)
    ImageView imgEyeYes;
    @BindView(R.id.img_eye_no)
    ImageView imgEyeNo;
    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    @BindView(R.id.btn_import)
    Button btnImport;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_keystore;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.img_scan, R.id.img_eye_yes, R.id.img_eye_no, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_scan:
                break;
            case R.id.img_eye_yes:
                imgEyeYes.setVisibility(View.GONE);
                imgEyeNo.setVisibility(View.VISIBLE);
                editKeystorePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.img_eye_no:
                imgEyeYes.setVisibility(View.VISIBLE);
                imgEyeNo.setVisibility(View.GONE);
                editKeystorePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case R.id.btn_import:
                break;
        }
    }
}
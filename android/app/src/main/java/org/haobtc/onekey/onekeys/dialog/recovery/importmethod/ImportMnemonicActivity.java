package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chaquo.python.Kwarg;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.SetDeriveWalletNameActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportMnemonicActivity extends BaseActivity {

    @BindView(R.id.edit_one)
    EditText editOne;
    @BindView(R.id.edit_two)
    EditText editTwo;
    @BindView(R.id.edit_three)
    EditText editThree;
    @BindView(R.id.edit_four)
    EditText editFour;
    @BindView(R.id.edit_five)
    EditText editFive;
    @BindView(R.id.edit_six)
    EditText editSix;
    @BindView(R.id.edit_seven)
    EditText editSeven;
    @BindView(R.id.edit_eight)
    EditText editEight;
    @BindView(R.id.edit_nine)
    EditText editNine;
    @BindView(R.id.edit_ten)
    EditText editTen;
    @BindView(R.id.edit_eleven)
    EditText editEleven;
    @BindView(R.id.edit_twelve)
    EditText editTwelve;
    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_mnemonic;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_recovery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_recovery:
                if (TextUtils.isEmpty(editSetWalletName.getText().toString())){
                    mToast(getString(R.string.please_input_walletname));
                    return;
                }
                String strone = editOne.getText().toString();
                String strtwo = editTwo.getText().toString();
                String strthree = editThree.getText().toString();
                String strfour = editFour.getText().toString();
                String strfive = editFive.getText().toString();
                String strsix = editSix.getText().toString();
                String strseven = editSeven.getText().toString();
                String streight = editEight.getText().toString();
                String strnine = editNine.getText().toString();
                String strten = editTen.getText().toString();
                String streleven = editEleven.getText().toString();
                String strtwelve = editTwelve.getText().toString();
                if ((TextUtils.isEmpty(strone) || TextUtils.isEmpty(strtwo) || TextUtils.isEmpty(strthree) || TextUtils.isEmpty(strfour))
                        || TextUtils.isEmpty(strfive) || TextUtils.isEmpty(strsix) || TextUtils.isEmpty(strseven) || TextUtils.isEmpty(streight)
                        || TextUtils.isEmpty(strnine) || TextUtils.isEmpty(strten) || TextUtils.isEmpty(streleven) || TextUtils.isEmpty(strtwelve)) {
                    mToast(getString(R.string._12_help_word));
                    return;
                }
                String strNewseed = strone + " " + strtwo + " " + strthree + " " + strfour + " " + strfive + " " + strsix + " " + strseven + " " + streight + " " + strnine + " " + strten + " " + streleven + " " + strtwelve;
                Intent intent = new Intent(ImportMnemonicActivity.this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword", "importMnemonic");
                intent.putExtra("recoverySeed", strNewseed);
                intent.putExtra("walletName", editSetWalletName.getText().toString());
                startActivity(intent);

                break;
        }
    }
}

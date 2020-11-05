package org.haobtc.onekey.onekeys.homepage.process;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetDeriveWalletNameActivity extends BaseActivity {

    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    @BindView(R.id.btn_import)
    Button btnImport;
    private String walletType;
    private String currencyType;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_derive_wallet_name;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        walletType = getIntent().getStringExtra("walletType");
        currencyType = getIntent().getStringExtra("currencyType");

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_import:
                if (TextUtils.isEmpty(editSetWalletName.getText().toString())) {
                    mToast(getString(R.string.please_input_walletname));
                    return;
                }
                inputPassDialog();
                break;
        }
    }

    private void inputPassDialog() {
        View view1 = LayoutInflater.from(SetDeriveWalletNameActivity.this).inflate(R.layout.input_wallet_pass, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(SetDeriveWalletNameActivity.this).setView(view1).create();
        EditText strPass = view1.findViewById(R.id.edit_password);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view1.findViewById(R.id.btn_enter_wallet).setOnClickListener(v -> {
            if ("derive".equals(walletType)) {
                //create hd derive wallet
                createDeriveWallet(strPass.getText().toString());
            } else {
                //Create a separate Wallet
                createSingleWallet(strPass.getText().toString());
            }

        });

        view1.findViewById(R.id.cancel_select_wallet).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    //create derive wallet
    private void createDeriveWallet(String pass) {
        try {
            Daemon.commands.callAttr("create_derived_wallet", editSetWalletName.getText().toString(), pass, currencyType);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);

    }

    //Create a separate Wallet
    private void createSingleWallet(String pass) {
        try {
            Daemon.commands.callAttr("create", editSetWalletName.getText().toString(), pass);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);

    }

}
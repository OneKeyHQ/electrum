package org.haobtc.onekey.onekeys.dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.WalletManageActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetLongPassActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_set_pass)
    TextView testSetPass;
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.edit_pass)
    EditText editPass;
    @BindView(R.id.img_eye_yes)
    ImageView imgEyeYes;
    @BindView(R.id.img_eye_no)
    ImageView imgEyeNo;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.text_long)
    TextView textLong;
    @BindView(R.id.text_change)
    TextView textChange;
    private boolean input = false;
    private String firstPass;
    private SharedPreferences.Editor edit;
    private String importHdword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_long_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        editPass.addTextChangedListener(this);
    }

    @Override
    public void initData() {
        importHdword = getIntent().getStringExtra("importHdword");
        if ("importHdword".equals(importHdword)) {
            testSetPass.setText(getString(R.string.input_your_pass));
            textTip.setText(getString(R.string.dont_tell));
            textLong.setText(getString(R.string.long_pass_tip));
            textChange.setText(getString(R.string.change_short_keyboard));
        } else if ("fixHdPass".equals(importHdword)) {
            testSetPass.setText(getString(R.string.input_your_former_pass));
            textTip.setText(getString(R.string.fix_former_tip));
            textLong.setText(getString(R.string.long_pass_tip));
        }

    }

    @OnClick({R.id.img_back, R.id.img_eye_yes, R.id.img_eye_no, R.id.btn_next, R.id.lin_short_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_eye_yes:
                imgEyeYes.setVisibility(View.GONE);
                imgEyeNo.setVisibility(View.VISIBLE);
                editPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.img_eye_no:
                imgEyeYes.setVisibility(View.VISIBLE);
                imgEyeNo.setVisibility(View.GONE);
                editPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case R.id.btn_next:
                if ("importHdword".equals(importHdword)) {
                    //export Mnemonic words
                    exportWord();
                } else if ("fixHdPass".equals(importHdword)) {
                    //fix pass
                    fixHdPass();
                } else {
                    if (!input) {
                        firstPass = editPass.getText().toString();
                        testSetPass.setText(getText(R.string.input_you_pass));
                        textTip.setText(getText(R.string.dont_tell));
                        textLong.setText(getText(R.string.long_pass_tip));
                        btnNext.setEnabled(false);
                        btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
                        editPass.setText("");
                        input = true;
                    } else {
                        if (!firstPass.equals(editPass.getText().toString())) {
                            mToast(getString(R.string.two_different_pass));
                            return;
                        }
                        PyObject createHdWallet = null;
                        try {
                            createHdWallet = Daemon.commands.callAttr("create_hd_wallet", editPass.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (createHdWallet != null) {
                            Log.i("createHdWallet", "onViewClicked:-- " + createHdWallet);
                            Intent intent = new Intent(SetLongPassActivity.this, HomeOnekeyActivity.class);
                            startActivity(intent);
                            edit.putBoolean("isHaveWallet", true);
                            edit.apply();
                            finish();

                        }
                    }
                }
                break;
            case R.id.lin_short_pass:
                finish();
                break;
        }
    }

    private void fixHdPass() {
        if (!input) {
            firstPass = editPass.getText().toString();
            testSetPass.setText(getString(R.string.set_new_pass));
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
            editPass.setText("");
            input = true;
        } else {
            try {
                Daemon.commands.callAttr("update_wallet_password", firstPass, editPass.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mToast(getString(R.string.fix_success));
            finish();
        }
    }

    //export Mnemonic words
    private void exportWord() {
        PyObject createHdWallet = null;
        try {
            createHdWallet = Daemon.commands.callAttr("export_seed", editPass.getText().toString());
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new SecondEvent("finish"));
        Intent intent = new Intent(SetLongPassActivity.this, HdRootMnemonicsActivity.class);
        intent.putExtra("exportWord", createHdWallet.toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ((editPass.length() > 0)) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
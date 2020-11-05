package org.haobtc.onekey.onekeys.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.PwdEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetHDWalletPassActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_set_pass)
    TextView testSetPass;
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.pwd_edittext)
    PwdEditText pwdEdittext;
    @BindView(R.id.text_long)
    TextView textLong;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.text_confirm)
    TextView textConfirm;
    private boolean input = false;
    private String sixPass;
    private SharedPreferences.Editor edit;
    private String importHdword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_h_d_wallet_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        importHdword = getIntent().getStringExtra("importHdword");
        if ("importHdword".equals(importHdword)) {
            testSetPass.setText(getString(R.string.input_your_pass));
            textTip.setText(getString(R.string.dont_tell));
            textLong.setText(getString(R.string.long_pass_tip));
            textConfirm.setText(getString(R.string.change_keyboard));
        } else if ("fixHdPass".equals(importHdword)) {
            testSetPass.setText(getString(R.string.input_your_former_pass));
            textTip.setText(getString(R.string.fix_former_tip));
            textLong.setText(getString(R.string.long_pass_tip));
            textConfirm.setText(getString(R.string.change_keyboard));
        }

    }

    @Override
    public void initData() {
        pwdEdittext.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.btn_next, R.id.lin_short_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_short_pass:
                Intent intent1 = new Intent(SetHDWalletPassActivity.this, SetLongPassActivity.class);
                intent1.putExtra("importHdword", importHdword);
                startActivity(intent1);
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
                        sixPass = pwdEdittext.getText().toString();
                        testSetPass.setText(getString(R.string.input_pass));
                        textTip.setText(getString(R.string.dont_tell));
                        pwdEdittext.clearText();
                        textLong.setText(getString(R.string.test_long_tip));
                        input = true;
                    } else {
                        if (!sixPass.equals(pwdEdittext.getText().toString())) {
                            mToast(getString(R.string.two_different_pass));
                            return;
                        }
                        PyObject createHdWallet = null;
                        try {
                            createHdWallet = Daemon.commands.callAttr("create_hd_wallet", pwdEdittext.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        if (createHdWallet != null) {
                            Log.i("createHdWallet", "onViewClicked:-- " + createHdWallet);
                            Intent intent = new Intent(SetHDWalletPassActivity.this, HomeOnekeyActivity.class);
                            startActivity(intent);
                            edit.putBoolean("isHaveWallet", true);
                            edit.apply();
                        }
                    }
                }
                break;
        }
    }

    //fix pass
    private void fixHdPass() {
        if (!input) {
            sixPass = pwdEdittext.getText().toString();
            testSetPass.setText(getString(R.string.set_new_pass));
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
            pwdEdittext.clearText();
            input = true;
        } else {
            try {
                Daemon.commands.callAttr("update_wallet_password", sixPass, pwdEdittext.getText().toString());
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
            createHdWallet = Daemon.commands.callAttr("export_seed", pwdEdittext.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(SetHDWalletPassActivity.this, HdRootMnemonicsActivity.class);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void afterTextChanged(Editable s) {
        int length = s.length();
        if ((length == 6)) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }
}
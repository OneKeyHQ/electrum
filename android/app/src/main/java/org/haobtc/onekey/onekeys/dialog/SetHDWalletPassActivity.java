package org.haobtc.onekey.onekeys.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
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
    private boolean input = false;
    private String sixPass;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_h_d_wallet_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        pwdEdittext.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.text_confirm, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_confirm:
                mIntent(SetLongPassActivity.class);

                break;
            case R.id.btn_next:
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

                    }
//                    mIntent(HomeOnekeyActivity.class);

                }
                break;
        }
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
}
package org.haobtc.onekey.onekeys.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;

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
    private boolean input = false;
    private String firstPass;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_long_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        editPass.addTextChangedListener(this);
    }

    @Override
    public void initData() {

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
                    }else{
                        mIntent(HomeOnekeyActivity.class);
                        finish();
                    }
                }

                break;
            case R.id.lin_short_pass:
                mIntent(SetHDWalletPassActivity.class);
                finish();
                break;
        }
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
}
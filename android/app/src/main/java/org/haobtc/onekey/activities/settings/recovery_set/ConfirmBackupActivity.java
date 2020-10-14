package org.haobtc.onekey.activities.settings.recovery_set;

import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.utils.PasswordInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmBackupActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.trader_pwd_set_pwd_edittext)
    PasswordInputView traderPwdSetPwdEdittext;
    @BindView(R.id.btn_setPin)
    Button btnSetPin;
    @BindView(R.id.keyboard_view)
    KeyboardView keyboardView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_confirm_backup;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_setPin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_setPin:
                break;
            default:
        }
    }
}

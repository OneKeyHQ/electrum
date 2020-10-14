package org.haobtc.onekey.activities.personalwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatAppWalletActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.tet_setWalletName)
    EditText tetSetWalletName;
    @BindView(R.id.btn_setPin)
    Button btnSetPin;
    @BindView(R.id.number)
    TextView number;
    private int defaultName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_creat_app_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        defaultName = preferences.getInt("defaultName", 0);
        if (defaultName != 0) {
            btnSetPin.setEnabled(true);
            btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
        }else{
            btnSetPin.setEnabled(false);
            btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
        }
    }

    @Override
    public void initData() {
        nameEditStyle();
        int walletNameNum = defaultName + 1;
        tetSetWalletName.setText(String.format("钱包%s", String.valueOf(walletNameNum)));
        number.setText(String.format(Locale.CHINA, "%d/16", tetSetWalletName.length()));
        tetSetWalletName.addTextChangedListener(this);

    }

    private void nameEditStyle() {
        tetSetWalletName.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                number.setText(String.format(Locale.CHINA, "%d/16", input.length()));
                if (input.length() > 15) {
                    mToast(getString(R.string.moreinput_text_fixbixinkey));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    btnSetPin.setEnabled(true);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    btnSetPin.setEnabled(false);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }
        });
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_setPin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_setPin:
                String strWalletName = tetSetWalletName.getText().toString();
                if (TextUtils.isEmpty(strWalletName)) {
                    mToast(getString(R.string.set_wallet));
                    return;
                }
                Intent intent = new Intent(CreatAppWalletActivity.this, AppWalletSetPassActivity.class);
                intent.putExtra("strName", strWalletName);
                startActivity(intent);
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            btnSetPin.setEnabled(false);
            btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
        } else {
            btnSetPin.setEnabled(true);
            btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

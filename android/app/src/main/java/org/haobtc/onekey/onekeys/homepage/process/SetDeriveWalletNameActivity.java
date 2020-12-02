package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetDeriveWalletNameActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    @BindView(R.id.btn_import)
    Button btnImport;
    private String walletType;
    private String currencyType;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;
    private Intent intent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_derive_wallet_name;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        walletType = getIntent().getStringExtra("walletType");
        currencyType = getIntent().getStringExtra("currencyType");

    }

    @Override
    public void initData() {
        editSetWalletName.addTextChangedListener(this);
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
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    intent = new Intent(this, SetHDWalletPassActivity.class);
                } else {
                    intent = new Intent(this, SetLongPassActivity.class);
                }
                intent.putExtra("importHdword", walletType);
                intent.putExtra("currencyType", currencyType);
                intent.putExtra("walletName", editSetWalletName.getText().toString());
                startActivity(intent);
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
        if (!TextUtils.isEmpty(s.toString())) {
            if (s.length() > 14) {
                mToast(getString(R.string.name_lenth));
            }
        }
    }
}
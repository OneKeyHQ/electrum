package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.NameSettedEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SoftWalletNameSettingActivity extends BaseActivity implements TextWatcher {
    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    @BindView(R.id.btn_import)
    Button btnImport;
    private int type;

    public static void gotoSoftWalletNameSettingActivity (Context context, int type) {
        Intent intent = new Intent(context, SoftWalletNameSettingActivity.class);
        intent.putExtra(Constant.WALLET_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId () {
        return R.layout.activity_set_derive_wallet_name;
    }

    @Override
    public void initView () {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        type = getIntent().getIntExtra(Constant.WALLET_TYPE, 0);
        editSetWalletName.addTextChangedListener(this);
    }

    @SingleClick
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
                NameSettedEvent nameSettedEvent = new NameSettedEvent(editSetWalletName.getText().toString());
                nameSettedEvent.type = type;
                EventBus.getDefault().post(nameSettedEvent);
                finish();
                break;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 禁止EditText输入空格
        if (s.toString().contains(" ")) {
            String[] str = s.toString().split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length; i++) {
                sb.append(str[i]);
            }
            editSetWalletName.setText(sb.toString());
            editSetWalletName.setSelection(start);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().replace(" ", "");
        if (!TextUtils.isEmpty(text)) {
            btnImport.setEnabled(true);
            if (s.length() > 14) {
                mToast(getString(R.string.name_lenth));
            }
        } else {
            btnImport.setEnabled(false);
        }
    }
}
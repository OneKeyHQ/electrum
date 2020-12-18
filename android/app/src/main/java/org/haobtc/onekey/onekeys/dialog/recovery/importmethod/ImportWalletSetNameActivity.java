package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.NameSettedEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportWalletSetNameActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_wallet_set_name;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        editSetWalletName.addTextChangedListener(this);
    }

    @SingleClick(value = 1000)
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
                EventBus.getDefault().post(new NameSettedEvent(editSetWalletName.getText().toString()));
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
        if (!TextUtils.isEmpty(s.toString())) {
            if (s.length() > 14) {
                mToast(getString(R.string.name_lenth));
            }
        }
    }
}
package org.haobtc.onekey.ui.fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.SpConstant;
import org.haobtc.onekey.data.prefs.PreferencesManager;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.passageway.HandleCommands;
import org.haobtc.onekey.ui.listener.IGiveNameListener;

import butterknife.BindView;

public class SetWalletNameFragment extends BaseFragment<IGiveNameListener> implements View.OnClickListener {

    @BindView(R.id.device_name)
    protected EditText mDeviceNameEditText;
    @BindView(R.id.btn_create)
    protected Button mCreate;

    @Override
    public void init(View view) {
        mCreate.setOnClickListener(this);
        mDeviceNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                mCreate.setEnabled(TextUtils.isEmpty(name));
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_set_wallet_name;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                initColdDevice();
                break;
        }
    }

    private void initColdDevice() {
        String name = mDeviceNameEditText.getText().toString();
        String language = (String) PreferencesManager.get(getContext(), SpConstant.SP_NAME_PREFERENCES,
                SpConstant.Preferences.LANGUAGE, SpConstant.Preferences.LANGUAGE_DEFAULT);
        HandleCommands.init(name, language.toLowerCase(), "1", result -> {

            if (getListener() != null) {
                getListener().onWalletInitSuccess();
            }
        });
    }
}

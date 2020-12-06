package org.haobtc.onekey.ui.fragment;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.CreateWalletEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 */
public class SetWalletNameFragment extends BaseFragment {

    @BindView(R.id.device_name)
    protected EditText mDeviceNameEditText;
    @BindView(R.id.btn_create)
    protected Button mCreate;

    @Override
    public void init(View view) {
    }

    @OnTextChanged(value = R.id.device_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChange(Editable s) {
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_set_wallet_name;
    }

    @OnClick(R.id.btn_create)
    public void onViewClicked(View view) {
        if (Strings.isNullOrEmpty(mDeviceNameEditText.getText().toString())) {
            showToast(R.string.name_empty);
        }
        EventBus.getDefault().post(new CreateWalletEvent(mDeviceNameEditText.getText().toString()));
    }
}

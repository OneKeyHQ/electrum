package org.haobtc.onekey.ui.fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 */
public class DeviceNameSettingFragment extends BaseFragment {

    @BindView(R.id.device_name)
    protected EditText mDeviceNameEditText;
    @BindView(R.id.btn_next)
    protected Button mNext;

    @Override
    public void init(View view) {
    }

    @OnTextChanged(value = R.id.device_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        String name = s.toString();
        mNext.setEnabled(!TextUtils.isEmpty(name));
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_give_name;
    }

    @OnClick(R.id.btn_next)
    public void onViewClicked(View view) {
        EventBus.getDefault().post(new NameSettedEvent(mDeviceNameEditText.getText().toString()));
    }
}

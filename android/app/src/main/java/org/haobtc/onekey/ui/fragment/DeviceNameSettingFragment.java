package org.haobtc.onekey.ui.fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        mDeviceNameEditText.addTextChangedListener(new TextWatcher() {
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
                    mDeviceNameEditText.setText(sb.toString());
                    mDeviceNameEditText.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    if (s.length() > 7) {
                        Toast.makeText(getActivity(), getString(R.string.name_lenth_8), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

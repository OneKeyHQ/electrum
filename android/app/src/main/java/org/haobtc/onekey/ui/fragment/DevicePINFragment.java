package org.haobtc.onekey.ui.fragment;

import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnTouch;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.ui.widget.AsteriskPasswordTransformationMethod;
import org.haobtc.onekey.utils.NumKeyboardUtil;

/** @author liyan */
public class DevicePINFragment extends BaseFragment implements NumKeyboardUtil.CallBack {

    private static final String TAG = DevicePINFragment.class.getSimpleName();

    @BindView(R.id.pwd_edit_text)
    protected PwdInputView mPwdInputView;

    @BindView(R.id.promote)
    TextView promote;

    private NumKeyboardUtil mKeyboardUtil;

    @BindView(R.id.relativeLayout_key)
    protected RelativeLayout mRelativeLayoutKey;

    private int type;

    @BindView(R.id.edit_pass_long)
    EditText mLongEdit;

    public DevicePINFragment(int type) {
        this.type = type;
    }

    @Override
    public void init(View view) {
        switch (type) {
            case PyConstant.PIN_CURRENT:
                promote.setText(R.string.set_pin_promote);
            default:
        }
        mKeyboardUtil = new NumKeyboardUtil(view, getContext(), mLongEdit, R.xml.number, this);
        mLongEdit.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mLongEdit.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 9) {
                            showToast(R.string.pass_longest_nine);
                            mLongEdit.setText(s.subSequence(0, 9));
                        }
                    }
                });
    }

    @OnTouch(R.id.edit_pass_long)
    public boolean onTouch() {
        if (mKeyboardUtil.getKeyboardVisible() != View.VISIBLE) {
            mRelativeLayoutKey.setVisibility(View.VISIBLE);
            mKeyboardUtil.showKeyboard();
        }
        return true;
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_set_device_pass;
    }

    @Override
    public void onKey(int key) {
        if (key == Keyboard.KEYCODE_CANCEL) {
            String pin = mLongEdit.getText().toString();
            Log.e(TAG, "pin : " + pin);
            if (pin.length() < 1) {
                showToast(R.string.hint_please_enter_pin_code);
                mKeyboardUtil.showKeyboard();
                return;
            }
            mRelativeLayoutKey.setVisibility(View.GONE);
            mKeyboardUtil.hideKeyboard();
            EventBus.getDefault().post(new ChangePinEvent(pin, ""));
        }
    }
}

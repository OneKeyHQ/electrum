package org.haobtc.onekey.ui.fragment;

import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.passageway.HandleCommands;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;
import org.haobtc.onekey.utils.NumKeyboardUtil;

import butterknife.BindView;

public class SetDevicePINFragment extends BaseFragment<ISetDevicePassListener> implements NumKeyboardUtil.CallBack {

    private static final String TAG = SetDevicePINFragment.class.getSimpleName();
    @BindView(R.id.pwd_edit_text)
    protected PwdInputView mPwdInputView;
    private NumKeyboardUtil mKeyboardUtil;
    @BindView(R.id.relativeLayout_key)
    protected RelativeLayout mRelativeLayoutKey;


    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.activate_cold_wallet);
        mKeyboardUtil = new NumKeyboardUtil(view, getContext(), mPwdInputView, R.xml.number, this);
        mPwdInputView.setOnTouchListener((v, event) -> {
            if (mKeyboardUtil.getKeyboardVisible() != View.VISIBLE) {
                mRelativeLayoutKey.setVisibility(View.VISIBLE);
                mKeyboardUtil.showKeyboard();
            }
            return true;
        });
        HandleCommands.resetPin((HandleCommands.CallBack<String>) result -> {

        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_set_device_pass;
    }

    @Override
    public void onKey(int key) {
        if (key == Keyboard.KEYCODE_CANCEL) {
            String pin = mPwdInputView.getText().toString();
            Log.e(TAG, "pin : " + pin);
            if (pin.length() != 6) {
                showToast(R.string.pass_morethan_6);
                mKeyboardUtil.showKeyboard();
                return;
            }
            mRelativeLayoutKey.setVisibility(View.GONE);
            mKeyboardUtil.hideKeyboard();

            HandleCommands.setPin(pin);
            if (getListener() != null) {
                getListener().onSetDevicePassSuccess();
            }

        }
    }

}

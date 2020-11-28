package org.haobtc.onekey.ui.fragment;

import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.utils.NumKeyboardUtil;

import butterknife.BindView;
import butterknife.OnTouch;

/**
 * @author liyan
 */
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

    public DevicePINFragment(int type) {
        this.type = type;
    }

    @Override
    public void init(View view) {
        switch (type) {
            case PyConstant.PIN_CURRENT:
                promote.setText("根据设备显示的\n" +
                        "PIN 码位置对照表\n" +
                        "输入您曾设置的6位密码");
            default:


        }
        mKeyboardUtil = new NumKeyboardUtil(view, getContext(), mPwdInputView, R.xml.number, this);
    }

    @OnTouch(R.id.pwd_edit_text)
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
            String pin = mPwdInputView.getText().toString();
            Log.e(TAG, "pin : " + pin);
            if (pin.length() != 6) {
                showToast(R.string.pass_morethan_6);
                mKeyboardUtil.showKeyboard();
                return;
            }
            mRelativeLayoutKey.setVisibility(View.GONE);
            mKeyboardUtil.hideKeyboard();
            EventBus.getDefault().post(new ChangePinEvent(pin, ""));
        }
    }

}

package org.haobtc.onekey.ui.activity;

import static org.haobtc.onekey.ui.activity.HardwarePinFragment.PinActionType.CHANGE_PIN;
import static org.haobtc.onekey.ui.activity.HardwarePinFragment.PinActionType.NEW_PIN;
import static org.haobtc.onekey.ui.activity.HardwarePinFragment.PinActionType.VERIFY_PIN;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import butterknife.BindView;
import butterknife.OnTouch;
import com.google.common.base.Strings;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.ui.widget.AsteriskPasswordTransformationMethod;
import org.haobtc.onekey.utils.NumKeyboardUtil;

/**
 * 硬件验证修改密码或提示硬件解锁
 *
 * @author Onekey@QuincySx
 * @create 2021-03-12 11:18 AM
 */
public class HardwarePinFragment extends BaseFragment implements NumKeyboardUtil.CallBack {

    @StringDef({CHANGE_PIN, VERIFY_PIN, NEW_PIN})
    public @interface PinActionType {

        String CHANGE_PIN = BusinessAsyncTask.CHANGE_PIN;
        String NEW_PIN = "new_pin";
        String VERIFY_PIN = "verify_pin";
    }

    private static final String EXT_ACTION = "action";
    private static final String EXT_PIN_ORIGIN = Constant.PIN_ORIGIN;

    public static HardwarePinFragment newInstance(@PinActionType String action, String originPin) {
        HardwarePinFragment fragment = new HardwarePinFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXT_ACTION, action);
        if (!TextUtils.isEmpty(originPin)) {
            bundle.putString(EXT_PIN_ORIGIN, originPin);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private HardwareTitleChangeCallback mHardwareTitleChangeCallback;
    private OnHardwarePinSuccessCallback mOnHardwarePinSuccessCallback;

    @BindView(R.id.edit_pass_long)
    protected EditText mLongEdit;

    @BindView(R.id.pwd_edit_text)
    protected PwdInputView mPwdInputView;

    @BindView(R.id.promote)
    TextView promote;

    private NumKeyboardUtil mKeyboardUtil;

    @BindView(R.id.relativeLayout_key)
    protected RelativeLayout mRelativeLayoutKey;

    private String action;
    private String originPin;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HardwareTitleChangeCallback) {
            mHardwareTitleChangeCallback = (HardwareTitleChangeCallback) context;
        }
        if (context instanceof OnHardwarePinSuccessCallback) {
            mOnHardwarePinSuccessCallback = (OnHardwarePinSuccessCallback) context;
        }

        if (getParentFragment() != null) {
            if (getParentFragment() instanceof HardwareTitleChangeCallback) {
                mHardwareTitleChangeCallback = (HardwareTitleChangeCallback) getParentFragment();
            }
            if (getParentFragment() instanceof OnHardwarePinSuccessCallback) {
                mOnHardwarePinSuccessCallback = (OnHardwarePinSuccessCallback) getParentFragment();
            }
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_input_hardware_pin;
    }

    @Override
    public void init(View view) {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        action = getArguments().getString(EXT_ACTION);
        originPin = getArguments().getString(EXT_PIN_ORIGIN);

        changeTitleContent();

        mKeyboardUtil =
                new NumKeyboardUtil(
                        mRelativeLayoutKey, requireContext(), mLongEdit, R.xml.number, this);
        mLongEdit.setTransformationMethod(new AsteriskPasswordTransformationMethod());
    }

    private void changeInputType(@PinActionType String action) {
        this.action = action;
        mLongEdit.setText("");
        changeTitleContent();
        mRelativeLayoutKey.setVisibility(View.VISIBLE);
        mKeyboardUtil.showKeyboard();
    }

    private void changeTitleContent() {
        switch (action) {
            case NEW_PIN:
                if (Strings.isNullOrEmpty(originPin)) {
                    promote.setText(R.string.set_pin);
                } else {
                    promote.setText(R.string.change_pin_promote);
                }
            case CHANGE_PIN:
                if (mHardwareTitleChangeCallback != null) {
                    mHardwareTitleChangeCallback.setTitle(getString(R.string.change_pin));
                }
                break;
            case VERIFY_PIN:
                if (mHardwareTitleChangeCallback != null) {
                    mHardwareTitleChangeCallback.setTitle(getString(R.string.verify_pin_onkey));
                }
                promote.setText(R.string.input_pin_promote);
                break;
        }
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
    public void onKey(int key) {
        if (key == Keyboard.KEYCODE_CANCEL) {
            String pin = mLongEdit.getText().toString();
            if (pin.length() < 1) {
                showToast(R.string.hint_please_enter_pin_code);
                mKeyboardUtil.showKeyboard();
                return;
            }
            Logger.e(action);
            if (!action.equalsIgnoreCase(CHANGE_PIN)) {
                mRelativeLayoutKey.setVisibility(View.GONE);
                mKeyboardUtil.hideKeyboard();
            }
            switch (action) {
                case CHANGE_PIN:
                    originPin = pin;
                    changeInputType(NEW_PIN);
                    break;
                case NEW_PIN:
                    if (mOnHardwarePinSuccessCallback != null) {
                        ChangePinEvent changePinEvent =
                                new ChangePinEvent(
                                        numberAfterFillZero(pin, 9),
                                        numberAfterFillZero(originPin, 9));
                        changePinEvent.setAction(action);
                        mOnHardwarePinSuccessCallback.onSuccess(changePinEvent);
                    }
                    break;
                case VERIFY_PIN:
                    if (mOnHardwarePinSuccessCallback != null) {
                        ChangePinEvent changePinEvent = new ChangePinEvent(pin, "");
                        changePinEvent.setAction(action);
                        mOnHardwarePinSuccessCallback.onSuccess(changePinEvent);
                    }
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(ExitEvent exitEvent) {
        if (mOnHardwarePinSuccessCallback != null) {
            mOnHardwarePinSuccessCallback.onFinish();
        }
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    public static String numberAfterFillZero(String str, int length) {
        StringBuilder buffer = new StringBuilder(str);
        if (buffer.length() >= length) {
            return buffer.toString();
        } else {
            while (buffer.length() < length) {
                buffer.append("0");
            }
        }
        return buffer.toString();
    }

    interface HardwareTitleChangeCallback {

        void setTitle(String title);
    }

    interface OnHardwarePinSuccessCallback {

        void onSuccess(ChangePinEvent event);

        void onFinish();
    }
}

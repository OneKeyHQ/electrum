package org.haobtc.onekey.ui.activity;

import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.google.common.base.Strings;
import java.util.Optional;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.ui.widget.AsteriskPasswordTransformationMethod;
import org.haobtc.onekey.utils.NumKeyboardUtil;

/**
 * @author liyan
 * @date 11/25/20
 */
public class PinNewActivity extends BaseActivity implements NumKeyboardUtil.CallBack {

    @BindView(R.id.edit_pass_long)
    protected EditText mLongEdit;

    @BindView(R.id.pwd_edit_text)
    protected PwdInputView mPwdInputView;

    @BindView(R.id.promote)
    TextView promote;

    @BindView(R.id.img_back)
    ImageView imgBack;

    private NumKeyboardUtil mKeyboardUtil;

    @BindView(R.id.relativeLayout_key)
    RelativeLayout mRelativeLayoutKey;

    private String pinOrigin;

    @Override
    public void init() {
        updateTitle(R.string.change_pin);
        pinOrigin = Optional.ofNullable(getIntent().getStringExtra(Constant.PIN_ORIGIN)).orElse("");
        if (Strings.isNullOrEmpty(pinOrigin)) {
            promote.setText(R.string.set_pin);
        } else {
            promote.setText(R.string.change_pin_promote);
        }
        mKeyboardUtil =
                new NumKeyboardUtil(mRelativeLayoutKey, this, mLongEdit, R.xml.number, this);
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
    public void onKey(int key) {
        if (key == Keyboard.KEYCODE_CANCEL) {
            String pin = mLongEdit.getText().toString();
            if (pin.length() < 1) {
                showToast(R.string.hint_please_enter_pin_code);
                mKeyboardUtil.showKeyboard();
                return;
            }
            mRelativeLayoutKey.setVisibility(View.GONE);
            mKeyboardUtil.hideKeyboard();
            EventBus.getDefault()
                    .post(
                            new ChangePinEvent(
                                    numberAfterFillZero(pin, 9),
                                    numberAfterFillZero(pinOrigin, 9)));
        }
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.input_pin_activity;
    }

    @OnClick(R.id.img_back)
    public void onClick(View view) {
        if (Strings.isNullOrEmpty(pinOrigin)) {
            PyEnv.cancelPinInput();
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExit(ExitEvent exitEvent) {
        finish();
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    public static String numberAfterFillZero(String str, int length) {
        StringBuffer buffer = new StringBuffer(str);
        if (buffer.length() >= length) {
            return buffer.toString();
        } else {
            while (buffer.length() < length) {
                buffer.append("0");
            }
        }
        return buffer.toString();
    }
}

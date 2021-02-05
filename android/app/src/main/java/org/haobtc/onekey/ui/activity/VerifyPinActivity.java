package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
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
public class VerifyPinActivity extends BaseActivity implements NumKeyboardUtil.CallBack {

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
    protected RelativeLayout mRelativeLayoutKey;

    private String action;

    @Override
    public void init() {
        action = getIntent().getAction();
        if (BusinessAsyncTask.CHANGE_PIN.equals(action)) {
            updateTitle(R.string.change_pin);
        } else {
            updateTitle(R.string.verify_pin_onkey);
            promote.setText(R.string.input_pin_promote);
        }
        mKeyboardUtil =
                new NumKeyboardUtil(mRelativeLayoutKey, this, mLongEdit, R.xml.number, this);
        mLongEdit.setTransformationMethod(new AsteriskPasswordTransformationMethod());
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
            if (BusinessAsyncTask.CHANGE_PIN.equals(action)) {
                Intent intent = new Intent(this, PinNewActivity.class);
                intent.putExtra(Constant.PIN_ORIGIN, pin);
                startActivity(intent);
            } else {
                EventBus.getDefault().post(new ChangePinEvent(pin, ""));
                finish();
            }
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

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        PyEnv.cancelPinInput();
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
}

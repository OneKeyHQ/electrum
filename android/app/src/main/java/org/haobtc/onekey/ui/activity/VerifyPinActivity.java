package org.haobtc.onekey.ui.activity;


import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.custom.PwdInputView;
import org.haobtc.onekey.utils.NumKeyboardUtil;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * @author liyan
 * @date 11/25/20
 */

public class VerifyPinActivity extends BaseActivity implements NumKeyboardUtil.CallBack {

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
            promote.setText("请输入PIN码");
        }
        mKeyboardUtil = new NumKeyboardUtil(mRelativeLayoutKey, this, mPwdInputView, R.xml.number, this);
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
    public void onKey(int key) {
        if (key == Keyboard.KEYCODE_CANCEL) {
            String pin = mPwdInputView.getText().toString();
            if (pin.length() != 6) {
                showToast(R.string.pass_morethan_6);
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
            }
        }
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.input_pin_activity;
    }
    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked() {
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

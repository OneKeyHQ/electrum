package org.haobtc.onekey.ui.activity;


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
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
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

public class PinNewActivity extends BaseActivity implements NumKeyboardUtil.CallBack{

    @BindView(R.id.pwd_edit_text)
    protected PwdInputView mPwdInputView;
    @BindView(R.id.promote)
    TextView promote;
    @BindView(R.id.img_back)
    ImageView imgBack;
    private NumKeyboardUtil mKeyboardUtil;
    @BindView(R.id.relativeLayout_key)
    protected RelativeLayout mRelativeLayoutKey;
    private String pinOrigin;

    @Override
    public void init() {
        updateTitle(R.string.change_pin);
        pinOrigin = getIntent().getStringExtra(Constant.PIN_ORIGIN);
        promote.setText("干的好，继续根据设备\n" +
                        "上的 PIN 码位置\n" +
                        "设置您的新6位 PIN 码");
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
            EventBus.getDefault().post(new ChangePinEvent(pin, pinOrigin));
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

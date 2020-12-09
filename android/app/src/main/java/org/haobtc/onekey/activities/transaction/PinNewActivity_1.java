package org.haobtc.onekey.activities.transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.settings.fixpin.ChangePinProcessingActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.OperationTimeoutEvent;
import org.haobtc.onekey.utils.NumKeyboardUtil;
import org.haobtc.onekey.utils.PasswordInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@Deprecated
public class PinNewActivity_1 extends BaseActivity {
    @BindView(R.id.trader_pwd_set_pwd_edittext)
    PasswordInputView edtPwd;
    @BindView(R.id.img_back_pin)
    ImageView imgBack;
    @BindView(R.id.bn_next)
    Button bnCreateWallet;
    @BindView(R.id.pin_description)
    TextView textViewPinDescription;
    @BindView(R.id.relativeLayout_key)
    RelativeLayout relativeLayoutKey;
    private NumKeyboardUtil keyboardUtil;
    public static final String TAG = PinNewActivity_1.class.getSimpleName();
    private String originPin;

    @Override
    public int getLayoutId() {
        return R.layout.pin_input_new;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        keyboardUtil = new NumKeyboardUtil(this, this, edtPwd, R.xml.number);
        textViewPinDescription.setText(R.string.new_pin);
        originPin = getIntent().getStringExtra("pin_origin");
        init();
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        edtPwd.setOnTouchListener((v, event) -> {
            keyboardUtil.showKeyboard();
            return false;
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timeout(OperationTimeoutEvent event) {
        Toast.makeText(this, getString(R.string.pin_timeout), Toast.LENGTH_LONG).show();
        EventBus.getDefault().post(new ExitEvent());
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                relativeLayoutKey.setVisibility(View.GONE);
                keyboardUtil.hideKeyboard();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.img_back_pin, R.id.bn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_pin:
                finish();
                break;
            case R.id.bn_next:
                if (edtPwd.getText().length() == 6) {
                    Intent intent = new Intent(this, ChangePinProcessingActivity.class);
                    intent.putExtra("pin_origin", originPin);
                    intent.putExtra("pin_new", edtPwd.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.pass_morethan_6), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

}

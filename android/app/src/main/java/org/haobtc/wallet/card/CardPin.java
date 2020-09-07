package org.haobtc.wallet.card;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.settings.fixpin.ConfirmActivity;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.event.ButtonRequestEvent;
import org.haobtc.wallet.event.ExitEvent;
import org.haobtc.wallet.event.OperationTimeoutEvent;
import org.haobtc.wallet.utils.NumKeyboardUtil;
import org.haobtc.wallet.utils.PasswordInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
*/
public class CardPin extends BaseActivity {
    public static final String TAG = org.haobtc.wallet.card.CardPin.class.getSimpleName();
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

    @Override
    public int getLayoutId() {
        return R.layout.pin_input_card;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        boolean isSet = getIntent().getBooleanExtra("set", false);
        if (isSet) {
           textViewPinDescription.setText(R.string.bixin_keylite_pin);
        }
        keyboardUtil = new NumKeyboardUtil(this, this, edtPwd, R.xml.plain_numbers);
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
        Toast.makeText(this, "pin 输入超时", Toast.LENGTH_LONG).show();
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


    @SingleClick
    @OnClick({R.id.img_back_pin, R.id.bn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_pin:
                finish();
                break;
            case R.id.bn_next:
                String pin = edtPwd.getText().toString();
                if (pin.length() == 6) {
                    if ("backup".equals(getIntent().getAction())) {
                        Intent intent = new Intent();
                        intent.putExtra("pin", pin);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else if ("recovery".equals(getIntent().getAction())) {
                        Intent intent = new Intent(this, SmartCardHelper.class);
                        intent.setAction("recovery");
                        intent.putExtra("pin", pin);
                        intent.putExtra("step", 3);
                        intent.putExtra("extras", getIntent().getStringExtra("extras"));
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.pass_morethan_6), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onButtonRequest(ButtonRequestEvent event) {
        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra("tag", "set_pin");
        startActivity(intent);
        finish();
    }
//    @Subscribe()
//    public void onFinish(FinishEvent finishEvent) {
//        finish();
//    }
}


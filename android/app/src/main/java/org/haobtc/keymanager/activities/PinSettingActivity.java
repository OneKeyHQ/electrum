package org.haobtc.keymanager.activities;

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
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.NfcNotifyHelper;
import org.haobtc.keymanager.activities.settings.HardwareDetailsActivity;
import org.haobtc.keymanager.activities.settings.fixpin.ChangePinProcessingActivity;
import org.haobtc.keymanager.activities.settings.recovery_set.ResetDeviceActivity;
import org.haobtc.keymanager.activities.settings.recovery_set.ResetDeviceProcessing;
import org.haobtc.keymanager.activities.transaction.PinNewActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.ExitEvent;
import org.haobtc.keymanager.event.FinishEvent;
import org.haobtc.keymanager.event.OperationTimeoutEvent;
import org.haobtc.keymanager.event.PinEvent;
import org.haobtc.keymanager.event.SecondEvent;
import org.haobtc.keymanager.utils.Global;
import org.haobtc.keymanager.utils.NumKeyboardUtil;
import org.haobtc.keymanager.utils.PasswordInputView;

import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;

public class PinSettingActivity extends BaseActivity {
    @BindView(R.id.trader_pwd_set_pwd_edittext)
    PasswordInputView edtPwd;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.bn_next)
    Button bnCreateWallet;
    @BindView(R.id.pin_description)
    TextView textViewPinDescription;
    private NumKeyboardUtil keyboardUtil;
    private String tag;
    public static final String TAG = PinSettingActivity.class.getSimpleName();
    private RelativeLayout relativeLayoutKey;
    private int pinType;
    private boolean shouldFinish = true;

    @Override
    public int getLayoutId() {
        return R.layout.pin_input;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        relativeLayoutKey = findViewById(R.id.relativeLayout_key);
        keyboardUtil = new NumKeyboardUtil(this, this, edtPwd, R.xml.number);
        pinType = getIntent().getIntExtra("pin_type", 0);
        tag = Optional.ofNullable(getIntent().getStringExtra("tag")).orElse("");
        switch (pinType) {
            case 2:
                textViewPinDescription.setText(getString(R.string.pin_setting));
                break;
            case 3:
                textViewPinDescription.setText(getString(R.string.pin_original));
                break;
            default:
                textViewPinDescription.setText(getString(R.string.pin_input));

        }
        init();
    }

    @Override
    public void initData() {
        keyboardUtil.showKeyboard();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        edtPwd.setOnTouchListener((v, event) -> {
            relativeLayoutKey.setVisibility(View.VISIBLE);
            keyboardUtil.showKeyboard();
            return false;
        });
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
    @OnClick({R.id.img_back, R.id.bn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                Global.py.getModule("trezorlib.customer_ui").get("CustomerUI").put("user_cancel", 1);
                finish();
                break;
            case R.id.bn_next:
                String pin = edtPwd.getText().toString();
                if (pin.length() == 6) {
                    switch (tag) {
//                        case SetNameActivity.TAG:
//                            Intent intent = new Intent(this, ActivatedProcessing.class);
//                            intent.putExtra("pin", pin);
//                            startActivity(intent);
//                            finish();
//                            break;
                        // change pin
                        case SettingActivity.TAG_CHANGE_PIN:
                        case HardwareDetailsActivity.TAG:
                            if (pinType == 2) {
                                Intent intent = new Intent(this, ChangePinProcessingActivity.class);
                                intent.putExtra("pin_new", pin);
                                startActivity(intent);
                            } else {
                                shouldFinish = false;
                                Intent intent1 = new Intent(this, PinNewActivity.class);
                                intent1.putExtra("pin_origin", pin);
                                startActivity(intent1);
                            }
                            break;
                        case ResetDeviceActivity.TAG:
                            Intent intent2 = new Intent(this, ResetDeviceProcessing.class);
                            intent2.putExtra("pin", pin);
                            startActivity(intent2);
                            finish();
                            break;
                        default:
                            if (isNFC) {
                                Intent intent = new Intent(this, NfcNotifyHelper.class);
                                intent.putExtra("tag", "PIN");
                                intent.putExtra("pin", pin);
                                startActivity(intent);

                            } else {
                                EventBus.getDefault().post(new PinEvent(pin, ""));
                                finish();
                            }
                    }

                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.pass_morethan_6), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("Keyboard".equals(msgVote)) {
            relativeLayoutKey.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void timeout(OperationTimeoutEvent event) {
        if (hasWindowFocus()) {
            Toast.makeText(this, getString(R.string.pin_timeout), Toast.LENGTH_SHORT).show();
        }
        EventBus.getDefault().post(new ExitEvent());
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (shouldFinish) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

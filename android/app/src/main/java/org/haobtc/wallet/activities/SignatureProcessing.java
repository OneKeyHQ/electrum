package org.haobtc.wallet.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.event.PinEvent;
import org.haobtc.wallet.event.ResultEvent;
import org.haobtc.wallet.event.SendingFailedEvent;
import org.haobtc.wallet.utils.NfcUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@Deprecated
public class SignatureProcessing extends BaseActivity {
    public static final String TAG = SignatureProcessing.class.getSimpleName();
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.first_promote)
    TextView firstPromote;
    @BindView(R.id.second_promote)
    TextView secondPromote;
    @BindView(R.id.third_promote)
    TextView thirdPromote;

    private String pin, passphrash;
    private final int MAX_LEVEL = 10000;

    @Override
    public int getLayoutId() {
        return CommunicationModeSelector.isNFC ? R.layout.processing_nfc : R.layout.processing_ble;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        // 设置沉浸式状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        List<Drawable> drawables = new ArrayList<>();
        if (CommunicationModeSelector.isNFC) {
            secondPromote.setText(R.string.verifying_pin);
            thirdPromote.setText(R.string.reading_signature);
        }
        drawables.addAll(Arrays.asList(firstPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(secondPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(thirdPromote.getCompoundDrawables()));
        drawables.stream().filter(Objects::nonNull)
                .forEach(drawable -> {
                    ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
                    animator.setDuration(800);
                    animator.setRepeatCount(-1);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                });
        pin = getIntent().getStringExtra("pin");
        passphrash = getIntent().getStringExtra("passphrash");
    }

    @Override
    public void initData() {
        NfcUtils.nfc(this, false);
        if (!CommunicationModeSelector.isNFC) {
            EventBus.getDefault().post(new PinEvent(pin, ""));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProcessing(ResultEvent resultEvent) {
        switch (resultEvent.getResult()) {
            case "1":
                Drawable drawableStart = getDrawable(R.drawable.chenggong);
                Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
                firstPromote.setCompoundDrawables(drawableStart, null, null, null);
                secondPromote.setCompoundDrawables(drawableStart, null, null, null);
                startActivity(new Intent(this, ActivateSuccessActivity.class));
                break;
            case "0":
                Toast.makeText(this, getString(R.string.device_activat_fail), Toast.LENGTH_LONG).show();
                finishAffinity();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resultEvent.getResult());
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onButtonRequest(ButtonRequestEvent event) {
//        Drawable drawableStart = getDrawable(R.drawable.chenggong);
//        Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
//        if (CommunicationModeSelector.isNFC) {
//            secondPromote.setText(R.string.order_sending_successful);
//            secondPromote.setCompoundDrawables(drawableStart, null, null, null);
//            startActivity(new Intent(this, ConfirmActivity.class));
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendingFailed(SendingFailedEvent event) {
        // todo: 发送指令失败，修改图标加文案
        startActivity(new Intent(this, ActiveFailedActivity.class));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Drawable drawableStart = getDrawable(R.drawable.chenggong);
            Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
            firstPromote.setCompoundDrawables(drawableStart, null, null, null);
            firstPromote.setText(R.string.connectting_successful);
            EventBus.getDefault().post(new PinEvent(pin, passphrash));
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
//            // enable nfc discovery for the app
//            Log.d("NFC", "为本App启用NFC感应");
//            NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
//        }
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (NfcUtils.mNfcAdapter != null && NfcUtils.mNfcAdapter.isEnabled()) {
//            // disable nfc discovery for the app
//            NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
//            Log.d("NFC", "禁用本App的NFC感应");
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}

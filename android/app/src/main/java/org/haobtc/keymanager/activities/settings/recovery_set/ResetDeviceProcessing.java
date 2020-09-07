package org.haobtc.keymanager.activities.settings.recovery_set;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.ExitEvent;
import org.haobtc.keymanager.event.FinishEvent;
import org.haobtc.keymanager.event.PinEvent;
import org.haobtc.keymanager.event.ResultEvent;
import org.haobtc.keymanager.utils.NfcUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.bleHandler;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.nfcHandler;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.protocol;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.usbTransport;


public class ResetDeviceProcessing extends BaseActivity {
    private final int MAX_LEVEL = 10000;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.first_promote)
    TextView firstPromote;
    @BindView(R.id.second_promote)
    TextView secondPromote;
    @BindView(R.id.third_promote)
    TextView thirdPromote;
    private String pin;
    private Timer timer;
    private final static String TAG = ResetDeviceProcessing.class.getSimpleName();

    @Override
    public int getLayoutId() {
        return isNFC ? R.layout.processing_nfc : R.layout.processing_ble;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        List<Drawable> drawables = new ArrayList<>();
        drawables.addAll(Arrays.asList(firstPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(secondPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(thirdPromote.getCompoundDrawables()));
        drawables.stream().filter(Objects::nonNull).forEach(drawable -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
            animator.setRepeatCount(-1);
            animator.setDuration(500);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        });
        pin = getIntent().getStringExtra("pin");
    }

    @Subscribe
    public void onEventMainThread(ResultEvent resultEvent) {
        EventBus.getDefault().removeStickyEvent(ResultEvent.class);
        if ("0".equals(resultEvent.getResult())) {
            Log.d("RESET", "恢复出厂设置失败");
            Drawable drawableStart = getDrawable(R.drawable.shibai);
            Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
            if (isNFC) {
                secondPromote.setText(R.string.pin_wrong);
                secondPromote.setCompoundDrawables(drawableStart, null, null, null);
            } else {
               firstPromote.setText(R.string.pin_wrong);
               firstPromote.setCompoundDrawables(drawableStart, null, null, null);
            }
            startActivity(new Intent(this, ResetDeviceFailedActivity.class));
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        Drawable drawableStart = getDrawable(R.drawable.chenggong);
        Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
        if (isNFC) {
            secondPromote.setText(R.string.order_sending_successful);
            secondPromote.setCompoundDrawables(drawableStart, null, null, null);
        } else {
            firstPromote.setCompoundDrawables(drawableStart, null, null, null);
        }
        EventBus.getDefault().postSticky(new FinishEvent());
        startActivity(new Intent(this, ResetDeviceSuccessActivity.class));
        finish();
    }
    @Override
    public void initData() {
        timer = new Timer();
        NfcUtils.nfc(this, false);
        // NOTE: don't edit 👇
//        if (!isNFC) {
            EventBus.getDefault().post(new PinEvent(pin, ""));
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (hasWindowFocus()) {
                        Log.d(TAG, "something went wrong");
                        finishAffinity();
                        EventBus.getDefault().post(new ExitEvent());                    }
                }
            }, 30 * 1000L);
        // NOTE: don't edit 👇
//        }
    }


    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finishAffinity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
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
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcTransport.put("ENABLED", true);
            bleHandler.put("ENABLED", false);
            usbTransport.put("ENABLED", false);
            nfcHandler.put("device", tags);
            protocol.callAttr("notify");
            // NOTE: don't edit 👇
//            EventBus.getDefault().post(new PinEvent(pin, ""));
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (hasWindowFocus()) {
//                        Log.d(TAG, "something went wrong");
//                        finishAffinity();
//                        EventBus.getDefault().post(new ExistEvent());
//                    }
//                }
//            }, 30 * 1000L);
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
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }
}

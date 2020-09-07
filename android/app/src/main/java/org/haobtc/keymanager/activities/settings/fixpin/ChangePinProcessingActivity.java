package org.haobtc.keymanager.activities.settings.fixpin;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.ChangePinEvent;
import org.haobtc.keymanager.event.ExitEvent;
import org.haobtc.keymanager.event.FinishEvent;
import org.haobtc.keymanager.event.PinEvent;
import org.haobtc.keymanager.event.ResultEvent;
import org.haobtc.keymanager.event.TimeoutEvent;
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

public class ChangePinProcessingActivity extends BaseActivity {

    public static final String TAG = ChangePinProcessingActivity.class.getSimpleName();
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.first_promote)
    TextView firstPromote;
    @BindView(R.id.second_promote)
    TextView secondPromote;
    private String pinOrigin, pinNew;
    static final int MAX_LEVEL = 10000;
    private Timer timer;

    @Override
    public int getLayoutId() {
        return isNFC ? R.layout.processing_nfc : R.layout.processing_ble;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        NfcUtils.nfc(this, false);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        pinOrigin = intent.getStringExtra("pin_origin");
        pinNew = intent.getStringExtra("pin_new");
        List<Drawable> drawables = new ArrayList<>();
        drawables.addAll(Arrays.asList(firstPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(secondPromote.getCompoundDrawables()));
        drawables.stream().filter(Objects::nonNull).forEach(drawable -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
            animator.setDuration(800);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setAutoCancel(true);
            animator.start();
        });
    }

    @Override
    public void initData() {
        timer = new Timer();
        // NOTE: don't edit 👇
//        if (!isNFC) {
            if (Strings.isNullOrEmpty(pinOrigin)) {
                EventBus.getDefault().post(new PinEvent(pinNew, ""));
            } else {
                EventBus.getDefault().post(new ChangePinEvent(pinNew, pinOrigin));
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (hasWindowFocus()) {
                        Log.d(TAG, "something went wrong");
                        finishAffinity();
                        EventBus.getDefault().post(new ExitEvent());
                    }
                }
            }, 30 * 1000L);
        // NOTE: don't edit 👇
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changePinResult(ResultEvent resultEvent) {
        if ("0".equals(resultEvent.getResult())) {
            Drawable drawable = getDrawable(R.drawable.shibai);
            Objects.requireNonNull(drawable).setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            if (isNFC) {
                secondPromote.setText(R.string.original_pin_wrong);
                secondPromote.setCompoundDrawables(drawable, null, null, null);
            } else {
                firstPromote.setText(R.string.original_pin_wrong);
                firstPromote.setCompoundDrawables(drawable, null, null, null);
            }
            startActivity(new Intent(this, ChangePinFailedActivity.class));
            finish();
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
            firstPromote.setText(R.string.pin_sussce);
            firstPromote.setCompoundDrawables(drawableStart, null, null, null);
        }
        Intent intent = new Intent(this, ConfirmActivity.class);
        if (Strings.isNullOrEmpty(pinOrigin)) {
            intent.putExtra("tag", "set_pin");
        } else {
            intent.putExtra("tag", TAG);
        }
        EventBus.getDefault().postSticky(new FinishEvent());
        startActivity(intent);
        finish();
    }
    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finishAffinity();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeout(TimeoutEvent event) {

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
//            if (Strings.isNullOrEmpty(pinOrigin)) {
//                EventBus.getDefault().post(new PinEvent(pinNew, ""));
//            } else {
//                EventBus.getDefault().post(new ChangePinEvent(pinNew, pinOrigin));
//            }
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
        timer.cancel();
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}

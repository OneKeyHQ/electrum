package org.haobtc.onekey.activities;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.personalwallet.SingleSigWalletCreator;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.InitEvent;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.event.SendXpubToSigwallet;
import org.haobtc.onekey.utils.NfcUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.bleHandler;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcHandler;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.protocol;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.usbTransport;
/**
 * @author jinxiaomin
 */
@Deprecated
public class ActivatedProcessing extends BaseActivity {
    public static final String TAG = ActivatedProcessing.class.getSimpleName();
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.first_promote)
    TextView firstPromote;
    @BindView(R.id.second_promote)
    TextView secondPromote;
    private boolean useSe;
    private Timer timer;
    /*
        private String pin;
    */
    private final int MAX_LEVEL = 10000;
    private String tagSendxpub;

    @Override
    public int getLayoutId() {
        return isNFC ? R.layout.processing_nfc : R.layout.processing_ble;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        tagSendxpub = getIntent().getStringExtra("tag_xpub");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        List<Drawable> drawables = new ArrayList<>();
        if (isNFC) {
            secondPromote.setText(R.string.order_sending);
        } else {
            firstPromote.setText(R.string.order_sending);
        }
        drawables.addAll(Arrays.asList(firstPromote.getCompoundDrawables()));
        drawables.addAll(Arrays.asList(secondPromote.getCompoundDrawables()));
        drawables.stream().filter(Objects::nonNull)
                .forEach(drawable -> {
                    ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);
                    animator.setDuration(800);
                    animator.setRepeatCount(-1);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                });
        useSe = getIntent().getBooleanExtra("use_se", true);
    }

    @Override
    public void initData() {
        NfcUtils.nfc(this, false);
        timer = new Timer();
        // NOTE: don't edit 👇
//        if (!isNFC) {
            EventBus.getDefault().post(new InitEvent("Activate", useSe));
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (hasWindowFocus()) {
                        Log.d(TAG, "something went wrong");
                        finish();
                        EventBus.getDefault().post(new ExitEvent());
                    }
                }
            }, 30 * 1000L);
        // NOTE: don't edit 👇
//        }
        /*if (!isNFC) {
            EventBus.getDefault().post(new PinEvent(pin, ""));
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProcessing(ResultEvent resultEvent) {
        EventBus.getDefault().removeStickyEvent(ResultEvent.class);
        switch (resultEvent.getResult()) {
            case "1":
                Drawable drawableStart = getDrawable(R.drawable.chenggong);
                Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
                firstPromote.setCompoundDrawables(drawableStart, null, null, null);
                secondPromote.setCompoundDrawables(drawableStart, null, null, null);
                if (SingleSigWalletCreator.TAG.equals(tagSendxpub)) {
                    EventBus.getDefault().post(new SendXpubToSigwallet("get_xpub_and_send"));
                } else {
                    startActivity(new Intent(this, ActivateSuccessActivity.class));
                    finish();
                }

                break;
            case "0":
                Log.d(TAG, "设备激活失败");
                Drawable drawableStartFail = getDrawable(R.drawable.shibai);
                Objects.requireNonNull(drawableStartFail).setBounds(0, 0, drawableStartFail.getMinimumWidth(), drawableStartFail.getMinimumHeight());
                if (isNFC) {
                    secondPromote.setText(R.string.active_failed);
                    secondPromote.setCompoundDrawables(drawableStartFail, null, null, null);
                } else {
                    firstPromote.setText(R.string.active_failed);
                    firstPromote.setCompoundDrawables(drawableStartFail, null, null, null);
                }
                startActivity(new Intent(this, ActiveFailedActivity.class));
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + resultEvent.getResult());
        }
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
            EventBus.getDefault().post(new InitEvent("Activate", useSe));
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (hasWindowFocus()) {
                        Log.d(TAG, "something went wrong");
                        finish();
                        EventBus.getDefault().post(new ExitEvent());
                    }
                }
            }, 30 * 1000L);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void event(SecondEvent updataHint) {
        EventBus.getDefault().removeStickyEvent(SecondEvent.class);
        String msgVote = updataHint.getMsg();
        if ("ActivateFinish".equals(msgVote)) {
            finish();
        }
    }

}

package org.haobtc.onekey.activities.service;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.PinEvent;
import org.haobtc.onekey.utils.NfcUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.bleTransport;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcHandler;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcTag;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.protocol;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.usbTransport;


/**
 * @author liyan
 */
public class NfcNotifyHelper extends BaseActivity {
    @BindView(R.id.text_prompt)
    TextView textPrompt;
    @BindView(R.id.radio_ble)
    RadioButton radioBle;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.input_layout)
    RelativeLayout inputLayout;
    @BindView(R.id.touch_nfc)
    ImageView imageView;
    private String tag;
    private AnimationDrawable animationDrawable;


    @Override
    public int getLayoutId() {
        return R.layout.bluetooth_nfc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        textPrompt.setText(R.string.retouch);
        inputLayout.setVisibility(View.GONE);
        radioBle.setVisibility(View.GONE);
        tag = getIntent().getStringExtra("tag");
        EventBus.getDefault().register(this);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
        animationDrawable.selectDrawable(0);
        animationDrawable.start();
        Optional.ofNullable(nfcTag).ifPresent((tags) -> {
            boolean buttonRequest = getIntent().getBooleanExtra("is_button_request", false);
            if (!buttonRequest) {
                IsoDep isoDep = IsoDep.get(tags);
                try {
                    isoDep.connect();
                    isoDep.close();
                    animationDrawable.stop();
                    nfcHandler.put("device", tags);
                    notifyNfc();
                } catch (IOException e) {
                    Log.d("NFC", "try connect failed");
                    nfcTag = null;
                } catch (IllegalStateException e) {
                    notifyNfc();
                }
            }
        });
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick(R.id.img_cancel)
    public void onViewClicked(View v) {
        if (v.getId() == R.id.img_cancel) {
            nfc.put("IS_CANCEL", true);
            protocol.callAttr("notify");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // get the action of the coming intent
        String action = intent.getAction();
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED)
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            animationDrawable.stop();
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            usbTransport.put("ENABLED", false);
            bleTransport.put("ENABLED", false);
            nfcTransport.put("ENABLED", true);
            new Handler().postDelayed(() -> {
                nfcHandler.put("device", tags);
                    notifyNfc();
            }, 1000);
        }
    }

    private void notifyNfc() {
        if ("PIN".equals(tag)) {
            String pin = getIntent().getStringExtra("pin");
            EventBus.getDefault().post(new PinEvent(pin, ""));
        } else if ("Passphrase".equals(tag)) {
            String passphrase = getIntent().getStringExtra("passphrase");
            EventBus.getDefault().post(new PinEvent("", passphrase));
        }
        protocol.callAttr("notify");
        if (!CommunicationModeSelector.backupTip) {
            EventBus.getDefault().post(new FinishEvent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFinish(FinishEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
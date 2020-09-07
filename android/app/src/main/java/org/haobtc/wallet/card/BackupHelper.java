package org.haobtc.wallet.card;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.settings.fixpin.ConfirmActivity;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.asynctask.BusinessAsyncTask;
import org.haobtc.wallet.bean.HardwareFeatures;
import org.haobtc.wallet.event.ButtonRequestEvent;
import org.haobtc.wallet.event.ExitEvent;
import org.haobtc.wallet.fragment.NeedNewVersion;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.NfcUtils;

import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.wallet.activities.service.CommunicationModeSelector.COMMUNICATION_MODE_NFC;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.bleTransport;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.executorService;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.futureTask;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfcHandler;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfcTransport;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.protocol;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.usbTransport;
import static org.haobtc.wallet.asynctask.BusinessAsyncTask.SE_PROXY;

/**
 * @author liyan
 * @date 2020/8/26
 */
//
public class BackupHelper extends BaseActivity implements BusinessAsyncTask.Helper {
    public static final String TAG = BackupHelper.class.getSimpleName();
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
        EventBus.getDefault().register(this);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.stop();
        animationDrawable.selectDrawable(0);
        animationDrawable.start();
        int step = getIntent().getIntExtra("step", 0);
        if (step == 2) {
            textPrompt.setText(R.string.step_two);
        }
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
                if (!Strings.isNullOrEmpty(getIntent().getAction())) {
                    try {
                        HardwareFeatures features = getFeatures();
                        if (!features.isInitialized() && "backup2card".equals(getIntent().getAction())) {
                            Toast.makeText(this, R.string.need_active, Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        } else if (features.isInitialized() && "recovery".equals(getIntent().getAction())) {
                            Toast.makeText(this, R.string.need_reset, Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                        // features.getSeVersion() present after 2.0.0.2,if the version of se is below 2.0.0.2, it can be ""
                        if (Strings.isNullOrEmpty(features.getSeVersion())) {
                            Toast.makeText(this, R.string.old_se_version, Toast.LENGTH_LONG).show();
                            EventBus.getDefault().post(new ExitEvent());
                            finish();
                            return;
                        }
                        // if the version of stm32 is below 1.9.6, turn to upgrade page
                        if (features.getMajorVersion() <= 1 && features.getMinorVersion() <= 9 && features.getPatchVersion() < 7) {
                            NeedNewVersion fragment = new NeedNewVersion(R.string.update2_new_version, R.string.old_version);
                            fragment.setActivity(this);
                            fragment.show(getSupportFragmentManager(), "");
                            return;
                        }
                        String message = getIntent().getStringExtra("message");
                        new BusinessAsyncTask().setHelper(this).execute(SE_PROXY, message, COMMUNICATION_MODE_NFC);
                    } catch (Exception e) {
                        finish();
                    }
                }
            }, 1000);
        }
    }

    @NonNull
    private HardwareFeatures getFeatures() throws Exception {
        String feature;
        try {
            futureTask = new FutureTask<>(() -> Daemon.commands.callAttr("get_feature", COMMUNICATION_MODE_NFC));
            executorService.submit(futureTask);
            feature = futureTask.get(5, TimeUnit.SECONDS).toString();
            return HardwareFeatures.objectFromData(feature);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_message), Toast.LENGTH_SHORT).show();
            if (nfc != null) {
                nfc.put("IS_CANCEL", true);
                protocol.callAttr("notify");
            }
            e.printStackTrace();
            throw e;
        }
    }
    @Subscribe
    public void onButtonRequest(ButtonRequestEvent event) {
        if("backup2card".equals(getIntent().getAction())) {
            Intent intent = new Intent(this, ConfirmActivity.class);
            intent.putExtra("tag", TAG);
            startActivity(intent);
        }
    }
    @Subscribe
    public void onExit(ExitEvent event) {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        NfcUtils.mNfcAdapter = null;
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onResult(String s) {
        if ("backup2card".equals(getIntent().getAction())) {
            Intent intent = new Intent();
            intent.putExtra("res", s);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if ("recovery".equals(getIntent().getAction())) {
            Intent intent = new Intent(this, CardPin.class);
            intent.setAction("recovery");
            intent.putExtra("extras", s);
            startActivity(intent);
            finish();
       }

    }

    @Override
    public void onCancelled() {

    }
}

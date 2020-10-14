package org.haobtc.onekey.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.personalwallet.SingleSigWalletCreator;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.activities.settings.recovery_set.RecoveryActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.card.BackupHelper;
import org.haobtc.onekey.event.InitEvent;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.event.SendXpubToSigwallet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.features;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;

public class WalletUnActivatedActivity extends BaseActivity {
    public static final String TAG = "org.wallet.activities.WalletUnActivatedActivity";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.button_activate)
    Button buttonActivate;
    @BindView(R.id.button_recover)
    Button buttonRecover;
    @BindView(R.id.img_use_se)
    ImageView useSe;
    @BindView(R.id.linear_use_se)
    LinearLayout linearUseSe;
    private String tagXpub = "";
    private boolean buseSe = false;
    private static final String MIN_SELECTABLE_SE_VERSION_NUMBER = "1.0.0.4";

    @Override
    public int getLayoutId() {
        return R.layout.activate;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        tagXpub = intent.getStringExtra("tag_Xpub");
        if (Strings.isNullOrEmpty(features.getSeVersion()) || MIN_SELECTABLE_SE_VERSION_NUMBER.compareTo(features.getSeVersion()) >= 0) {
            linearUseSe.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
    }


    @SingleClick(value = 2000)
    @OnClick({R.id.img_back, R.id.button_recover, R.id.linear_use_se, R.id.button_activate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.button_recover:
                chooseBackupDialog(WalletUnActivatedActivity.this);
                break;
            case R.id.linear_use_se:
                if (buseSe) {
                    buseSe = false;
                    useSe.setImageDrawable(getDrawable(R.drawable.circle_empty));
                } else {
                    buseSe = true;
                    useSe.setImageDrawable(getDrawable(R.drawable.chenggong));
                }
                break;
            case R.id.button_activate:
                //Select backup method
                if (isNFC) {
                    Intent intent = new Intent(this, CommunicationModeSelector.class);
                    if (SingleSigWalletCreator.TAG.equals(tagXpub)) {
                        intent.putExtra("tag", SingleSigWalletCreator.TAG);
                    }
                    intent.putExtra("use_se", buseSe).setAction("init");
                    startActivity(intent);
                } else {
                    EventBus.getDefault().post(new InitEvent("Activate", buseSe));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }


    private void chooseBackupDialog(Context context) {
        //set see view
        View view = View.inflate(context, R.layout.select_backup_method, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.recovery_from_local_backup).setOnClickListener(v -> {
            startActivity(new Intent(this, RecoveryActivity.class));
            dialogBtoms.dismiss();
            finish();
        });

        view.findViewById(R.id.recovery_from_key_lite).setOnClickListener(v -> {
            if (!isNFC) {
                Toast.makeText(this, R.string.nfc_only, Toast.LENGTH_SHORT).show();
                dialogBtoms.dismiss();
                return;
            }
            Intent intent = new Intent(this, BackupHelper.class);
            intent.setAction("recovery");
            intent.putExtra("message", "00F8010100");
            startActivity(intent);
            dialogBtoms.dismiss();
        });

        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void showProcessing(ResultEvent resultEvent) {
        EventBus.getDefault().removeStickyEvent(ResultEvent.class);
        switch (resultEvent.getResult()) {
            case "1":
                if (SingleSigWalletCreator.TAG.equals(tagXpub)) {
                    EventBus.getDefault().post(new SendXpubToSigwallet("get_xpub_and_send"));
                }
                startActivity(new Intent(this, ActivatePromptPIN.class));
                finish();
                break;
            case "0":
                Log.d(TAG, "设备激活失败");
                startActivity(new Intent(this, ActiveFailedActivity.class));
                finish();
                break;
            default:

                throw new IllegalStateException("Unexpected value: " + resultEvent.getResult());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

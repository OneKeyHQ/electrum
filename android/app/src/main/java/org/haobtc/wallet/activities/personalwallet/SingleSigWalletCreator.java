package org.haobtc.wallet.activities.personalwallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.event.ExitEvent;
import org.haobtc.wallet.event.FixWalletNameEvent;
import org.haobtc.wallet.event.ReceiveXpub;
import org.haobtc.wallet.utils.Daemon;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleSigWalletCreator extends BaseActivity {

    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    @BindView(R.id.edit_walletName_setting)
    EditText editWalletNameSetting;
    @BindView(R.id.bn_multi_next)
    Button bnMultiNext;
    @BindView(R.id.number)
    TextView number;
    private SharedPreferences.Editor edit;
    private int defaultName;
    private int walletNameNum;
    public String pin = "";
    public static final String TAG = SingleSigWalletCreator.class.getSimpleName();
    private long lastNotify;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_personal_wallet;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        defaultName = preferences.getInt("defaultName", 0);
        init();

    }

    private void init() {
        walletNameNum = defaultName + 1;
        editWalletNameSetting.setText(String.format("钱包%s", String.valueOf(walletNameNum)));
        number.setText(String.format(Locale.CHINA, "%d/16", editWalletNameSetting.length()));

        editWalletNameSetting.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                number.setText(String.format(Locale.CHINA, "%d/16", input.length()));
                if (input.length() > 15) {
                    mToast(getString(R.string.moreinput_text_fixbixinkey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    bnMultiNext.setEnabled(true);
                    bnMultiNext.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    bnMultiNext.setEnabled(false);
                    bnMultiNext.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }
        });

    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_backCreat, R.id.bn_multi_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                finish();
                break;
            case R.id.bn_multi_next:
                mCreatOnlyWallet();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    //creat personal wallet
    private void mCreatOnlyWallet() {
        String strWalletName = editWalletNameSetting.getText().toString();
        if (TextUtils.isEmpty(strWalletName)) {
            mToast(getString(R.string.set_wallet));
            return;
        }
        CommunicationModeSelector.runnables.clear();
        CommunicationModeSelector.runnables.add(null);
        Intent intent = new Intent(this, CommunicationModeSelector.class);
        intent.putExtra("tag", TAG);
        startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void doInit(ReceiveXpub event) {
        if (System.currentTimeMillis() - lastNotify > 10 * 1000L) {
            lastNotify = System.currentTimeMillis();
        } else {
            return;
        }
        String xpub = event.getXpub();
        String deviceId = event.getDeviceId();
        String strXpub = "[[\"" + xpub + "\",\"" + deviceId + "\"]]";
        String walletName = editWalletNameSetting.getText().toString();
        try {
            Daemon.commands.callAttr("import_create_hw_wallet", walletName, 1, 1, strXpub);
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            EventBus.getDefault().post(new ExitEvent());
            if ("BaseException: file already exists at path".equals(message)) {
                mToast(getString(R.string.changewalletname));
            } else {
                assert message != null;
                if (message.contains("The same xpubs have create wallet")) {
                    String haveWalletName = message.substring(message.indexOf("name=") + 5);
                    mToast(getString(R.string.xpub_have_wallet) + haveWalletName);
                }
            }
            return;
        }
        edit.putInt("defaultName", walletNameNum);
        edit.apply();
        walletName = editWalletNameSetting.getText().toString();
        if (event.isShowUI()) {
            Intent intent = new Intent(SingleSigWalletCreator.this, CreatFinishPersonalActivity.class);
            intent.putExtra("walletNames", walletName);
            intent.putExtra("flagTag", "personal");
            intent.putExtra("strBixinname", xpub);
            intent.putExtra("needBackup", event.getNeedBackup());
            startActivity(intent);
        } else {
            EventBus.getDefault().post(new FixWalletNameEvent(walletName));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

package org.haobtc.keymanager.activities.personalwallet.mnemonic_word;

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

import com.chaquo.python.Kwarg;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.FirstEvent;
import org.haobtc.keymanager.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.executorService;


public class CreatePersonalHelpPassActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.edt_Pass1)
    EditText edtPass1;
    @BindView(R.id.edt_Pass2)
    EditText edtPass2;
    @BindView(R.id.btn_setPin)
    Button btnSetPin;
    private String seed;
    private String name;
    private int walletNameNum;
    private SharedPreferences.Editor edit;
    private String mnemonicWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_personal_help_pass;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        Intent intent = getIntent();
        seed = intent.getStringExtra("strNewseed");
        name = intent.getStringExtra("newWalletName");
        walletNameNum = intent.getIntExtra("walletNameNum", 0);
        mnemonicWalletType = intent.getStringExtra("mnemonicWalletType");
        inits();

    }

    private void inits() {
        edtPass1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(edtPass2.getText().toString())) {
                    btnSetPin.setEnabled(true);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    btnSetPin.setEnabled(false);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtPass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(edtPass1.getText().toString())) {
                    btnSetPin.setEnabled(true);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    btnSetPin.setEnabled(false);
                    btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_setPin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_setPin:
                Intent intent = new Intent(CreatePersonalHelpPassActivity.this, CreateInputHelpWordWalletSuccseActivity.class);
                intent.putExtra("newWalletName", name);
                startActivity(intent);
                finish();
                improtWallet();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }


    private void improtWallet() {
        String strPass1 = edtPass1.getText().toString();
        String strPass2 = edtPass2.getText().toString();
        if (TextUtils.isEmpty(strPass1)) {
            mToast(getString(R.string.set_pass));
            return;
        }
        if (TextUtils.isEmpty(strPass2)) {
            mToast(getString(R.string.set_pass_second));
            return;
        }
        if (!strPass1.equals(strPass2)) {
            mToast(getString(R.string.two_different_pass));
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!TextUtils.isEmpty(mnemonicWalletType)) {
                        Daemon.commands.callAttr("create", name, strPass1, new Kwarg("seed", seed), new Kwarg("bip39_derivation", mnemonicWalletType));
                    } else {
                        Daemon.commands.callAttr("create", name, strPass1, new Kwarg("seed", seed));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("path is exist")) {
                        mToast(getString(R.string.changewalletname));
                    } else if (e.getMessage().contains("The same seed have create wallet")) {
                        String haveWalletName = e.getMessage().substring(e.getMessage().indexOf("name=") + 5);
                        mToast(getString(R.string.same_seed_have) + haveWalletName);
                    }
                    return;
                }
                edit.putBoolean("haveCreateNopass", true);
                edit.putInt("defaultName", walletNameNum);
                edit.apply();
                try {
                    Daemon.commands.callAttr("load_wallet", name);
                    Daemon.commands.callAttr("select_wallet", name);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                EventBus.getDefault().postSticky(new FirstEvent("createSinglePass"));
            }
        });
    }
}





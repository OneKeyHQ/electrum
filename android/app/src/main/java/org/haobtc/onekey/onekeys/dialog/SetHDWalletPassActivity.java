package org.haobtc.onekey.onekeys.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.InputPassSendEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.LoadWalletlistEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.event.WalletAddressEvent;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.RecoveryChooseWalletActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.onekeys.homepage.process.ExportPrivateActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.PwdEditText;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetHDWalletPassActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_set_pass)
    TextView testSetPass;
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.pwd_edittext)
    PwdEditText pwdEdittext;
    @BindView(R.id.text_long)
    TextView textLong;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.text_confirm)
    TextView textConfirm;
    @BindView(R.id.text_page_title)
    TextView textPageTitle;
    private boolean input = false;
    private String sixPass;
    private SharedPreferences.Editor edit;
    private String importHdword;
    private String seed;
    private String walletName;
    private String currencyType;
    private String privateKey;
    private String exportType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_h_d_wallet_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        currencyType = getIntent().getStringExtra("currencyType");
        seed = getIntent().getStringExtra("recoverySeed");
        privateKey = getIntent().getStringExtra("privateKey");

        if ("importHdword".equals(importHdword) || "exportPrivateKey".equals(importHdword) || "deleteAllWallet".equals(importHdword) || "derive".equals(importHdword) || "single".equals(importHdword) || "importMnemonic".equals(importHdword) || "importPrivateKey".equals(importHdword) || "deleteSingleWallet".equals(importHdword) || "send".equals(importHdword)) {
            exportType = getIntent().getStringExtra("exportType");
            checkPassTip();
        } else if ("fixHdPass".equals(importHdword)) {
            textPageTitle.setText(getString(R.string.fix_pass));
            testSetPass.setText(getString(R.string.input_your_former_pass));
            textTip.setText(getString(R.string.fix_former_tip));
            textLong.setText(getString(R.string.long_pass_tip));
            textConfirm.setText(getString(R.string.change_keyboard));
        } else if ("recoveryHdWallet".equals(importHdword)) {
            checkPassTip();
        } else {
            textPageTitle.setText(getString(R.string.create_new_walt));
        }
    }

    //check password tip
    private void checkPassTip() {
        testSetPass.setText(getString(R.string.input_your_pass));
        textTip.setText(getString(R.string.dont_tell));
        textLong.setText(getString(R.string.long_pass_tip));
        textConfirm.setText(getString(R.string.change_keyboard));
    }

    @Override
    public void initData() {
        pwdEdittext.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.btn_next, R.id.lin_short_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_short_pass:
                pwdEdittext.clearText();
                Intent intent1 = new Intent(SetHDWalletPassActivity.this, SetLongPassActivity.class);
                intent1.putExtra("importHdword", importHdword);
                intent1.putExtra("walletName", walletName);
                intent1.putExtra("recoverySeed", seed);
                intent1.putExtra("privateKey", privateKey);
                if ("derive".equals(importHdword)) {
                    intent1.putExtra("currencyType", currencyType);
                } else if ("importPrivateKey".equals(importHdword)) {
                    intent1.putExtra("privateKey", privateKey);
                }
                startActivity(intent1);
                break;
            case R.id.btn_next:
                if ("importHdword".equals(importHdword)) {
                    //export Mnemonic words
                    exportWord();
                } else if ("fixHdPass".equals(importHdword)) {
                    //fix pass
                    fixHdPass();
                } else if ("exportPrivateKey".equals(importHdword)) {
                    //import private key
                    exportPrivateKey();
                } else if ("deleteAllWallet".equals(importHdword)) {
                    deleteAllWallet();
                } else if ("recoveryHdWallet".equals(importHdword)) {
                    recoveryHdWallet();
                } else if ("derive".equals(importHdword)) {
                    createDeriveWallet();
                } else if ("single".equals(importHdword)) {
                    createSingleWallet();
                } else if ("importMnemonic".equals(importHdword)) {
                    importMnemonic();
                } else if ("importPrivateKey".equals(importHdword)) {
                    importPrivateKey();
                } else if ("deleteSingleWallet".equals(importHdword)) {
                    deleteSingleWallet();
                } else if ("send".equals(importHdword)) {
                    EventBus.getDefault().post(new InputPassSendEvent(pwdEdittext.getText().toString()));
                } else {
                    if (!input) {
                        sixPass = pwdEdittext.getText().toString();
                        testSetPass.setText(getString(R.string.input_pass));
                        textTip.setText(getString(R.string.dont_tell));
                        pwdEdittext.clearText();
                        textLong.setText(getString(R.string.test_long_tip));
                        input = true;
                    } else {
                        if (!sixPass.equals(pwdEdittext.getText().toString())) {
                            mToast(getString(R.string.two_different_pass));
                            return;
                        }
                        PyObject createHdWallet = null;
                        try {
                            createHdWallet = Daemon.commands.callAttr("create_hd_wallet", pwdEdittext.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage().contains("Incorrect password")) {
                                mToast(getString(R.string.wrong_pass));
                            }
                            return;
                        }
                        if (createHdWallet != null) {
                            Log.i("createHdWallet", "onViewClicked:-- " + createHdWallet);
                            Intent intent = new Intent(SetHDWalletPassActivity.this, HomeOnekeyActivity.class);
                            startActivity(intent);
                            edit.putBoolean("isHaveWallet", true);
                            edit.putString("loadWalletName", "BTC-1");
                            edit.apply();
                        }
                    }
                }
                break;
        }
    }

    private void deleteSingleWallet() {
        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", walletName));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            return;
        }
        mToast(getString(R.string.delete_succse));
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        EventBus.getDefault().post(new SecondEvent("finish"));
        finish();
    }

    private void importPrivateKey() {
        //import private key wallet
        try {
            Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString(), new Kwarg("privkeys", privateKey));

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("path is exist")) {
                mToast(getString(R.string.changewalletname));
            } else if (e.getMessage().contains("The same seed have create wallet")) {
                String haveWalletName = e.getMessage().substring(e.getMessage().indexOf("name=") + 5);
                mToast(getString(R.string.same_seed_have) + haveWalletName);
            } else if (e.getMessage().contains("'NoneType' object is not iterable")) {
                mToast(getString(R.string.private_key_wrong));
            } else if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            finish();
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);
    }

    private void importMnemonic() {
        try {
            Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString(), new Kwarg("seed", seed));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("path is exist")) {
                mToast(getString(R.string.changewalletname));
            } else if (e.getMessage().contains("The same seed have create wallet")) {
                String haveWalletName = e.getMessage().substring(e.getMessage().indexOf("name=") + 5);
                mToast(getString(R.string.same_seed_have) + haveWalletName);
            } else if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);
    }

    private void createDeriveWallet() {
        try {
            Daemon.commands.callAttr("create_derived_wallet", walletName, pwdEdittext.getText().toString(), currencyType);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            } else if (e.getMessage().contains("The file already exists")) {
                mToast(getString(R.string.changewalletname));
            }
            e.printStackTrace();
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);
        finish();
    }

    private void createSingleWallet() {
        try {
            Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString());
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        edit.putBoolean("isHaveWallet", true);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOnekeyActivity.class);
        finish();
    }

    //recovery Hd Wallet
    private void recoveryHdWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create_hd_wallet", pwdEdittext.getText().toString(), new Kwarg("seed", seed));
            if (pyObject.toString().length() > 2) {
                Intent intent = new Intent(SetHDWalletPassActivity.this, RecoveryChooseWalletActivity.class);
                intent.putExtra("recoveryData", pyObject.toString());
                startActivity(intent);
                mlToast(getString(R.string.loading));
                finish();
            } else {
                try {
                    Daemon.commands.callAttr("recovery_confirmed", "[]");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                mToast(getString(R.string.not_recovery_wallet));
                edit.putBoolean("isHaveWallet", true);
                edit.putString("loadWalletName", "BTC-1");
                edit.apply();
                mIntent(HomeOnekeyActivity.class);
                finish();
            }

        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
        }

    }

    private void deleteAllWallet() {
        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", "BTC-1"));
//            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", "ETH-1"));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            return;
        }
        mToast(getString(R.string.delete_succse));
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        EventBus.getDefault().post(new LoadWalletlistEvent());
        EventBus.getDefault().post(new FinishEvent());
        finish();
    }

    private void exportPrivateKey() {
        try {
            PyObject exportPrivateKey = Daemon.commands.callAttr("export_privkey", pwdEdittext.getText().toString());
            Intent intent = new Intent(SetHDWalletPassActivity.this, ExportPrivateActivity.class);
            intent.putExtra("privateKey", exportPrivateKey.toString());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            return;
        }
    }

    //fix pass
    private void fixHdPass() {
        if (!input) {
            sixPass = pwdEdittext.getText().toString();
            testSetPass.setText(getString(R.string.set_new_pass));
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
            pwdEdittext.clearText();
            input = true;
        } else {
            try {
                Daemon.commands.callAttr("update_wallet_password", sixPass, pwdEdittext.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass_next_input));
                    testSetPass.setText(getString(R.string.input_your_former_pass));
                    pwdEdittext.clearText();
                    btnNext.setEnabled(false);
                    btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
                    input = false;
                }
                return;
            }
            mToast(getString(R.string.fix_success));
            finish();
        }
    }

    //export Mnemonic words
    private void exportWord() {
        PyObject createHdWallet = null;
        try {
            createHdWallet = Daemon.commands.callAttr("export_seed", pwdEdittext.getText().toString(), walletName);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(SetHDWalletPassActivity.this, HdRootMnemonicsActivity.class);
        intent.putExtra("exportWord", createHdWallet.toString());
        intent.putExtra("importHdword", importHdword);
        intent.putExtra("exportType", "backup");
        startActivity(intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void afterTextChanged(Editable s) {
        int length = s.length();
        if ((length == 6)) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }
}
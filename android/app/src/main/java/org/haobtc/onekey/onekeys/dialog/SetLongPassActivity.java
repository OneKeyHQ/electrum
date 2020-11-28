package org.haobtc.onekey.onekeys.dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.RecoveryChooseWalletActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.onekeys.homepage.process.ExportPrivateActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetLongPassActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_set_pass)
    TextView testSetPass;
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.edit_pass)
    EditText editPass;
    @BindView(R.id.img_eye_yes)
    ImageView imgEyeYes;
    @BindView(R.id.img_eye_no)
    ImageView imgEyeNo;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.text_long)
    TextView textLong;
    @BindView(R.id.text_change)
    TextView textChange;
    @BindView(R.id.text_page_title)
    TextView textPageTitle;
    private boolean input = false;
    private String firstPass;
    private SharedPreferences.Editor edit;
    private String importHdword;
    private String recoverySeed;
    private String walletName;
    private String privateKey;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_long_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        editPass.addTextChangedListener(this);
    }

    @Override
    public void initData() {
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        privateKey = getIntent().getStringExtra("privateKey");
        if ("importHdword".equals(importHdword) || "exportPrivateKey".equals(importHdword) || "deleteAllWallet".equals(importHdword) || "derive".equals(importHdword) || "single".equals(importHdword) || "importMnemonic".equals(importHdword) || "importPrivateKey".equals(importHdword) || "deleteSingleWallet".equals(importHdword) || "send".equals(importHdword)) {
            textContent();
        } else if ("fixHdPass".equals(importHdword)) {
            textPageTitle.setText(getString(R.string.fix_pass));
            editPass.setHint(getString(R.string.input_your_former_pass));
            testSetPass.setText(getString(R.string.input_your_former_pass));
            textTip.setText(getString(R.string.fix_former_tip));
            textLong.setText(getString(R.string.long_pass_tip));
        } else if ("exportKeystore".equals(importHdword)) {
            textContent();
        } else if ("recoveryHdWallet".equals(importHdword)) {
            recoverySeed = getIntent().getStringExtra("recoverySeed");
            textContent();
        } else {
            textPageTitle.setText(getString(R.string.create_new_walt));
        }

    }

    public void textContent() {
        testSetPass.setText(getString(R.string.input_your_pass));
        textTip.setText(getString(R.string.dont_tell));
        textLong.setText(getString(R.string.long_pass_tip));
        textChange.setText(getString(R.string.change_short_keyboard));
        editPass.setHint(getString(R.string.input_password));
    }

    @OnClick({R.id.img_back, R.id.img_eye_yes, R.id.img_eye_no, R.id.btn_next, R.id.lin_short_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_eye_yes:
                imgEyeYes.setVisibility(View.GONE);
                imgEyeNo.setVisibility(View.VISIBLE);
                editPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editPass.setSelection(editPass.getText().toString().length());
                break;
            case R.id.img_eye_no:
                imgEyeYes.setVisibility(View.VISIBLE);
                imgEyeNo.setVisibility(View.GONE);
                editPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editPass.setSelection(editPass.getText().toString().length());
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
                    EventBus.getDefault().post(new InputPassSendEvent(editPass.getText().toString()));
                } else {
                    if (!input) {
                        firstPass = editPass.getText().toString();
                        testSetPass.setText(getText(R.string.input_you_pass));
                        textTip.setText(getText(R.string.dont_tell));
                        textLong.setText(getText(R.string.long_pass_tip));
                        btnNext.setEnabled(false);
                        btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
                        editPass.setHint(getString(R.string.input_you_pass));
                        editPass.setText("");
                        input = true;
                    } else {
                        if (!firstPass.equals(editPass.getText().toString())) {
                            mToast(getString(R.string.two_different_pass));
                            return;
                        }
                        PyObject createHdWallet = null;
                        try {
                            createHdWallet = Daemon.commands.callAttr("create_hd_wallet", editPass.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage().contains("Incorrect password")) {
                                mToast(getString(R.string.wrong_pass));
                            }
                            return;
                        }
                        if (createHdWallet != null) {
                            Log.i("createHdWallet", "onViewClicked:-- " + createHdWallet);
                            Intent intent = new Intent(SetLongPassActivity.this, HomeOneKeyActivity.class);
                            startActivity(intent);
                            PyEnv.loadLocalWalletInfo(this);
                            edit.putString("loadWalletName", "BTC-1");
                            edit.apply();
                            finish();

                        }
                    }
                }
                break;
            case R.id.lin_short_pass:
                finish();
                break;
        }
    }

    private void deleteSingleWallet() {
        try {
            Daemon.commands.callAttr("delete_wallet", editPass.getText().toString(), new Kwarg("name", walletName));
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
            Daemon.commands.callAttr("create", walletName, editPass.getText().toString(), new Kwarg("privkeys", privateKey));

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
        PyEnv.loadLocalWalletInfo(this);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
    }

    private void importMnemonic() {
        String seed = getIntent().getStringExtra("strNewseed");
        try {
            Daemon.commands.callAttr("create", walletName, editPass.getText().toString(), new Kwarg("seed", seed));

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
        PyEnv.loadLocalWalletInfo(this);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
    }

    private void createDeriveWallet() {
        String currencyType = getIntent().getStringExtra("currencyType");
        try {
            Daemon.commands.callAttr("create_derived_wallet", walletName, editPass.getText().toString(), currencyType);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
        finish();
    }

    private void createSingleWallet() {
        try {
            Daemon.commands.callAttr("create", walletName, editPass.getText().toString());
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString("loadWalletName", walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
        finish();
    }

    private void recoveryHdWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create_hd_wallet", editPass.getText().toString(), new Kwarg("seed", recoverySeed));
            if (pyObject.toString().length() > 2) {
                Intent intent = new Intent(SetLongPassActivity.this, RecoveryChooseWalletActivity.class);
                intent.putExtra("recoveryData", pyObject.toString());
                startActivity(intent);
                mlToast(getString(R.string.loading));
                finish();
            } else {
                try {
                    Daemon.commands.callAttr("recovery_confirmed", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                mToast(getString(R.string.not_recovery_wallet));
                PyEnv.loadLocalWalletInfo(this);
                edit.putString("loadWalletName", "BTC-1");
                edit.apply();
                mIntent(HomeOneKeyActivity.class);
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
            Daemon.commands.callAttr("delete_wallet", editPass.getText().toString(), new Kwarg("name", "BTC-1"));
//            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", "ETH-1"));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
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
            PyObject exportPrivateKey = Daemon.commands.callAttr("export_privkey", editPass.getText().toString());
            Intent intent = new Intent(SetLongPassActivity.this, ExportPrivateActivity.class);
            intent.putExtra("privateKey", exportPrivateKey.toString());
            startActivity(intent);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }

    }

    private void fixHdPass() {
        if (!input) {
            firstPass = editPass.getText().toString();
            testSetPass.setText(getString(R.string.set_new_pass));
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
            editPass.setText("");
            input = true;
        } else {
            try {
                Daemon.commands.callAttr("update_wallet_password", firstPass, editPass.getText().toString());
            } catch (Exception e) {
                if (e.getMessage().contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass));
                }
                e.printStackTrace();
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
            createHdWallet = Daemon.commands.callAttr("export_seed", editPass.getText().toString(), walletName);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new SecondEvent("finish"));
        Intent intent = new Intent(SetLongPassActivity.this, HdRootMnemonicsActivity.class);
        intent.putExtra("exportWord", createHdWallet.toString());
        intent.putExtra("importHdword", importHdword);
        startActivity(intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if ((editPass.length() > 0)) {
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
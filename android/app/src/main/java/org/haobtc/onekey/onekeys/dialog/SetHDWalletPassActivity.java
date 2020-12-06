package org.haobtc.onekey.onekeys.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.InputPassSendEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.LoadWalletlistEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.RecoveryChooseWalletActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.onekeys.homepage.process.ExportPrivateActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.PwdEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    private String operateType;
    private String deleteHdWalletName;
    private boolean isHaveWallet;
    private SharedPreferences preferences;
    private ArrayList<String> typeList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_h_d_wallet_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        currencyType = getIntent().getStringExtra("currencyType");
        seed = getIntent().getStringExtra("recoverySeed");
        privateKey = getIntent().getStringExtra("privateKey");
        operateType = getIntent().getStringExtra(Constant.OPERATE_TYPE);
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");//删除所有hd钱包的名字
        isHaveWallet = PreferencesManager.getAll(this, Constant.WALLETS).isEmpty();
        inits();

    }

    private void inits() {
        typeList = new ArrayList<>();
        Map<String, ?> jsonToMap = PreferencesManager.getAll(this, Constant.WALLETS);
        Set keySets = jsonToMap.keySet();
        Iterator ki = keySets.iterator();
        while (ki.hasNext()) {
            //get key
            String key = (String) ki.next();
            String type = jsonToMap.get(key).toString();
            if (!type.contains("hw") && !"btc-watch-standard".equals(type)) {
                typeList.add(key);
            }
        }
        Log.i("typeListjxmjxm", "inits======: " + isHaveWallet);
        Log.i("typeListjxmjxm", "inits------: " + typeList);
        if (typeList == null || typeList.size() == 0) {
            textPageTitle.setText(getString(R.string.create_new_walt));
        } else {
            if ("exportHdword".equals(importHdword) || "backupMnemonic".equals(importHdword) || "exportPrivateKey".equals(importHdword) || "deleteAllWallet".equals(importHdword) || "derive".equals(importHdword) || "single".equals(importHdword) || "importMnemonic".equals(importHdword) || "importPrivateKey".equals(importHdword) || "deleteSingleWallet".equals(importHdword) || "send".equals(importHdword) || "recoveryHdWallet".equals(importHdword)) {
                checkPassTip();
            } else if ("fixHdPass".equals(importHdword)) {
                textPageTitle.setText(getString(R.string.fix_pass));
                testSetPass.setText(getString(R.string.input_your_former_pass));
                textTip.setText(getString(R.string.fix_former_tip));
                textLong.setText(getString(R.string.long_pass_tip));
                textConfirm.setText(getString(R.string.change_keyboard));
            }
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
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    pwdEdittext.clearText();
                    Intent intent1 = new Intent(SetHDWalletPassActivity.this, SetLongPassActivity.class);
                    intent1.putExtra("importHdword", importHdword);
                    intent1.putExtra("walletName", walletName);
                    intent1.putExtra("recoverySeed", seed);
                    intent1.putExtra("privateKey", privateKey);
                    intent1.putExtra("deleteHdWalletName", deleteHdWalletName);
                    if ("derive".equals(importHdword)) {
                        intent1.putExtra("currencyType", currencyType);
                    } else if ("importPrivateKey".equals(importHdword)) {
                        intent1.putExtra("privateKey", privateKey);
                    }
                    startActivity(intent1);
                } else {
                    finish();
                }
                break;
            case R.id.btn_next:
                if (typeList == null || typeList.size() == 0) {
                    if (!input) {
                        sixPass = pwdEdittext.getText().toString();
                        testSetPass.setText(getString(R.string.input_pass));
                        textTip.setText(getString(R.string.dont_tell));
                        pwdEdittext.clearText();
                        textLong.setText(getString(R.string.test_long_tip));
                        input = true;
                    } else {
                        edit.putString("shortOrLongPass", "short");
                        edit.apply();
                        if (!sixPass.equals(pwdEdittext.getText().toString())) {
                            mToast(getString(R.string.two_different_pass));
                            return;
                        } else {
                            walletStatus();
                        }
                    }
                } else {
                    walletStatus();
                }
                break;
        }
    }

    //如果没有钱包创建需要设置密码，否则验证密码即可
    private void walletStatus() {
        if ("exportHdword".equals(importHdword) || "backupMnemonic".equals(importHdword)) {
            //export Mnemonic words
            exportWord(importHdword);
        } else if ("fixHdPass".equals(importHdword)) {
            //fix pass
            fixHdPass();
        } else if ("exportPrivateKey".equals(importHdword)) {
            //import private key
            exportPrivateKey();
        } else if ("deleteAllWallet".equals(importHdword)) {
            deleteAllWallet();
        } else if ("recoveryHdWallet".equals(importHdword)) {
            if (Constant.RECOVERY_TYPE.equals(operateType)) {
                EventBus.getDefault().post(new GotPassEvent(pwdEdittext.getText().toString()));
                finish();
                return;
            }
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
            createNewHdWallet();
        }
    }

    private void createNewHdWallet() {
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
            Intent intent = new Intent(SetHDWalletPassActivity.this, HomeOneKeyActivity.class);
            startActivity(intent);
            PyEnv.loadLocalWalletInfo(this);
//            edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, "BTC-1");
            edit.apply();
        }
    }

    private void deleteSingleWallet() {
        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", walletName));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("path is exist")) {
                mToast(getString(R.string.changewalletname));
            } else if (e.getMessage().contains("'NoneType' object is not iterable")) {
                mToast(getString(R.string.private_key_wrong));
            } else if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            } else if (e.getMessage().contains("The file already exists")) {
                mToast(getString(R.string.changemessage));
            }
            return;
        }
        mToast(getString(R.string.delete_succse));
        PreferencesManager.remove(this, Constant.WALLETS, walletName);
        PyEnv.loadLocalWalletInfo(this);
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
            } else if (e.getMessage().contains("The file already exists")) {
                mToast(getString(R.string.changemessage));
            } else if (e.getMessage().contains("Invalid private")) {
                mToast(getString(R.string.private_invalid));
            }
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
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
            } else if (e.getMessage().contains("Invalid private")) {
                mToast(getString(R.string.wrong_private));
            } else if (e.getMessage().contains("The file already exist")) {
                mToast(getString(R.string.have_private));
            }
            finish();
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
    }

    private void createDeriveWallet() {
        try {
            Daemon.commands.callAttr("create_derived_wallet", walletName, pwdEdittext.getText().toString(), currencyType);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            } else if (e.getMessage().contains("The file already exists")) {
                mToast(getString(R.string.changemessage));
            }
            e.printStackTrace();
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
        finish();
    }

    private void createSingleWallet() {
        try {
            Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString());
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            } else if (e.getMessage().contains("path is exist")) {
                mToast(getString(R.string.changewalletname));
            }
            e.printStackTrace();
            return;
        }
        PyEnv.loadLocalWalletInfo(this);
        edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, walletName);
        edit.apply();
        mIntent(HomeOneKeyActivity.class);
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
                PyEnv.loadLocalWalletInfo(this);
                edit.putString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET, "BTC-1");
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
        ArrayList<String> hd = new ArrayList<>();
        Map<String, ?> jsonToMap = PreferencesManager.getAll(this, Constant.WALLETS);
        Set keySets = jsonToMap.keySet();
        Iterator ki = keySets.iterator();
        while (ki.hasNext()) {
            //get key
            String key = (String) ki.next();
            String type = jsonToMap.get(key).toString();
            if (type.contains("hd") || type.contains("derived")) {
                hd.add(key);
            }
        }

        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", deleteHdWalletName));
//            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", "ETH-1"));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            return;
        }
        mToast(getString(R.string.delete_succse));
        for (int i = 0; i < hd.size(); i++) {
            PreferencesManager.remove(this, Constant.WALLETS, hd.get(i));
        }

        PyEnv.loadLocalWalletInfo(this);
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
            finish();
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

    // export Mnemonic words
    private void exportWord(String type) {
        PyObject exportSeed = null;
        try {
            exportSeed = Daemon.commands.callAttr("export_seed", pwdEdittext.getText().toString(), walletName);
            if (Constant.EXPORT_DESTINATIONS.equals(operateType)) {
                Intent intent = new Intent(this, SearchDevicesActivity.class);
                intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_BACKUP_HD_WALLET_TO_DEVICE);
                intent.putExtra(Constant.MNEMONICS, exportSeed.toString());
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
        if ("exportHdword".equals(type)) {
            Intent intent = new Intent(SetHDWalletPassActivity.this, BackupGuideActivity.class);
            intent.putExtra("exportWord", exportSeed.toString());
            intent.putExtra("importHdword", importHdword);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SetHDWalletPassActivity.this, HdRootMnemonicsActivity.class);
            intent.putExtra("exportWord", exportSeed.toString());
            intent.putExtra("importHdword", importHdword);
            startActivity(intent);
            finish();
        }
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
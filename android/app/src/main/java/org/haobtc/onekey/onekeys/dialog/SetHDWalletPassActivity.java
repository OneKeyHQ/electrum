package org.haobtc.onekey.onekeys.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.RecoveryWalletBean;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.CreateSuccessEvent;
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
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;
import org.haobtc.onekey.utils.PwdEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.utils.LogUtil;

/**
 * see {@link SoftPassActivity}
 **/
@Deprecated
public class SetHDWalletPassActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_set_pass)
    TextView testSetPass;
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.pwd_edittext)
    PwdEditText pwdEdittext;
    @BindView(R.id.text_long)
    TextView textLong;
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
    private SharedPreferences preferences;
    private ArrayList<String> typeList;
    private MyDialog myDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_h_d_wallet_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        myDialog = MyDialog.showDialog(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        currencyType = getIntent().getStringExtra("currencyType");
        seed = getIntent().getStringExtra("recoverySeed");
        privateKey = getIntent().getStringExtra("privateKey");
        operateType = getIntent().getStringExtra(Constant.OPERATE_TYPE);
        // 删除所有hd钱包的名字
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");
        inits();
    }

    @Override
    protected void onResume() {
        super.onResume();
        keyBroad();
    }

    private void keyBroad() {
        pwdEdittext.setFocusable(true);
        pwdEdittext.setFocusableInTouchMode(true);
        pwdEdittext.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(pwdEdittext, 0);
                           }
                       },
                200);
    }

    private void inits() {
        typeList = new ArrayList<>();
        Map<String, ?> jsonToMap = PreferencesManager.getAll(this, Constant.WALLETS);
        jsonToMap.entrySet().forEach(stringEntry -> {
            LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
            String type = info.getType();
            String label = info.getLabel();
            if (!type.contains("hw") && !"btc-watch-standard".equals(type)) {
                typeList.add(label);
            }
        });

        if (typeList.isEmpty()) {
            textPageTitle.setText(getString(R.string.set_you_pass));
        } else {
            if ("exportHdword".equals(importHdword) || "backupMnemonic".equals(importHdword) || "exportPrivateKey".equals(importHdword) || "deleteAllWallet".equals(importHdword) || "derive".equals(importHdword) || "single".equals(importHdword) || "importMnemonic".equals(importHdword) || "importPrivateKey".equals(importHdword) || "deleteSingleWallet".equals(importHdword) || "send".equals(importHdword) || "recoveryHdWallet".equals(importHdword)) {
                checkPassTip();
            } else if ("fixHdPass".equals(importHdword)) {
                textPageTitle.setText(getString(R.string.fix_pass));
                testSetPass.setText(getString(R.string.input_your_former_pass));
                textTip.setText(getString(R.string.change_pin_warning));
                textLong.setText(getString(R.string.pass_warning));
                textConfirm.setText(getString(R.string.change_keyboard));
            }
        }
    }

    //check password tip
    private void checkPassTip() {
        testSetPass.setText(getString(R.string.input_your_pass));
        textTip.setText(getString(R.string.dont_tell));
        textLong.setText(getString(R.string.pass_warning));
        textConfirm.setText(getString(R.string.change_keyboard));
    }

    @Override
    public void initData() {
        pwdEdittext.addTextChangedListener(this);
    }

    @SingleClick(value = 3000)
    @OnClick({R.id.img_back, R.id.lin_short_pass})
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
                intent1.putExtra(Constant.OPERATE_TYPE, operateType);
                intent1.putExtra("deleteHdWalletName", deleteHdWalletName);
                if ("derive".equals(importHdword)) {
                    intent1.putExtra("currencyType", currencyType);
                } else if ("importPrivateKey".equals(importHdword)) {
                    intent1.putExtra("privateKey", privateKey);
                }
                startActivity(intent1);
                break;
        }
    }

    // 如果没有钱包创建需要设置密码，否则验证密码即可
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
            finish();
        } else {
            createNewHdWallet();
        }
    }

    private void createNewHdWallet() {
        try {
            String createHdWallet = Daemon.commands.callAttr("create_hd_wallet", pwdEdittext.getText().toString()).toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(createHdWallet);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
        }
    }

    private void deleteSingleWallet() {
        String keyName = PreferencesManager.get(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", keyName));
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
            return;
        }
        mToast(getString(R.string.delete_succse));
        PreferencesManager.remove(this, Constant.WALLETS, keyName);
        PyEnv.loadLocalWalletInfo(this);
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        EventBus.getDefault().post(new SecondEvent("finish"));
        finish();
    }

    private void importPrivateKey() {
        //import private key wallet
        try {
            String str = Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString(), new Kwarg("privkeys", privateKey)).toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(str);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
        }
    }

    private void importMnemonic() {
        try {
            String str = Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString(), new Kwarg("seed", seed)).toString();
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(str);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
            finish();
        }
    }

    private void createDeriveWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create_derived_wallet", walletName, pwdEdittext.getText().toString(), currencyType);
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(pyObject.toString());
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
            finish();
        } catch (Exception e) {
            mToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSingleWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create", walletName, pwdEdittext.getText().toString());
            CreateWalletBean createWalletBean = CreateWalletBean.objectFromData(pyObject.toString());
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
            finish();
        } catch (Exception e) {
            mToast(e.getMessage());
            e.printStackTrace();
        }
    }

    //recovery Hd Wallet
    private void recoveryHdWallet() {
        myDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    PyObject pyObject = Daemon.commands.callAttr("create_hd_wallet", pwdEdittext.getText().toString(), new Kwarg("seed", seed));
                    RecoveryWalletBean recoveryWalletBean = new Gson().fromJson(pyObject.toString(), RecoveryWalletBean.class);
                    List<RecoveryWalletBean.DerivedInfoBean> derivedInfo = recoveryWalletBean.getDerivedInfo();
                    myDialog.dismiss();
                    if (derivedInfo != null && derivedInfo.size() > 0) {
                        Intent intent = new Intent(SetHDWalletPassActivity.this, RecoveryChooseWalletActivity.class);
                        intent.putExtra("recoveryData", pyObject.toString());
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            Daemon.commands.callAttr("recovery_confirmed", "[]");
                        } catch (Exception e) {
                            e.printStackTrace();
                            mToast(e.getMessage());
                            return;
                        }
                        List<RecoveryWalletBean.WalletInfoBean> walletInfo = recoveryWalletBean.getWalletInfo();
                        String name = walletInfo.get(0).getName();
                        EventBus.getDefault().post(new CreateSuccessEvent(name));
                        mIntent(HomeOneKeyActivity.class);
                        finish();
                    }
                } catch (Exception e) {
                    mToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void deleteAllWallet() {
        ArrayList<String> hd = new ArrayList<>();
        Map<String, ?> jsonToMap = PreferencesManager.getAll(this, Constant.WALLETS);
        jsonToMap.entrySet().forEach(stringEntry -> {
            LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
            String type = info.getType();
            String name = info.getName();
            if ("btc-hd-standard".equals(type) || "btc-derived-standard".equals(type)) {
                hd.add(name);
            }
        });
        try {
            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", deleteHdWalletName));
            PreferencesManager.put(this, "Preferences", Constant.HAS_LOCAL_HD, false);
//            Daemon.commands.callAttr("delete_wallet", pwdEdittext.getText().toString(), new Kwarg("name", "ETH-1"));
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
            return;
        }
        mToast(getString(R.string.delete_succse));
        for (int i = 0; i < hd.size(); i++) {
            PreferencesManager.remove(this, Constant.WALLETS, hd.get(i));
        }
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
            mToast(e.getMessage());
        }
    }

    //fix pass
    private void fixHdPass() {
        if (!input) {
            // 原密码是否输入正确
            try {
                Daemon.commands.callAttr("check_password", pwdEdittext.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                mToast(e.getMessage());
                return;
            }
            sixPass = pwdEdittext.getText().toString();
            testSetPass.setText(getString(R.string.set_new_pass));
            pwdEdittext.clearText();
            input = true;
        } else {
            try {
                Daemon.commands.callAttr("update_wallet_password", sixPass, pwdEdittext.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                mToast(e.getMessage());
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
            mToast(e.getMessage());
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
//            EventBus.getDefault().post(new FinishEvent());
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int length = s.length();
                if ((length == 6)) {
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
                            } else {
                                walletStatus();
                            }
                        }
                    } else {
                        walletStatus();
                    }
                }
            }
        }, 200);
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
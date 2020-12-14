package org.haobtc.onekey.onekeys.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
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
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.utils.LogUtil;

import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_LONG;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

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
    private String deleteHdWalletName;
    private boolean isHaveWallet;
    private SharedPreferences preferences;
    private String currencyType;
    private ArrayList<String> typeList;
    private String operateType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_long_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        editPass.addTextChangedListener(this);
        isHaveWallet = PreferencesManager.getAll(this, Constant.WALLETS).isEmpty();
    }

    @Override
    public void initData() {
        currencyType = getIntent().getStringExtra("currencyType");
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        privateKey = getIntent().getStringExtra("privateKey");
        recoverySeed = getIntent().getStringExtra("recoverySeed");
        operateType = getIntent().getStringExtra(Constant.OPERATE_TYPE);
        //删除所有hd钱包的名字
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");
        inits();
    }

    @Override
    protected void onResume() {
        super.onResume();
        editPass.setFocusable(true);
        editPass.setFocusableInTouchMode(true);
        editPass.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               LogUtil.d("xiaopeng", "定时器");
                               InputMethodManager inputManager =
                                       (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editPass, 0);
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

        if (typeList == null || typeList.size() == 0) {
            textPageTitle.setText(getString(R.string.set_you_pass));
        } else {
            if ("exportHdword".equals(importHdword) || "backupMnemonic".equals(importHdword) || "exportPrivateKey".equals(importHdword) || "deleteAllWallet".equals(importHdword) || "derive".equals(importHdword) || "single".equals(importHdword) || "importMnemonic".equals(importHdword) || "importPrivateKey".equals(importHdword) || "deleteSingleWallet".equals(importHdword) || "send".equals(importHdword) || "exportKeystore".equals(importHdword) || "recoveryHdWallet".equals(importHdword)) {
                textContent();
            } else if ("fixHdPass".equals(importHdword)) {
                textPageTitle.setText(getString(R.string.fix_pass));
                editPass.setHint(getString(R.string.input_your_former_pass));
                testSetPass.setText(getString(R.string.input_your_former_pass));
                textTip.setText(getString(R.string.fix_former_tip));
                textLong.setText(getString(R.string.long_pass_tip));
            }
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
                if (editPass.getText().toString().length() < 8) {
                    inputTip(this, R.layout.longpass_imput_tip, "little");
                    return;
                }
                if (typeList == null || typeList.size() == 0) {
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
                        edit.putString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_LONG);
                        edit.apply();
                        if (!firstPass.equals(editPass.getText().toString())) {
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
            case R.id.lin_short_pass:
                if (SOFT_HD_PASS_TYPE_LONG.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                    editPass.setText("");
                    Intent intent1 = new Intent(this, SetHDWalletPassActivity.class);
                    intent1.putExtra("importHdword", importHdword);
                    intent1.putExtra("walletName", walletName);
                    intent1.putExtra("recoverySeed", recoverySeed);
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
        }
    }

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
                EventBus.getDefault().post(new GotPassEvent(editPass.getText().toString()));
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
            EventBus.getDefault().post(new InputPassSendEvent(editPass.getText().toString()));
            finish();
        } else {
            createNewHdWallet();
        }
    }

    private void createNewHdWallet() {
        PyObject createHdWallet = null;
        try {
            createHdWallet = Daemon.commands.callAttr("create_hd_wallet", editPass.getText().toString());
            if (!TextUtils.isEmpty(createHdWallet.toString())) {
                CreateWalletBean createWalletBean = new Gson().fromJson(createHdWallet.toString(), CreateWalletBean.class);
                EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
                mIntent(HomeOneKeyActivity.class);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
        }
    }

    private void deleteSingleWallet() {
        String keyName = PreferencesManager.get(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        try {
            Daemon.commands.callAttr("delete_wallet", editPass.getText().toString(), new Kwarg("name", keyName));
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
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
            PyObject pyObject = Daemon.commands.callAttr("create", walletName, editPass.getText().toString(), new Kwarg("privkeys", privateKey));
            CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);

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
    }

    private void importMnemonic() {
        String seed = getIntent().getStringExtra("strNewseed");
        try {
            PyObject pyObject = Daemon.commands.callAttr("create", walletName, editPass.getText().toString(), new Kwarg("seed", seed));
            CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);

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
            }
            return;
        }
    }

    private void createDeriveWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create_derived_wallet", walletName, editPass.getText().toString(), currencyType);
            CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
            finish();
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
    }

    private void createSingleWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create", walletName, editPass.getText().toString());
            CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
            finish();
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect password")) {
                mToast(getString(R.string.wrong_pass));
            }
            e.printStackTrace();
            return;
        }
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
                CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
                EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
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
        jsonToMap.entrySet().forEach(stringEntry -> {
            LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
            String type = info.getType();
            String name = info.getName();
            if ("btc-hd-standard".equals(type) || "btc-derived-standard".equals(type)) {
                hd.add(name);
            }
        });

        try {
            Daemon.commands.callAttr("delete_wallet", editPass.getText().toString(), new Kwarg("name", deleteHdWalletName));
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
        for (int i = 0; i < hd.size(); i++) {
            PreferencesManager.remove(this, Constant.WALLETS, hd.get(i));
        }

        PyEnv.loadLocalWalletInfo(this);
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        EventBus.getDefault().post(new LoadWalletlistEvent());
        EventBus.getDefault().post(new FinishEvent());
        EventBus.getDefault().post(new SecondEvent("finish"));
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
            //原密码是否输入正确
            try {
                Daemon.commands.callAttr("check_password", editPass.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("fixHdPassfixHdPass", "fixHdPass: " + e.getMessage());
                if (e.getMessage().contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass_next_input));
                }
                return;
            }
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
    private void exportWord(String type) {
        PyObject createHdWallet = null;
        try {
            createHdWallet = Daemon.commands.callAttr("export_seed", editPass.getText().toString(), walletName);
            if (Constant.EXPORT_DESTINATIONS.equals(operateType)) {
                Intent intent = new Intent(this, SearchDevicesActivity.class);
                intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_BACKUP_HD_WALLET_TO_DEVICE);
                intent.putExtra(Constant.MNEMONICS, createHdWallet.toString());
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
        EventBus.getDefault().post(new SecondEvent("finish"));
        if ("exportHdword".equals(type)) {
            Intent intent = new Intent(this, BackupGuideActivity.class);
            intent.putExtra("exportWord", createHdWallet.toString());
            intent.putExtra("importHdword", importHdword);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, HdRootMnemonicsActivity.class);
            intent.putExtra("exportWord", createHdWallet.toString());
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

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 34) {
            inputTip(this, R.layout.longpass_imput_tip, "big");
        }
        if ((editPass.length() > 0)) {
            btnNext.setEnabled(true);
            btnNext.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    private void inputTip(Context context, @LayoutRes int resource, String type) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        TextView passTip = view.findViewById(R.id.pass_tip);
        if ("big".equals(type)) {
            passTip.setText(getString(R.string.long_pass_34));
        }
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_input_again).setOnClickListener(v -> {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote) || "finishInputPass".equals(msgVote)) {
            finish();
        }
    }

}
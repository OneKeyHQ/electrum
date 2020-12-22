package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.BackupRequireDialog;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeleteWalletActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    @BindView(R.id.btn_forward)
    Button btnForward;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.delete_wallet_tip1)
    TextView deleteWalletTip1;
    @BindView(R.id.delete_wallet_tip2)
    TextView deleteWalletTip2;
    private String deleteHdWalletName;
    private String importHdword;
    private String walletName;
    private boolean isBackup;
    private SharedPreferences preferences;
    private Intent intent;
    private String deleteWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_delete_wallet;
    }



    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        checkboxOk.setOnCheckedChangeListener(this);
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");
        importHdword = getIntent().getStringExtra("importHdword");
        walletName = getIntent().getStringExtra("walletName");
        isBackup = getIntent().getBooleanExtra("isBackup", false);
        deleteWalletType = getIntent().getStringExtra("delete_wallet_type");
        Log.i("deleteWalletType", "initView: " + deleteWalletType);
        if ("deleteSingleWallet".equals(importHdword)) {
            textTitle.setText(getString(R.string.delete_single_wallet));
            deleteWalletTip1.setText(getString(R.string.delele_tip1));
            deleteWalletTip2.setText(getString(R.string.delete_tip2));
        }
    }

    @Override
    public void initData() {
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_forward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_forward:
//                if (!TextUtils.isEmpty(deleteWalletType)) {
//                    if (deleteWalletType.contains("watch") || deleteWalletType.contains("hw")) {
//                        // 删除观察钱包
//                        deleteWatchWallet();
//                    } else {
//                        //删除除观察钱包以外的钱包
//                        deleteOtherWallet();
//                    }
//                } else {
//                    //删除除观察钱包以外的钱包
//                    deleteOtherWallet();
//                }
                deleteOtherWallet();
                break;
        }
    }

    private void deleteOtherWallet() {
        if ("deleteSingleWallet".equals(importHdword) && !isBackup) {
            // 没备份提示备份
            new BackupRequireDialog().show(getSupportFragmentManager(), "backup_require");
            return;
        }
        startActivity(new Intent(this, SoftPassActivity.class));
    }

    @Subscribe
    public void onGotPass(GotPassEvent event) {
        if ("deleteSingleWallet".equals(importHdword)) {
            deleteSingleWallet(event.getPassword());
        } else if (!Strings.isNullOrEmpty(deleteHdWalletName)) {
            deleteAllWallet(event.getPassword());
        }
    }

    public void onDeleteSuccess(String walletName) {
        mToast(getString(R.string.delete_succse));
        PreferencesManager.remove(this, Constant.WALLETS, walletName);
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        finish();
    }

    private void deleteSingleWallet(String password) {
        String keyName = PreferencesManager.get(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        PyResponse<Void> response = PyEnv.deleteWallet(password, keyName);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            onDeleteSuccess(keyName);
        } else {
            mlToast(errors);
        }
    }

    private void deleteAllWallet(String password) {
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
        PyResponse<Void> response = PyEnv.deleteWallet(password, deleteHdWalletName);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            hd.forEach((name) -> {
                PreferencesManager.remove(this, Constant.WALLETS, name);
            });
            onDeleteSuccess(deleteHdWalletName);
        } else {
//            mToast(getString(R.string.delete_succse));
            mlToast(errors);
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            btnForward.setEnabled(true);
            btnForward.setBackground(getDrawable(R.drawable.delete_wallet_yes));
        } else {
            btnForward.setEnabled(false);
            btnForward.setBackground(getDrawable(R.drawable.delete_wallet_no));
        }
    }

    @Subscribe
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

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
            if (deleteWalletType.contains("watch")) {
                textTitle.setText(getString(R.string.delete_watch_wallet));
            } else {
                textTitle.setText(getString(R.string.delete_single_wallet));
            }
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
                if (!TextUtils.isEmpty(deleteWalletType)) {
                    if (deleteWalletType.contains("watch") || deleteWalletType.contains("hw")) {
                        //删除观察钱包
                        deleteWatchWallet();
                    } else {
                        //删除除观察钱包以外的钱包
                        deleteOtherWallet();
                    }
                } else {
                    //删除除观察钱包以外的钱包
                    deleteOtherWallet();
                }
                break;
        }
    }

    private void deleteOtherWallet() {
        if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
            intent = new Intent(this, SetHDWalletPassActivity.class);
        } else {
            intent = new Intent(this, SetLongPassActivity.class);
        }
        if ("deleteSingleWallet".equals(importHdword)) {
            if (isBackup) {
                intent.putExtra("importHdword", "deleteSingleWallet");
                intent.putExtra("walletName", walletName);
                startActivity(intent);
            } else {
                //没备份提示备份
                dontBackup(this, R.layout.confrim_delete_hdwallet);
            }
        } else {
            intent.putExtra("importHdword", "deleteAllWallet");
            intent.putExtra("deleteHdWalletName", deleteHdWalletName);
            startActivity(intent);
        }
    }

    private void deleteWatchWallet() {
        String keyName = PreferencesManager.get(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        try {
            Daemon.commands.callAttr("delete_wallet", "111111", new Kwarg("name", keyName));
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

    private void dontBackup(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
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
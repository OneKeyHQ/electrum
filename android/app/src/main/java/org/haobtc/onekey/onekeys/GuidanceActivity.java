package org.haobtc.onekey.onekeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.UserAgreementActivity;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.HomeWalletBean;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuidanceActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    private SharedPreferences.Editor edit;
    private boolean isAgree = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_guidance;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        inits();

    }

    private void inits() {
        checkboxOk.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {
//        loadAllWallets();
        set();

    }

    private void set() {
        edit.putBoolean("bluetoothStatus", true);//open bluetooth
        edit.apply();
        try {
            Daemon.commands.callAttr("set_currency", "CNY");
            Daemon.commands.callAttr("set_base_uint", "mBTC");
            edit.putString("base_unit", "mBTC");
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_rbf", true);
            edit.putBoolean("set_rbf", true);
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_unconf", false);
            edit.putBoolean("set_unconf", true);
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Daemon.commands.callAttr("set_syn_server", true);
            edit.putBoolean("set_syn_server", true);//setting synchronize server
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Daemon.commands.callAttr("set_dust", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        edit.putBoolean("set_prevent_dust", false);
        edit.apply();
    }

    private void loadAllWallets() {
        try {
            Daemon.commands.callAttr("load_all_wallet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.text_user1, R.id.btn_begin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_user1:
                mIntent(UserAgreementActivity.class);
                break;
            case R.id.btn_begin:
                if (isAgree) {
                    edit.putBoolean("is_first_run", true);
                    edit.apply();
                    mIntent(HomeOnekeyActivity.class);
                } else {
                    mToast(getString(R.string.agree_user));
                }
                break;
            default:
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isAgree = true;
        } else {
            isAgree = false;
        }
    }
}
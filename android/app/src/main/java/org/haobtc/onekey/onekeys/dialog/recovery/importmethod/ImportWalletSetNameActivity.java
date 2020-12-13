package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

public class ImportWalletSetNameActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.edit_set_wallet_name)
    EditText editSetWalletName;
    private String importHdword;
    private String privateKey;
    private String recoverySeed;
    private String watchAddress;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;
    private Intent intent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_wallet_set_name;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        watchAddress = getIntent().getStringExtra("watchAddress");
        importHdword = getIntent().getStringExtra("importHdword");
        privateKey = getIntent().getStringExtra("privateKey");
        recoverySeed = getIntent().getStringExtra("recoverySeed");

    }

    @Override
    public void initData() {
        editSetWalletName.addTextChangedListener(this);
    }

    @OnClick({R.id.img_back, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_import:
                if (TextUtils.isEmpty(editSetWalletName.getText().toString())) {
                    mToast(getString(R.string.please_input_walletname));
                    return;
                }
                if (!TextUtils.isEmpty(watchAddress)) {
                    importWallet();

                } else {
                    if (TextUtils.isEmpty(editSetWalletName.getText().toString())) {
                        mToast(getString(R.string.please_input_walletname));
                        return;
                    }
                    if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                        intent = new Intent(this, SetHDWalletPassActivity.class);
                    } else {
                        intent = new Intent(this, SetLongPassActivity.class);
                    }
                    intent.putExtra("importHdword", importHdword);
                    intent.putExtra("privateKey", privateKey);
                    intent.putExtra("recoverySeed", recoverySeed);
                    intent.putExtra("walletName", editSetWalletName.getText().toString());
                    startActivity(intent);
                }
                break;
        }
    }

    private void importWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create", editSetWalletName.getText().toString(), new Kwarg("addresses", watchAddress));
            Log.i("pyObjectpyObjectpyObject", "importWallet: "+pyObject);
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
            } else if (e.getMessage().contains("The file already exists")) {
                mToast(getString(R.string.have_private));
            } else if (e.getMessage().contains("Please enter the correct address or pubkey")) {
                mToast(getString(R.string.private_invalid));
            }
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
        if (!TextUtils.isEmpty(s.toString())) {
            if (s.length() > 14) {
                mToast(getString(R.string.name_lenth));
            }
        }
    }
}
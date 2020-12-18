package org.haobtc.onekey.onekeys.dialog.recovery;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportKeystoreActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportMnemonicActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportPrivateKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.WatchWalletActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseImportMethodActivity extends BaseActivity {

    @BindView(R.id.rel_import_keystore)
    RelativeLayout relImportKeystore;
    private String name;
    private String data;
    private int currentAction;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_import_method;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        String importType = getIntent().getStringExtra("importType");
        if ("BTC".equals(importType)) {
            relImportKeystore.setVisibility(View.GONE);
        }else{
            relImportKeystore.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initData() {

    }

    @SingleClick(value = 1000)
    @OnClick({R.id.img_back, R.id.rel_import_private, R.id.rel_import_help, R.id.rel_import_keystore, R.id.rel_watch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_import_private:
                currentAction = R.id.rel_import_private;
                Intent intent = new Intent(ChooseImportMethodActivity.this, ImportPrivateKeyActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_import_help:
                currentAction = R.id.rel_import_help;
                Intent intent1 = new Intent(ChooseImportMethodActivity.this, ImportMnemonicActivity.class);
                startActivity(intent1);
                break;
            case R.id.rel_import_keystore:
                currentAction = R.id.rel_import_keystore;
                // eth
                Intent intent2 = new Intent(ChooseImportMethodActivity.this, ImportKeystoreActivity.class);
                startActivity(intent2);
                break;
            case R.id.rel_watch:
                currentAction = R.id.rel_watch;
                Intent intent3 = new Intent(ChooseImportMethodActivity.this, WatchWalletActivity.class);
                startActivity(intent3);
                break;
        }
    }
    @Subscribe()
    public void onGotData(ResultEvent event) {
        data = event.getResult();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotName(NameSettedEvent event) {
        name = event.getName();
        if (currentAction == R.id.rel_watch) {
            importWallet();
        } else {
            startActivity(new Intent(this, SoftPassActivity.class));
        }
    }
    @Subscribe
    public void onGotPass(GotPassEvent event) {
        if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(data)) {
            switch (currentAction) {
                case R.id.rel_import_help:
                    PyEnv.createWallet(this, name, event.getPassword(), null, data);
                    break;
                case R.id.rel_import_private:
                    PyEnv.createWallet(this, name, event.getPassword(), data, null);
                    break;
                default:
            }
        }
    }
    @Subscribe
    public void onFinish(ExitEvent exitEvent) {
        finish();
    }
    private void importWallet() {
        try {
            PyObject pyObject = Daemon.commands.callAttr("create", name, new Kwarg("addresses", data));
            CreateWalletBean createWalletBean = new Gson().fromJson(pyObject.toString(), CreateWalletBean.class);
            EventBus.getDefault().post(new CreateSuccessEvent(createWalletBean.getWalletInfo().get(0).getName()));
            mIntent(HomeOneKeyActivity.class);
        } catch (Exception e) {
            e.printStackTrace();
            mToast(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
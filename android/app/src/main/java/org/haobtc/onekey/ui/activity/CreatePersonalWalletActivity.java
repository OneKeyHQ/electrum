package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.CreateWalletEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.SelectAddressTypeDialog;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.SetWalletNameFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/23/20
 */

public class CreatePersonalWalletActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    @BindView(R.id.img_back)
    ImageView imgBack;
    private String coinType;
    private String xpub;

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.create_new_walt);
        startFragment(new AddAssetFragment());
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetXpub(GetXpubEvent event) {
        coinType = event.getCoinName();
        switch (coinType) {
            case Constant.COIN_TYPE_BTC:
                new SelectAddressTypeDialog().show(getSupportFragmentManager(), "");
            case Constant.COIN_TYPE_ETH:
                break;
            case Constant.COIN_TYPE_EOS:
        }

    }
    /**
     * 获取用于个人钱包的扩展公钥
     */
    public void getXpub(String type) {

        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY_PERSONAL,
                MyApplication.getInstance().getDeviceWay(),
                type);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWallet(CreateWalletEvent event) {
        switch (coinType) {
            case Constant.COIN_TYPE_BTC:
                String walletName = event.getName();
                String xpubs = "[[\"" + xpub + "\", \"" + FindNormalDeviceActivity.deviceId + "\"]]";
                String name = PyEnv.createWallet(this, walletName, 1, 1, xpubs);
                if (!Strings.isNullOrEmpty(name)) {
                    startActivity(new Intent(this, HomeOneKeyActivity.class));
                }
                finish();
                break;
            case Constant.COIN_TYPE_ETH:
                break;
            case Constant.COIN_TYPE_EOS:
                break;
            default:

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        // 回写PIN码
        PyEnv.setPin(event.getPinNew());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (PyConstant.PIN_CURRENT == event.getType()) {

            startFragment(new DevicePINFragment(PyConstant.PIN_CURRENT));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWalletSuccess(CreateSuccessEvent event) {
        startActivity(new Intent(this, HomeOneKeyActivity.class));
        finish();
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {
        if (e.getMessage().contains("PIN invalid")){
            showToast(getString(R.string.pin_input_wrong));
        }
        showToast(e.getMessage());
        finish();
    }

    @Override
    public void onResult(String s) {
        EventBus.getDefault().post(new ExitEvent());
        xpub = s;
        startFragment(new SetWalletNameFragment());
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {

    }

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        PyEnv.cancelPinInput();
        finish();
    }
}

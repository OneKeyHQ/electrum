package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

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
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.SetWalletNameFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
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
                getXpubP2wpkh();
            case Constant.COIN_TYPE_ETH:
                break;
            case Constant.COIN_TYPE_EOS:
        }

    }

    /**
     * 获取用于个人钱包的扩展公钥
     */
    private void getXpubP2wpkh() {

        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY_SINGLE,
                MyApplication.getInstance().getDeviceWay(),
                PyConstant.XPUB_P2WPKH
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWallet(CreateWalletEvent event) {
        switch (coinType) {
            case Constant.COIN_TYPE_BTC:
                String walletName = event.getName();
                String xpubs = "[[\"" + xpub + "\", \"btc\"]]";
                PyEnv.createWallet(this, walletName, 1, 1, xpubs);
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

    }

    @Override
    public void onResult(String s) {
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
    public void onViewClicked() {
        finish();
    }
}

package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.CreateWalletEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.SetWalletNameFragment;

/**
 * @author liyan
 * @date 11/23/20
 */
public class CreatePersonalWalletActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    @BindView(R.id.img_back)
    ImageView imgBack;

    private String coinType;
    private String xpub;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /** init */
    @Override
    public void init() {
        updateTitle(R.string.choose_amount);
        startFragment(new AddAssetFragment());
    }

    /**
     * * init layout
     *
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
                new XPopup.Builder(mContext)
                        .asCustom(
                                new SelectWalletTypeDialog(
                                        mContext,
                                        mode -> {
                                            String walletType = null;
                                            switch (mode) {
                                                case SelectWalletTypeDialog.RecommendType:
                                                    walletType =
                                                            PyConstant.ADDRESS_TYPE_P2SH_P2WPKH;
                                                    break;
                                                case SelectWalletTypeDialog.NativeType:
                                                    walletType = PyConstant.ADDRESS_TYPE_P2WPKH;
                                                    break;
                                                case SelectWalletTypeDialog.NormalType:
                                                    walletType = PyConstant.ADDRESS_TYPE_P2PKH;
                                                    break;
                                            }
                                            getBtcXpub(walletType, Vm.CoinType.BTC.callFlag);
                                        }))
                        .show();
                break;
            case Constant.COIN_TYPE_ETH:
                getEthXpub(Vm.CoinType.ETH.callFlag);
                break;
            case Constant.COIN_TYPE_EOS:
                break;
        }
    }

    /** 获取用于个人钱包的扩展公钥 */
    public void getBtcXpub(String addressType, String coinType) {
        Disposable disposable =
                getXpubObserVable(coinType, addressType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    if (Strings.isNullOrEmpty(result.getErrors())) {
                                        onResult(result.getResult());
                                    } else {
                                        startFragment(new AddAssetFragment());
                                        mToast(result.getErrors());
                                    }
                                });
        mCompositeDisposable.add(disposable);
    }

    public void getEthXpub(String coinType) {
        Disposable disposable =
                getXpubObserVable(Vm.CoinType.ETH.callFlag, "")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    if (Strings.isNullOrEmpty(result.getErrors())) {
                                        onResult(result.getResult());
                                    } else {
                                        startFragment(new AddAssetFragment());
                                        mToast(result.getErrors());
                                    }
                                });
        mCompositeDisposable.add(disposable);
    }

    private Observable<PyResponse<String>> getXpubObserVable(String coinType, String addressType) {
        return Observable.create(
                emitter -> {
                    PyResponse<String> response =
                            PyEnv.createEthHwDerivedWallet(
                                    MyApplication.getInstance().getDeviceWay(),
                                    coinType,
                                    addressType);
                    emitter.onNext(response);
                    emitter.onComplete();
                });
    }

    private Observable<PyResponse<String>> getCreateObservable(
            String name, String xpub, String coinType) {
        return Observable.create(
                emitter -> {
                    PyResponse<String> response = PyEnv.createWalletNew(name, 1, 1, xpub, coinType);
                    emitter.onNext(response);
                    emitter.onComplete();
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWallet(CreateWalletEvent event) {
        String walletName = event.getName();
        String xpubs = "[[\"" + xpub + "\", \"" + PyEnv.currentHwFeatures.getSerialNum() + "\"]]";
        String callFlag = "";
        switch (coinType) {
            case Constant.COIN_TYPE_BTC:
                callFlag = Vm.CoinType.BTC.callFlag;
                break;
            case Constant.COIN_TYPE_ETH:
                callFlag = Vm.CoinType.ETH.callFlag;
                break;
            case Constant.COIN_TYPE_EOS:
                break;
            default:
        }
        if (!Strings.isNullOrEmpty(callFlag)) {
            createWallet(walletName, xpubs, callFlag);
        }
    }

    /**
     * 创建钱包
     *
     * @param walletName 钱包名称
     * @param xpub 公钥
     * @param coinName 钱包类型
     */
    private void createWallet(String walletName, String xpub, String coinName) {
        Disposable disposable =
                getCreateObservable(walletName, xpub, coinName)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(show -> showProgress())
                        .doFinally(this::dismissProgress)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    if (!Strings.isNullOrEmpty(result.getErrors())) {
                                        mToast(result.getErrors());
                                    }
                                });
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
    public void onPreExecute() {}

    @Override
    public void onException(Exception e) {
        if (e.getMessage().contains("PIN invalid")) {
            showToast(getString(R.string.pin_input_wrong));
        } else {
            showToast(e.getMessage());
        }
        finish();
    }

    @Override
    public void onResult(String s) {
        EventBus.getDefault().post(new ExitEvent());
        xpub = s;
        startFragment(new SetWalletNameFragment());
    }

    @Override
    public void onCancelled() {}

    @Override
    public void currentMethod(String methodName) {}

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        PyEnv.cancelPinInput();
        finish();
    }

    @Override
    protected void onDestroy() {
        PyEnv.cancelPinInput();
        super.onDestroy();
    }
}

package org.haobtc.onekey.viewmodel;

import android.util.Pair;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.BalanceManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 存放 App 当前余额，钱包类型，Application 生命周期的 ViewModel
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 11:09 AM
 */
public class AppWalletViewModel extends ViewModel {
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public MutableLiveData<Boolean> existsWallet = new MutableLiveData<>();
    public MutableLiveData<LocalWalletInfo> currentWalletInfo = new MutableLiveData<>();
    public MutableLiveData<String> currentWalletBalance = new MediatorLiveData<>();
    public MutableLiveData<String> currentWalletFiatBalance = new MutableLiveData<>();

    private final BalanceManager mBalanceManager = new BalanceManager();
    private final AccountManager mAccountManager = new AccountManager(MyApplication.getInstance());
    private final SystemConfigManager mSystemConfigManager = new SystemConfigManager(MyApplication.getInstance());

    public AppWalletViewModel() {
        EventBus.getDefault().register(this);
        refresh();
    }

    public void refresh() {
        mCompositeDisposable.add(Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    refreshExistsWallet();
                    refreshWalletInfo();
                    emitter.onNext("");
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                }, throwable -> {
                }));
    }

    public void refreshExistsWallet() {
        existsWallet.postValue(mAccountManager.existsWallets());
    }

    public void refreshWalletInfo() {
        String currentWalletName = mAccountManager.getCurrentWalletName();
        currentWalletInfo.postValue(mAccountManager.getLocalWalletByName(currentWalletName));
    }

    public void refreshBalance() {
        LocalWalletInfo localWalletInfo = currentWalletInfo.getValue();
        if (localWalletInfo == null) {
            currentWalletBalance.postValue(null);
            currentWalletFiatBalance.postValue(null);
            return;
        }
        Pair<String, String> balancePair = mBalanceManager.getBalanceByWalletName(localWalletInfo.getName());
        if (balancePair == null) {
            currentWalletBalance.postValue(null);
            currentWalletFiatBalance.postValue(null);
        } else {
            currentWalletBalance.postValue(balancePair.first);
            currentWalletFiatBalance.postValue(balancePair.second);
        }
    }

    /**
     * 切换当前钱包
     *
     * @param name 钱包名称
     */
    public void changeCurrentWallet(String name) {
        LocalWalletInfo localWalletInfo = mAccountManager.selectWallet(name);
        if (localWalletInfo == null) {
            mSystemConfigManager.setPassWordType(SystemConfigManager.SoftHdPassType.SHORT);
        }
        currentWalletInfo.postValue(localWalletInfo);
    }

    /**
     * 删除钱包后选择其他钱包
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(LoadOtherWalletEvent event) {
        LocalWalletInfo localWalletInfo = mAccountManager.autoSelectNextWallet();
        if (localWalletInfo == null) {
            mSystemConfigManager.setPassWordType(SystemConfigManager.SoftHdPassType.SHORT);
        }
        currentWalletInfo.postValue(localWalletInfo);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(SecondEvent event) {
        Pair<String, String> balancePair = mBalanceManager.decodePythonBalanceNotice(event.getMsg());
        currentWalletBalance.postValue(balancePair.first);
        currentWalletFiatBalance.postValue(balancePair.second);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
        EventBus.getDefault().unregister(this);
    }
}

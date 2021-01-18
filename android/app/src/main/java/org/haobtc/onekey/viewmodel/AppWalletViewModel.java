package org.haobtc.onekey.viewmodel;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.FiatUnitSymbolBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.WalletBalanceFiatVo;
import org.haobtc.onekey.bean.WalletBalanceVo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.BalanceManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PyEnv;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 存放 App 当前余额，钱包类型，Application 生命周期的 ViewModel
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 11:09 AM
 */
public class AppWalletViewModel extends ViewModel {
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    public MutableLiveData<Boolean> existsWallet = new MutableLiveData<>();
    public MutableLiveData<LocalWalletInfo> currentWalletInfo = new MutableLiveData<>();
    public LiveData<WalletBalanceVo> currentWalletBalance = new MutableLiveData<>(new WalletBalanceVo("0", "BTC"));
    public LiveData<WalletBalanceFiatVo> currentWalletFiatBalance = new MutableLiveData<>(new WalletBalanceFiatVo("0", "CNY", "¥"));
    private final BalanceManager mBalanceManager = new BalanceManager();
    private final AccountManager mAccountManager = new AccountManager(MyApplication.getInstance());
    private final SystemConfigManager mSystemConfigManager = new SystemConfigManager(MyApplication.getInstance());

    public AppWalletViewModel() {
        EventBus.getDefault().register(this);
        refresh();
    }

    public void refresh() {
        refreshExistsWallet();
        refreshWalletInfo();
    }

    public void refreshExistsWallet() {
        mExecutorService.execute(() -> {
            existsWallet.postValue(mAccountManager.existsWallets());
        });
    }

    public void refreshWalletInfo() {
        mExecutorService.execute(() -> {
            String currentWalletName = mAccountManager.getCurrentWalletName();
            LocalWalletInfo localWallet = mAccountManager.getLocalWalletByName(currentWalletName);
            if (localWallet == null) {
                // 容错处理：如果本地信息存储错误，则随机选择一下钱包账户。
                localWallet = mAccountManager.autoSelectNextWallet();
            }
            currentWalletInfo.postValue(localWallet);
        });
    }

    public void refreshBalance() {
        mExecutorService.execute(() -> {
            LocalWalletInfo localWalletInfo = currentWalletInfo.getValue();
            String currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit();
            FiatUnitSymbolBean currentFiatUnitSymbol = mSystemConfigManager.getCurrentFiatUnitSymbol();
            if (localWalletInfo == null) {
                setCurrentWalletBalance(new WalletBalanceVo("0", currentBaseUnit));
                setCurrentWalletFiatBalance(
                        new WalletBalanceFiatVo(
                                "0",
                                currentFiatUnitSymbol.getUnit(),
                                currentFiatUnitSymbol.getSymbol())
                );
                return;
            }
            Pair<String, String> balancePair = mBalanceManager.getBalanceByWalletName(localWalletInfo.getName());
            if (balancePair == null) {
                setCurrentWalletBalance(new WalletBalanceVo("0", currentBaseUnit));
                setCurrentWalletFiatBalance(
                        new WalletBalanceFiatVo(
                                "0",
                                currentFiatUnitSymbol.getUnit(),
                                currentFiatUnitSymbol.getSymbol())
                );
            } else {
                setCurrentWalletBalance(
                        new WalletBalanceVo(
                                TextUtils.isEmpty(balancePair.first) ? "0" : balancePair.first,
                                currentBaseUnit)
                );
                setCurrentWalletFiatBalance(
                        new WalletBalanceFiatVo(
                                TextUtils.isEmpty(balancePair.second) ? "0" : balancePair.second,
                                currentFiatUnitSymbol.getUnit(),
                                currentFiatUnitSymbol.getSymbol())
                );
            }
        });
    }

    /**
     * 切换当前钱包
     *
     * @param name 钱包名称
     */
    public void changeCurrentWallet(@NonNull String name) {
        mExecutorService.execute(() -> {
            LocalWalletInfo localWalletInfo = mAccountManager.selectWallet(name);
            if (localWalletInfo == null) {
                mSystemConfigManager.setPassWordType(SystemConfigManager.SoftHdPassType.SHORT);
            }
            currentWalletInfo.postValue(localWalletInfo);
        });
    }

    /**
     * 自动选择并切换一个钱包
     */
    public void autoSelectWallet() {
        mExecutorService.execute(() -> {
            LocalWalletInfo localWalletInfo = mAccountManager.autoSelectNextWallet();
            if (localWalletInfo == null) {
                mSystemConfigManager.setPassWordType(SystemConfigManager.SoftHdPassType.SHORT);
            }
            currentWalletInfo.postValue(localWalletInfo);
        });
    }

    /**
     * 删除钱包后选择其他钱包
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(LoadOtherWalletEvent event) {
        mExecutorService.execute(() -> {
            LocalWalletInfo localWalletInfo = mAccountManager.autoSelectNextWallet();
            if (localWalletInfo == null) {
                mSystemConfigManager.setPassWordType(SystemConfigManager.SoftHdPassType.SHORT);
            }
            currentWalletInfo.postValue(localWalletInfo);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(SecondEvent event) {
        mExecutorService.execute(() -> {
            Pair<String, String> balancePair = mBalanceManager.decodePythonBalanceNotice(event.getMsg());
            String currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit();
            FiatUnitSymbolBean currentFiatUnitSymbol = mSystemConfigManager.getCurrentFiatUnitSymbol();
            setCurrentWalletBalance(
                    new WalletBalanceVo(
                            TextUtils.isEmpty(balancePair.first) ? "0" : balancePair.first,
                            currentBaseUnit)
            );
            setCurrentWalletFiatBalance(
                    new WalletBalanceFiatVo(TextUtils.isEmpty(balancePair.second) ? "0" : balancePair.second,
                            currentFiatUnitSymbol.getUnit(),
                            currentFiatUnitSymbol.getSymbol())
            );
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateWalletSuccess(CreateSuccessEvent event) {
        PyEnv.loadLocalWalletInfo(MyApplication.getInstance());
        if (TextUtils.isEmpty(event.getName())) {
            // 容错处理：如果有人发送一个空的名字，则随机选择一下钱包账户。
            autoSelectWallet();
        } else {
            changeCurrentWallet(event.getName());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
        mExecutorService.shutdown();
    }

    private void setCurrentWalletBalance(@NonNull WalletBalanceVo balance) {
        checkRepeatAssignment(((MutableLiveData<WalletBalanceVo>) currentWalletBalance), balance);
    }

    private void setCurrentWalletFiatBalance(@NonNull WalletBalanceFiatVo balance) {
        checkRepeatAssignment(((MutableLiveData<WalletBalanceFiatVo>) currentWalletFiatBalance), balance);
    }

    private <T> void checkRepeatAssignment(MutableLiveData<T> liveData, @Nullable T value) {
        if (value == null && liveData.getValue() != null) {
            liveData.postValue(null);
        } else if ((value != null && liveData.getValue() == null) ||
                (value != null && liveData.getValue() != null && !value.equals(liveData.getValue())) ||
                (value != null && liveData.getValue() != null && value instanceof Number && value != liveData.getValue())
        ) {
            liveData.postValue(value);
        }
    }
}

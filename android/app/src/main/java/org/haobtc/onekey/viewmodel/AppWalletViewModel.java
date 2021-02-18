package org.haobtc.onekey.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.BalanceBroadcastEventBean;
import org.haobtc.onekey.bean.FiatUnitSymbolBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.WalletBalanceFiatVo;
import org.haobtc.onekey.bean.WalletBalanceVo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.BalanceManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PyEnv;

/**
 * 存放 App 当前余额，钱包类型，Application 生命周期的 ViewModel
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 11:09 AM
 */
public class AppWalletViewModel extends ViewModel {

    public static final WalletBalanceFiatVo DEF_WALLET_FIAT_BALANCE =
            new WalletBalanceFiatVo("0", "CNY", "¥");

    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(4);
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    public MutableLiveData<Boolean> existsWallet = new MutableLiveData<>();
    public MutableLiveData<LocalWalletInfo> currentWalletInfo = new MutableLiveData<>();
    public LiveData<WalletBalanceVo> currentWalletBalance =
            new MutableLiveData<>(new WalletBalanceVo(new BigDecimal("0"), "BTC"));
    public LiveData<WalletBalanceFiatVo> currentWalletFiatBalance =
            new MutableLiveData<>(DEF_WALLET_FIAT_BALANCE);
    private final BalanceManager mBalanceManager = new BalanceManager();
    private final AccountManager mAccountManager = new AccountManager(MyApplication.getInstance());
    private final SystemConfigManager mSystemConfigManager =
            new SystemConfigManager(MyApplication.getInstance());

    public AppWalletViewModel() {
        EventBus.getDefault().register(this);
        refresh();
    }

    public void refresh() {
        refreshExistsWallet();
        refreshWalletInfo();
    }

    public void refreshExistsWallet() {
        mExecutorService.execute(
                () -> {
                    existsWallet.postValue(mAccountManager.existsWallets());
                });
    }

    public void refreshWalletInfo() {
        mExecutorService.execute(
                () -> {
                    String currentWalletName = mAccountManager.getCurrentWalletName();
                    LocalWalletInfo localWallet =
                            mAccountManager.getLocalWalletByName(currentWalletName);
                    if (localWallet == null) {
                        // 容错处理：如果本地信息存储错误，则随机选择一下钱包账户。
                        localWallet = mAccountManager.autoSelectNextWallet();
                    }
                    currentWalletInfo.postValue(localWallet);
                    mMainHandler.post(this::refreshBalance);
                });
    }

    public void refreshBalance() {
        mExecutorService.execute(
                () -> {
                    LocalWalletInfo localWalletInfo = currentWalletInfo.getValue();
                    String currentBaseUnit;
                    if (localWalletInfo != null) {
                        currentBaseUnit =
                                mSystemConfigManager.getCurrentBaseUnit(
                                        localWalletInfo.getCoinType());
                    } else {
                        currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit();
                    }

                    FiatUnitSymbolBean currentFiatUnitSymbol =
                            mSystemConfigManager.getCurrentFiatUnitSymbol();

                    // 防止网络请求慢，先恢复一下初始状态。
                    setCurrentWalletBalance(
                            new WalletBalanceVo(new BigDecimal("0"), currentBaseUnit));
                    setCurrentWalletFiatBalance(
                            new WalletBalanceFiatVo(
                                    DEF_WALLET_FIAT_BALANCE.getBalance(),
                                    currentFiatUnitSymbol.getUnit(),
                                    currentFiatUnitSymbol.getSymbol()));

                    if (localWalletInfo == null) {
                        return;
                    }
                    WalletBalanceBean balance =
                            mBalanceManager.getBalanceByWalletName(localWalletInfo.getName());
                    if (balance != null) {
                        BigDecimal balanceBigDecimal;
                        try {
                            balanceBigDecimal =
                                    new BigDecimal(
                                            TextUtils.isEmpty(balance.getBalance())
                                                    ? "0"
                                                    : balance.getBalance());
                        } catch (NumberFormatException e) {
                            balanceBigDecimal = BigDecimal.ZERO;
                        }

                        setCurrentWalletBalance(
                                new WalletBalanceVo(balanceBigDecimal, currentBaseUnit));
                        setCurrentWalletFiatBalance(
                                new WalletBalanceFiatVo(
                                        TextUtils.isEmpty(balance.getBalanceFiat())
                                                ? DEF_WALLET_FIAT_BALANCE.getBalance()
                                                : balance.getBalanceFiat(),
                                        currentFiatUnitSymbol.getUnit(),
                                        currentFiatUnitSymbol.getSymbol()));
                    }
                });
    }

    /**
     * 切换当前钱包
     *
     * @param name 钱包名称
     */
    public void changeCurrentWallet(@NonNull String name) {
        mExecutorService.execute(
                () -> {
                    LocalWalletInfo localWalletInfo = mAccountManager.selectWallet(name);
                    if (localWalletInfo == null) {
                        mSystemConfigManager.setPassWordType(
                                SystemConfigManager.SoftHdPassType.SHORT);
                    }
                    mMainHandler.post(
                            () -> {
                                currentWalletInfo.setValue(localWalletInfo);
                                refreshExistsWallet();
                                refreshBalance();
                            });
                });
    }

    /** 自动选择并切换一个钱包 */
    public void autoSelectWallet() {
        mExecutorService.execute(
                () -> {
                    LocalWalletInfo localWalletInfo = mAccountManager.autoSelectNextWallet();
                    if (localWalletInfo == null) {
                        mSystemConfigManager.setPassWordType(
                                SystemConfigManager.SoftHdPassType.SHORT);
                    }
                    mMainHandler.post(
                            () -> {
                                currentWalletInfo.setValue(localWalletInfo);
                                refreshExistsWallet();
                                refreshBalance();
                            });
                });
    }

    /** 删除钱包后选择其他钱包 */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(LoadOtherWalletEvent event) {
        autoSelectWallet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void event(SecondEvent event) {
        mExecutorService.execute(
                () -> {
                    BalanceBroadcastEventBean eventBean =
                            mBalanceManager.decodePythonBalanceNotice(event.getMsg());
                    if (currentWalletInfo.getValue() == null) {
                        return;
                    }
                    String addr = currentWalletInfo.getValue().getAddr();
                    if (eventBean == null || !addr.equals(eventBean.getAddress())) {
                        return;
                    }
                    String currentBaseUnit =
                            mSystemConfigManager.getCurrentBaseUnit(
                                    Vm.CoinType.convert(eventBean.getCoin().toLowerCase()));
                    FiatUnitSymbolBean currentFiatUnitSymbol =
                            mSystemConfigManager.getCurrentFiatUnitSymbol();

                    BigDecimal balance;
                    try {
                        balance = new BigDecimal(eventBean.getBalance());
                    } catch (NumberFormatException e) {
                        balance = BigDecimal.ZERO;
                    }

                    setCurrentWalletBalance(new WalletBalanceVo(balance, currentBaseUnit));
                    setCurrentWalletFiatBalance(
                            new WalletBalanceFiatVo(
                                    eventBean.getFiat(),
                                    currentFiatUnitSymbol.getUnit(),
                                    currentFiatUnitSymbol.getSymbol()));
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
        mExecutorService.shutdown();
        EventBus.getDefault().unregister(this);
        super.onCleared();
    }

    private void setCurrentWalletBalance(@NonNull WalletBalanceVo balance) {
        checkRepeatAssignment(((MutableLiveData<WalletBalanceVo>) currentWalletBalance), balance);
    }

    private void setCurrentWalletFiatBalance(@NonNull WalletBalanceFiatVo balance) {
        checkRepeatAssignment(
                ((MutableLiveData<WalletBalanceFiatVo>) currentWalletFiatBalance), balance);
    }

    private <T> void checkRepeatAssignment(MutableLiveData<T> liveData, @Nullable T value) {
        if (value == null && liveData.getValue() != null) {
            mMainHandler.post(() -> liveData.setValue(null));
        } else if ((value != null && liveData.getValue() == null)
                || (value != null
                        && liveData.getValue() != null
                        && !value.equals(liveData.getValue()))
                || (value != null
                        && liveData.getValue() != null
                        && value instanceof Number
                        && value != liveData.getValue())) {
            mMainHandler.post(() -> liveData.setValue(value));
        }
    }
}

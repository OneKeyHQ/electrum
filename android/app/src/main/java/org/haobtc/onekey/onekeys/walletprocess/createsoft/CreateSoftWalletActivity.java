package org.haobtc.onekey.onekeys.walletprocess.createsoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;
import com.orhanobut.logger.Logger;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.exception.AccountException;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.SelectBitcoinAddressTypeDialogFragment.BitcoinAddressType;
import org.haobtc.onekey.onekeys.walletprocess.SelectBitcoinAddressTypeDialogFragment.OnSelectBitcoinAddressTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectChainCoinFragment.OnSelectCoinTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectWalletTypeFragment.OnSelectWalletTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectWalletTypeFragment.SoftWalletType;
import org.haobtc.onekey.onekeys.walletprocess.SoftWalletNameSettingFragment.OnSetWalletNameCallback;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.custom.CustomCoverWatchPopup;
import org.haobtc.onekey.utils.NavUtils;

/**
 * 创建软件钱包流程
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 5:02 PM
 */
public class CreateSoftWalletActivity extends BaseActivity
        implements CreateSoftWalletProvider,
                OnSelectWalletTypeCallback,
                OnSelectCoinTypeCallback,
                OnSelectBitcoinAddressTypeCallback,
                OnSetWalletNameCallback,
                OnFinishViewCallBack {

    private static final int REQUEST_SET_PWD = 1;

    public static void start(Context context) {
        Intent intent = new Intent(context, CreateSoftWalletActivity.class);
        context.startActivity(intent);
    }

    @SoftWalletType private int mSoftWalletType = SoftWalletType.HD_WALLET;
    private Vm.CoinType mCoinType;
    @BitcoinAddressType private int mBitcoinAddressPurpose = BitcoinAddressType.NormalType;
    private String mWalletName;
    private String mWalletPassword;

    private AccountManager mAccountManager;
    private Disposable mCreateDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_soft_wallet;
    }

    @Override
    public void initView() {}

    @Override
    public void initData() {
        mAccountManager = new AccountManager(getApplicationContext());
    }

    private NavController getNavController() {
        NavHostFragment navHostFragment =
                (NavHostFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.nav_create_soft_wallet_fragment);
        return navHostFragment.getNavController();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return getNavController().navigateUp();
    }

    @Override
    public boolean existsHDWallet() {
        return mAccountManager.existsLocalHD();
    }

    @Override
    public boolean isImport() {
        return false;
    }

    @Override
    public boolean supportETH() {
        if (mSoftWalletType == SoftWalletType.SINGLE) {
            return true;
        }
        List<WalletInfo> allMainWallet = mAccountManager.getAllMainWallet();
        int count = 0;
        for (WalletInfo item : allMainWallet) {
            if (Vm.convertCoinType(item.type) == Vm.CoinType.ETH) {
                count++;
            }
        }
        return count < 1;
    }

    @Override
    public void onSelectSoftWalletType(@SoftWalletType int type) {
        mSoftWalletType = type;
        getNavController()
                .navigate(R.id.action_selectorWalletTypeFragment_to_selectorChainCoinFragment);
    }

    @Override
    public void onSelectCoinType(Vm.CoinType coinType) {
        mCoinType = coinType;
        if (coinType == Vm.CoinType.BTC) {
            getNavController()
                    .navigate(
                            R.id.action_selectChainCoinFragment_to_selectBitcoinAddressTypeDialogFragment);
        } else {
            getNavController()
                    .navigate(R.id.action_selectChainCoinFragment_to_softWalletNameSettingFragment);
        }
    }

    @Override
    public void onSelectBitcoinAddressType(@BitcoinAddressType int purpose) {
        mBitcoinAddressPurpose = purpose;
        getNavController()
                .navigate(
                        R.id.action_selectBitcoinAddressTypeDialogFragment_to_softWalletNameSettingFragment);
    }

    @Override
    public void onSetWalletName(String name) {
        mWalletName = name;
        SoftPassActivity.startForResult(this, REQUEST_SET_PWD, -1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SET_PWD:
                    SoftPassActivity.ResultDataBean resultDataBean1 =
                            SoftPassActivity.decodeResultData(data);
                    mWalletPassword = resultDataBean1.password;
                    handleWalletCreate();
                    break;
            }
        }
    }

    @Override
    public void onFinishView() {
        if (!getNavController().navigateUp()) {
            finish();
        }
    }

    private void handleWalletCreate() {
        if (mCreateDisposable != null && !mCreateDisposable.isDisposed()) {
            mCreateDisposable.dispose();
        }
        mCreateDisposable =
                Single.create(
                                (SingleOnSubscribe<CreateWalletBean>)
                                        emitter -> {
                                            assertX(
                                                    mCoinType != null,
                                                    "mCoinType Assertion failed");
                                            assertX(
                                                    !TextUtils.isEmpty(mWalletName),
                                                    "mWalletName Assertion failed");
                                            assertX(
                                                    !TextUtils.isEmpty(mWalletPassword),
                                                    "mWalletPassword Assertion failed");

                                            CreateWalletBean walletBean;
                                            if (mSoftWalletType == SoftWalletType.HD_WALLET) {
                                                // 创建 HD 钱包
                                                walletBean =
                                                        mAccountManager.deriveHdWallet(
                                                                mCoinType,
                                                                mWalletName,
                                                                mWalletPassword,
                                                                mBitcoinAddressPurpose);
                                            } else if (mSoftWalletType == SoftWalletType.SINGLE) {
                                                // 创建独立钱包
                                                walletBean =
                                                        mAccountManager.createNewSingleWallet(
                                                                mCoinType,
                                                                mWalletName,
                                                                mWalletPassword,
                                                                mBitcoinAddressPurpose);
                                            } else {
                                                // 暂不支持
                                                Logger.d("暂不支持的钱包类型：" + mSoftWalletType);
                                                walletBean = null;
                                            }
                                            emitter.onSuccess(walletBean);
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(
                                disposable -> {
                                    showProgress();
                                })
                        .doFinally(this::dismissProgress)
                        .subscribe(
                                s -> {
                                    NavUtils.gotoMainActivityTask(this, false);
                                    finish();
                                },
                                e -> {
                                    // 处理覆盖观察钱包的问题
                                    if (e
                                            instanceof
                                            AccountException.WalletWatchAlreadyExistsException) {
                                        String watchName =
                                                ((AccountException
                                                                        .WalletWatchAlreadyExistsException)
                                                                e)
                                                        .walletName;
                                        coverReadOnlyWallet(watchName);
                                    } else if (e
                                            instanceof
                                            AccountException.WalletAlreadyExistsException) {
                                        mToast(getString(R.string.hint_wallet_existence));
                                        finish();
                                    } else if (e instanceof Exception) {
                                        // 其他异常处理
                                        MyApplication.getInstance().toastErr((Exception) e);
                                    }
                                    e.printStackTrace();
                                });
    }

    private void coverReadOnlyWallet(String watchName) {
        LocalWalletInfo localWalletByName = mAccountManager.getLocalWalletByName(watchName);
        CustomCoverWatchPopup popup =
                new CustomCoverWatchPopup(
                        mContext,
                        () -> {
                            PyResponse<String> response = PyEnv.replaceWatchOnlyWallet(true);
                            if (Strings.isNullOrEmpty(response.getErrors())) {
                                EventBus.getDefault()
                                        .post(new CreateSuccessEvent(response.getResult()));
                                mContext.startActivity(
                                        new Intent(mContext, HomeOneKeyActivity.class));
                            } else {
                                mToast(response.getErrors());
                            }
                        },
                        CustomCoverWatchPopup.deleteWatch);
        if (localWalletByName != null && !TextUtils.isEmpty(localWalletByName.getLabel())) {
            popup.setWalletName(localWalletByName.getLabel());
        } else {
            popup.setWalletName(localWalletByName.getLabel());
        }
        new XPopup.Builder(mContext).asCustom(popup).show();
    }

    private void assertX(boolean b, String message) {
        if (!b && BuildConfig.DEBUG) {
            throw new AssertionError(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCreateDisposable != null && !mCreateDisposable.isDisposed()) {
            mCreateDisposable.dispose();
        }
    }
}

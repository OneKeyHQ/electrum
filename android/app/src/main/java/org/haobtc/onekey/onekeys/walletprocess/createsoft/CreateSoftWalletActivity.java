package org.haobtc.onekey.onekeys.walletprocess.createsoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.orhanobut.logger.Logger;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.SelectBitcoinAddressTypeDialogFragment.OnSelectBitcoinAddressTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectChainCoinFragment.OnSelectCoinTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectWalletTypeFragment.OnSelectWalletTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectWalletTypeFragment.SoftWalletType;
import org.haobtc.onekey.onekeys.walletprocess.SoftWalletNameSettingFragment.OnSetWalletNameCallback;
import org.haobtc.onekey.ui.activity.SoftPassActivity;

/**
 * 创建软件钱包流程
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 5:02 PM
 */
public class CreateSoftWalletActivity extends BaseActivity
        implements CreateSoftWalletProvider, OnSelectWalletTypeCallback, OnSelectCoinTypeCallback,
        OnSelectBitcoinAddressTypeCallback, OnSetWalletNameCallback, OnFinishViewCallBack {

    private static final int REQUEST_SET_PWD = 1;

    public static void start(Context context) {
        Intent intent = new Intent(context, CreateSoftWalletActivity.class);
        context.startActivity(intent);
    }

    @SoftWalletType
    private int mSoftWalletType = SoftWalletType.HD_WALLET;
    private Vm.CoinType mCoinType;
    private int mBitcoinAddressPurpose;
    private String mWalletName;
    private String mWalletPassword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_soft_wallet;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {

    }

    private NavController getNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_create_soft_wallet_fragment);
        return navHostFragment.getNavController();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return getNavController().navigateUp();
    }

    @Override
    public boolean existsHDWallet() {
        return true;
    }

    @Override
    public boolean isImport() {
        return false;
    }

    @Override
    public void onSelectSoftWalletType(@SoftWalletType int type) {
        mSoftWalletType = type;
        getNavController().navigate(R.id.action_selectorWalletTypeFragment_to_selectorChainCoinFragment);
    }

    @Override
    public void onSelectCoinType(Vm.CoinType coinType) {
        mCoinType = coinType;
        if (coinType == Vm.CoinType.BTC) {
            getNavController().navigate(R.id.action_selectChainCoinFragment_to_selectBitcoinAddressTypeDialogFragment);
        } else {
            getNavController().navigate(R.id.action_selectChainCoinFragment_to_softWalletNameSettingFragment);
        }
    }

    @Override
    public void onSelectBitcoinAddressType(int purpose) {
        mBitcoinAddressPurpose = purpose;
        getNavController().navigate(R.id.action_selectBitcoinAddressTypeDialogFragment_to_softWalletNameSettingFragment);
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
                    SoftPassActivity.ResultDataBean resultDataBean1 = SoftPassActivity.decodeResultData(data);
                    mWalletPassword = resultDataBean1.password;
                    printData();
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

    private void printData() {
        Logger.e(mSoftWalletType + "  " + mCoinType.name + "   " + mBitcoinAddressPurpose + "  " + mWalletName + "   " + mWalletPassword);
    }
}

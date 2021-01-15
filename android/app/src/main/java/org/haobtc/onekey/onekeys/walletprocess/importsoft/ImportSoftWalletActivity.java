package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.orhanobut.logger.Logger;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.SelectBitcoinAddressTypeDialogFragment.OnSelectBitcoinAddressTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SelectChainCoinFragment.OnSelectCoinTypeCallback;
import org.haobtc.onekey.onekeys.walletprocess.SoftWalletNameSettingFragment.OnSetWalletNameCallback;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ChooseImportModeFragment.OnChooseImportModeCallback;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ChooseImportModeFragment.SoftWalletImportMode;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportKeystoreFragment.OnImportKeystoreCallback;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportMnemonicFragment.OnImportMnemonicCallback;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportPrivateKeyFragment.OnImportPrivateKeyCallback;
import org.haobtc.onekey.onekeys.walletprocess.importsoft.ImportWatchWalletFragment.OnImportWatchAddressCallback;
import org.haobtc.onekey.ui.activity.SoftPassActivity;

/**
 * 导入软件钱包流程
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 5:02 PM
 */
public class ImportSoftWalletActivity extends BaseActivity
        implements ImportSoftWalletProvider, OnSelectCoinTypeCallback, OnSelectBitcoinAddressTypeCallback,
        OnFinishViewCallBack, OnChooseImportModeCallback, OnSetWalletNameCallback,
        OnImportPrivateKeyCallback, OnImportKeystoreCallback, OnImportWatchAddressCallback, OnImportMnemonicCallback {

    private static final int REQUEST_SET_PWD = 2;

    public static void start(Context context) {
        Intent intent = new Intent(context, ImportSoftWalletActivity.class);
        context.startActivity(intent);
    }

    private Vm.CoinType mCoinType = Vm.CoinType.BTC;
    @SoftWalletImportMode
    private int mWalletImportMode;

    private String mImportPrivateKey;
    private String mImportKeystoreContent;
    private String mImportKeystorePassword;
    private String mImportMnemonic;
    private String mImportWatchAddress;

    private int mBitcoinAddressPurpose;
    private String mWalletName;
    private String mWalletPassword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_soft_wallet;
    }

    @Override
    public void initView() {
        NavInflater inflater = getNavController().getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.nav_host);
        graph.setStartDestination(R.id.selectChainCoinFragment);
        getNavController().setGraph(graph);
    }

    @Override
    public void initData() {

    }

    private NavController getNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_import_soft_wallet_fragment);
        return navHostFragment.getNavController();
    }

    @Override
    public boolean existsHDWallet() {
        return false;
    }

    @Override
    public boolean isImport() {
        return true;
    }

    @Override
    public Vm.CoinType currentCoinType() {
        return mCoinType;
    }

    @Override
    public boolean isImportHDWallet() {
        return false;
    }

    @Override
    public void onSelectCoinType(Vm.CoinType coinType) {
        mCoinType = coinType;
        getNavController().navigate(R.id.action_selectChainCoinFragment_to_chooseImportModeFragment);
    }

    @Override
    public void onChooseImportMode(@SoftWalletImportMode int mode) {
        mWalletImportMode = mode;
        switch (mode) {
            case SoftWalletImportMode.Private:
                getNavController().navigate(R.id.action_chooseImportModeFragment_to_importPrivateKeyFragment);
                break;
            case SoftWalletImportMode.Mnemonic:
                getNavController().navigate(R.id.action_chooseImportModeFragment_to_importMnemonicFragment);
                break;
            case SoftWalletImportMode.Keystore:
                getNavController().navigate(R.id.action_chooseImportModeFragment_to_importKeystoreFragment);
                break;
            case SoftWalletImportMode.Watch:
                getNavController().navigate(R.id.action_chooseImportModeFragment_to_importWatchWalletFragment);
                break;
        }
    }

    // region 导入钱包不同类型返回结果后的路由处理

    @Override
    public void onImportPrivateKey(String privateKey) {
        mImportPrivateKey = privateKey;
        if (currentCoinType() == Vm.CoinType.BTC) {
            getNavController().navigate(R.id.action_importPrivateKeyFragment_to_selectBitcoinAddressTypeDialogFragment);
        } else {
            getNavController().navigate(R.id.action_importPrivateKeyFragment_to_softWalletNameSettingFragment);
        }
    }

    @Override
    public void onImportMnemonic(String mnemonic) {
        mImportMnemonic = mnemonic;
        if (currentCoinType() == Vm.CoinType.BTC) {
            getNavController().navigate(R.id.action_importMnemonicFragment_to_selectBitcoinAddressTypeDialogFragment);
        } else {
            getNavController().navigate(R.id.action_importMnemonicFragment_to_softWalletNameSettingFragment);
        }
    }

    @Override
    public void onImportWatchAddress(String watchAddress) {
        mImportWatchAddress = watchAddress;
        getNavController().navigate(R.id.action_importWatchWalletFragment_to_softWalletNameSettingFragment);
    }

    @Override
    public void onImportKeystore(String keystore, String password) {
        mImportKeystoreContent = keystore;
        mImportKeystorePassword = password;
        getNavController().navigate(R.id.action_importKeystoreFragment_to_softWalletNameSettingFragment);
    }

    // endregion

    @Override
    public void onSelectBitcoinAddressType(int purpose) {
        mBitcoinAddressPurpose = purpose;
        getNavController().navigate(R.id.action_selectBitcoinAddressTypeDialogFragment_to_softWalletNameSettingFragment);
    }

    @Override
    public void onSetWalletName(String name) {
        mWalletName = name;
        if (mWalletImportMode == SoftWalletImportMode.Watch) {
            printData();
        } else {
            SoftPassActivity.startForResult(this, REQUEST_SET_PWD, -1);
        }
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
        Logger.e(mCoinType.name + "  " + mWalletName + "   " + mWalletPassword);
    }

}

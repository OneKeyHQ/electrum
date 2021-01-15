package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.databinding.FragmentChooseImportModeBinding;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;

import static org.haobtc.onekey.onekeys.walletprocess.importsoft.ChooseImportModeFragment.SoftWalletImportMode.*;

/**
 * 选择钱包导入方式
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 9:00 AM
 */
public class ChooseImportModeFragment extends BaseFragment implements View.OnClickListener {

    private FragmentChooseImportModeBinding mBinding;

    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnChooseImportModeCallback mOnChooseImportModeCallback;
    private ImportSoftWalletProvider mImportSoftWalletProvider;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
        if (context instanceof OnChooseImportModeCallback) {
            mOnChooseImportModeCallback = (OnChooseImportModeCallback) context;
        }
        if (context instanceof ImportSoftWalletProvider) {
            mImportSoftWalletProvider = (ImportSoftWalletProvider) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentChooseImportModeBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mBinding.imgBack.setOnClickListener(this);
        mBinding.relImportPrivate.setOnClickListener(this);
        mBinding.relImportKeystore.setOnClickListener(this);
        mBinding.relImportHelp.setOnClickListener(this);
        mBinding.relWatch.setOnClickListener(this);

        if (mImportSoftWalletProvider != null
                && mImportSoftWalletProvider.currentCoinType() == Vm.CoinType.ETH
                && Vm.CoinType.ETH.enable) {
            mBinding.relImportKeystore.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getContentViewId() {
        return 0;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mOnFinishViewCallBack != null) {
                    mOnFinishViewCallBack.onFinishView();
                }
                break;
            case R.id.rel_import_private:
                dealImport(Private);
                break;
            case R.id.rel_import_help:
                dealImport(Mnemonic);
                break;
            case R.id.rel_import_keystore:
                dealImport(Keystore);
                break;
            case R.id.rel_watch:
                dealImport(Watch);
                break;
        }
    }

    private void dealImport(@SoftWalletImportMode int mode) {
        if (mOnChooseImportModeCallback != null) {
            mOnChooseImportModeCallback.onChooseImportMode(mode);
        }
    }

    @IntDef({Private, Mnemonic, Keystore, Watch})
    public @interface SoftWalletImportMode {
        int Private = 0;
        int Mnemonic = 1;
        int Keystore = 2;
        int Watch = 3;
    }

    public interface OnChooseImportModeCallback {
        void onChooseImportMode(@SoftWalletImportMode int mode);
    }
}

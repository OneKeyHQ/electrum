package org.haobtc.onekey.onekeys.walletprocess;

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
import org.haobtc.onekey.databinding.FragmentCreateSelectorWalletTypeBinding;
import org.haobtc.onekey.onekeys.walletprocess.createsoft.CreateSoftWalletProvider;
import org.haobtc.onekey.ui.base.BaseFragment;

/**
 * 选择钱包币种类型，比如 BTC、ETH
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 5:07 PM
 */
public class SelectWalletTypeFragment extends BaseFragment implements View.OnClickListener {

    private FragmentCreateSelectorWalletTypeBinding mViewBinding;

    private OnSelectWalletTypeCallback mOnSelectWalletTypeCallback = null;
    private CreateSoftWalletProvider mCreateSoftWalletProvider = null;
    private OnFinishViewCallBack mOnFinishViewCallBack = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectWalletTypeCallback) {
            mOnSelectWalletTypeCallback = (OnSelectWalletTypeCallback) context;
        }
        if (context instanceof CreateSoftWalletProvider) {
            mCreateSoftWalletProvider = (CreateSoftWalletProvider) context;
        }
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = FragmentCreateSelectorWalletTypeBinding.inflate(inflater);
        init(mViewBinding.getRoot());
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mCreateSoftWalletProvider != null && !mCreateSoftWalletProvider.existsHDWallet()) {
            mViewBinding.relDeriveHd.setVisibility(View.GONE);
        }
        mViewBinding.imgBack.setOnClickListener(this);
        mViewBinding.relDeriveHd.setOnClickListener(this);
        mViewBinding.relSingleWallet.setOnClickListener(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_create_selector_wallet_type;
    }

    @Override
    public void init(View view) {

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
            case R.id.rel_derive_hd:
                dealCreate(SoftWalletType.HD_WALLET);
                break;
            case R.id.rel_single_wallet:
                dealCreate(SoftWalletType.SINGLE);
                break;
        }
    }

    private void dealCreate(@SoftWalletType int type) {
        if (mOnSelectWalletTypeCallback != null) {
            mOnSelectWalletTypeCallback.onSelectSoftWalletType(type);
        }
    }

    @IntDef({SoftWalletType.SINGLE, SoftWalletType.HD_WALLET})
    public @interface SoftWalletType {
        int SINGLE = 0;
        int HD_WALLET = 1;
    }

    public interface OnSelectWalletTypeCallback {
        void onSelectSoftWalletType(@SoftWalletType int type);
    }
}

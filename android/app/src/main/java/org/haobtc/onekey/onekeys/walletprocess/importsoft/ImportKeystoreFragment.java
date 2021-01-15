package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import org.haobtc.onekey.R;
import org.haobtc.onekey.databinding.FragmentImportKeystoreBinding;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;

/**
 * 使用 Keystore 导入钱包
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 12:13 PM
 */
@Keep
public class ImportKeystoreFragment extends BaseFragment implements View.OnClickListener {

    private FragmentImportKeystoreBinding mBinding;

    private ImportSoftWalletProvider mImportSoftWalletProvider;
    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnImportKeystoreCallback mOnImportKeystoreCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImportKeystoreCallback) {
            mOnImportKeystoreCallback = (OnImportKeystoreCallback) context;
        }
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
        if (context instanceof ImportSoftWalletProvider) {
            mImportSoftWalletProvider = (ImportSoftWalletProvider) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentImportKeystoreBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mBinding.imgBack.setOnClickListener(this);
        mBinding.imgScan.setOnClickListener(this);
        mBinding.imgEyeYes.setOnClickListener(this);
        mBinding.imgEyeNo.setOnClickListener(this);
        mBinding.btnImport.setOnClickListener(this);

        if (mImportSoftWalletProvider != null) {
            switch (mImportSoftWalletProvider.currentCoinType()) {
                case BTC:
                    mBinding.imgCoinType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.token_btc, null));
                    break;
                case ETH:
                    mBinding.imgCoinType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.token_eth, null));
                    break;
            }
        }
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mOnFinishViewCallBack != null) {
                    mOnFinishViewCallBack.onFinishView();
                }
                break;
            case R.id.img_scan:
                break;
            case R.id.img_eye_yes:
                mBinding.imgEyeYes.setVisibility(View.GONE);
                mBinding.imgEyeNo.setVisibility(View.VISIBLE);
                mBinding.editKeystorePass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.img_eye_no:
                mBinding.imgEyeYes.setVisibility(View.VISIBLE);
                mBinding.imgEyeNo.setVisibility(View.GONE);
                mBinding.editKeystorePass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case R.id.btn_import:
                String keystoreContent = mBinding.editKeystoreContent.getText().toString().trim();
                String keystorePass = mBinding.editKeystorePass.getText().toString().trim();
                // TODO: 1/17/21 验证 Keystore 内容是否合法，密码是否正确
                if (mOnImportKeystoreCallback != null) {
                    mOnImportKeystoreCallback.onImportKeystore(keystoreContent, keystorePass);
                }
                break;
        }
    }

    public interface OnImportKeystoreCallback {
        void onImportKeystore(String keystore, String password);
    }
}

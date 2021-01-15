package org.haobtc.onekey.onekeys.walletprocess;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.databinding.FragmentSoftWalletNameSettingBinding;
import org.haobtc.onekey.ui.base.BaseFragment;

/**
 * 设置钱包名称页面
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 11:38 PM
 */
@Keep
public class SoftWalletNameSettingFragment extends BaseFragment implements TextWatcher, View.OnClickListener {

    private FragmentSoftWalletNameSettingBinding mBinding;

    private OnSetWalletNameCallback mOnSetWalletNameCallback;
    private OnFinishViewCallBack mOnFinishViewCallBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSetWalletNameCallback) {
            mOnSetWalletNameCallback = (OnSetWalletNameCallback) context;
        }
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSoftWalletNameSettingBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void init(View view) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.editSetWalletName.addTextChangedListener(this);
        mBinding.imgBack.setOnClickListener(this);
        mBinding.btnImport.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                if (mOnFinishViewCallBack != null) {
                    mOnFinishViewCallBack.onFinishView();
                }
                break;
            case R.id.btn_import:
                if (TextUtils.isEmpty(mBinding.editSetWalletName.getText().toString())) {
                    showToast(getString(R.string.please_input_walletname));
                    return;
                }
                String name = mBinding.editSetWalletName.getText().toString().trim();
                if (mOnSetWalletNameCallback != null) {
                    mOnSetWalletNameCallback.onSetWalletName(name);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 禁止EditText输入空格
        if (s.toString().contains(" ")) {
            String[] str = s.toString().split(" ");
            StringBuilder sb = new StringBuilder();
            for (String value : str) {
                sb.append(value);
            }
            mBinding.editSetWalletName.setText(sb.toString());
            mBinding.editSetWalletName.setSelection(start);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().replace(" ", "");
        if (!TextUtils.isEmpty(text)) {
            mBinding.btnImport.setEnabled(true);
            if (s.length() > 14) {
                showToast(getString(R.string.name_lenth));
            }
        } else {
            mBinding.btnImport.setEnabled(false);
        }
    }

    public interface OnSetWalletNameCallback {
        void onSetWalletName(String name);
    }
}

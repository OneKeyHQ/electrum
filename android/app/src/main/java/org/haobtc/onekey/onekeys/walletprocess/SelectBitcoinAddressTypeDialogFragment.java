package org.haobtc.onekey.onekeys.walletprocess;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.databinding.DialogfragmentSelectBitcoinAddressTypeBinding;

import java.util.Objects;

/**
 * 选择比特币地址类型
 *
 * @author Onekey@QuincySx
 * @create 2021-01-16 6:10 PM
 */
public class SelectBitcoinAddressTypeDialogFragment extends DialogFragment implements View.OnClickListener {
    private DialogfragmentSelectBitcoinAddressTypeBinding mBinding;

    private OnSelectBitcoinAddressTypeCallback mOnSelectBitcoinAddressTypeCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectBitcoinAddressTypeCallback) {
            mOnSelectBitcoinAddressTypeCallback = (OnSelectBitcoinAddressTypeCallback) context;
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DialogfragmentSelectBitcoinAddressTypeBinding.inflate(getLayoutInflater(), container, false);
        View view = mBinding.getRoot();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
            window.setWindowAnimations(R.style.AnimBottom);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.nativeLayout.setOnClickListener(this);
        mBinding.normalLayout.setOnClickListener(this);
        mBinding.recommendLayout.setOnClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.native_layout:
                dealSelect(BitcoinAddressType.NativeType);
                break;
            case R.id.normal_layout:
                dealSelect(BitcoinAddressType.NormalType);
                break;
            case R.id.recommend_layout:
                dealSelect(BitcoinAddressType.RecommendType);
                break;
        }
        dismiss();
    }

    private void dealSelect(@BitcoinAddressType int type) {
        if (mOnSelectBitcoinAddressTypeCallback != null) {
            mOnSelectBitcoinAddressTypeCallback.onSelectBitcoinAddressType(type);
        }
    }

    @IntDef
    public @interface BitcoinAddressType {
        int RecommendType = 49;
        int NativeType = 84;
        int NormalType = 44;
    }

    public interface OnSelectBitcoinAddressTypeCallback {
        void onSelectBitcoinAddressType(@BitcoinAddressType int purpose);
    }
}

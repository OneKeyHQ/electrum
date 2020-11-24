package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.activity.AuthVerifyActivity;
import org.haobtc.onekey.ui.listener.IVerifyResultListener;

import butterknife.BindView;

/**
 * @author liyan
 */
public class VerifyResultFragment extends BaseFragment {

    @BindView(R.id.verify_ret)
    protected TextView mVerifyRet;
    @BindView(R.id.verify_img)
    protected ImageView mVerifyImg;
    @BindView(R.id.verify_wallet_name)
    protected TextView mVerifyWalletName;
    @BindView(R.id.verify_hide)
    protected TextView mVerifyHide;

    @Override
    public void init(View view) {

//        view.findViewById(R.id.back_device).setOnClickListener(this);
        boolean ret = false;

        mVerifyWalletName.setText("wallet1");

        if (ret) {
            mVerifyRet.setText(R.string.verify_success);
            mVerifyImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device_success));
            mVerifyHide.setText(R.string.verify_success_hide);
            return;
        }

        mVerifyRet.setText(R.string.verify_failed);
        mVerifyImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device_failed));
        mVerifyHide.setText(R.string.verify_failed_hide);

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_verify_result;
    }

}

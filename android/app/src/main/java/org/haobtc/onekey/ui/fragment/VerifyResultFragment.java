package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class VerifyResultFragment extends BaseFragment {


    @BindView(R.id.verify_ret)
    TextView verifyRet;
    @BindView(R.id.verify_img)
    ImageView verifyImg;
    @BindView(R.id.verify_wallet_name)
    TextView verifyWalletName;
    @BindView(R.id.verify_promote)
    TextView verifyPromote;
    @BindView(R.id.back)
    Button back;
    private String hardwareName;
    private boolean isSuccess;

    public VerifyResultFragment(String name, boolean success) {
        this.hardwareName = name;
        this.isSuccess = success;
    }
    @Override
    public void init(View view) {

        verifyWalletName.setText(hardwareName);

        if (isSuccess) {
            verifyRet.setText(R.string.verify_success);
            verifyImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device_success));
            verifyPromote.setText(R.string.verify_success_hide);
            return;
        }
        verifyRet.setText(R.string.verify_failed);
        verifyImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.device_failed));
        verifyPromote.setText(R.string.verify_failed_hide);

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_verify_result;
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        EventBus.getDefault().post(new ExitEvent());
    }
}

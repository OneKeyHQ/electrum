package org.haobtc.onekey.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

/**
 * @author liyan
 * @date 12/20/20
 */
public class DeleteWalletTipsDialog extends BaseDialogFragment {

    @BindView(R.id.content)
    TextView content;

    private int type;
    private ConfirmClickListener mConfirmClickListener;

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.delete_wallet_dialog;
    }

    @Override
    public void init() {
        Bundle args = getArguments();
        if (args != null) {
            type = args.getInt(Constant.WALLET_TYPE, 0);
        }
        if (type == 1) {
            content.setText(R.string.delete_watch_wallet_warning);
        }
    }

    @OnClick({R.id.confirm_button, R.id.cancel_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_button:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onClick(this);
                }
                break;
            case R.id.cancel_button:
                dismiss();
                break;
        }
    }

    public void setConfirmClickListener(ConfirmClickListener clickListener) {
        mConfirmClickListener = clickListener;
    }

    public interface ConfirmClickListener {
        void onClick(DialogFragment dialogFragment);
    }
}

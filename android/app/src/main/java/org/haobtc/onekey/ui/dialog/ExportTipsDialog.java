package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

/**
 * @author liyan
 * @date 12/18/20
 */
public class ExportTipsDialog extends BaseDialogFragment {

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.btn_i_know)
    Button btnIKnow;

    @BindView(R.id.img_cancel)
    ImageView imgCancel;

    private View.OnClickListener mOnConfirmListener;

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.export_mnemonic_tip;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_i_know, R.id.img_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_i_know:
                if (mOnConfirmListener != null) {
                    mOnConfirmListener.onClick(view);
                }
                dismiss();
                break;
        }
    }

    public ExportTipsDialog setOnConfirmListener(View.OnClickListener onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
        return this;
    }
}

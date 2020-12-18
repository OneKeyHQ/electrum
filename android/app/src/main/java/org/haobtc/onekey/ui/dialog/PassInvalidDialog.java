package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/17/20
 */

public class PassInvalidDialog extends BaseDialogFragment {
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.pass_tip)
    TextView passTip;
    @BindView(R.id.btn_input_again)
    Button btnInputAgain;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {

        return R.layout.longpass_input_tip;
    }

    @Override
    public void init() {
        super.init();
        int type = getArguments().getInt("type", 0);
        if (type == 1) {
            passTip.setText(R.string.long_pass_34);
        }
    }

    @OnClick({R.id.img_cancel, R.id.btn_input_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
            case R.id.btn_input_again:
                dismiss();
                break;
        }
    }
}

package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/17/20
 */

public class BackupRequireDialog extends BaseDialogFragment {
    @BindView(R.id.btn_back)
    Button btnBack;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.confrim_delete_hdwallet;
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked(View view) {
        dismiss();
    }
}

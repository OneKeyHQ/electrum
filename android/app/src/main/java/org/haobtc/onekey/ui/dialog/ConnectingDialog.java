package org.haobtc.onekey.ui.dialog;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

/**
 * @author liyan
 * @date 12/29/20
 */

public class ConnectingDialog extends BaseDialogFragment {
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.connecting_dialog;
    }

    @Override
    public boolean requireGravityCenter() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}

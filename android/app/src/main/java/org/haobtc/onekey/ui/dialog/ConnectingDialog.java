package org.haobtc.onekey.ui.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

/**
 * @author liyan
 * @date 12/29/20
 */

public class ConnectingDialog extends CenterPopupView {

    public ConnectingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.connecting_dialog;
    }
}

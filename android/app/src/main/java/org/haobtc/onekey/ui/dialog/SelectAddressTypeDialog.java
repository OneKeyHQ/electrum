package org.haobtc.onekey.ui.dialog;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.ui.activity.CreatePersonalWalletActivity;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/21/20
 */

public class SelectAddressTypeDialog extends BaseDialogFragment {

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.dialog_select_wallet_type;
    }

    @OnClick({R.id.recommend_layout, R.id.native_layout, R.id.normal_layout})
    public void onViewClicked(View view) {
        String walletType = null;
        switch (view.getId()) {
            case R.id.recommend_layout:
                walletType = PyConstant.ADDRESS_TYPE_P2SH_P2WPKH;
                break;
            case R.id.native_layout:
                walletType = PyConstant.ADDRESS_TYPE_P2WPKH;
                break;
            case R.id.normal_layout:
                walletType = PyConstant.ADDRESS_TYPE_P2PKH;
                break;
        }
        ((CreatePersonalWalletActivity)requireActivity()).getXpub(walletType);
        dismiss();
    }
}

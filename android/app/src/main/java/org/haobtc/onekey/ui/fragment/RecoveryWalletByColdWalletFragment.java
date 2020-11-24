package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.custom.LoadingTextView;
import org.haobtc.onekey.ui.listener.IRecoveryWalletByColdWalletListener;

import butterknife.BindView;

public class RecoveryWalletByColdWalletFragment extends BaseFragment {

    @BindView(R.id.way_img)
    protected ImageView mWayImageView;
    @BindView(R.id.loading_wait_response)
    protected LoadingTextView mWaitResponse;
    @BindView(R.id.loading_import_to_phone)
    protected LoadingTextView mImportToPhone;
    @BindView(R.id.loading_complete_recovery)
    protected LoadingTextView mCompleteRecovery;

    @Override
    public void init(View view) {

        //todo recovery wallet

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_recovery_wallet_by_cold_wallet;
    }
}

package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.custom.LoadingTextView;
import org.haobtc.onekey.ui.listener.IBackupWalletToColdWalletListener;

import butterknife.BindView;

/**
 * @author liyan
 */
public class BackupWalletToColdWalletFragment extends BaseFragment {

    @BindView(R.id.way_img)
    protected ImageView mWayImageView;
    @BindView(R.id.loading_wait_response)
    protected LoadingTextView mWaitResponse;
    @BindView(R.id.loading_backup_to_device)
    protected LoadingTextView mBackupToDevice;
    @BindView(R.id.loading_complete_recovery)
    protected LoadingTextView mCompleteRecovery;
    @BindView(R.id.backup_device_name)
    protected TextView mBackupTitle;

    @Override
    public void init(View view) {

        mBackupTitle.setText(getString(R.string.backup_wallet_to, "wallet 1"));

        //todo backwallet wallet

//        if (getListener() != null) {
//            getListener().onBackupSuccess();
//        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_backup_wallet_to_cold_wallet;
    }
}

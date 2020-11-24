package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.IBackupSuccessListener;

import butterknife.BindView;

/**
 * @author liyan
 */
public class BackupSuccessFragment extends BaseFragment {

    @BindView(R.id.wallet_name)
    protected Button mWalletName;

    @Override
    public void init(View view) {

        mWalletName.setText("test");
//        view.findViewById(R.id.back_wallet).setOnClickListener(this);

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_backup_success;
    }

}

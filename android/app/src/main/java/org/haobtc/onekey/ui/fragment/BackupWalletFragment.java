package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.IBackupWalletListener;

public class BackupWalletFragment extends BaseFragment<IBackupWalletListener> implements View.OnClickListener {


    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.backup_wallet);
        view.findViewById(R.id.copy_to_other_device).setOnClickListener(this);
        view.findViewById(R.id.ready_go).setOnClickListener(this);

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_backup_wallet;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_to_other_device:

                break;
            case R.id.ready_go:
                if (getListener() != null) {
                    getListener().onReadyGo();
                }
                break;
        }
    }
}

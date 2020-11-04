package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseMvpFragment;
import org.haobtc.onekey.mvp.presenter.FindBackupFromPhonePresenter;
import org.haobtc.onekey.mvp.view.IFindBackupFromPhoneView;
import org.haobtc.onekey.ui.listener.IFindBackupFromPhoneListener;

public class FindBackupFromPhoneFragment extends BaseMvpFragment<FindBackupFromPhonePresenter
        , IFindBackupFromPhoneListener> implements IFindBackupFromPhoneView {

    @Override
    public void init(View view) {

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_find_backup_from_phone;
    }

    @Override
    protected FindBackupFromPhonePresenter initPresenter() {
        return new FindBackupFromPhonePresenter(this);
    }
}

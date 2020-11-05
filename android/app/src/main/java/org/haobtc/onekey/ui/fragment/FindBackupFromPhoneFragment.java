package org.haobtc.onekey.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BackupWalletBean;
import org.haobtc.onekey.mvp.base.BaseMvpFragment;
import org.haobtc.onekey.mvp.presenter.FindBackupFromPhonePresenter;
import org.haobtc.onekey.mvp.view.IFindBackupFromPhoneView;
import org.haobtc.onekey.ui.adapter.BackupWalletListAdapter;
import org.haobtc.onekey.ui.listener.IFindBackupFromPhoneListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FindBackupFromPhoneFragment extends BaseMvpFragment<FindBackupFromPhonePresenter
        , IFindBackupFromPhoneListener> implements IFindBackupFromPhoneView, BackupWalletListAdapter.CallBack {

    @BindView(R.id.backup_list)
    protected RecyclerView mBackupListView;
    private List<BackupWalletBean> mBackupList;
    private BackupWalletListAdapter mAdapter;

    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.recovery_device_title);
        mBackupList = new ArrayList<>();

        mAdapter = new BackupWalletListAdapter(getContext(), mBackupList, this);
        mBackupListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBackupListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_find_backup_from_phone;
    }

    @Override
    protected FindBackupFromPhonePresenter initPresenter() {
        return new FindBackupFromPhonePresenter(this);
    }

    @Override
    public void onItemClick(int position) {
        if (mBackupList == null || position >= mBackupList.size() || getListener() == null) {
            return;
        }

        getListener().onBackupToRecovery(mBackupList.get(position));
    }
}

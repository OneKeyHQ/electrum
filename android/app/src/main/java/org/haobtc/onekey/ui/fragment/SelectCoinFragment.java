package org.haobtc.onekey.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.adapter.CoinsAdapter;
import org.haobtc.onekey.ui.listener.ISelectCoinListener;
import org.haobtc.onekey.utils.CoinUtils;

import java.util.List;

import butterknife.BindView;

public class SelectCoinFragment extends BaseFragment<ISelectCoinListener> implements CoinsAdapter.CallBack {

    @BindView(R.id.coin_list)
    protected RecyclerView mCoinView;
    private List<CoinBean> mCoinList;
    private CoinsAdapter mAdapter;

    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.create);
        mCoinList = CoinUtils.getSupportCoins(getContext());
        mAdapter = new CoinsAdapter(getContext(), mCoinList, this);
        mCoinView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCoinView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_select_coin;
    }


    @Override
    public void onItemClick(int position) {
        if (position >= mCoinList.size() || getListener() == null) {
            return;
        }
        CoinBean bean = mCoinList.get(position);
        getListener().onCoinChoose(bean);
    }
}

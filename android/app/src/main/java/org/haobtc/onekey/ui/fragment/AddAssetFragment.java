package org.haobtc.onekey.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.AssetBean;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.adapter.AssetAdapter;
import org.haobtc.onekey.ui.listener.IAddAssetListener;
import org.haobtc.onekey.utils.CoinUtils;

import java.util.List;

import butterknife.BindView;

public class AddAssetFragment extends BaseFragment<IAddAssetListener> implements View.OnClickListener {

    @BindView(R.id.asset_list)
    protected RecyclerView mAssetView;
    private List<AssetBean> mAssetList;
    private AssetAdapter mAdapter;

    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.add_asset);
        view.findViewById(R.id.complete).setOnClickListener(this);
        mAssetList = CoinUtils.getSupportCoins(getContext());
        mAdapter = new AssetAdapter(getContext(),mAssetList);
        mAssetView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAssetView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_add_asset;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.complete:

                break;
        }
    }
}

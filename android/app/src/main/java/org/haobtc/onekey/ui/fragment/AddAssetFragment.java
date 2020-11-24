package org.haobtc.onekey.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.adapter.AssetAdapter;
import org.haobtc.onekey.utils.CoinUtils;

import java.util.List;

import butterknife.BindView;

/**
 * @author liyan
 */
public class AddAssetFragment extends BaseFragment {

    @BindView(R.id.asset_list)
    protected RecyclerView mAssetView;
    private List<CoinBean> mAssetList;
    private AssetAdapter mAdapter;

    @Override
    public void init(View view) {
        mAssetList = CoinUtils.getSupportCoins();
        mAdapter = new AssetAdapter(getContext(), mAssetList);
        mAssetView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAssetView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_add_asset;
    }
}

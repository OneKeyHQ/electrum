package org.haobtc.wallet.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.wallet.R;

import java.util.List;

public class AddWhiteListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AddWhiteListAdapter(@Nullable List<String> data) {
        super(R.layout.white_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.test_list,item);

    }
}

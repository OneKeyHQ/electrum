package org.haobtc.onekey.adapter;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;

import java.util.List;

public class HomeHdAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HomeHdAdapter(@Nullable List<String> data) {
        super(R.layout.home_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text_btc_amount,item);
    }
}

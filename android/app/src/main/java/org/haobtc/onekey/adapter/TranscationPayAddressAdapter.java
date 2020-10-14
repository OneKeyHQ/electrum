package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;

import java.util.List;


public class TranscationPayAddressAdapter extends BaseQuickAdapter<GetnewcreatTrsactionListBean.InputAddrBean, BaseViewHolder> {
    public TranscationPayAddressAdapter(@Nullable List<GetnewcreatTrsactionListBean.InputAddrBean> data) {
        super(R.layout.payaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetnewcreatTrsactionListBean.InputAddrBean item) {
        helper.setText(R.id.tet_moreaddress,item.getAddr());

    }
}

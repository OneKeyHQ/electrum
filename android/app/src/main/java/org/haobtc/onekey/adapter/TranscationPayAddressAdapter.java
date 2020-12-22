package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.TransactionInfoBean;

import java.util.List;


public class TranscationPayAddressAdapter extends BaseQuickAdapter<TransactionInfoBean.InputAddrBean, BaseViewHolder> {
    public TranscationPayAddressAdapter(@Nullable List<TransactionInfoBean.InputAddrBean> data) {
        super(R.layout.payaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransactionInfoBean.InputAddrBean item) {
        helper.setText(R.id.tet_moreaddress,item.getAddress());

    }
}

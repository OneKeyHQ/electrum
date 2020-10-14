package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.ScanCheckDetailBean;

import java.util.List;

public class InputaddrScanAdapter extends BaseQuickAdapter<ScanCheckDetailBean.DataBean.InputAddrBean, BaseViewHolder> {
    public InputaddrScanAdapter(@Nullable List<ScanCheckDetailBean.DataBean.InputAddrBean> data) {
        super(R.layout.payaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScanCheckDetailBean.DataBean.InputAddrBean item) {
        helper.setText(R.id.tet_moreaddress, item.getAddr());

    }
}

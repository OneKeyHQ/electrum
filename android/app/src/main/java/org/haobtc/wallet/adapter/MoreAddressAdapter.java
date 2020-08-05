package org.haobtc.wallet.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.wallet.R;
import org.haobtc.wallet.bean.GetnewcreatTrsactionListBean;

import java.util.List;

public class MoreAddressAdapter extends BaseQuickAdapter<GetnewcreatTrsactionListBean.OutputAddrBean, BaseViewHolder> {

    public MoreAddressAdapter(@Nullable List<GetnewcreatTrsactionListBean.OutputAddrBean> data) {
        super(R.layout.moreaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetnewcreatTrsactionListBean.OutputAddrBean item) {
        helper.setText(R.id.tet_moreaddress, item.getAddr());
        String amount = item.getAmount();
        if (!TextUtils.isEmpty(amount)) {
            helper.setText(R.id.tet_payNum, amount);
        }
        if (item.getIs_change()){
            helper.getView(R.id.test_change_address).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.test_change_address).setVisibility(View.GONE);
        }
    }
}


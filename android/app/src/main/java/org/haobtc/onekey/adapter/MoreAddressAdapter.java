package org.haobtc.onekey.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;

import java.util.List;

public class MoreAddressAdapter extends BaseQuickAdapter<GetnewcreatTrsactionListBean.OutputAddrBean, BaseViewHolder> {
    private String plusNums;

    public MoreAddressAdapter(@Nullable List<GetnewcreatTrsactionListBean.OutputAddrBean> data, String plusNum) {
        super(R.layout.moreaddress_item, data);
        plusNums = plusNum;
    }

    @Override
    protected void convert(BaseViewHolder helper, GetnewcreatTrsactionListBean.OutputAddrBean item) {
        helper.setText(R.id.tet_moreaddress, item.getAddr());
        String amount = item.getAmount();
        if (plusNums.contains("-")) {
            if (!TextUtils.isEmpty(amount)) {
                String amountPlus = plusNums.replace("-", "");
                if (amountPlus.equals(amount)) {
                    helper.setText(R.id.tet_payNum, "- " + amount);
                } else {
                    helper.setText(R.id.tet_payNum, amount);
                }
            }

        } else if (plusNums.contains("+")) {
            if (!TextUtils.isEmpty(amount)) {
                String amountPlus = plusNums.replace("+", "");
                if (amountPlus.equals(amount)) {
                    helper.setText(R.id.tet_payNum, "+ " + amount);
                } else {
                    helper.setText(R.id.tet_payNum, amount);
                }
            }
        }

        if (item.getIs_change()) {
            helper.getView(R.id.test_change_address).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.test_change_address).setVisibility(View.GONE);
        }
    }
}


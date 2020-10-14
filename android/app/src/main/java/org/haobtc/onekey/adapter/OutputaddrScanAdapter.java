package org.haobtc.onekey.adapter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.ScanCheckDetailBean;

import java.util.List;

public class OutputaddrScanAdapter extends BaseQuickAdapter<ScanCheckDetailBean.DataBean.OutputAddrBean, BaseViewHolder> {
    public OutputaddrScanAdapter(@Nullable List<ScanCheckDetailBean.DataBean.OutputAddrBean> data) {
        super(R.layout.moreaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScanCheckDetailBean.DataBean.OutputAddrBean item) {
        helper.setText(R.id.tet_moreaddress, item.getAddr());

        String amount = item.getAmount();
//        Log.i("amountamount", "convert: " + amount);
        if (!TextUtils.isEmpty(amount)) {
            helper.setText(R.id.tet_payNum, amount);

        }
    }
}

package org.haobtc.onekey.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;

import java.util.List;

public class OnekeyTxListAdapter extends BaseQuickAdapter<MaintrsactionlistEvent, BaseViewHolder> {
    public OnekeyTxListAdapter(@Nullable List<MaintrsactionlistEvent> data) {
        super(R.layout.btc_detail_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaintrsactionlistEvent item) {
        String amount = item.getAmount().substring(0, item.getAmount().indexOf("("));
        helper.setText(R.id.text_address, item.getTxHash()).setText(R.id.text_time, item.getDate());
        TextView tetAmount = helper.getView(R.id.text_send_amount);
        if (item.isMine()) {
            //send
            tetAmount.setTextColor(mContext.getColor(R.color.text_eight));
            helper.setText(R.id.text_send_amount, "-" + amount);
        } else {
            //get
            tetAmount.setTextColor(mContext.getColor(R.color.onekey));
            helper.setText(R.id.text_send_amount, "+" + amount);
        }

    }
}

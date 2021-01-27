package org.haobtc.onekey.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.TransactionSummaryVo;
import org.haobtc.onekey.extensions.StringKt;

import java.util.List;

public class OnekeyTxListAdapter extends BaseQuickAdapter<TransactionSummaryVo, BaseViewHolder> {

    public OnekeyTxListAdapter(@Nullable List<TransactionSummaryVo> data) {
        super(R.layout.btc_detail_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransactionSummaryVo item) {
        helper.setText(R.id.text_address, item.getAddress());
        String date = item.getDate();
        helper.setText(R.id.text_time, date);
        TextView tetAmount = helper.getView(R.id.text_send_amount);
        ImageView imgStatus = helper.getView(R.id.imageView);

        String amount = StringKt.interceptDecimal(item.getAmount()) + " " + item.getAmountUnit();
        if (item.isMine()) {
            //send
            tetAmount.setTextColor(mContext.getColor(R.color.text_eight));
            helper.setText(R.id.text_send_amount, "-" + amount);
            imgStatus.setImageDrawable(ResourcesCompat.getDrawable(helper.itemView.getResources(), R.drawable.send_, null));
        } else {
            //get
            tetAmount.setTextColor(mContext.getColor(R.color.onekey));
            helper.setText(R.id.text_send_amount, "+" + amount);
            imgStatus.setImageDrawable(ResourcesCompat.getDrawable(helper.itemView.getResources(), R.drawable.receive_, null));
        }
        TextView sendStatus = helper.getView(R.id.text_send_status);
        if (!TextUtils.isEmpty(item.getStatus())) {
            sendStatus.setTextColor(mContext.getColor(R.color.text_six));
            sendStatus.setText(item.getStatus());
        } else {
            sendStatus.setVisibility(View.GONE);
        }
    }
}

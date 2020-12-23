package org.haobtc.onekey.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;

import java.text.DecimalFormat;
import java.util.List;

public class OnekeyTxListAdapter extends BaseQuickAdapter<MaintrsactionlistEvent, BaseViewHolder> {
    private String amountFinal;

    public OnekeyTxListAdapter(@Nullable List<MaintrsactionlistEvent> data) {
        super(R.layout.btc_detail_item, data);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(BaseViewHolder helper, MaintrsactionlistEvent item) {
        String amount = "";
        if (item.getAmount().contains("(")) {
            amount = item.getAmount().substring(0, item.getAmount().indexOf("("));
        } else {
            amount = item.getAmount();
        }
        helper.setText(R.id.text_address, item.getAddress());
        String date = item.getDate();
        if (date.contains("-")) {
            String str = date.substring(5);
            String strs = str.substring(0, str.length() - 3);
            String time = strs.replace("-", "/");
            helper.setText(R.id.text_time, time);
        } else {
            helper.setText(R.id.text_time, date);
        }
        TextView tetAmount = helper.getView(R.id.text_send_amount);
        ImageView imgStatus = helper.getView(R.id.imageView);
        String substring = amount.substring(0, amount.indexOf(" "));
        String amountFix = substring.substring(substring.indexOf(".") + 1);
        if (amountFix.length() > 8) {
            DecimalFormat dfs = new DecimalFormat("0.00000000");
            amountFinal = dfs.format(amountFix);
        } else {
            amountFinal = amount;
        }
        if (item.isMine()) {
            //send
            tetAmount.setTextColor(mContext.getColor(R.color.text_eight));
            helper.setText(R.id.text_send_amount, "-" + amountFinal);
            imgStatus.setImageDrawable(mContext.getDrawable(R.drawable.send_));
        } else {
            //get
            tetAmount.setTextColor(mContext.getColor(R.color.onekey));
            helper.setText(R.id.text_send_amount, "+" + amountFinal);
            imgStatus.setImageDrawable(mContext.getDrawable(R.drawable.receive_));
        }
        TextView sendStatus = helper.getView(R.id.text_send_status);
        if (!TextUtils.isEmpty(item.getTxStatus())) {
            sendStatus.setTextColor(mContext.getColor(R.color.text_six));
            sendStatus.setText(item.getTxStatus());
        } else {
            sendStatus.setVisibility(View.GONE);
        }
    }
}

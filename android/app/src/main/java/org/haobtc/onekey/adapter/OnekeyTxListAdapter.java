package org.haobtc.onekey.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.TransactionSummaryVo;
import org.haobtc.onekey.extensions.StringKt;
import org.haobtc.onekey.onekeys.homepage.process.TransactionListFragment;

public class OnekeyTxListAdapter extends BaseQuickAdapter<TransactionSummaryVo, BaseViewHolder> {
    @TransactionListFragment.TransactionListType private String mType;

    public OnekeyTxListAdapter(
            @Nullable List<TransactionSummaryVo> data,
            @TransactionListFragment.TransactionListType String type) {
        super(R.layout.btc_detail_item, data);
        mType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, TransactionSummaryVo item) {
        helper.setText(R.id.text_address, item.getAddress());
        String date = item.getDate();
        helper.setText(R.id.text_time, date);
        TextView tetAmount = helper.getView(R.id.text_send_amount);
        ImageView imgStatus = helper.getView(R.id.imageView);

        String amount = StringKt.interceptDecimal(item.getAmount()) + " " + item.getAmountUnit();
        switch (mType) {
            default:
            case TransactionListFragment.TransactionListType.ALL:
                if (item.isMine()) {
                    // send
                    tetAmount.setTextColor(mContext.getColor(R.color.text_eight));
                    helper.setText(R.id.text_send_amount, "-" + amount);
                    imgStatus.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    helper.itemView.getResources(), R.drawable.send_, null));
                } else {
                    // get
                    tetAmount.setTextColor(mContext.getColor(R.color.onekey));
                    helper.setText(R.id.text_send_amount, "+" + amount);
                    imgStatus.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    helper.itemView.getResources(), R.drawable.receive_, null));
                }
                break;
            case TransactionListFragment.TransactionListType.RECEIVE:
                tetAmount.setTextColor(mContext.getColor(R.color.onekey));
                helper.setText(R.id.text_send_amount, "+" + amount);
                imgStatus.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                helper.itemView.getResources(), R.drawable.receive_, null));
                break;
            case TransactionListFragment.TransactionListType.SEND:
                tetAmount.setTextColor(mContext.getColor(R.color.text_eight));
                helper.setText(R.id.text_send_amount, "-" + amount);
                imgStatus.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                helper.itemView.getResources(), R.drawable.send_, null));
                break;
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

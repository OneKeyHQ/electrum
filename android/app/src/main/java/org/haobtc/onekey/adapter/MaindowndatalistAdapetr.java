package org.haobtc.onekey.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;

import java.util.List;

public class MaindowndatalistAdapetr extends BaseQuickAdapter<MaintrsactionlistEvent, BaseViewHolder> {
    public MaindowndatalistAdapetr(@Nullable List<MaintrsactionlistEvent> data) {
        super(R.layout.fragment_item_trans, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MaintrsactionlistEvent item) {
        boolean isMine = item.isMine();
        if (isMine) {
            helper.setText(R.id.tet_name, R.string.send);
        } else {
            helper.setText(R.id.tet_name, R.string.receive);
        }
        helper.setText(R.id.tet_address, item.getTxHash());

        String amount = item.getAmount();
        if (!TextUtils.isEmpty(amount)) {
            if (amount.contains("(")) {
                String substring = amount.substring(0, amount.indexOf("("));
                helper.setText(R.id.tet_BTC, substring);
            } else {
                helper.setText(R.id.tet_BTC, amount);
            }
        }

        String date = item.getDate();

        if ("unknown".equals(date) || TextUtils.isEmpty(date)) {
            helper.setText(R.id.tet_Time, "");
        } else {
            helper.setText(R.id.tet_Time, item.getDate());
        }

        LinearLayout linearDelete = helper.getView(R.id.linear_delete);// delete button
        //judge type
        String type = item.getType();
        if ("history".equals(type)) {
            //history
            String confirmations = item.getConfirmations();
            String txStatus = item.getTxStatus();
            int anInt = Integer.parseInt(confirmations);
            if (anInt > 0) {//Confirmed
                helper.setText(R.id.tet_zt, R.string.alreadychoose);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FF838383"));
                tetview.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                tetview.setBackground(mContext.getDrawable(R.drawable.gray_tuocircle));
                linearDelete.setVisibility(View.GONE);//hide delete button
            } else if (txStatus.contains("Unconfirmed")) {//Unconfirmed
                helper.setText(R.id.tet_zt, R.string.waitchoose);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FF838383"));
                tetview.setBackground(mContext.getDrawable(R.drawable.gray_tuocircle));
                linearDelete.setVisibility(View.GONE);//hide delete button
            } else if ("Unsigned".equals(txStatus)) {//Unsigned
                //new creat trsaction
                helper.setText(R.id.tet_zt, R.string.transaction_waitting);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FFF26A3A"));
                tetview.setBackground(mContext.getDrawable(R.drawable.orange_circle));
                linearDelete.setVisibility(View.VISIBLE);//hide delete button
            } else if (txStatus.contains("Partially signed")) {
                //new creat trsaction
                helper.setText(R.id.tet_zt, R.string.transaction_waitting);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FFF26A3A"));
                tetview.setBackground(mContext.getDrawable(R.drawable.orange_circle));
                linearDelete.setVisibility(View.VISIBLE);//hide delete button
            } else if ("Signed".equals(txStatus) || "Local".equals(txStatus)) {
                //new creat trsaction
                helper.setText(R.id.tet_zt, R.string.wait_broadcast);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FF838383"));
                tetview.setBackground(mContext.getDrawable(R.drawable.gray_tuocircle));
                linearDelete.setVisibility(View.VISIBLE);//hide delete button
            }else if ("Not verified".equals(txStatus)){//Confirmed
                helper.setText(R.id.tet_zt, R.string.alreadychoose);
                TextView tetview = helper.getView(R.id.tet_zt);
                tetview.setTextColor(Color.parseColor("#FF6182F5"));
                tetview.setBackground(mContext.getDrawable(R.drawable.gray_tuocircle));
                linearDelete.setVisibility(View.GONE);//hide delete button
            }
        }
        helper.addOnClickListener(R.id.lin_Item);
        helper.addOnClickListener(R.id.txt_delete);

    }

}

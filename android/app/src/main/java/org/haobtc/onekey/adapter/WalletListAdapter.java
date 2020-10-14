package org.haobtc.onekey.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;

import java.util.List;

public class WalletListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public WalletListAdapter(@Nullable List<String> data) {
        super(R.layout.hd_wallet_item, data);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text_type, item);
        RelativeLayout view = helper.getView(R.id.rel_background);
        if ("BTC".equals(item)){
            view.setBackground(mContext.getDrawable(R.drawable.orange_back));
        }else if ("ETH".equals(item)){
            view.setBackground(mContext.getDrawable(R.drawable.eth_blue_back));
        }else if ("EOS".equals(item)){
            view.setBackground(mContext.getDrawable(R.drawable.eos_gray_back));
        }
    }
}

package org.haobtc.onekey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CNYBean;

import java.util.ArrayList;

public class QuetationChooseAdapter extends BaseQuickAdapter<CNYBean, QuetationChooseAdapter.myViewHolder> {
    private ArrayList<CNYBean> exchangeList;

    public QuetationChooseAdapter(Context context, ArrayList<CNYBean> exchangeList, int exchange) {
        super(R.layout.price_quotation_item, exchangeList);
        this.exchangeList = exchangeList;
        if (exchangeList.size() - 1 >= exchange) {
            this.exchangeList.get(exchange).setStatus(true);
        }
    }

    public class myViewHolder extends BaseViewHolder {
        TextView tetWalletName;
        ImageView imgChoose;

        public myViewHolder(View view) {
            super(view);
            tetWalletName = view.findViewById(R.id.tet_WalletName);
            imgChoose = view.findViewById(R.id.img_choose);
        }
    }

    @Override
    protected void convert(myViewHolder holder, CNYBean item) {
        holder.tetWalletName.setText(item.getName());
        holder.tetWalletName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < exchangeList.size(); i++) {
                    exchangeList.get(i).setStatus(false);
                }
                item.setStatus(true);
                onLisennorClick.itemClick(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        if (item.isStatus()) {
            holder.imgChoose.setVisibility(View.VISIBLE);
        } else {
            holder.imgChoose.setVisibility(View.GONE);
        }
    }

    public interface onLisennorClick {
        void itemClick(int pos);
    }

    private onLisennorClick onLisennorClick;

    public void setOnLisennorClick(onLisennorClick onLisennorClick) {
        this.onLisennorClick = onLisennorClick;
    }
}

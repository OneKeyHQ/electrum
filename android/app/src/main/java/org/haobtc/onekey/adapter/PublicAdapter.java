package org.haobtc.onekey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CNYBean;

import java.util.ArrayList;
import java.util.List;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.myViewHolder> {
    private Context context;
    private ArrayList<CNYBean> pubList;

    public PublicAdapter(Context context, ArrayList<CNYBean> pubList, int pos) {
        this.context = context;
        this.pubList = pubList;
        this.pubList.get(pos).setStatus(true);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView textNum;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            textNum = itemView.findViewById(R.id.text_num);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.public_item, null);
        return new myViewHolder(inflate);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.textNum.setText(pubList.get(position).getName());
        holder.textNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < pubList.size(); i++) {
                    pubList.get(i).setStatus(false);
                }
                pubList.get(position).setStatus(true);
                if (onLisennorClick!=null){
                    onLisennorClick.itemClick(position);
                }
                notifyDataSetChanged();

            }
        });
        if (pubList.get(position).isStatus()) {
            holder.textNum.setBackground(context.getDrawable(R.drawable.graeen_tuo_15));
            holder.textNum.setTextColor(context.getColor(R.color.button_bk_ddake));
        } else {
            holder.textNum.setBackground(context.getDrawable(R.drawable.gray_tuo_15));
            holder.textNum.setTextColor(context.getColor(R.color.text_two));
        }

    }

    @Override
    public int getItemCount() {
        return pubList != null ? pubList.size() : 0;
    }

    public interface onLisennorClick {
        void itemClick(int pos);
    }

    private onLisennorClick onLisennorClick;

    public void setOnLisennorClick(onLisennorClick onLisennorClick) {
        this.onLisennorClick = onLisennorClick;
    }

}

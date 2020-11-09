package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;

import java.util.List;


public class CoinsAdapter extends RecyclerView.Adapter<CoinsAdapter.ViewHolder> {

    public List<CoinBean> mValues;
    private LayoutInflater mInflater;
    private CallBack mCallBack;


    public CoinsAdapter(Context context, List<CoinBean> list, CallBack callBack) {
        this.mValues = list;
        this.mCallBack = callBack;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.item_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCoin = mValues.get(position);
        holder.mIcon.setImageDrawable(holder.mCoin.getIcon());
        holder.mName.setText(holder.mCoin.getName());
        holder.mView.setOnClickListener(v -> {
            if (this.mCallBack != null) {
                this.mCallBack.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final ImageView mIcon;
        public final View mView;
        public CoinBean mCoin;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.item_name);
            mIcon = view.findViewById(R.id.item_icon);
        }

    }

    public interface CallBack {
        void onItemClick(int position);
    }

}

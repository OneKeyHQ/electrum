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
import org.haobtc.onekey.bean.AssetBean;

import java.util.List;


public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    public List<AssetBean> mValues;
    private LayoutInflater mInflater;


    public AssetAdapter(Context context, List<AssetBean> list) {
        this.mValues = list;
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
        holder.mAsset = mValues.get(position);
        holder.mIcon.setImageDrawable(holder.mAsset.getIcon());
        holder.mName.setText(holder.mAsset.getName());
        holder.mChecked.setVisibility(holder.mAsset.isChecked() ? View.VISIBLE : View.INVISIBLE);
        holder.mView.setOnClickListener(v -> {
            holder.mAsset.setChecked(!holder.mAsset.isChecked());
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final ImageView mIcon;
        public final ImageView mChecked;
        public final View mView;
        public AssetBean mAsset;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.item_name);
            mIcon = view.findViewById(R.id.item_icon);
            mChecked = view.findViewById(R.id.item_checked);
        }

    }
}

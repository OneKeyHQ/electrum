package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.GetXpubEvent;

import java.util.List;


/**
 * @author liyan
 */
public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    public List<CoinBean> mValues;
    private Context context;


    public AssetAdapter(Context context, List<CoinBean> list) {
        this.mValues = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mAsset = mValues.get(position);
        holder.mIcon.setImageDrawable(context.getDrawable(holder.mAsset.getIconId()));
        holder.mName.setText(context.getString(holder.mAsset.getNameId()));
        holder.mView.setOnClickListener(v -> {
            holder.mView.setClickable(false);
            switch (holder.mAsset.getNameId()) {
                case R.string.coin_btc:
                    EventBus.getDefault().post(new GetXpubEvent(Constant.COIN_TYPE_BTC));
                    break;
                case R.string.coin_eth:
                    EventBus.getDefault().post(new GetXpubEvent(Constant.COIN_TYPE_ETH));
                    break;
                case R.string.coin_eos:
                    EventBus.getDefault().post(new GetXpubEvent(Constant.COIN_TYPE_EOS));
                    break;
                default:
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
        public CoinBean mAsset;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.item_name);
            mIcon = view.findViewById(R.id.item_icon);
        }

    }
}

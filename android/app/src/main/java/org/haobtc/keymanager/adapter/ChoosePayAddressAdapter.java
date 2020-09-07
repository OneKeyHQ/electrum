package org.haobtc.keymanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.bean.AddressEvent;

import java.util.ArrayList;
import java.util.List;

public class ChoosePayAddressAdapter extends RecyclerView.Adapter<ChoosePayAddressAdapter.myViewHolder> {

    private Context context;
    private List<Boolean> isClicks;
    private List<AddressEvent> data;

    public ChoosePayAddressAdapter(Context context, List<AddressEvent> data) {
        this.context = context;
        this.data = data;

        isClicks = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            isClicks.add(false);
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tetWalletName;
        TextView tetWalletType;
        RelativeLayout relBackground;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tetWalletName = itemView.findViewById(R.id.tet_WalletName);
            tetWalletType = itemView.findViewById(R.id.tet_WalletType);
            relBackground = itemView.findViewById(R.id.rel_background);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.chooseaddress, null);
        return new myViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tetWalletName.setText(data.get(position).getName());
        if ("standard".equals(data.get(position).getType())) {
            holder.tetWalletType.setText(R.string.software_wallet);
        } else {
            holder.tetWalletType.setText(data.get(position).getType());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition(); // 1
                for (int i = 0; i < isClicks.size(); i++) {
                    isClicks.set(i, false);
                }
                isClicks.set(position, true);
                notifyDataSetChanged();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position); // 2
                }
            }
        });

        holder.itemView.setTag(holder.tetWalletName);
        holder.itemView.setTag(holder.tetWalletType);

        if (isClicks.get(position)) {
            holder.relBackground.setBackgroundColor(Color.parseColor("#F6F7F8"));
        } else {
            holder.relBackground.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
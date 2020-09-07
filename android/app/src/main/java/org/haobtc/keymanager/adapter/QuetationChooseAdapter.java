package org.haobtc.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.bean.CNYBean;

import java.util.ArrayList;

public class QuetationChooseAdapter extends RecyclerView.Adapter<QuetationChooseAdapter.myViewHolder> {
    private Context context;
    private ArrayList<CNYBean> exchangeList;

    public QuetationChooseAdapter(Context context, ArrayList<CNYBean> exchangeList, int exchange) {
        this.context = context;
        this.exchangeList = exchangeList;
        if (exchangeList.size() - 1 >= exchange) {
            this.exchangeList.get(exchange).setStatus(true);
        }
    }


    public class myViewHolder extends BaseViewHolder {
        TextView tetWalletName;

        public myViewHolder(View view) {
            super(view);
            tetWalletName = view.findViewById(R.id.tet_WalletName);
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
        holder.tetWalletName.setText(exchangeList.get(position).getName());
        holder.tetWalletName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < exchangeList.size(); i++) {
                    exchangeList.get(i).setStatus(false);
                }
                exchangeList.get(position).setStatus(true);
                onLisennorClick.itemClick(position);
                notifyDataSetChanged();

            }
        });
        if (exchangeList.get(position).isStatus()) {
            holder.tetWalletName.setTextColor(context.getColor(R.color.button_bk_disableok));
//            holder.tet_types.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.tetWalletName.setTextColor(context.getColor(R.color.text));
        }

    }

    @Override
    public int getItemCount() {
        if (exchangeList != null) {
            return exchangeList.size();
        } else {
            return 0;
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

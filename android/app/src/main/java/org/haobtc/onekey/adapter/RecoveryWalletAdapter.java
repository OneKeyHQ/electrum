package org.haobtc.onekey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.event.WalletAddressEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaomin
 */
public class RecoveryWalletAdapter extends RecyclerView.Adapter<RecoveryWalletAdapter.myViewHolder> {
    private Context context;
    private ArrayList<BalanceInfo> walletList;
    private Map<Integer, Boolean> checkStatus;

    public RecoveryWalletAdapter(Context context, ArrayList<BalanceInfo> walletList) {
        this.context = context;
        this.walletList = walletList;
        initData();
    }

    private void initData() {
        checkStatus = new HashMap<>();
        for (int i = 0; i < walletList.size(); i++) {
            checkStatus.put(i, true);//
        }
    }

    //Get the final map storage data
    public Map<Integer, Boolean> getMap() {
        return checkStatus;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tetWalletName, textWalletBalance;
        CheckBox checkbox;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tetWalletName = itemView.findViewById(R.id.text_wallet_name);
            textWalletBalance = itemView.findViewById(R.id.text_wallet_balance);
            checkbox = itemView.findViewById(R.id.check_wallet);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.choose_hd_wallet_item, null);
        return new myViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tetWalletName.setText(walletList.get(position).getLabel());
        String strBalance = walletList.get(position).getBalance();
        if (strBalance.contains("(")) {
            String balance = strBalance.substring(0, strBalance.indexOf("("));
            holder.textWalletBalance.setText(balance);
        } else {
            holder.textWalletBalance.setText(walletList.get(position).getBalance());
        }

        holder.checkbox.setOnCheckedChangeListener(null);
        if (checkStatus.get(position) != null) {
            holder.checkbox.setChecked(checkStatus.get(position));
        }
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.put(position, isChecked);
            }
        });
        if (checkStatus.get(position) == null) {
            checkStatus.put(position, true);
        }
    }

    @Override
    public int getItemCount() {
        return walletList == null ? 0 : walletList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

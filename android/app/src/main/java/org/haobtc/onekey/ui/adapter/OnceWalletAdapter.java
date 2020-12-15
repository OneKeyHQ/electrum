package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liyan
 */
public class OnceWalletAdapter extends RecyclerView.Adapter<OnceWalletAdapter.ViewHolder> {
    private Context context;
    private List<BalanceInfo> walletList;
    private Map<String, CheckBox> selectMap;

    public OnceWalletAdapter(Context context, List<BalanceInfo> walletList) {
        this.context = context;
        this.walletList = walletList;
        this.selectMap = new HashMap<>();
    }

    public Map<String, CheckBox> getSelectMap() {
        return selectMap;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tetWalletName, textWalletBalance;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tetWalletName = itemView.findViewById(R.id.text_wallet_name);
            textWalletBalance = itemView.findViewById(R.id.text_wallet_balance);
            checkbox = itemView.findViewById(R.id.check_wallet);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.choose_hd_wallet_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tetWalletName.setText(walletList.get(position).getLabel());
        holder.textWalletBalance.setText(walletList.get(position).getBalance());
        selectMap.put(walletList.get(position).getName(), holder.checkbox);
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }
}

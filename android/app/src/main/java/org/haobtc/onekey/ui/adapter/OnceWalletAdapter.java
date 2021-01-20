package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfoDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liyan
 */
public class OnceWalletAdapter extends RecyclerView.Adapter<OnceWalletAdapter.ViewHolder> {
    private Context context;
    private List<BalanceInfoDTO> walletList;
    private Map<String, CheckBox> selectMap;
    private OnItemClickListener mOnItemClickListener;

    public OnceWalletAdapter(Context context, List<BalanceInfoDTO> walletList) {
        this.context = context;
        this.walletList = walletList;
        this.selectMap = new HashMap<>();
    }

    public Map<String, CheckBox> getSelectMap() {
        return selectMap;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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
        BalanceInfoDTO item = walletList.get(position);
        String strBalance;
        if (item.getWallets().size() > 0) {
            strBalance = item.getWallets().get(0).getBalance();
        } else {
            strBalance = "0";
        }

        holder.tetWalletName.setText(item.getLabel());
        holder.textWalletBalance.setText(strBalance);
        if ("BTC-1".equals(walletList.get(position).getLabel())) {
            holder.checkbox.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_not_check));
            holder.checkbox.setChecked(true);
            holder.checkbox.setOnCheckedChangeListener(((buttonView, isChecked) ->
            {
                if (!isChecked) {
                    buttonView.setChecked(true);
                }
                Toast.makeText(context, R.string.not_allow_un_check, Toast.LENGTH_SHORT).show();
            }));
        } else {
            holder.checkbox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }));
        }
        selectMap.put(walletList.get(position).getName(), holder.checkbox);
    }

    @Override
    public int getItemCount() {
        return walletList.size();
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

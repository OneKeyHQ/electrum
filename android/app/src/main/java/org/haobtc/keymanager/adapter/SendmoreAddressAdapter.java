package org.haobtc.keymanager.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.event.SendMoreAddressEvent;
import java.util.List;

public class SendmoreAddressAdapter extends RecyclerView.Adapter<SendmoreAddressAdapter.myViewHoleder> {
    private Context context;
    private List<SendMoreAddressEvent> sendMoreAddressList;

    public SendmoreAddressAdapter(Context context, List<SendMoreAddressEvent> sendMoreAddressList) {
        this.context = context;
        this.sendMoreAddressList = sendMoreAddressList;
    }

    @NonNull
    @Override
    public myViewHoleder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.vout_item, null);
        return new myViewHoleder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHoleder holder, int position) {
        SharedPreferences preferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String baseUnit = preferences.getString("base_unit", "mBTC");
        holder.tetAddress.setText(sendMoreAddressList.get(position).getInputAddress());

        holder.tetAmount.setText(String.format("%s %s", sendMoreAddressList.get(position).getInputAmount(),baseUnit));
        holder.imgDelete.setOnClickListener(v -> {
            if (onItemDeleteListener!=null){
                onItemDeleteListener.onItemClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (sendMoreAddressList!=null){
            return sendMoreAddressList.size();
        }else{
            return 0;
        }

    }

    public class myViewHoleder extends RecyclerView.ViewHolder {
        TextView tetAddress, tetAmount;
        ImageView imgDelete;

        public myViewHoleder(@NonNull View itemView) {
            super(itemView);
            tetAddress = itemView.findViewById(R.id.address_to);
            tetAmount = itemView.findViewById(R.id.amount_item);
            imgDelete = itemView.findViewById(R.id.remove_vout);
        }
    }

    public interface OnItemDeleteClickListener {
        void onItemClick(int position);
    }

    private OnItemDeleteClickListener onItemDeleteListener;

    public void setmOnDeleteItemClickListener(OnItemDeleteClickListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }


}

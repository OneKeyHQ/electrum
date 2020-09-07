package org.haobtc.keymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.haobtc.keymanager.R;
import java.util.ArrayList;
@Deprecated
public class CosignerAdapter extends RecyclerView.Adapter<CosignerAdapter.myViewHolder> {
    private Context context;
    private ArrayList<String> xlistData;
    private ArrayList<Integer> nameNums;

    public CosignerAdapter(Context context, ArrayList<String> listData, ArrayList<Integer> nameNums) {
        this.context = context;
        this.xlistData = listData;
        this.nameNums = nameNums;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView tetName,tetContent;
        ImageView imgdelete;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tetName=itemView.findViewById(R.id.wallet_name);
            tetContent=itemView.findViewById(R.id.textView3);
            imgdelete=itemView.findViewById(R.id.img_deletes);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.fragment_item_cosigner, null);
        return new myViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tetName.setText("联署人 "+nameNums.get(position));
        holder.tetContent.setText(xlistData.get(position));
        holder.imgdelete.setOnClickListener(v -> onItemDeteleLisoner.onClick(position));

    }

    @Override
    public int getItemCount() {
        return xlistData.size();
    }

    public interface onItemDeteleLisoner{
        void onClick(int pos);
    }
    private onItemDeteleLisoner onItemDeteleLisoner;

    public void setOnItemDeteleLisoner(CosignerAdapter.onItemDeteleLisoner onItemDeteleLisoner) {
        this.onItemDeteleLisoner = onItemDeteleLisoner;
    }
}

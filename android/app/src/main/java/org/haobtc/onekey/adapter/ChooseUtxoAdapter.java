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

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ChooseUtxoEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUtxoAdapter extends RecyclerView.Adapter<ChooseUtxoAdapter.myViewHolder> {
    private Context context;
    private ArrayList<ChooseUtxoEvent> utxoList;
    private Map<Integer, Boolean> checkStatus;
    private ArrayList<String> utxoPositionDatas;

    public ChooseUtxoAdapter(Context context, ArrayList<ChooseUtxoEvent> utxoList, ArrayList<String> utxoPositionData) {
        this.context = context;
        this.utxoList = utxoList;
        utxoPositionDatas = utxoPositionData;
        initData();
    }

    private void initData() {
        checkStatus = new HashMap<>();
        for (int i = 0; i < utxoList.size(); i++) {
            checkStatus.put(i, false);//
        }
    }

    //Get the final map storage data
    public Map<Integer, Boolean> getMap() {
        return checkStatus;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tetTrsactionHash, tetTransactionNum;
        CheckBox checkboxUtxo;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tetTrsactionHash = itemView.findViewById(R.id.tet_trsactionHash);
            tetTransactionNum = itemView.findViewById(R.id.text_transactionNum);
            checkboxUtxo = itemView.findViewById(R.id.checkbox_utxo);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.utxo_item, null);
        return new myViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.tetTrsactionHash.setText(utxoList.get(position).getAddress());
        holder.tetTransactionNum.setText(utxoList.get(position).getValue());
        holder.checkboxUtxo.setOnCheckedChangeListener(null);
        holder.checkboxUtxo.setChecked(checkStatus.get(position));
        if (utxoPositionDatas != null) {
            for (int i = 0; i < utxoPositionDatas.size(); i++) {
                if (utxoPositionDatas.get(i).equals(utxoList.get(position).getHash())) {
                    holder.checkboxUtxo.setChecked(true);
                    checkStatus.put(position, true);
                }
            }
            EventBus.getDefault().post(new ChooseUtxoEvent());
        }
        holder.checkboxUtxo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkStatus.put(position, isChecked);
                EventBus.getDefault().post(new ChooseUtxoEvent());
            }
        });
        if (checkStatus.get(position) == null) {
            checkStatus.put(position, false);
        }
    }

    @Override
    public int getItemCount() {
        return utxoList == null ? 0 : utxoList.size();
    }


}

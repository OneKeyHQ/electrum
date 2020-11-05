package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BackupWalletBean;

import java.util.List;


public class BackupWalletListAdapter extends RecyclerView.Adapter<BackupWalletListAdapter.ViewHolder> {

    public List<BackupWalletBean> mValues;
    private LayoutInflater mInflater;
    private CallBack mCallBack;


    public BackupWalletListAdapter(Context context, List<BackupWalletBean> list, CallBack callBack) {
        this.mValues = list;
        this.mCallBack = callBack;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.item_backup_wallet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mBackupWallet = mValues.get(position);
        holder.mHint.setText(String.valueOf(holder.mBackupWallet.getType()));
        holder.mName.setText(holder.mBackupWallet.getName());
        holder.mView.setOnClickListener(v -> {
            if (mCallBack != null) {
                mCallBack.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mHint;
        public final View mView;
        public BackupWalletBean mBackupWallet;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.item_name);
            mHint = view.findViewById(R.id.item_hint);
        }

    }

    public interface CallBack {
        void onItemClick(int position);
    }
}

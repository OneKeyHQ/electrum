package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.MnemonicInfo;

import java.util.List;


/**
 * @author liyan
 */
public class MnemonicsAdapter extends RecyclerView.Adapter<MnemonicsAdapter.ViewHolder> {

    public List<MnemonicInfo> mValues;
    private LayoutInflater mInflater;
    private CallBack mCallBack;


    public MnemonicsAdapter(Context context, List<MnemonicInfo> infos,CallBack callBack) {
        this.mValues = infos;
        this.mCallBack = callBack;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.item_mnemonic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mInfo = mValues.get(position);
        holder.mIndex.setText(String.valueOf(holder.mInfo.getIndex()));
        holder.mInputMnemonic.setText(holder.mInfo.getMnemonic());
        holder.mInputMnemonic.setSelection(holder.mInfo.getMnemonic().length());
        holder.mInputMnemonic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String mnemonic = s.toString();
                holder.mInfo.setMnemonic(mnemonic);
                if(mCallBack != null){
                    mCallBack.onCheckState();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIndex;
        public final EditText mInputMnemonic;
        public MnemonicInfo mInfo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIndex = view.findViewById(R.id.item_index);
            mInputMnemonic = view.findViewById(R.id.item_mnemonic);
        }

    }

    public interface CallBack{
        void onCheckState();
    }
}

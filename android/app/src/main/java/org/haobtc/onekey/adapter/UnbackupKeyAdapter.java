package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;

import java.util.List;

public class UnbackupKeyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public UnbackupKeyAdapter(@Nullable List<String> data) {
        super(R.layout.un_backup_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        String keyName = mContext.getString(R.string.now_wallet) + item + mContext.getString(R.string.no_backup);
        helper.setText(R.id.test_un_backup_name, keyName);
        helper.addOnClickListener(R.id.test_go_to_backup);
    }
}

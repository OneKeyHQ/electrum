package org.haobtc.keymanager.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;

import java.util.List;

//
// Created by liyan on 2020/5/27.
//
public class BixinkeyBackupAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public BixinkeyBackupAdapter( @Nullable List<String> data) {
        super(R.layout.bixinkey_check_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        if (!TextUtils.isEmpty(item.split(":", 3)[1])){
            helper.setText(R.id.tet_keyName, item.split(":", 3)[1]);
        } else {
            helper.setText(R.id.tet_keyName,"BixinKey");
        }

        helper.addOnClickListener(R.id.relativeLayout_bixinkey);
        helper.addOnClickListener(R.id.linear_delete);
    }
}

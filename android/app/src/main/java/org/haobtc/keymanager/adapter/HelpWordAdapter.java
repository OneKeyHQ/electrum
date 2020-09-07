package org.haobtc.keymanager.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;

import java.util.List;

public class HelpWordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public HelpWordAdapter(@Nullable List<String> data) {
        super(R.layout.creatwallet_help_word_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tet_helpWord,item);

    }
}

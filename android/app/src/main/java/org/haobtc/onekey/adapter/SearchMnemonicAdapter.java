package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;

import java.util.List;

public class SearchMnemonicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SearchMnemonicAdapter(@Nullable List<String> data) {
        super(R.layout.search_mnemonic_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text_word, item);
    }
}

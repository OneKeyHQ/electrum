package org.haobtc.keymanager.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.event.WalletDetailBixinKeyEvent;

import java.util.List;

public class WalletDetailKeyAdapter extends BaseQuickAdapter<WalletDetailBixinKeyEvent, BaseViewHolder> {
    public WalletDetailKeyAdapter( @Nullable List<WalletDetailBixinKeyEvent> data) {
        super(R.layout.wallet_detail_key_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletDetailBixinKeyEvent item) {
        helper.setText(R.id.tet_publicperson_item_id,item.getLabel());
    }
}

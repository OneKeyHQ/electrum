package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.event.AddBixinKeyEvent;

import java.util.List;

public class AddBixinKeyAdapter extends BaseQuickAdapter<AddBixinKeyEvent, BaseViewHolder> {
    public AddBixinKeyAdapter(@Nullable List<AddBixinKeyEvent> data) {
        super(R.layout.public_wallet_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddBixinKeyEvent item) {
        helper.setText(R.id.tet_Walletname,item.getKeyname()).setText(R.id.tet_AddBixinkey,item.getKeyaddress());
        helper.addOnClickListener(R.id.img_deleteKey);
    }
}

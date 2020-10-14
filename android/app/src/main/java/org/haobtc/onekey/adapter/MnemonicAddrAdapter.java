package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.event.MnemonicAddrEvent;

import java.util.List;

public class MnemonicAddrAdapter extends BaseQuickAdapter<MnemonicAddrEvent, BaseViewHolder> {


    public MnemonicAddrAdapter(@Nullable List<MnemonicAddrEvent> data) {
        super(R.layout.mnemonic_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MnemonicAddrEvent item) {
        helper.setText(R.id.text_wallet_type,item.getType()).setText(R.id.text_wallet_address,item.getAddress());

    }
}

package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.event.WalletAddressEvent;

import java.util.List;

public class WalletAddressAdapter extends BaseQuickAdapter<WalletAddressEvent, BaseViewHolder> {
    public WalletAddressAdapter(@Nullable List<WalletAddressEvent> data) {
        super(R.layout.wallet_addr, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletAddressEvent item) {
        String balance = item.getBalance();
        if (balance.contains("(")) {
            String balanceNum = balance.substring(0, balance.indexOf("("));
            helper.setText(R.id.tet_keyName, item.getAddress()).setText(R.id.text_amount, balanceNum);
        } else {
            helper.setText(R.id.tet_keyName, item.getAddress()).setText(R.id.text_amount, item.getBalance());
        }

    }
}

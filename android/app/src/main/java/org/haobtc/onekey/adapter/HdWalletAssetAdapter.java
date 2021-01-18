package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.common.base.Strings;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.HdWalletAllAssetBean;

import java.util.List;

public class HdWalletAssetAdapter extends BaseQuickAdapter<HdWalletAllAssetBean.WalletInfoBean, BaseViewHolder> {
    public HdWalletAssetAdapter(@Nullable List<HdWalletAllAssetBean.WalletInfoBean> data) {
        super(R.layout.all_assets_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HdWalletAllAssetBean.WalletInfoBean item) {
        helper.setText(R.id.text_wallet_name, item.getName()).setText(R.id.text_balance, item.getBtc());
        String strFiat = item.getFiat();
        if (!Strings.isNullOrEmpty(strFiat)) {
            String fiat = strFiat.substring(0, strFiat.indexOf(" "));
            if (strFiat.contains("CNY")) {
                helper.setText(R.id.text_fiat, "≈ ￥ " + fiat);
            } else if (strFiat.contains("USD")) {
                helper.setText(R.id.text_fiat, "≈ $ " + fiat);
            } else {
                helper.setText(R.id.text_fiat, strFiat);
            }
        }
    }
}

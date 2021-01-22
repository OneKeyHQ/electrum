package org.haobtc.onekey.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.common.base.Strings;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.HdWalletAllAssetBean;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean;

import java.util.List;

public class HdWalletAssetAdapter extends BaseQuickAdapter<WalletBalanceBean, BaseViewHolder> {
    private final SystemConfigManager mSystemConfigManager;

    public HdWalletAssetAdapter(Context context, @Nullable List<WalletBalanceBean> data) {
        super(R.layout.all_assets_item, data);
        mSystemConfigManager = new SystemConfigManager(context.getApplicationContext());
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletBalanceBean item) {
        Drawable drawable;
        switch (item.getCoinType()) {
            default:
            case BTC:
                drawable = ResourcesCompat.getDrawable(helper.itemView.getResources(), R.drawable.token_btc, null);
                break;
            case ETH:
                drawable = ResourcesCompat.getDrawable(helper.itemView.getResources(), R.drawable.token_eth, null);
                break;
        }
        helper.setImageDrawable(R.id.imageView, drawable);
        helper.setText(R.id.text_wallet_name, item.getName()).setText(R.id.text_balance, item.getBalance());
        String strFiat = item.getBalanceFiat();
        if (!Strings.isNullOrEmpty(strFiat)) {
            helper.setText(R.id.text_fiat, "≈ " + mSystemConfigManager.getCurrentFiatSymbol() + " " + strFiat);
        }
    }
}

package org.haobtc.onekey.utils;

import android.content.Context;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.AssetBean;

import java.util.ArrayList;
import java.util.List;

public final class CoinUtils {

    private CoinUtils() {
    }

    public static List<AssetBean> getSupportCoins(Context context) {
        AssetBean btc = new AssetBean(context.getResources().getDrawable(R.drawable.token_btc, null)
                , context.getString(R.string.coin_btc));
        AssetBean eth = new AssetBean(context.getResources().getDrawable(R.drawable.token_eth, null)
                , context.getString(R.string.coin_eth));
        AssetBean eos = new AssetBean(context.getResources().getDrawable(R.drawable.token_eos, null)
                , context.getString(R.string.coin_eos));
        List<AssetBean> list = new ArrayList<>();
        list.add(btc);
        list.add(eth);
        list.add(eos);
        return list;
    }
}

package org.haobtc.onekey.utils;

import android.content.Context;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;

import java.util.ArrayList;
import java.util.List;

public final class CoinUtils {

    private CoinUtils() {
    }

    public static List<CoinBean> getSupportCoins(Context context) {
        CoinBean btc = new CoinBean(context.getResources().getDrawable(R.drawable.token_btc, null)
                , context.getString(R.string.coin_btc));
        CoinBean eth = new CoinBean(context.getResources().getDrawable(R.drawable.token_eth, null)
                , context.getString(R.string.coin_eth));
        CoinBean eos = new CoinBean(context.getResources().getDrawable(R.drawable.token_eos, null)
                , context.getString(R.string.coin_eos));
        List<CoinBean> list = new ArrayList<>();
        list.add(btc);
        list.add(eth);
        list.add(eos);
        return list;
    }
}

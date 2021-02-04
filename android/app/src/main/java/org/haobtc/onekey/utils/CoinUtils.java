package org.haobtc.onekey.utils;

import java.util.ArrayList;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;

public final class CoinUtils {

    private CoinUtils() {}

    public static List<CoinBean> getSupportCoins() {
        CoinBean btc = new CoinBean(R.drawable.token_btc, R.string.coin_btc);
        CoinBean eth = new CoinBean(R.drawable.token_eth, R.string.coin_eth);
        List<CoinBean> list = new ArrayList<>();
        list.add(btc);
        list.add(eth);
        return list;
    }
}

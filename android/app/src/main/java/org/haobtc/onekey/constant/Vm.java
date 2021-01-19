package org.haobtc.onekey.constant;

import androidx.annotation.StringDef;

import org.haobtc.onekey.BuildConfig;

/**
 * 存放钱包运行时状态
 *
 * @author Onekey@QuincySx
 * @create 2021-01-15 6:08 PM
 */
public class Vm {
    public enum CoinType {
        BTC("btc", true), ETH("eth", true);

        public final String name;
        public final boolean enable;

        CoinType(String name, boolean enable) {
            this.name = name;
            this.enable = enable;
        }
    }

    @StringDef
    public @interface PyenvETHNetworkType {
        String MainNet = "mainnet";
        String TestNet = "testnet";
    }

    @PyenvETHNetworkType
    public static String getEthNetwork() {
        if (BuildConfig.net_type.equals("MainNet")) {
            return PyenvETHNetworkType.MainNet;
        } else {
            return PyenvETHNetworkType.TestNet;
        }
    }
}

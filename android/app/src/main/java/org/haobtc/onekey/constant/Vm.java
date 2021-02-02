package org.haobtc.onekey.constant;

import android.text.TextUtils;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
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
        BTC("btc", true),
        ETH("eth", true);

        public final String coinName;
        public final boolean enable;

        CoinType(String coinName, boolean enable) {
            this.coinName = coinName;
            this.enable = enable;
        }

        public static CoinType convert(String coinName) {
            for (CoinType item : CoinType.values()) {
                if (item.coinName.equals(coinName)) {
                    return item;
                }
            }
            return CoinType.BTC;
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

    @StringDef
    public @interface BTCNetworkType {
        String MainNet = "mainnet";
        String TestNet = "testnet";
        String RegTest = "regtest";
    }

    @PyenvETHNetworkType
    public static String getBTCNetwork() {
        if (BuildConfig.net_type.equals("MainNet")) {
            return BTCNetworkType.MainNet;
        } else if (BuildConfig.net_type.equals("TestNet")) {
            return BTCNetworkType.TestNet;
        } else {
            return BTCNetworkType.RegTest;
        }
    }

    @IntDef({
        WalletType.MAIN,
        WalletType.STANDARD,
        WalletType.IMPORT_WATCH,
        WalletType.IMPORT_PRIVATE,
        WalletType.HARDWARE
    })
    public @interface WalletType {
        /** HD 派生钱包 */
        int MAIN = 0;
        /** 创建的独立钱包，助记词导入的钱包。 */
        int STANDARD = 1;
        /** 通过地址导入的观察钱包 */
        int IMPORT_WATCH = 2;
        /** 通过私钥导入的钱包 */
        int IMPORT_PRIVATE = 3;

        /** 硬件钱包 */
        int HARDWARE = 4;
    }

    @WalletType
    public static int convertWalletType(@Nullable String type) {
        if (TextUtils.isEmpty(type)) {
            return WalletType.STANDARD;
        }
        if (type.contains("derived-standard")) {
            return WalletType.MAIN;
        } else if (type.contains("private-standard")) {
            return WalletType.IMPORT_PRIVATE;
        } else if (type.contains("watch-standard")) {
            return WalletType.IMPORT_WATCH;
        } else if (type.contains("hw-derived")) {
            return WalletType.HARDWARE;
        } else if (type.contains("standard")) {
            return WalletType.STANDARD;
        } else {
            return WalletType.STANDARD;
        }
    }

    public static Vm.CoinType convertCoinType(@Nullable String type) {
        if (TextUtils.isEmpty(type)) {
            return CoinType.BTC;
        }
        if (type.contains("btc")) {
            return Vm.CoinType.BTC;
        } else if (type.contains("eth")) {
            return Vm.CoinType.ETH;
        } else {
            return Vm.CoinType.BTC;
        }
    }
}

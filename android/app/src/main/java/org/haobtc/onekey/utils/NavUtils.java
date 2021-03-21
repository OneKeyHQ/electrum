package org.haobtc.onekey.utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.LunchActivity;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.bean.Assets;
import org.haobtc.onekey.bean.ERC20Assets;
import org.haobtc.onekey.business.blockBrowser.BlockBrowserManager;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportWalletSetNameActivity;
import org.haobtc.onekey.onekeys.homepage.process.*;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.jetbrains.annotations.NotNull;

/** @Description: 页面跳转的管理类 @Author: peter Qin @CreateDate: 2020/12/16$ 4:12 PM$ */
public class NavUtils {

    public static void gotoMainActivityTask(Context context, boolean isNewTask, boolean reset) {
        Intent intent = new Intent(context, HomeOneKeyActivity.class);
        if (reset) {
            intent.putExtra(HomeOneKeyActivity.EXT_RESTART, true);
        }
        if (isNewTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void gotoMainActivityTask(Context context, boolean isNewTask) {
        gotoMainActivityTask(context, isNewTask, false);
    }

    public static void reSetApp(Context mContext) {
        Intent intent = new Intent(mContext, LunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        System.exit(0);
    }

    /**
     * 跳转到导入私钥的设置名称页面
     *
     * @param context
     * @param purpose
     */
    public static void gotoImportWalletSetNameActivity(Context context, int purpose) {
        ImportWalletSetNameActivity.gotoImportWalletSetNameActivity(context, purpose);
    }

    public static void gotoSoftPassActivity(Context context, int from) {
        SoftPassActivity.start(context, from);
    }

    /**
     * after scan go into transfer
     *
     * @param context
     * @param walletID
     * @param address
     * @param amount
     * @param vm
     */
    public static void afterScanGotoTransferActivity(
            Context context,
            @NotNull String walletID,
            @Nullable String address,
            @Nullable String amount,
            Vm.CoinType vm) {
        if (vm == Vm.CoinType.BTC) {
            SendHdActivity.start(context, walletID, address, amount);
        } else {
            SendEthActivity.start(context, walletID, address, amount);
        }
    }

    /**
     * @param context
     * @param walletID BTC useless
     * @param assetID  BTC useless
     * @param vm       type
     */
    public static void gotoTransferActivity(
            Context context, @NotNull String walletID, int assetID, Vm.CoinType vm) {
        if (vm.chainType.equalsIgnoreCase(Vm.CoinType.BTC.chainType)) {
            SendHdActivity.start(context, walletID, assetID);
        } else if (vm.chainType.equalsIgnoreCase(Vm.CoinType.ETH.chainType)) {
            SendEthActivity.start(context, walletID, assetID);
        }
    }

    public static void gotoTransactionDetails(
            Context context, Vm.CoinType coinType, String txId, String txTime
    ) {
        if(coinType.chainType.equalsIgnoreCase(Vm.CoinType.BTC.chainType)){
            DetailTransactionActivity.startRawTx(context, txId);
        }else if(coinType.chainType.equalsIgnoreCase(Vm.CoinType.ETH.chainType)){
            DetailETHTransactionActivity.start(
                    context, coinType, txId, txTime);
        }
    }
}

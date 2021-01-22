package org.haobtc.onekey.bean

import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean

/**
 * 全部钱包余额
 * 软件业务使用的实体类
 */
data class AllWalletBalanceBean(
    val allBalance: String,
    val walletInfo: List<WalletBalanceBean>
)

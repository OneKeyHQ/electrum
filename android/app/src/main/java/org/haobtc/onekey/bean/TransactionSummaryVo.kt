package org.haobtc.onekey.bean

import org.haobtc.onekey.constant.Vm

/**
 * 交易记录信息简要
 */
class TransactionSummaryVo(
    val coinType: Vm.CoinType,
    val txId: String,
    val isMine: Boolean,
    val type: String,
    val address: String,
    val date: String,
    val status: String,
    val amount: String,
    val amountUnit: String,
    val amountFiat: String,
    val amountFiatUnit: String
)

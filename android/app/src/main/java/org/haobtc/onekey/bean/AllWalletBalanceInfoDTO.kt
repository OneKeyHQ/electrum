package org.haobtc.onekey.bean

import com.google.gson.annotations.SerializedName

data class AllWalletBalanceInfoDTO(
    @SerializedName("all_balance")
    val allBalance: String,
    @SerializedName("wallet_info")
    val walletInfo: List<BalanceInfoDTO>
)

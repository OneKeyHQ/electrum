package org.haobtc.onekey.bean

import com.google.gson.annotations.SerializedName
import org.haobtc.onekey.viewmodel.AppWalletViewModel.DEF_WALLET_FIAT_BALANCE


data class BalanceBroadcastEventBean(
    @SerializedName("address")
    val address: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("coin")
    val coin: String,
    @SerializedName("fiat")
    private val fiat: String
) {
  fun getFiat(): String {
    return fiat.split(" ").getOrElse(0) { DEF_WALLET_FIAT_BALANCE.balance }
  }

  fun getFiatUnit(): String {
    return fiat.split(" ").getOrElse(1) { DEF_WALLET_FIAT_BALANCE.unit }
  }
}

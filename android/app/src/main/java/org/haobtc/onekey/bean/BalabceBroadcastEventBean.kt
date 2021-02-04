package org.haobtc.onekey.bean

import com.google.gson.annotations.SerializedName


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
    return fiat.split(" ").getOrElse(0) { "0.00" }
  }

  fun getFiatUnit(): String {
    return fiat.split(" ").getOrElse(1) { "CNY" }
  }
}

package org.haobtc.onekey.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.haobtc.onekey.constant.Vm
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * 交易记录信息简要
 */
data class TransactionSummaryVo(
    @SerializedName("address")
    val address: String,
    @SerializedName("amount")
    val amountStr: String,
    @SerializedName("coin")
    val assetsName: String,
    @SerializedName("confirmations")
    val confirmations: Int,
    @SerializedName("date")
    val dateStr: String,
    @SerializedName("fee")
    val fee: String,
    @SerializedName("fee_fiat")
    val feeFiat: String,
    @SerializedName("fiat")
    val fiat: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("input_addr")
    val inputAddr: List<String>,
    @SerializedName("is_mine")
    val isMine: Boolean,
    @SerializedName("output_addr")
    val outputAddr: List<String>,
    @SerializedName("show_status")
    val showStatus: List<Any>,
    @SerializedName("tx_hash")
    val txId: String,
    @SerializedName("tx_status")
    val status: String,
    @SerializedName("type")
    val type: String
) {
  @Expose(serialize = true, deserialize = false)
  var coinType: Vm.CoinType = Vm.CoinType.BTC

  fun getDate(): String = getFormatData()

  fun getAmount(): String = getAmountSplit().getOrNull(0)?.let {
    BigDecimal(it).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
  } ?: "0"

  fun getAmountUnit(): String = getAmountSplit().getOrNull(1)?.trim() ?: coinType.coinName

  fun getAmountFiat(): String = getAmountSplit().getOrNull(2)?.trim()?.replace("(", "") ?: "0.00"

  fun getAmountFiatUnit(): String = getAmountSplit().getOrNull(3)?.trim()?.replace(")", "") ?: "CNY"

  private fun getAmountSplit(): List<String> = amountStr.split(" ")

  private fun getFormatData(): String {
    return if (dateStr.contains("-")) {
      val str = dateStr.substring(5)
      val strs = str.substring(0, str.length - 3)
      strs.replace("-", "/")
    } else {
      dateStr
    }
  }
}

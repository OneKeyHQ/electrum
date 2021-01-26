package org.haobtc.onekey.bean

import com.google.gson.annotations.SerializedName


/**
 * 以太坊交易详情
 *
 * @author Onekey@QuincySx
 * @create 2021-01-26 7:43 PM
 */
data class TransactionETHInfoBean(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("can_broadcast")
    val canBroadcast: Boolean,
    @SerializedName("cosigner")
    val cosigner: List<Any>,
    @SerializedName("description")
    val description: String,
    @SerializedName("fee")
    val fee: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("input_addr")
    val inputAddr: List<String>?,
    @SerializedName("output_addr")
    val outputAddr: List<String>?,
    @SerializedName("sign_status")
    val signStatus: Any,
    @SerializedName("tx")
    val tx: String,
    @SerializedName("tx_status")
    val txStatus: String,
    @SerializedName("txid")
    val txid: String,
    @SerializedName("show_status")
    val showStatus: List<Any>
)

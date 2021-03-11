package org.haobtc.onekey.bean

import com.google.gson.annotations.SerializedName

data class RPCInfoBean(
    @SerializedName("chain_id")
    val chainId: Int,
    @SerializedName("rpc")
    val rpc: String
)

data class SignTxResponseBean(
    @SerializedName("raw")
    val raw: String?,
    @SerializedName("tx")
    val tx: Tx
)

data class Tx(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("gas")
    val gas: String,
    @SerializedName("gasPrice")
    val gasPrice: String,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("nonce")
    val nonce: String,
    @SerializedName("r")
    val r: String,
    @SerializedName("s")
    val s: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("v")
    val v: String,
    @SerializedName("value")
    val value: String
)

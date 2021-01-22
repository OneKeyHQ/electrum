package org.haobtc.onekey.business.chain.ethereum

/**
 * 以太坊交易概括
 * Transaction Record Summary
 */
data class EthTxSummaryBean(
    val to_address: String,
    val from_address: String,
    val received: String,
    val sent: String,
    val time: String,
    val value_eth: String
)

package org.haobtc.onekey.business.chain

import androidx.annotation.StringDef

@StringDef(TransactionListType.ALL, TransactionListType.RECEIVE, TransactionListType.SEND)
annotation class TransactionListType {
  companion object {
    const val ALL = "all"
    const val RECEIVE = "receive"
    const val SEND = "send"
  }
}

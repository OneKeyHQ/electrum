package org.haobtc.onekey.business.blockBrowser

interface BlockBrowser {
  fun uniqueTag(): String

  fun showName(): String

  fun url(): String

  fun browseAddressUrl(address: String): String

  fun browseTransactionDetailsUrl(txHash: String): String

  fun browseBlockUrl(block: String): String
}

interface ETHBlockBrowser : BlockBrowser

interface BTCBlockBrowser : BlockBrowser

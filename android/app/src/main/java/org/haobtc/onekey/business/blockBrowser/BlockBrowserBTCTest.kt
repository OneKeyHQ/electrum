package org.haobtc.onekey.business.blockBrowser

fun getBlockBrowserBTCTest(): Map<String, BlockBrowserBTCTest> {
  return arrayListOf(
      BlockBrowserBTCTest.BlockCypher(),
      BlockBrowserBTCTest.BlockChain(),
      BlockBrowserBTCTest.BitPay(),
      BlockBrowserBTCTest.SoChain(),
      BlockBrowserBTCTest.BitAps(),
      BlockBrowserBTCTest.BlockExplorer(),
  ).associateBy { it.uniqueTag() }
}

abstract class BlockBrowserBTCTest(val url: String) : BTCBlockBrowser {
  override fun showName() = url()
  override fun url() = url
  override fun uniqueTag() = "${blockBrowserTag()}BTCTestNet"
  abstract fun blockBrowserTag(): String

  class BlockCypher : BlockBrowserBTCTest("https://live.blockcypher.com/") {
    override fun browseAddressUrl(address: String) = "${url()}btc-testnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc-testnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc-testnet/block/$block"
    override fun blockBrowserTag() = "BlockCypher"
    override fun uniqueTag() = BlockBrowserManager.BLOCK_BROWSER_DEFAULT
  }

  class BlockChain : BlockBrowserBTCTest("https://www.blockchain.com/") {
    override fun browseAddressUrl(address: String) = "${url()}btc-testnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc-testnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc-testnet/block/$block"
    override fun blockBrowserTag() = "BlockChain"
  }

  class BitPay : BlockBrowserBTCTest("https://bitpay.com/") {
    override fun browseAddressUrl(address: String) = "${url()}insight/#/BTC/testnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}insight/#/BTC/testnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}insight/#/BTC/testnet/block/$block"
    override fun blockBrowserTag() = "BitPay"
  }

  class SoChain : BlockBrowserBTCTest("https://sochain.com/") {
    override fun browseAddressUrl(address: String) = "${url()}address/BTCTEST/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}tx/BTCTEST/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/BTCTEST/$block"
    override fun blockBrowserTag() = "SoChain"
  }

  class BitAps : BlockBrowserBTCTest("https://tbtc.bitaps.com/") {
    override fun browseAddressUrl(address: String) = "${url()}$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}$txHash"
    override fun browseBlockUrl(block: String) = "${url()}$block"
    override fun blockBrowserTag() = "BitAps"
  }

  class BlockExplorer : BlockBrowserBTCTest("https://blockexplorer.one/") {
    override fun browseAddressUrl(address: String) = "${url()}btc/testnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc/testnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc/testnet/blockId/$block"
    override fun blockBrowserTag() = "BlockExplorer"
  }
}

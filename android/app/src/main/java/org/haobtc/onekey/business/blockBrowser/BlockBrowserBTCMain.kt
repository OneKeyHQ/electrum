package org.haobtc.onekey.business.blockBrowser

fun getBlockBrowserBTCMain(): Map<String, BlockBrowserBTCMain> {
  return arrayListOf(
      BlockBrowserBTCMain.BTC(),
      BlockBrowserBTCMain.BlockIn(),
      BlockBrowserBTCMain.BlockChain(),
      BlockBrowserBTCMain.OkLink(),
      BlockBrowserBTCMain.BlockChair(),
      BlockBrowserBTCMain.BitInfoCharts(),
      BlockBrowserBTCMain.TradeBlock(),
      BlockBrowserBTCMain.BlockCypher(),
      BlockBrowserBTCMain.BitPay(),
      BlockBrowserBTCMain.SoChain(),
      BlockBrowserBTCMain.SmartBit(),
      BlockBrowserBTCMain.BitAps(),
      BlockBrowserBTCMain.BlockExplorer(),
  ).associateBy { it.uniqueTag() }
}

abstract class BlockBrowserBTCMain(val url: String) : BTCBlockBrowser {
  override fun browseAddressUrl(address: String) = "${url()}bitcoin/address/$address"
  override fun browseTransactionDetailsUrl(txHash: String) = "${url()}bitcoin/tx/$txHash"
  override fun browseBlockUrl(block: String) = "${url()}bitcoin/block/$block"
  override fun showName() = url()
  override fun url() = url
  override fun uniqueTag() = "${blockBrowserTag()}BTCMain"
  abstract fun blockBrowserTag(): String

  class BTC : BlockBrowserBTCMain("https://btc.com/") {
    override fun browseAddressUrl(address: String) = url() + address
    override fun browseTransactionDetailsUrl(txHash: String) = url() + txHash
    override fun browseBlockUrl(block: String) = "${url()}$block"
    override fun uniqueTag() = BlockBrowserManager.BLOCK_BROWSER_DEFAULT
    override fun blockBrowserTag() = "btc"
  }

  class BlockIn : BlockBrowserBTCMain("https://explorer.poolin.com/") {
    override fun browseAddressUrl(address: String) = "${url()}address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/$block"
    override fun blockBrowserTag() = "BlockIn"
  }

  class BlockChain : BlockBrowserBTCMain("https://www.blockchain.com/") {
    override fun browseAddressUrl(address: String) = "${url()}btc/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc/block/$block"
    override fun blockBrowserTag() = "BlockChain"
  }

  class OkLink : BlockBrowserBTCMain("https://www.oklink.com/") {
    override fun browseAddressUrl(address: String) = "${url()}btc/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc/block/$block"
    override fun blockBrowserTag() = "OkLink"
  }

  class BlockChair : BlockBrowserBTCMain("https://blockchair.com/") {
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}bitcoin/transaction/$txHash"
    override fun blockBrowserTag() = "BlockChair"
  }

  class BitInfoCharts : BlockBrowserBTCMain("https://bitinfocharts.com/") {
    override fun blockBrowserTag() = "BitInfoCharts"
  }

  class TradeBlock : BlockBrowserBTCMain("https://tradeblock.com/") {
    override fun blockBrowserTag() = "BChain"
  }

  class BlockCypher : BlockBrowserBTCMain("https://live.blockcypher.com/") {
    override fun browseAddressUrl(address: String) = "${url()}btc/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc/block/$block"
    override fun blockBrowserTag() = "BlockCypher"
  }

  class BitPay : BlockBrowserBTCMain("https://bitpay.com/") {
    override fun browseAddressUrl(address: String) = "${url()}insight/#/BTC/mainnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}insight/#/BTC/mainnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}insight/#/BTC/mainnet/block/$block"
    override fun blockBrowserTag() = "BitPay"
  }

  class SoChain : BlockBrowserBTCMain("https://sochain.com/") {
    override fun browseAddressUrl(address: String) = "${url()}address/BTC/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}tx/BTC/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/BTC/$block"
    override fun blockBrowserTag() = "SoChain"
  }

  class SmartBit : BlockBrowserBTCMain("https://www.smartbit.com.au/") {
    override fun browseAddressUrl(address: String) = "${url()}address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/$block"
    override fun blockBrowserTag() = "SmartBit"
  }

  class BitAps : BlockBrowserBTCMain("https://btc.bitaps.com/") {
    override fun browseAddressUrl(address: String) = "${url()}$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}$txHash"
    override fun browseBlockUrl(block: String) = "${url()}$block"
    override fun blockBrowserTag() = "BitAps"
  }

  class BlockExplorer : BlockBrowserBTCMain("https://blockexplorer.one/") {
    override fun browseAddressUrl(address: String) = "${url()}btc/mainnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}btc/mainnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}btc/mainnet/blockId/$block"
    override fun blockBrowserTag() = "BlockExplorer"
  }
}

package org.haobtc.onekey.business.blockBrowser

fun getBlockBrowserEthMain(): Map<String, BlockBrowserEthMain> {
  return arrayListOf(
      BlockBrowserEthMain.EtherScanCN(),
      BlockBrowserEthMain.EtherScan(),
      BlockBrowserEthMain.BlockChain(),
      BlockBrowserEthMain.BlockChair(),
      BlockBrowserEthMain.TradeBlock(),
      BlockBrowserEthMain.BitPay(),
      BlockBrowserEthMain.BitAps(),
      BlockBrowserEthMain.BlockExplorer(),
  ).associateBy { it.uniqueTag() }
}

abstract class BlockBrowserEthMain(val url: String) : ETHBlockBrowser {
  override fun browseBlockUrl(block: String) = "${url()}ethereum/block/$block"
  override fun showName() = url()
  override fun url() = url
  override fun uniqueTag() = "${blockBrowserTag()}ETHMain"
  abstract fun blockBrowserTag(): String

  class EtherScanCN : BlockBrowserEthMain("https://cn.etherscan.com/") {
    override fun browseAddressUrl(address: String) = "${url()}tx/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}address/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/$block"
    override fun uniqueTag() = BlockBrowserManager.BLOCK_BROWSER_DEFAULT
    override fun blockBrowserTag() = "EtherScanCN"
  }

  class EtherScan : BlockBrowserEthMain("https://etherscan.io/") {
    override fun browseAddressUrl(address: String) = "${url()}tx/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}address/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/$block"
    override fun blockBrowserTag() = "EtherScan"
  }

  class BlockChain : BlockBrowserEthMain("https://www.blockchain.com/") {
    override fun browseAddressUrl(address: String) = "${url()}eth/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}eth/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}eth/block/$block"
    override fun blockBrowserTag() = "BlockChain"
  }

  class BlockChair : BlockBrowserEthMain("https://blockchair.com/") {
    override fun browseAddressUrl(address: String) = "${url()}ethereum/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}ethereum/transaction/$txHash"
    override fun blockBrowserTag() = "BlockChair"
  }

  class TradeBlock : BlockBrowserEthMain("https://tradeblock.com/") {
    override fun browseAddressUrl(address: String) = "${url()}ethereum/account/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}ethereum/tx/$txHash"
    override fun blockBrowserTag() = "BChain"
  }

  class BitPay : BlockBrowserEthMain("https://bitpay.com/") {
    override fun browseAddressUrl(address: String) = "${url()}insight/#/ETH/mainnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}insight/#/ETH/mainnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}insight/#/ETH/mainnet/block/$block"
    override fun blockBrowserTag() = "BitPay"
  }

  class BitAps : BlockBrowserEthMain("https://eth.bitaps.com/") {
    override fun browseAddressUrl(address: String) = "${url()}$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}$txHash"
    override fun browseBlockUrl(block: String) = "${url()}$block"
    override fun blockBrowserTag() = "BitAps"
  }

  class BlockExplorer : BlockBrowserEthMain("https://blockexplorer.one/") {
    override fun browseAddressUrl(address: String) = "${url()}eth/mainnet/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}eth/mainnet/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}eth/mainnet/blockId/$block"
    override fun blockBrowserTag() = "BlockExplorer"
  }
}

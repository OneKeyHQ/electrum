package org.haobtc.onekey.business.blockBrowser

fun getBlockBrowserEthRopsten(): Map<String, BlockBrowserEthRopsten> {
  return arrayListOf(
      BlockBrowserEthRopsten.BitAps(),
      BlockBrowserEthRopsten.EtherScan(),
      BlockBrowserEthRopsten.BlockExplorer(),
      BlockBrowserEthRopsten.AnyBlock(),
  ).associateBy { it.uniqueTag() }
}

abstract class BlockBrowserEthRopsten(val url: String) : ETHBlockBrowser {
  override fun showName() = url()
  override fun url() = url
  override fun uniqueTag() = "${blockBrowserTag()}ETHRopsten"
  abstract fun blockBrowserTag(): String

  class BitAps : BlockBrowserEthRopsten("https://teth.bitaps.com/") {
    override fun browseAddressUrl(address: String) = "${url()}$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}$txHash"
    override fun browseBlockUrl(block: String) = "${url()}$block"
    override fun blockBrowserTag() = "BitAps"
    override fun uniqueTag() = BlockBrowserManager.BLOCK_BROWSER_DEFAULT
  }

  class EtherScan : BlockBrowserEthRopsten("https://ropsten.etherscan.io/") {
    override fun browseAddressUrl(address: String) = "${url()}tx/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}address/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}block/$block"
    override fun blockBrowserTag() = "EtherScan"
  }

  class BlockExplorer : BlockBrowserEthRopsten("https://blockexplorer.one/") {
    override fun browseAddressUrl(address: String) = "${url()}eth/ropsten/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}eth/ropsten/tx/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}eth/ropsten/blockId/$block"
    override fun blockBrowserTag() = "BlockExplorer"
  }

  class AnyBlock : BlockBrowserEthRopsten("https://explorer.anyblock.tools/") {
    override fun browseAddressUrl(address: String) = "${url()}ethereum/ethereum/ropsten/address/$address"
    override fun browseTransactionDetailsUrl(txHash: String) = "${url()}ethereum/ethereum/ropsten/transaction/$txHash"
    override fun browseBlockUrl(block: String) = "${url()}ethereum/ethereum/ropsten/block/$block"
    override fun blockBrowserTag() = "AnyBlock"
  }
}

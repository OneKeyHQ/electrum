package org.haobtc.onekey.business.wallet.bean

import org.haobtc.onekey.constant.Vm

data class WalletBalanceBean(
    val coinType: Vm.CoinType,
    val balance: String?,
    val balanceFiat: String?,
    val balanceFiaUnit: String?,
    val tokens: List<TokenBalanceBean> = arrayListOf(),
    val name: String,
    val label: String
)

interface TokenBalanceBean {
  fun getTokenID(): String
  fun getTokenName(): String
  fun getTokenBalance(): String
  fun getTokenBalanceFiat(): String?
  fun getTokenBalanceFiatUnit(): String?
}

class ETHTokenBalanceBean(
    val address: String,
    val name: String,
    val balance: String,
    val balanceFiat: String,
    val balanceFiatUnit: String,
) : TokenBalanceBean {
  override fun getTokenID() = address

  override fun getTokenName() = name

  override fun getTokenBalance() = balance

  override fun getTokenBalanceFiat() = balanceFiat

  override fun getTokenBalanceFiatUnit() = balanceFiatUnit
}

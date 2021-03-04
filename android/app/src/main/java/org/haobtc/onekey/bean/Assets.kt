package org.haobtc.onekey.bean

import androidx.annotation.Keep
import com.google.common.base.Objects
import org.haobtc.onekey.constant.Vm.CoinType
import java.math.BigDecimal
import java.util.*

@JvmField
val DEF_WALLET_FIAT_BALANCE = AssetsBalanceFiat("0", "CNY", "¥")
fun defWalletBalance(defUnit: String) = AssetsBalance(BigDecimal.ZERO, defUnit)

/**
 * 子类记得复写 hashCode
 */
@Keep
abstract class Assets(
    // 币种类型
    val coinType: CoinType,
    // 资产名称
    val name: String,
    // 资产小数位数
    val digits: Int,
    // 资产描述
    val describe: String,
    // 资产 Logo
    val logo: ImageResources,
    // 资产余额
    var balance: AssetsBalance,
    // 资产法币余额
    var balanceFiat: AssetsBalanceFiat,
) {
  fun uniqueId() = hashCode()

  abstract fun newInstance(): Assets

  override fun toString(): String {
    return "Assets(coinType=$coinType, name='$name', digits=$digits, describe='$describe', logo=$logo, balance=$balance, balanceFiat=$balanceFiat)"
  }
}

class CoinAssets @JvmOverloads constructor(
    coinType: CoinType,
    name: String,
    digits: Int,
    describe: String,
    logo: ImageResources,
    balance: AssetsBalance = defWalletBalance(coinType.defUnit),
    balanceFiat: AssetsBalanceFiat = DEF_WALLET_FIAT_BALANCE,
) : Assets(coinType, name, digits, describe, logo, balance, balanceFiat) {
  companion object {
    @JvmStatic
    fun generateUniqueId(coinType: CoinType): Int {
      return Objects.hashCode("coin", coinType.coinName)
    }
  }

  override fun hashCode(): Int {
    return generateUniqueId(coinType)
  }

  override fun newInstance(): Assets {
    return CoinAssets(
        coinType,
        name,
        digits,
        describe,
        logo,
        balance,
        balanceFiat
    )
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }
}

class ERC20Assets @JvmOverloads constructor(
    val contractAddress: String,
    name: String,
    digits: Int,
    describe: String,
    logo: ImageResources,
    balance: AssetsBalance,
    balanceFiat: AssetsBalanceFiat = DEF_WALLET_FIAT_BALANCE,
) : Assets(CoinType.ETH, name, digits, describe, logo, balance, balanceFiat) {
  companion object {
    @JvmStatic
    fun generateUniqueId(contractAddress: String, coinType: CoinType): Int {
      return Objects.hashCode("erc20",
          contractAddress.replace("0x", "").toLowerCase(Locale.ROOT),
          coinType.coinName)
    }
  }

  override fun hashCode(): Int {
    return generateUniqueId(contractAddress, coinType)
  }

  override fun newInstance(): Assets {
    return ERC20Assets(
        contractAddress,
        name,
        digits,
        describe,
        logo,
        balance,
        balanceFiat
    )
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }
}

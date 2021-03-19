package org.haobtc.onekey.onekeys.dappbrowser.ui

import org.haobtc.onekey.constant.Vm.CoinType

interface CurrentCoinTypeProvider {
  fun currentCoinType(): CoinType?
}

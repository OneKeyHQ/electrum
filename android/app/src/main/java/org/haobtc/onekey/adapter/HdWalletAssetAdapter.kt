package org.haobtc.onekey.adapter

import android.content.Context
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.common.base.Strings
import org.haobtc.onekey.R
import org.haobtc.onekey.business.wallet.SystemConfigManager
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean
import org.haobtc.onekey.constant.Vm.CoinType
import java.math.BigDecimal
import java.math.RoundingMode

class HdWalletAssetAdapter(context: Context, data: List<WalletBalanceBean?>?) : BaseQuickAdapter<WalletBalanceBean, BaseViewHolder>(R.layout.all_assets_item, data) {
  private val mSystemConfigManager by lazy {
    SystemConfigManager(context.applicationContext)
  }

  override fun convert(helper: BaseViewHolder, item: WalletBalanceBean) {
    val drawable = when (item.coinType) {
      CoinType.BTC -> ResourcesCompat.getDrawable(helper.itemView.resources, R.drawable.token_btc, null)
      CoinType.ETH -> ResourcesCompat.getDrawable(helper.itemView.resources, R.drawable.token_eth, null)
    }

    val balance = if (!TextUtils.isEmpty(item.balance) && !item.balance.equals("0")) {
      BigDecimal(item.balance)
          .setScale(8, RoundingMode.DOWN)
          .stripTrailingZeros().toPlainString()
    } else {
      "0"
    }

    helper.setImageDrawable(R.id.imageView, drawable)
    helper.setText(R.id.text_wallet_name, item.name).setText(R.id.text_balance, balance)

    val strFiat = if (!TextUtils.isEmpty(item.balanceFiat) && item.balanceFiat == "0") {
      "0.00"
    } else {
      item.balanceFiat
    }
    if (!Strings.isNullOrEmpty(strFiat)) {
      helper.setText(R.id.text_fiat, "≈ " + mSystemConfigManager.currentFiatSymbol + " " + strFiat)
    }

  }

}

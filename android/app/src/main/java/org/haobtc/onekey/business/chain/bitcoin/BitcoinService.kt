package org.haobtc.onekey.business.chain.bitcoin

import androidx.annotation.WorkerThread
import com.chaquo.python.Kwarg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.haobtc.onekey.bean.MaintrsactionlistEvent
import org.haobtc.onekey.bean.TransactionSummaryVo
import org.haobtc.onekey.business.chain.TransactionListType
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.utils.Daemon
import java.text.DecimalFormat

class BitcoinService {
  private val mGson by lazy {
    Gson()
  }

  @WorkerThread
  fun getTxList(@TransactionListType status: String = TransactionListType.ALL): List<TransactionSummaryVo> {
    val historyTx = when (status) {
      TransactionListType.ALL -> Daemon.commands.callAttr("get_all_tx_list", Kwarg("coin", Vm.CoinType.BTC.coinName))
      else -> Daemon.commands.callAttr("get_all_tx_list", status, Vm.CoinType.BTC.coinName)
    }
    return try {
      historyTx?.toString()?.let {
        val listBeans: ArrayList<TransactionSummaryVo> = ArrayList(10)
        mGson.fromJson<List<MaintrsactionlistEvent>>(it, object : TypeToken<List<MaintrsactionlistEvent>>() {}.type).forEach {
          if ("history" == it.type) {
            // format date
            val formatDate = if (it.date.contains("-")) {
              val str = it.date.substring(5)
              val strs = str.substring(0, str.length - 3)
              strs.replace("-", "/")
            } else {
              it.date
            }

            // format amount 0.012029 BTC (2,904.02 CNY)
            val amountSplit = it.amount.split(" ")
            val amountStr = amountSplit.getOrNull(0)?.let {
              val amountFix = it.trim().substring(it.trim().indexOf(".") + 1)
              if (amountFix.length > 8) {
                val dfs = DecimalFormat("0.00000000")
                dfs.format(amountFix)
              } else {
                it
              }
            } ?: "0"

            val amountUnit = amountSplit.getOrNull(1)?.trim() ?: "BTC"

            val amountFiat = amountSplit.getOrNull(2)?.trim()?.replace("(", "") ?: "0.00"

            val amountFiatUnit = amountSplit.getOrNull(3)?.trim()?.replace(")", "") ?: "CNY"

            val item = TransactionSummaryVo(Vm.CoinType.BTC, it.txHash, it.isMine, it.type, it.address, formatDate, it.txStatus, amountStr, amountUnit, amountFiat, amountFiatUnit)
            listBeans.add(item)
          }
        }
        listBeans
      } ?: arrayListOf()
    } catch (e: Exception) {
      e.printStackTrace()
      arrayListOf()
    }
  }

}

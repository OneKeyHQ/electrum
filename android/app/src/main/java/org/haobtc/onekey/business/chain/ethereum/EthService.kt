package org.haobtc.onekey.business.chain.ethereum

import androidx.annotation.WorkerThread
import com.chaquo.python.Kwarg
import com.chaquo.python.PyObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.MaintrsactionlistEvent
import org.haobtc.onekey.bean.TransactionSummaryVo
import org.haobtc.onekey.business.chain.TransactionListType
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.utils.Daemon
import org.haobtc.onekey.utils.internet.NetUtil
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class EthService {
  private val mGson by lazy {
    Gson()
  }

  private fun request(@TransactionListType status: String = TransactionListType.ALL, position: Int = 0, limit: Int = 10): PyObject? {
    return when (status) {
      TransactionListType.ALL ->
        Daemon.commands.callAttr("get_all_tx_list", Kwarg("coin", Vm.CoinType.ETH.coinName))
      else -> Daemon.commands.callAttr("get_all_tx_list", status, Vm.CoinType.ETH.coinName)
    }
  }

  @WorkerThread
  fun getTxList(@TransactionListType status: String = TransactionListType.ALL, position: Int = 0, limit: Int = 10): List<TransactionSummaryVo> {
    return try {
      val historyTx = request(status, position, limit)
      historyTx?.toString()?.let {
        val listBeans: ArrayList<TransactionSummaryVo> = ArrayList(limit)

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
              BigDecimal(it).setScale(8, RoundingMode.DOWN).stripTrailingZeros().toPlainString()
            } ?: "0"

            val amountUnit = amountSplit.getOrNull(1)?.trim() ?: "ETH"

            val amountFiat = amountSplit.getOrNull(2)?.trim()?.replace("(", "") ?: "0.00"

            val amountFiatUnit = amountSplit.getOrNull(3)?.trim()?.replace(")", "") ?: "CNY"

            val item = TransactionSummaryVo(Vm.CoinType.ETH, it.txHash, it.isMine, it.type, it.address, formatDate, it.txStatus.replace("。",""), amountStr, amountUnit, amountFiat, amountFiatUnit)
            listBeans.add(item)
          }
        }
        listBeans
      } ?: arrayListOf()
    } catch (e: Exception) {
      e.printStackTrace()
      if (e.message?.contains("ConnectionError") == true && NetUtil.getNetStatus(MyApplication.getInstance())) {
        // 及时上报 eth 区块浏览器获取交易记录不可用的信息
        CrashReport.postCatchedException(e)
      }
      arrayListOf()
    }
  }
}

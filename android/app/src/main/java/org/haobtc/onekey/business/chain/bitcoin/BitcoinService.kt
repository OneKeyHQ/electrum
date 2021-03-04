package org.haobtc.onekey.business.chain.bitcoin

import androidx.annotation.WorkerThread
import com.chaquo.python.Kwarg
import com.chaquo.python.PyObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.bugly.crashreport.CrashReport
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.TransactionSummaryVo
import org.haobtc.onekey.business.chain.TransactionListType
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.utils.Daemon
import org.haobtc.onekey.utils.internet.NetUtil

class BitcoinService {
  private val mGson by lazy {
    Gson()
  }

  private fun request(@TransactionListType status: String = TransactionListType.ALL, position: Int = 0, limit: Int = 10): PyObject? {
    return when (status) {
      TransactionListType.ALL ->
        Daemon.commands.callAttr("get_all_tx_list",
            Kwarg("coin", Vm.CoinType.BTC.callFlag),
            Kwarg("start", position),
            Kwarg("end", position + limit))
      else ->
        Daemon.commands.callAttr("get_all_tx_list", status, Vm.CoinType.BTC.callFlag, position, position + limit)
    }
  }

  @Throws(Exception::class)
  @WorkerThread
  fun getTxList(@TransactionListType status: String = TransactionListType.ALL, position: Int = 0, limit: Int = 10): List<TransactionSummaryVo> {
    return try {
      val historyTx = request(status, position, limit)
      historyTx?.toString()?.let {
        mGson.fromJson<List<TransactionSummaryVo>>(it, object : TypeToken<List<TransactionSummaryVo>>() {}.type).apply {
          forEach {
            it.coinType = Vm.CoinType.BTC
          }
        }
      } ?: arrayListOf()
    } catch (e: Exception) {
      e.printStackTrace()
      if (e.message?.contains("ConnectionError") == true && NetUtil.getNetStatus(MyApplication.getInstance())) {
        // 及时上报 btc 区块浏览器获取交易记录不可用的信息
        CrashReport.postCatchedException(e)
      }
      e.printStackTrace()
      throw e
    }
  }

}

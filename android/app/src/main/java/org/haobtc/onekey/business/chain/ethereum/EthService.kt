package org.haobtc.onekey.business.chain.ethereum

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
import java.util.*

class EthService {
  private val mGson by lazy {
    Gson()
  }

  private fun request(coinType: Vm.CoinType, @TransactionListType status: String = TransactionListType.ALL, contractAddress: String? = null, position: Int = 0, limit: Int = 10): PyObject? {
    val argList = LinkedList<Kwarg>()
    argList.add(Kwarg("coin", coinType.callFlag))
    if (status != TransactionListType.ALL) {
      argList.add(Kwarg("search_type", status))
    }
    contractAddress?.let {
      argList.add(Kwarg("contract_address", it))
    }

    return Daemon.commands.callAttr("get_all_tx_list", *argList.toTypedArray())
  }

  @Throws(Exception::class)
  @WorkerThread
  fun getTxList(coinType: Vm.CoinType, @TransactionListType status: String = TransactionListType.ALL, contractAddress: String? = null, position: Int = 0, limit: Int = 10): List<TransactionSummaryVo> {
    return try {
      val historyTx = request(coinType, status, contractAddress, position, limit)
      historyTx?.toString()?.let {
        mGson.fromJson<List<TransactionSummaryVo>>(it, object : TypeToken<List<TransactionSummaryVo>>() {}.type).apply {
          forEach {
            it.coinType = coinType
          }
        }
      } ?: arrayListOf()
    } catch (e: Exception) {
      e.printStackTrace()
      if (e.message?.contains("ConnectionError") == true && NetUtil.getNetStatus(MyApplication.getInstance())) {
        // 及时上报 eth 区块浏览器获取交易记录不可用的信息
        CrashReport.postCatchedException(e)
      }
      throw e
    }
  }
}

package org.haobtc.onekey.business.wallet

import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.RPCInfoBean
import org.haobtc.onekey.bean.SignTxResponseBean
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.exception.PyEnvException
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction
import org.haobtc.onekey.onekeys.dappbrowser.ui.SignTransactionInterface

class DappWeb3Manager {

  fun getPRCInfo(): RPCInfoBean? {
    return PyEnv.getRpcInfo(Vm.CoinType.ETH).result
  }

  fun signTxByHardware(from: String, transaction: Web3Transaction, pwd: String?, callback: SignTransactionInterface) {
    return signTx(from, transaction, pwd, MyApplication.getInstance().deviceWay, callback)
  }

  @JvmOverloads
  fun signTx(from: String, transaction: Web3Transaction, pwd: String?, mode: String? = null, callback: SignTransactionInterface) {
    Single
        .create<SignTxResponseBean> {
          val jsonTransaction = JsonObject().apply {
            addProperty("from", from)
            addProperty("to", transaction.recipient.toString())
            addProperty("gasPrice", transaction.gasPrice)
            addProperty("gas", transaction.gasLimit)
            addProperty("value", transaction.value)
            if (transaction.payload?.isNotEmpty() == true) {
              addProperty("data", transaction.payload)
            }
            if (transaction.nonce != -1L) {
              addProperty("nonce", transaction.nonce)
            }
          }
          val signTx = PyEnv.signTx(Vm.CoinType.ETH, jsonTransaction, pwd, mode)
          if (signTx.result == null || signTx.errors?.isNotEmpty() == true) {
            it.onError(RuntimeException(signTx.errors))
          } else {
            it.onSuccess(signTx.result)
          }
        }
        .map {
          if (it.raw == null) {
            throw RuntimeException("Signing failed")
          }
          it
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          callback.transactionSuccess(transaction, it.raw)
        }, {
          callback.transactionError(transaction.leafPosition, PyEnvException.convert(it))
        })
  }

  fun signMessageByHardware(from: String, messageHex: String, pwd: String?, success: (String) -> Unit, error: (Exception) -> Unit) {
    signMessage(from, messageHex, pwd, MyApplication.getInstance().deviceWay, success, error)
  }

  @JvmOverloads
  fun signMessage(from: String, messageHex: String, pwd: String?, mode: String? = null, success: (String) -> Unit, error: (Exception) -> Unit) {
    Single
        .create<String> {
          val signTx = PyEnv.signMessage(Vm.CoinType.ETH, from, messageHex, pwd, mode)
          if (signTx.result == null || signTx.errors?.isNotEmpty() == true) {
            it.onError(RuntimeException(signTx.errors))
          } else {
            it.onSuccess(signTx.result)
          }
        }
        .map {
          if (it == null) {
            throw RuntimeException("Signing failed")
          }
          it
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          success.invoke(it)
        }, {
          error.invoke(PyEnvException.convert(it))
        })
    return
  }

}

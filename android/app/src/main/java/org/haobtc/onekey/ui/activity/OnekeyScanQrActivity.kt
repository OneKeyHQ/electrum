package org.haobtc.onekey.ui.activity

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.bean.HardwareFeatures
import org.haobtc.onekey.bean.MainSweepcodeBean
import org.haobtc.onekey.bean.WalletAccountBalanceInfo
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.manager.BleManager
import org.haobtc.onekey.manager.PreferencesManager
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.onekeys.homepage.process.SendEthActivity
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity
import org.haobtc.onekey.ui.dialog.SelectAccountBottomSheetDialog
import org.haobtc.onekey.utils.MyDialog
import org.json.JSONObject

class OnekeyScanQrActivity : CaptureActivity() {
  companion object {
    private const val REQUEST_CONNECT_DEVICE = 1
    private const val EXT_WALLET_NAME = "wallet_name"

    private fun obtainIntent(context: Context, walletID: String): Intent {
      return Intent(context, OnekeyScanQrActivity::class.java).apply {
        val config = ZxingConfig()
        config.isPlayBeep = true
        config.isShake = true
        config.isDecodeBarCode = false
        config.isFullScreenScan = true
        config.isShowAlbum = false
        config.isShowbottomLayout = false
        putExtra(Constant.INTENT_ZXING_CONFIG, config)
        putExtra(EXT_WALLET_NAME, walletID)
      }
    }

    @JvmStatic
    fun start(context: Context, walletName: String) {
      context.startActivity(obtainIntent(context, walletName))
    }

    @JvmStatic
    fun start(fragment: Fragment, walletID: String, requestCode: Int) {
      fragment.startActivityForResult(obtainIntent(fragment.requireContext(), walletID), requestCode)
    }
  }

  private var mProgressDialog: MyDialog? = null
  private var mTempDataBean: MainSweepcodeBean.DataBean? = null
  private var mTempRawResult: String? = null

  override fun handleDecode(rawResult: String?) {
    beepManager.playBeepSoundAndVibrate()
    Single
        .create<String> {
          val parseQrCode = PyEnv.parseQrCode(rawResult)
          if (parseQrCode.errors?.isNotEmpty() == true) {
            it.onError(RuntimeException(parseQrCode.errors))
          } else {
            it.onSuccess(parseQrCode.result ?: "")
          }
        }
        .map {
          try {
            val jsonObject = JSONObject(it)
            val type = jsonObject.getInt("type")
            if (type == 1) {
              Gson().fromJson(it, MainSweepcodeBean::class.java).data
            } else {
              null
            }
          } catch (e: Exception) {
            null
          }
        }
        .doOnSubscribe { showProgress() }
        .doFinally { dismissProgress() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it != null && !it.isEmpty) {
            showAccountSelect(it, rawResult)
          } else {
            handler.restartPreviewAndDecodeDelayed(800)
            rawResult?.let { it1 -> OnekeyScanQrResultActivity.start(this, it1) }
            finish()
          }
        }, {
          handler.restartPreviewAndDecodeDelayed(800)
          it.printStackTrace()
          rawResult?.let { it1 -> OnekeyScanQrResultActivity.start(this, it1) }
          finish()
        })
  }

  /**
   * 转账跳转
   */
  private fun showAccountSelect(dataBean: MainSweepcodeBean.DataBean, rawResult: String?) {
    SelectAccountBottomSheetDialog
        .newInstance(dataBean.coin)
        .setOnDismissListener {
          handler.restartPreviewAndDecodeDelayed(500)
        }
        .setOnSelectAccountCallback {
          deal(dataBean, rawResult, it)
        }
        .show(supportFragmentManager, "select_address")
  }

  private fun deal(dataBean: MainSweepcodeBean.DataBean, rawResult: String?, wallet: WalletAccountBalanceInfo) {
    if (Vm.convertWalletType(wallet.type) == Vm.WalletType.HARDWARE) {
      val deviceInfo: String = PreferencesManager.get(
          this,
          org.haobtc.onekey.constant.Constant.DEVICES,
          wallet.deviceId,
          "")
          ?.toString() ?: ""

      val info = HardwareFeatures.objectFromData(deviceInfo)
      if (info == null) {
        Toast.makeText(
            baseContext,
            getString(R.string.not_found_device_msg),
            Toast.LENGTH_SHORT)
            .show()
        return
      }
      val bleMac = PreferencesManager.get(
          this,
          org.haobtc.onekey.constant.Constant.BLE_INFO,
          info.bleName,
          "")
          ?.toString()

      if (bleMac == null) {
        Toast.makeText(
            baseContext,
            getString(R.string.not_found_device_msg),
            Toast.LENGTH_SHORT)
            .show()
      } else {
        SearchDevicesActivity.startSearchADevice(this, wallet.deviceId, REQUEST_CONNECT_DEVICE)
        BleManager.getInstance(this).connDevByMac(bleMac)
      }
      mTempDataBean = dataBean
      mTempRawResult = rawResult
      return
    }
    toSend(dataBean, rawResult)
  }

  private fun toSend(dataBean: MainSweepcodeBean.DataBean, rawResult: String?) {
    val address: String = dataBean.address
    when (dataBean.coin) {
      Vm.CoinType.BTC ->
        SendHdActivity.start(
            this,
            intent.getStringExtra(EXT_WALLET_NAME),
            address,
            dataBean.amount)
      Vm.CoinType.ETH ->
        SendEthActivity.start(
            this,
            intent.getStringExtra(EXT_WALLET_NAME),
            address,
            dataBean.amount)
      else -> {
        rawResult?.let { it1 -> OnekeyScanQrResultActivity.start(this, it1) }
      }
    }
    finish()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CONNECT_DEVICE && resultCode == RESULT_OK) {
      mTempDataBean?.let {
        toSend(it, mTempRawResult)
        mTempDataBean = null
        mTempRawResult = null
      }
    }
  }

  fun showProgress() {
    runOnUiThread {
      dismissProgress()
      mProgressDialog = MyDialog.showDialog(this)
      mProgressDialog?.show()
      mProgressDialog?.onTouchOutside(false)
    }
  }

  fun dismissProgress() {
    runOnUiThread {
      if (mProgressDialog?.isShowing() == true) {
        mProgressDialog?.dismiss()
        mProgressDialog = null
      }
    }
  }
}

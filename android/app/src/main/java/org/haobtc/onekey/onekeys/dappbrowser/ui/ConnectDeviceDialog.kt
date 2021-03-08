package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import cn.com.heaton.blelibrary.ble.model.BleDevice
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.business.wallet.DeviceException
import org.haobtc.onekey.business.wallet.DeviceManager
import org.haobtc.onekey.databinding.DialogConnectDeviceBinding
import org.haobtc.onekey.viewmodel.AppWalletViewModel

/**
 * @author Onekey@QuincySx
 * @create 2021-03-03 10:23 AM
 */
class ConnectDeviceDialog(context: Context) : Dialog(context) {
  enum class TEXT_STYLE {
    CENTERED, LEFT
  }

  private lateinit var mBinding: DialogConnectDeviceBinding
  private val deviceManager by lazy { DeviceManager.getInstance(getContext()) }
  private val mAppWalletViewModel by lazy {
    ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel::class.java)
  }
  private val mWalletInfo by lazy {
    mAppWalletViewModel.currentWalletAccountInfo.value
  }

  private var isDoneConnect = false
  private var success: (() -> Unit)? = null
  private var error: ((Exception) -> Unit)? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        DialogConnectDeviceBinding.inflate(layoutInflater).also {
          mBinding = it
        }.root
    )
    window?.apply {
      setLayout(
          ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      attributes?.apply {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.BOTTOM
        attributes = this
      }
      setWindowAnimations(R.style.AnimBottomPop)
    }

    mBinding.tvButton.setOnClickListener { v: View? -> dismiss() }
    setTitle(R.string.hint_device_connecting)
    setMessage(R.string.hint_device_connecting_keep_ble)
  }

  override fun show() {
    super.show()
    connect()
  }

  private fun connect() {
    mWalletInfo?.deviceId?.let { deviceId ->
      setOnDismissListener {
        if (!isDoneConnect) {
          context.let { it1 -> DeviceManager.getInstance(it1).cancelDevice(deviceId) }
        }
      }

      deviceManager.connectDeviceByDeviceId(deviceId, object : DeviceManager.OnConnectDeviceListener<BleDevice> {
        override fun onSuccess(t: BleDevice) {
          isDoneConnect = true
          success?.invoke()
          dismiss()
        }

        override fun onException(t: BleDevice?, e: Exception) {
          if (isShowing == true) {
            dismiss()
          }
          error?.invoke(e)
        }
      })
    } ?: error?.invoke(DeviceException.OnConnectError())
  }


  fun makeWide() {
    context?.resources?.displayMetrics?.density?.let { scale ->
      val dp15 = (15 * scale + 0.5f).toInt()
      val dp10 = (10 * scale + 0.5f).toInt()
      mBinding.layoutDialogContainer.setPadding(dp15, dp15, dp15, dp15)
      val marginLayout = mBinding.dialogView.layoutParams as MarginLayoutParams
      marginLayout.setMargins(dp10, dp10, dp10, dp10)
      mBinding.layoutDialogContainer.requestLayout()
    }
  }

  override fun setTitle(resId: Int) {
    mBinding.dialogMainText.visibility = View.VISIBLE
    mBinding.dialogMainText.text = context?.resources?.getString(resId)
  }

  override fun setTitle(message: CharSequence?) {
    mBinding.dialogMainText.visibility = View.VISIBLE
    mBinding.dialogMainText.text = message
  }

  fun setButtonText(resId: Int) {
    mBinding.tvButton.visibility = View.VISIBLE
    mBinding.tvButton.text = context?.resources?.getString(resId)
  }

  fun setButtonListener(listener: View.OnClickListener?) {
    mBinding.tvButton.setOnClickListener(listener)
  }

  fun setOnConnectListener(success: () -> Unit, error: (Exception) -> Unit) {
    this.success = success
    this.error = error
  }

  fun setMessage(resId: Int) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = context?.resources?.getString(resId)
  }

  fun setMessage(message: CharSequence?) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = message
  }

  fun setMessage(message: String?) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = message
  }

  fun setView(view: View?) {
    mBinding.dialogView.addView(view)
  }

  fun setTextStyle(style: TEXT_STYLE?) {
    when (style) {
      TEXT_STYLE.CENTERED -> mBinding.dialogSubText.gravity = Gravity.CENTER_HORIZONTAL
      TEXT_STYLE.LEFT -> mBinding.dialogSubText.gravity = Gravity.START
    }
  }
}

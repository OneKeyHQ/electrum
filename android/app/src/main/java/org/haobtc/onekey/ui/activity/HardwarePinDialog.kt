package org.haobtc.onekey.ui.activity

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import org.haobtc.onekey.R
import org.haobtc.onekey.constant.Constant
import org.haobtc.onekey.event.ChangePinEvent
import org.haobtc.onekey.manager.PyEnv

class HardwarePinDialog : DialogFragment(), HardwarePinFragment.HardwareTitleChangeCallback,
    HardwarePinFragment.OnHardwarePinSuccessCallback {

  companion object {
    private const val EXT_ACTION = "action"
    private const val EXT_PIN_ORIGIN = Constant.PIN_ORIGIN

    @JvmStatic
    fun newInstance(@HardwarePinFragment.PinActionType action: String, originPin: String? = null): HardwarePinDialog {
      val hardwarePinDialog = HardwarePinDialog()
      val bundle = Bundle()
      bundle.putString(EXT_ACTION, action)
      if (!TextUtils.isEmpty(originPin)) {
        bundle.putString(EXT_PIN_ORIGIN, originPin)
      }
      hardwarePinDialog.arguments = bundle
      return hardwarePinDialog
    }
  }

  var imgBack: ImageView? = null
  var textPageTitle: TextView? = null
  private var isSucceed = false

  private var mOnInputSuccessListener: OnInputSuccessListener? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCanceledOnTouchOutside(false)
    dialog?.window?.apply {
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      setNavigationBarColor(Color.WHITE)
      decorView.setPadding(0, 0, 0, 0)
      attributes?.apply {
        width = WindowManager.LayoutParams.MATCH_PARENT
        gravity = Gravity.BOTTOM
        dialog?.window?.attributes = this
      }
    }
    return inflater.inflate(R.layout.dialog_input_hardware_pin, container, false).apply {
      imgBack = findViewById(R.id.img_back)
      textPageTitle = findViewById(R.id.text_page_title)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val action = arguments?.getString(EXT_ACTION) ?: HardwarePinFragment.PinActionType.VERIFY_PIN
    val originPin = arguments?.getString(EXT_PIN_ORIGIN)

    val hardwarePinFragment = HardwarePinFragment
        .newInstance(action, originPin)
    childFragmentManager.beginTransaction()
        .add(R.id.view_container, hardwarePinFragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()

    setupCancelListeners()
  }

  private fun setupCancelListeners() {
    imgBack?.setOnClickListener {
      onFinish()
    }
    dialog?.setOnCancelListener {
      onFinish()
    }
    dialog?.setOnDismissListener {
      onFinish()
    }
    dialog?.setOnKeyListener { dialog, keyCode, event ->
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        onFinish()
        true
      } else {
        false //默认返回 false，这里false不能屏蔽返回键，改成true就可以了
      }
    }
  }

  fun setOnInputSuccessListener(listener: OnInputSuccessListener): HardwarePinDialog {
    mOnInputSuccessListener = listener
    return this
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    dismissAllowingStateLoss()
  }

  override fun setTitle(title: String) {
    textPageTitle?.text = title
  }

  override fun onFinish() {
    if (!isSucceed) {
      PyEnv.cancelPinInput()
      mOnInputSuccessListener?.onCancel()
    }
    dismissAllowingStateLoss()
  }

  override fun onSuccess(event: ChangePinEvent) {
    isSucceed = true
    // 回写PIN码
    PyEnv.setPin(event.toString())
    mOnInputSuccessListener?.onSuccess()
    dismissAllowingStateLoss()
  }

  interface OnInputSuccessListener {
    fun onSuccess()

    fun onCancel()
  }
}

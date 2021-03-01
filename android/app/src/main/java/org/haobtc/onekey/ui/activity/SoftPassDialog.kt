package org.haobtc.onekey.ui.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import org.haobtc.onekey.R
import org.haobtc.onekey.constant.Constant
import org.haobtc.onekey.ui.activity.SoftPassFragment.OnPasswordSuccessCallback
import org.haobtc.onekey.ui.activity.SoftPassFragment.PasswordTitleChangeCallback


class SoftPassDialog : DialogFragment(), PasswordTitleChangeCallback, OnPasswordSuccessCallback {

  companion object {

    @JvmStatic
    fun newInstance(operate: Int = SoftPassFragment.VERIFY): SoftPassDialog {
      val softPassDialog = SoftPassDialog()
      val bundle = Bundle()
      bundle.putInt(Constant.OPERATE_TYPE, operate)
      softPassDialog.arguments = bundle
      return softPassDialog
    }
  }

  var imgBack: ImageView? = null
  var textPageTitle: TextView? = null

  private var mOnInputSuccessListener: OnInputSuccessListener? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCanceledOnTouchOutside(false)
    dialog?.window?.apply {
      setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      decorView.setPadding(0, 0, 0, 0)
      attributes?.apply {
        width = WindowManager.LayoutParams.MATCH_PARENT
        gravity = Gravity.BOTTOM
        dialog?.window?.attributes = this
      }
    }
    return inflater.inflate(R.layout.dialog_soft_pass, container, false).apply {
      imgBack = findViewById(R.id.img_back)
      textPageTitle = findViewById(R.id.text_page_title)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val operate = arguments?.getInt(Constant.OPERATE_TYPE) ?: SoftPassFragment.VERIFY
    val softPassFragment = SoftPassFragment
        .newInstance(operate)
    childFragmentManager.beginTransaction()
        .add(R.id.view_container, softPassFragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commit()

    setupCancelListeners()
  }

  private fun setupCancelListeners() {
    imgBack?.setOnClickListener { onFinish() }
    dialog?.setOnCancelListener { onFinish() }
    // dialog?.setOnDismissListener { }
  }

  fun setOnInputSuccessListener(listener: OnInputSuccessListener): SoftPassDialog {
    mOnInputSuccessListener = listener
    return this
  }

  override fun setTitle(title: String) {
    textPageTitle?.text = title
  }

  override fun onFinish() {
    mOnInputSuccessListener?.onCancel()
    dismiss()
  }

  override fun onSuccess(pwd: String) {
    mOnInputSuccessListener?.onSuccess(pwd)
    dismiss()
  }

  interface OnInputSuccessListener {
    fun onSuccess(pwd: String)

    fun onCancel()
  }

}

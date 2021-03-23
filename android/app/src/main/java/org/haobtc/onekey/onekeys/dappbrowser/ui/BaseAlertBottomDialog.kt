package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.haobtc.onekey.R
import org.haobtc.onekey.databinding.DialogBaseBottomBinding
import org.haobtc.onekey.extensions.setCustomNavigationBar


/**
 * @author Onekey@QuincySx
 * @create 2021-03-03 10:23 AM
 */
open class BaseAlertBottomDialog(context: Context) : Dialog(context) {
  enum class TEXT_STYLE {
    CENTERED, LEFT
  }

  private lateinit var mBinding: DialogBaseBottomBinding
  private var mPrimaryButtonListener: View.OnClickListener? = null
  private var mSecondaryButtonListener: View.OnClickListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(
        DialogBaseBottomBinding.inflate(layoutInflater).also {
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
      setWindowAnimations(R.style.AnimBottom)
      addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      navigationBarColor = Color.WHITE
    }
    mBinding.tvButtonSecondary.setOnClickListener { v: View? ->
      mSecondaryButtonListener?.onClick(v)
      dismiss()
    }
    mBinding.tvButtonPrimary.setOnClickListener { v: View? ->
      mPrimaryButtonListener?.onClick(v)
      dismiss()
    }
  }

  open fun setProgressMode() {
    mBinding.layoutIconInfo.visibility = View.VISIBLE
    mBinding.ivDialogLogo.visibility = View.GONE
    mBinding.progressBar.setVisibility(View.VISIBLE)
  }

  open fun setIcon(resId: Int) {
    mBinding.layoutIconInfo.visibility = View.VISIBLE
    mBinding.progressBar.visibility = View.GONE
    mBinding.ivDialogLogo.visibility = View.VISIBLE
    mBinding.ivDialogLogo.setImageResource(resId)
  }

  open fun setIcon(url: String) {
    mBinding.layoutIconInfo.visibility = View.VISIBLE
    mBinding.progressBar.visibility = View.GONE
    mBinding.ivDialogLogo.visibility = View.VISIBLE
    Glide.with(mBinding.ivDialogLogo)
        .load(url)
        .centerCrop()
        .placeholder(R.mipmap.ic_launcher_foreground)
        .error(R.mipmap.ic_launcher_foreground)
        .fitCenter()
        .into(mBinding.ivDialogLogo)
  }

  override fun setTitle(resId: Int) {
    mBinding.dialogMainText.visibility = View.VISIBLE
    mBinding.dialogMainText.text = context.resources?.getString(resId)
  }

  override fun setTitle(message: CharSequence?) {
    mBinding.dialogMainText.visibility = View.VISIBLE
    mBinding.dialogMainText.text = message
  }

  fun setMessage(resId: Int) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = context.resources?.getString(resId)
  }

  fun setMessage(message: CharSequence?) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = message
  }

  fun setMessage(message: String?) {
    mBinding.dialogSubText.visibility = View.VISIBLE
    mBinding.dialogSubText.text = message
  }

  fun setPrimaryButtonText(resId: Int) {
    mBinding.tvButtonPrimary.visibility = View.VISIBLE
    mBinding.tvButtonPrimary.text = context.resources?.getString(resId)
  }

  fun setPrimaryButtonListener(listener: View.OnClickListener?): BaseAlertBottomDialog {
    mPrimaryButtonListener = listener
    return this
  }

  fun setSecondaryButtonText(resId: Int) {
    mBinding.tvButtonSecondary.visibility = View.VISIBLE
    mBinding.tvButtonSecondary.text = context.resources?.getString(resId)
  }

  fun setSecondaryButtonListener(listener: View.OnClickListener?): BaseAlertBottomDialog {
    mBinding.tvButtonSecondary.visibility = View.VISIBLE
    mBinding.viewButtonDivision.visibility = View.VISIBLE
    mSecondaryButtonListener = listener
    return this
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

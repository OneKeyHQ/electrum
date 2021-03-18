package org.haobtc.onekey.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout
import androidx.annotation.StringRes
import org.haobtc.onekey.databinding.ViewFeeSelectionBinding

class FeeSelectionView : FrameLayout, Checkable {
  private val mBinding by lazy {
    ViewFeeSelectionBinding.inflate(LayoutInflater.from(context), this, true)
  }
  private var mChecked: Boolean = false

  @JvmOverloads
  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr, defStyleRes) {
    mBinding
  }

  fun setTitle(title: String) {
    mBinding.tvTitle.text = title
  }

  fun setTitle(@StringRes strRes: Int) {
    mBinding.tvTitle.setText(strRes)
  }

  fun setAmount(title: String) {
    mBinding.tvFeeAmount.text = title
  }

  fun setAmount(@StringRes strRes: Int) {
    mBinding.tvFeeAmount.setText(strRes)
  }

  fun setAmountFiat(title: String) {
    mBinding.tvFeeAmountFiat.text = title
  }

  fun setAmountFiat(@StringRes strRes: Int) {
    mBinding.tvFeeAmountFiat.setText(strRes)
  }

  fun setEstimatedTime(title: String) {
    mBinding.tvSpeed.text = title
  }

  fun setEstimatedTime(@StringRes strRes: Int) {
    mBinding.tvSpeed.setText(strRes)
  }

  override fun setChecked(checked: Boolean) {
    mChecked = checked
    if (checked) {
      mBinding.checkboxSlow.visibility = View.VISIBLE
      mBinding.viewSlow.visibility = View.VISIBLE
    } else {
      mBinding.checkboxSlow.visibility = View.GONE
      mBinding.viewSlow.visibility = View.GONE
    }
  }

  override fun isChecked() = mChecked

  override fun toggle() {
    setChecked(!isChecked)
  }
}

package org.haobtc.onekey.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout
import androidx.annotation.StringRes
import org.haobtc.onekey.databinding.ViewFeeCustomSelectionBinding
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

class CustomFeeSelectionView : FrameLayout, Checkable {
  private val mBinding by lazy {
    ViewFeeCustomSelectionBinding.inflate(LayoutInflater.from(context), this, true)
  }
  private var mChecked: Boolean = false

  @JvmOverloads
  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(
      context,
      attrs,
      defStyleAttr,
      defStyleRes
  ) {
    mBinding
    isChecked = true
  }

  fun setTitle(title: String) {
    mBinding.textViewCustom.text = title
    mBinding.textViewCustomFolded.text = title
  }

  fun setTitle(@StringRes strRes: Int) {
    mBinding.textViewCustom.setText(strRes)
    mBinding.textViewCustomFolded.setText(strRes)
  }

  fun setAmount(title: String) {
    mBinding.textFeeInBtcCustom.text = title
  }

  fun setAmount(@StringRes strRes: Int) {
    mBinding.textFeeInBtcCustom.setText(strRes)
  }

  fun setEstimatedTime(title: String) {
    mBinding.textSpendTimeCustom.text = title
  }

  fun setEstimatedTime(@StringRes strRes: Int) {
    mBinding.textSpendTimeCustom.setText(strRes)
  }

  fun getEditGasPrice() = mBinding.editGasPrice

  fun getEditGasLimit() = mBinding.editGasLimit

  @SuppressLint("SetTextI18n")
  fun setGasLimit(gasLimit: BigInteger) {
    mBinding.editGasLimit.setText(gasLimit.toString())
  }

  fun setGasPrice(gasPrice: BigDecimal) {
    val toWei = Convert.fromWei(gasPrice, Convert.Unit.GWEI).stripTrailingZeros().toPlainString()
    mBinding.editGasPrice.setText(toWei)
  }

  fun getGasLimit() = mBinding.editGasLimit.text.toString().trim()

  fun getGasPrice() = mBinding.editGasPrice.text.toString().trim()

  fun getGasLimitNumber(): BigInteger {
    getGasLimit().also {
      return try {
        BigInteger(it)
      } catch (e: Exception) {
        BigInteger.ZERO
      }
    }
  }

  fun getGasPriceNumber(): BigDecimal {
    getGasPrice().also {
      return try {
        Convert.toWei(BigDecimal(it), Convert.Unit.GWEI)
      } catch (e: Exception) {
        BigDecimal.ZERO
      }
    }
  }

  override fun setChecked(checked: Boolean) {
    mChecked = checked
    if (checked) {
      mBinding.layoutFolded.visibility = View.GONE
      mBinding.layoutExpanded.visibility = View.VISIBLE
      mBinding.viewSlow.visibility = View.VISIBLE
      mBinding.divider.visibility = View.VISIBLE
      mBinding.layoutGasLimit.visibility = View.VISIBLE
      mBinding.layoutGasPrice.visibility = View.VISIBLE
    } else {
      mBinding.layoutFolded.visibility = View.VISIBLE
      mBinding.layoutExpanded.visibility = View.GONE
      mBinding.viewSlow.visibility = View.GONE
      mBinding.divider.visibility = View.GONE
      mBinding.layoutGasLimit.visibility = View.GONE
      mBinding.layoutGasPrice.visibility = View.GONE
    }
  }

  override fun isChecked() = mChecked

  override fun toggle() {
    setChecked(!isChecked)
  }
}

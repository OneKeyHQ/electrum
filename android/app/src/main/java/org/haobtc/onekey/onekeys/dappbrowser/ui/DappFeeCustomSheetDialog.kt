package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scwang.smartrefresh.layout.util.SmartUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.CurrentFeeDetails
import org.haobtc.onekey.business.wallet.SystemConfigManager
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.databinding.DialogDappFeeCustomSheetBinding
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.ui.widget.CustomFeeSelectionView
import org.haobtc.onekey.ui.widget.FeeSelectionView
import org.haobtc.onekey.utils.CoinDisplayUtils.Companion.getCoinPrecisionDisplay
import org.haobtc.onekey.utils.ToastUtils
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.*


class DappFeeCustomSheetDialog private constructor() : BottomSheetDialogFragment(), View.OnClickListener, OnInitDataInterface {

  companion object {
    private const val EXT_COIN_TYPE = "coin_type"
    private const val EXT_TX_DATA = "tx_data"
    private const val EXT_TX_AMOUNT = "tx_amount"
    private const val EXT_TX_TO_ADDRESS = "tx_to_address"

    @JvmStatic
    @JvmOverloads
    fun newInstance(
        coinType: Vm.CoinType,
        toAddress: String,
        value: String,
        data: String? = null,
    ): DappFeeCustomSheetDialog {
      return DappFeeCustomSheetDialog().apply {
        Bundle().apply {
          putString(EXT_COIN_TYPE, coinType.coinName)
          putString(EXT_TX_DATA, data)
          putString(EXT_TX_AMOUNT, value)
          putString(EXT_TX_TO_ADDRESS, toAddress)
          arguments = this
        }
      }
    }
  }

  private val mBinding: DialogDappFeeCustomSheetBinding by lazy { DialogDappFeeCustomSheetBinding.inflate(layoutInflater) }
  private var mDialog: BottomSheetDialog? = null
  private var mOnSelectFeeCallback: OnFeeSelectCallback? = null
  private lateinit var mCoinType: Vm.CoinType
  private lateinit var mToAddress: String
  private var mData: String? = null
  private var mValue: BigInteger = BigInteger.ZERO

  private var mCustomFeeGasPrice: BigDecimal = BigDecimal.ZERO
  private var mCustomFeeGasLimit: BigInteger = BigInteger.ZERO

  private var mCustomGasPriceLegal = true
  private var mCustomGasLimitLegal = true

  private val mSystemConfigManager by lazy {
    SystemConfigManager(MyApplication.getInstance())
  }
  private val mViewModel by lazy {
    ViewModelProvider(this).get(CustomFeeViewModel::class.java)
  }

  override fun getTheme() = R.style.BottomSheetEdit

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    mDialog = dialog
    dialog.setContentView(mBinding.root)
    dialog.delegate
        ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        ?.setBackgroundColor(Color.TRANSPARENT)
    dialog.setCanceledOnTouchOutside(false)

    val layoutParams = mBinding.root.layoutParams
    layoutParams.height = SmartUtil.dp2px(560f)
    mBinding.root.layoutParams = layoutParams;

    handleExtData()
    initViewListener()
    initViewModelListener()
    mOnSelectFeeCallback?.onInit(this)
    return dialog
  }

  private fun handleExtData() {
    mCoinType = Vm.CoinType.convertByCoinName(arguments?.getString(EXT_COIN_TYPE))
    mToAddress = arguments?.getString(EXT_TX_TO_ADDRESS) ?: ""
    mData = arguments?.getString(EXT_TX_DATA)
    mValue = BigInteger(arguments?.getString(EXT_TX_AMOUNT) ?: "0")
  }

  private fun initViewModelListener() {
    mViewModel.feeDetails.observe(this) {
      it?.let {
        fillFeeData(it)
      }
    }
    mViewModel.gasLimit.observe(this) {
      calculationCustomData()
    }
    mViewModel.gasPrice.observe(this) {
      calculationCustomData()
    }
    mViewModel.coinFiat.observe(this) {
      calculationCustomData()
    }
    mViewModel.currentFee.observe(this) {
      handleSelectFee()
    }
  }

  private fun initViewListener() {
    mBinding.imgCancel.setOnClickListener {
      checkParameterAndDismiss()
    }

    mBinding.viewFeeCustom.getEditGasLimit().addTextChangedListener {
      checkCustomGasLimit()
      if (it.toString() != mViewModel.gasLimit.value) {
        mViewModel.gasLimit.value = it.toString()
      }
    }
    mBinding.viewFeeCustom.getEditGasPrice().addTextChangedListener {
      checkCustomGasPrice()
      if (it.toString() != mViewModel.gasPrice.value) {
        mViewModel.gasPrice.value = it.toString()
      }
    }
    mBinding.viewFeeSlow.setOnClickListener {
      if (mViewModel.feeDetails.value == null) {
        ToastUtils.toastLong(getString(R.string.wait_fee_loading_recommend))
        return@setOnClickListener
      }
      mViewModel.currentFee.value = FeeType.SLOW
    }
    mBinding.viewFeeRecommend.setOnClickListener {
      if (mViewModel.feeDetails.value == null) {
        ToastUtils.toastLong(getString(R.string.wait_fee_loading_recommend))
        return@setOnClickListener
      }
      mViewModel.currentFee.value = FeeType.NORMAL
    }
    mBinding.viewFeeFast.setOnClickListener {
      if (mViewModel.feeDetails.value == null) {
        ToastUtils.toastLong(getString(R.string.wait_fee_loading_recommend))
        return@setOnClickListener
      }
      mViewModel.currentFee.value = FeeType.FAST
    }
    mBinding.viewFeeCustom.setOnClickListener {
      if (mViewModel.feeDetails.value == null) {
        ToastUtils.toastLong(getString(R.string.wait_fee_loading_recommend))
        return@setOnClickListener
      }
      mViewModel.currentFee.value = FeeType.CUSTOM
    }
  }

  override fun onDataInit(feeType: Int?, currentFeeDetails: CurrentFeeDetails?, defGasLimit: BigInteger, defGasPrice: BigDecimal) {
    initializeFeeData(feeType, currentFeeDetails, defGasLimit, defGasPrice)
  }

  private fun handleSelectFee() {
    mViewModel.currentFee.value?.let { feeType ->
      mBinding.viewFeeSlow.isChecked = false
      mBinding.viewFeeRecommend.isChecked = false
      mBinding.viewFeeFast.isChecked = false
      mBinding.viewFeeCustom.isChecked = false
      val feeBean = when (feeType) {
        FeeType.SLOW -> {
          mBinding.viewFeeSlow.isChecked = true
          setFeeCustomHint()
          getFeeBean(mViewModel.feeDetails.value?.slow)
        }
        FeeType.NORMAL -> {
          mBinding.viewFeeRecommend.isChecked = true
          setFeeCustomHint()
          getFeeBean(mViewModel.feeDetails.value?.normal)
        }
        FeeType.FAST -> {
          mBinding.viewFeeFast.isChecked = true
          setFeeCustomHint()
          getFeeBean(mViewModel.feeDetails.value?.fast)
        }
        FeeType.CUSTOM -> {
          mBinding.viewFeeCustom.isChecked = true
          calculationCustomData()
          getCustomFeeBean(mViewModel.feeDetails.value?.normal, mViewModel.gasLimitNumber, mViewModel.gasPriceNumber)
        }
        else -> null
      }
      feeBean?.let {
        mOnSelectFeeCallback?.onSelectFee(feeType, feeBean.getGasPrice().toBigInteger(), feeBean.getGasLimit())
      }
    }
  }

  private fun checkCustomGasPrice() {
    Single
        .fromCallable {
          judgeGasPrice()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it.result == 0) {
            mBinding.viewFeeCustom.getEditGasPrice().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color2))
            mCustomGasPriceLegal = true
          } else {
            mBinding.viewFeeCustom.getEditGasPrice().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_red))
            mCustomGasPriceLegal = false
            if (mBinding.viewFeeCustom.getGasPriceNumber() < BigDecimal.ONE) {
              mBinding.viewFeeCustom.getEditGasPrice().setText("1")
              mBinding.viewFeeCustom.getEditGasPrice().setSelection(1)
            }
          }
          setFeeCustomHint(it.hints)
          mDialog?.setCancelable(mCustomGasPriceLegal && mCustomGasLimitLegal)
        }, {

        })
  }

  private fun checkCustomGasLimit() {
    Single
        .fromCallable {
          judgeGasLimit()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it.result == 0) {
            mBinding.viewFeeCustom.getEditGasLimit().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color2))
            mCustomGasLimitLegal = true
          } else {
            mBinding.viewFeeCustom.getEditGasLimit().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_red))
            mCustomGasLimitLegal = false
            if (mBinding.viewFeeCustom.getGasLimitNumber() < BigInteger.ONE) {
              mBinding.viewFeeCustom.getEditGasLimit().setText("1")
              mBinding.viewFeeCustom.getEditGasLimit().setSelection(1)
            }
          }
          setFeeCustomHint(it.hints)
          mDialog?.setCancelable(mCustomGasPriceLegal && mCustomGasLimitLegal)
        }, {

        })
  }

  private fun setFeeCustomHint(message: String? = null) {
    if (mViewModel.currentFee.value == FeeType.CUSTOM) {
      mBinding.hintCustomFee.text = message ?: ""
    } else {
      mBinding.hintCustomFee.text = ""
    }
  }

  /**
   * 判断 GasPrice 是否不合法
   * @return -1:太小  0:正常  1:太大
   */
  private fun judgeGasPrice(): Judgment {
    val estimateGasPrice = Convert.toWei(BigDecimal(mViewModel.feeDetails.value?.fast?.gasPrice?.toString()
        ?: "1"), Convert.Unit.GWEI)

    var hints = ""

    val customLegal = mCustomFeeGasPrice != BigDecimal.ZERO && mBinding.viewFeeCustom.getGasPriceNumber() >= mCustomFeeGasPrice.divide(BigDecimal.TEN, 8, RoundingMode.DOWN)
    val estimateLegal = mViewModel.feeDetails.value?.fast != null && mBinding.viewFeeCustom.getGasPriceNumber() >= estimateGasPrice.divide(BigDecimal.TEN, 8, RoundingMode.DOWN)


    if (!customLegal) hints = getString(R.string.hint_custom_fee_gasprice_too_small)
        .format(
            Convert.fromWei(mCustomFeeGasPrice.divide(BigDecimal.TEN, 8, RoundingMode.DOWN), Convert.Unit.GWEI)
                .stripTrailingZeros().toPlainString())
    if (!estimateLegal) hints = getString(R.string.hint_custom_fee_gasprice_too_small)
        .format(
            Convert.fromWei(estimateGasPrice.divide(BigDecimal.TEN, 8, RoundingMode.DOWN), Convert.Unit.GWEI)
                .stripTrailingZeros().toPlainString())

    if (!(customLegal || estimateLegal)) {
      return Judgment(-1, hints)
    }

    val customLegalBig = mCustomFeeGasPrice != BigInteger.ZERO && mBinding.viewFeeCustom.getGasPriceNumber() < mCustomFeeGasPrice.multiply(BigDecimal.TEN)
    val estimateLegalBig = mViewModel.feeDetails.value?.fast != null && mBinding.viewFeeCustom.getGasPriceNumber() < estimateGasPrice.multiply(BigDecimal.TEN)

    if (!customLegalBig) hints = getString(R.string.hint_custom_fee_gasprice_too_big)
        .format(
            Convert.fromWei(estimateGasPrice.multiply(BigDecimal.TEN), Convert.Unit.GWEI)
                .stripTrailingZeros().toPlainString())
    if (!estimateLegalBig) hints = getString(R.string.hint_custom_fee_gasprice_too_big)
        .format(
            Convert.fromWei(estimateGasPrice.multiply(BigDecimal.TEN), Convert.Unit.GWEI)
                .stripTrailingZeros().toPlainString())

    if (!(customLegalBig || estimateLegalBig)) {
      return Judgment(1, hints)
    }
    return Judgment(0)
  }

  /**
   * 判断 GasLimit 是否不合法
   * @return -1:太小  0:正常  1:太大
   */
  private fun judgeGasLimit(): Judgment {
    val estimateGasLimit = mViewModel.feeDetails.value?.normal?.gasLimit?.toBigInteger()
        ?: BigInteger("53000")

    var hints = ""

    val customLegal = mCustomFeeGasLimit != BigInteger.ZERO && mBinding.viewFeeCustom.getGasLimitNumber() >= mCustomFeeGasLimit
    val estimateLegal = mViewModel.feeDetails.value?.normal != null && mBinding.viewFeeCustom.getGasLimitNumber() >= estimateGasLimit

    if (!customLegal) hints = getString(R.string.hint_custom_fee_gaslimit_too_small).format(mCustomFeeGasLimit)
    if (!estimateLegal) hints = getString(R.string.hint_custom_fee_gaslimit_too_small).format(estimateGasLimit)

    if (!(customLegal || estimateLegal)) {
      return Judgment(-1, hints)
    }

    val customLegalBig = mCustomFeeGasLimit != BigInteger.ZERO && mBinding.viewFeeCustom.getGasLimitNumber() < mCustomFeeGasLimit.multiply(BigInteger.TEN)
    val estimateLegalBig = mViewModel.feeDetails.value?.normal != null && mBinding.viewFeeCustom.getGasLimitNumber() < estimateGasLimit.multiply(BigInteger.TEN)

    if (!customLegalBig) hints = getString(R.string.hint_custom_fee_gaslimit_too_big).format(mCustomFeeGasLimit.multiply(BigInteger.TEN))
    if (!estimateLegalBig) hints = getString(R.string.hint_custom_fee_gaslimit_too_big).format(estimateGasLimit.multiply(BigInteger.TEN))

    if (!(customLegalBig || estimateLegalBig)) {
      return Judgment(1, hints)
    }
    return Judgment(0)
  }

  private fun calculationCustomData() {
    if (mViewModel.currentFee.value == FeeType.CUSTOM) {
      fillCustomFeeView(mBinding.viewFeeCustom)
    }
  }

  private fun getFeeBean(
      feeDetails: CurrentFeeDetails.DetailBean?,
  ): FeeBean {
    return FeeBean(
        Convert.toWei(BigDecimal(feeDetails?.gasPrice?.toString() ?: "0"), Convert.Unit.GWEI),
        feeDetails?.gasLimit?.toBigInteger() ?: BigInteger.ZERO
    )
  }

  private fun getCustomFeeBean(
      feeDetails: CurrentFeeDetails.DetailBean? = null,
      defGasLimit: BigInteger = BigInteger.ZERO,
      defGasPrice: BigDecimal = BigDecimal.ZERO,
  ): FeeBean {
    val gasLimit = if (defGasLimit == BigInteger.ZERO) {
      feeDetails?.gasLimit?.toBigInteger() ?: BigInteger.ZERO
    } else {
      defGasLimit
    }
    val gasPrice = if (defGasPrice == BigDecimal.ZERO) {
      Convert.toWei(BigDecimal(feeDetails?.gasPrice?.toString() ?: "0"), Convert.Unit.GWEI)
    } else {
      defGasPrice
    }
    return FeeBean(gasPrice, gasLimit)
  }

  private fun getDefaultFeeBean(
      feeDetails: CurrentFeeDetails.DetailBean? = null,
      defGasLimit: BigInteger = BigInteger.ZERO,
      defGasPrice: BigDecimal = BigDecimal.ZERO,
  ): FeeBean {
    return if (defGasLimit == BigInteger.ZERO && defGasPrice == BigDecimal.ZERO) {
      getFeeBean(feeDetails)
    } else {
      getCustomFeeBean(feeDetails, defGasLimit, defGasPrice)
    }
  }

  private fun getDefaultFeeType(
      defGasLimit: BigInteger = BigInteger.ZERO,
      defGasPrice: BigDecimal = BigDecimal.ZERO,
  ): Int {
    return if (defGasLimit == BigInteger.ZERO && defGasPrice == BigDecimal.ZERO) {
      FeeType.NORMAL
    } else {
      FeeType.CUSTOM
    }
  }


  @JvmOverloads
  @MainThread
  private fun initializeFeeData(
      @FeeType feeType: Int? = null,
      currentFeeDetails: CurrentFeeDetails? = null,
      defGasLimit: BigInteger = BigInteger.ZERO,
      defGasPrice: BigDecimal = BigDecimal.ZERO,
  ) {
    mCustomFeeGasPrice = defGasPrice
    mCustomFeeGasLimit = defGasLimit
    mBinding.viewFeeCustom.setGasLimit(defGasLimit)
    mBinding.viewFeeCustom.setGasPrice(defGasPrice)

    mViewModel.currentFee.value = feeType ?: getDefaultFeeType(defGasLimit, defGasPrice)

    currentFeeDetails?.let {
      setFeeDetails(it)
    }

    if (defGasLimit == BigInteger.ZERO || defGasPrice == BigDecimal.ZERO || currentFeeDetails == null) {
      loadFeeDetails()
    }
  }

  private fun setFeeDetails(currentFeeDetails: CurrentFeeDetails) {
    checkCustomGasPrice()
    checkCustomGasLimit()

    fillFeeData(currentFeeDetails)

    // 如果有某一项为 0,在重新填写一下
    val defaultFeeBean = getDefaultFeeBean(currentFeeDetails.normal, mViewModel.gasLimitNumber, mViewModel.gasPriceNumber)
    mBinding.viewFeeCustom.setGasLimit(defaultFeeBean.getGasLimit())
    mBinding.viewFeeCustom.setGasPrice(defaultFeeBean.getGasPrice())

    mViewModel.feeDetails.value = currentFeeDetails
    mViewModel.coinFiat.value = calculationDollars(currentFeeDetails.normal)
  }

  private fun fillFeeData(
      currentFeeDetails: CurrentFeeDetails
  ) {
    fillFeeView(mBinding.viewFeeSlow, R.string.slow, currentFeeDetails.slow)
    fillFeeView(mBinding.viewFeeRecommend, R.string.recommend, currentFeeDetails.normal)
    fillFeeView(mBinding.viewFeeFast, R.string.fast, currentFeeDetails.fast)
  }

  private fun calculationDollars(data: CurrentFeeDetails.DetailBean): BigDecimal {
    val fee = BigDecimal(data.fee)
    if (fee == BigDecimal.ZERO) {
      return BigDecimal.ZERO
    }
    return BigDecimal(data.fiat.substring(0, data.fiat.indexOf(" "))).divide(fee, 3, RoundingMode.DOWN)
  }

  /**
   * @param view 自定义选择框
   */
  private fun fillCustomFeeView(
      view: CustomFeeSelectionView
  ) {
    val customFeeBean = getCustomFeeBean(mViewModel.feeDetails.value?.normal, mViewModel.gasLimitNumber, mViewModel.gasPriceNumber)

    fillCustomFeeView(view, mViewModel.feeDetails.value, customFeeBean)
  }

  /**
   * @param view 自定义选择框
   * @param currentFeeDetails gasLimit unit:wei
   * @param feeBean gasPrice unit:wei
   */
  private fun fillCustomFeeView(
      view: CustomFeeSelectionView,
      currentFeeDetails: CurrentFeeDetails?,
      feeBean: FeeBean,
  ) {
    view.setTitle(R.string.custom_fee)
    val fee = Convert.fromWei(feeBean.getFee(), Convert.Unit.ETHER)

    view.setAmount(
        String.format(
            Locale.ENGLISH, "%s %s ≈ %s%s",
            getCoinPrecisionDisplay(fee.stripTrailingZeros().toPlainString(), mCoinType),
            mSystemConfigManager.getCurrentBaseUnit(mCoinType),
            mSystemConfigManager.currentFiatSymbol,
            mViewModel.coinFiat.value?.multiply(fee)?.setScale(2, RoundingMode.DOWN)?.stripTrailingZeros()?.toPlainString()
        )
    )

    currentFeeDetails?.let {
      val timeTemp = when {
        fee >= BigDecimal(currentFeeDetails.fast.fee) -> {
          currentFeeDetails.fast.time
        }
        fee >= BigDecimal(currentFeeDetails.normal.fee) -> {
          currentFeeDetails.normal.time
        }
        else -> {
          currentFeeDetails.slow.time
        }
      }

      view.setEstimatedTime(
          String.format(
              "%s %s %s",
              getString(R.string.about_),
              timeTemp,
              getString(R.string.minute)
          )
      )
    }
  }

  private fun fillFeeView(view: FeeSelectionView, @StringRes titleRes: Int, data: CurrentFeeDetails.DetailBean) {
    view.setTitle(titleRes)
    view.setAmount(
        String.format(
            Locale.ENGLISH, "%s %s",
            getCoinPrecisionDisplay(data.fee, mCoinType), mSystemConfigManager.getCurrentBaseUnit(mCoinType)
        )
    )
    view.setAmountFiat(
        String.format(
            Locale.ENGLISH,
            "%s %s",
            mSystemConfigManager.currentFiatSymbol,
            data.fiat.substring(0, data.fiat.indexOf(" "))
        )
    )
    view.setEstimatedTime(
        String.format(
            "%s %s %s",
            getString(R.string.about_),
            data.time,
            getString(R.string.minute)
        )
    )
  }

  private fun loadFeeDetails() {
    Single
        .fromCallable {
          PyEnv.getDefFeeInfo(mCoinType, mToAddress, mValue, mData).result
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          setFeeDetails(it)
        }, {

        })
  }

  private fun checkParameterAndDismiss() {
    if (mViewModel.currentFee.value == FeeType.CUSTOM) {
      Single
          .fromCallable {
            val judgeGasLimit = judgeGasLimit()
            val judgeGasPrice = judgeGasPrice()
            Pair(judgeGasLimit, judgeGasPrice)
          }
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            if (it.second.result == 0 && it.first.result == 0) {
              dismiss()
            } else {
              val builder = StringBuilder()
              if (it.first.result > 0) {
                builder.append(getString(R.string.hint_warning_custom_fee_gaslimit_too_big)).append("，")
              }
              if (it.first.result < 0) {
                builder.append(getString(R.string.hint_warning_custom_fee_gaslimit_too_small)).append("，")
              }
              if (it.second.result > 0) {
                builder.append(getString(R.string.hint_warning_custom_fee_gasprice_too_big)).append("，")
              }
              if (it.second.result < 0) {
                builder.append(getString(R.string.hint_warning_custom_fee_gasprice_too_small)).append("，")
              }
              builder.append(getString(R.string.hint_warning_whether_continue))

              DappResultAlertDialog(requireContext()).apply {
                setIcon(DappResultAlertDialog.WARNING)
                setTitle(R.string.title_warning)
                setMessage(builder.toString())
                setSecondaryButtonText(R.string.action_continue)
                setButtonListener {
                  dismiss()
                }
                setButtonText(R.string.cancel)
                setSecondaryButtonListener {
                  dismiss()
                  this@DappFeeCustomSheetDialog.dismiss()
                }
                show()
              }
            }
          }, {
            ToastUtils.toast(getString(R.string.hint_unknown_error))
          })
    } else {
      dismiss()
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    if (mViewModel.currentFee.value == FeeType.CUSTOM) {
      val feeBean = getCustomFeeBean(mViewModel.feeDetails.value?.normal, mViewModel.gasLimitNumber, mViewModel.gasPriceNumber)
      mViewModel.currentFee.value?.let {
        mOnSelectFeeCallback?.onSelectFee(it, feeBean.getGasPrice().toBigInteger(), feeBean.getGasLimit())
      }
    }
    super.onDismiss(dialog)
  }

  fun setOnFeeSelectCallback(callback: OnFeeSelectCallback): DappFeeCustomSheetDialog {
    mOnSelectFeeCallback = callback
    return this
  }

  interface OnFeeSelectCallback {
    fun onInit(init: OnInitDataInterface)

    fun onSelectFee(@FeeType feeType: Int, gasPrice: BigInteger, gas: BigInteger)
  }

  override fun onClick(v: View?) {
    dismiss()
  }
}

interface OnInitDataInterface {
  fun onDataInit(
      @FeeType feeType: Int? = null,
      currentFeeDetails: CurrentFeeDetails? = null,
      defGasLimit: BigInteger = BigInteger.ZERO,
      defGasPrice: BigDecimal = BigDecimal.ZERO,
  )
}

class FeeBean(
    private var gasPrice: BigDecimal = BigDecimal.ZERO,
    private var gasLimit: BigInteger = BigInteger.ZERO,
    private var fee: BigDecimal = BigDecimal.ZERO,
) {
  fun setGasPrice(gasPrice: BigDecimal) {
    this.gasPrice = gasPrice
  }

  fun setGasLimit(gasLimit: BigInteger) {
    this.gasLimit = gasLimit
  }

  fun getGasPrice() = gasPrice

  fun getGasLimit() = gasLimit

  fun getFee(): BigDecimal {
    return if (fee == BigDecimal.ZERO) {
      gasLimit.toBigDecimal().multiply(gasPrice)
    } else {
      fee
    }
  }
}

@IntDef(FeeType.SLOW, FeeType.NORMAL, FeeType.FAST, FeeType.CUSTOM)
annotation class FeeType {
  companion object {
    const val SLOW = 0
    const val NORMAL = 1
    const val FAST = 2
    const val CUSTOM = 3
  }
}

class CustomFeeViewModel : ViewModel() {
  val currentFee: MutableLiveData<Int> = MutableLiveData(null)
  val feeDetails: MutableLiveData<CurrentFeeDetails> = MutableLiveData(null)
  val coinFiat: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)
  val gasLimit: MutableLiveData<String> = MutableLiveData(null)
  val gasPrice: MutableLiveData<String> = MutableLiveData(null)

  val gasLimitNumber: BigInteger
    get() {
      return try {
        BigInteger(gasLimit.value?.toString() ?: "0")
      } catch (e: Exception) {
        BigInteger.ZERO
      }
    }

  val gasPriceNumber: BigDecimal
    get() {
      return try {
        Convert.toWei(BigDecimal(gasPrice.value?.toString()
            ?: "0"), Convert.Unit.GWEI)
      } catch (e: Exception) {
        BigDecimal.ZERO
      }
    }
}

data class Judgment(val result: Int, val hints: String? = null)

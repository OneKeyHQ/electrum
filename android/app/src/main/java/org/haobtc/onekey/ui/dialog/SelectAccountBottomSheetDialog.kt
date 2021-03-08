package org.haobtc.onekey.ui.dialog


import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.base.Strings
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.adapter.SelectAccountAdapter
import org.haobtc.onekey.bean.AllWalletBalanceBean
import org.haobtc.onekey.bean.AssetsBalance
import org.haobtc.onekey.bean.PyResponse
import org.haobtc.onekey.bean.WalletAccountBalanceInfo
import org.haobtc.onekey.business.assetsLogo.AssetsLogo
import org.haobtc.onekey.business.wallet.BalanceManager
import org.haobtc.onekey.business.wallet.DeviceManager
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.viewmodel.AppWalletViewModel


class SelectAccountBottomSheetDialog : BottomSheetDialogFragment() {
  companion object {
    private const val EXT_DATA = "data"

    @JvmStatic
    fun newInstance(data: Vm.CoinType): SelectAccountBottomSheetDialog {
      val selectAccountBottomSheetDialog = SelectAccountBottomSheetDialog()
      val bundle = Bundle()
      bundle.putString(EXT_DATA, data.callFlag)
      selectAccountBottomSheetDialog.arguments = bundle
      return selectAccountBottomSheetDialog
    }
  }

  private var mBehavior: BottomSheetBehavior<*>? = null
  private val mAdapter = SelectAccountAdapter()
  private var mLoadDisposable: Disposable? = null
  private var mSwitchDisposable: Disposable? = null

  private lateinit var mImgCoinType: ImageView
  private lateinit var mTvCoinType: TextView
  private lateinit var mRecyclerView: RecyclerView
  private var mOnSelectAccountCallback: OnSelectAccountCallback? = null
  private var mOnDismissListener: DialogInterface.OnDismissListener? = null
  private val mAppWalletViewModel by lazy {
    ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel::class.java)
  }
  private val mBalanceManager by lazy {
    BalanceManager()
  }

  fun setOnSelectAccountCallback(onCall: OnSelectAccountCallback): SelectAccountBottomSheetDialog {
    mOnSelectAccountCallback = onCall
    return this
  }

  fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): SelectAccountBottomSheetDialog {
    mOnDismissListener = listener
    return this
  }

  private val mDeviceManager by lazy {
    DeviceManager.getInstance()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    val view = View.inflate(context, R.layout.dialog_select_account, null)
    dialog.setContentView(view)
    mImgCoinType = view.findViewById(R.id.img_coin_type)
    mTvCoinType = view.findViewById(R.id.text_wallet_type)
    mRecyclerView = view.findViewById(R.id.recl_wallet_list)
    dialog.delegate
      ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        ?.setBackgroundColor(Color.TRANSPARENT)
    mBehavior = BottomSheetBehavior.from(view.parent as View)
    return dialog
  }

  override fun onStart() {
    super.onStart()
    dialog?.setOnDismissListener { dialog -> mOnDismissListener?.onDismiss(dialog) }
    initData()
  }

  private fun initData() {
    Vm.CoinType.convertByCallFlag(arguments?.getString(EXT_DATA)).let {

      when {
        it.callFlag.equals(Vm.CoinType.ETH.callFlag) -> {
          mTvCoinType.text = resources.getString(R.string.eth_wallet)
        }
        it.callFlag.equals(Vm.CoinType.BTC.callFlag) -> {
          mTvCoinType.text = resources.getString(R.string.btc_wallet)
        }
      }
      val drawableLogo = AssetsLogo().getLogoResources(it)
      mImgCoinType.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableLogo, null))
      mAdapter.setNewData(getWallets(it))
      getAccountBalance()
      mAdapter.setOnItemClickListener { adapter, view, position ->
        mAdapter.getItem(position)?.let { walletInfo ->
          if (mSwitchDisposable?.isDisposed() == false) {
            mSwitchDisposable?.dispose()
          }
          mSwitchDisposable = Single
            .fromCallable {
              mAppWalletViewModel.changeCurrentWallet(walletInfo.id)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              dismissAllowingStateLoss()
              mOnDismissListener?.onDismiss(dialog)
              mOnSelectAccountCallback?.onCall(walletInfo)
            }, {
              dismissAllowingStateLoss()
              mOnDismissListener?.onDismiss(dialog)
            })
        }
      }
      mRecyclerView.adapter = mAdapter
    }
  }

  private fun getAccountBalance() {
    if (mLoadDisposable?.isDisposed == false) {
      mLoadDisposable?.dispose()
    }
    mLoadDisposable = Observable.create(
        ObservableOnSubscribe { emitter: ObservableEmitter<PyResponse<AllWalletBalanceBean?>> ->
          val allWalletBalance: PyResponse<AllWalletBalanceBean?> = mBalanceManager.getAllWalletBalances()
          emitter.onNext(allWalletBalance)
          emitter.onComplete()
        } as ObservableOnSubscribe<PyResponse<AllWalletBalanceBean?>>)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            { allWalletBalanceString: PyResponse<AllWalletBalanceBean?> ->
              if (Strings.isNullOrEmpty(allWalletBalanceString.errors)) {
                val allWalletBalance = allWalletBalanceString.result
                if (allWalletBalance != null
                    && allWalletBalance.walletInfo.isNotEmpty()) {
                  val currentAccount = mAdapter.data.map { it.id to it }.toMap()
                  allWalletBalance.walletInfo.forEach {
                    if (currentAccount.containsKey(it.label)) {
                      currentAccount[it.label]?.balance = AssetsBalance(it.balance
                          ?: "0", it.coinType.defUnit)
                    }
                  }
                  mAdapter.notifyDataSetChanged()
                }
              }
            }
        ) { e: Throwable ->
          e.printStackTrace()
        }
  }

  override fun onDestroyView() {
    if (mSwitchDisposable?.isDisposed == false) {
      mSwitchDisposable?.dispose()
    }
    if (mLoadDisposable?.isDisposed == false) {
      mLoadDisposable?.dispose()
    }
    super.onDestroyView()
  }

  fun getWallets(coinType: Vm.CoinType): List<WalletAccountBalanceInfo> {
    val response = PyEnv.loadWalletByType(coinType.callFlag)
    if (Strings.isNullOrEmpty(response.errors)) {
      return response.result.map { info ->
        var hardwareLabel = "";
        info.deviceId?.let {
          val deviceInfo = mDeviceManager.getDeviceInfo(info.deviceId)
          deviceInfo?.let {
            when {
              !Strings.isNullOrEmpty(deviceInfo.label) -> hardwareLabel = deviceInfo.label
              else -> {
                deviceInfo.bleName?.let { hardwareLabel = deviceInfo.bleName }
              }
            }
          }
        }
        WalletAccountBalanceInfo.convert(
          info.type,
          info.addr,
          info.name,
          info.label,
          info.deviceId,
          hardwareLabel
        )
      }
    } else {
      return ArrayList()
    }
  }
}

fun interface OnSelectAccountCallback {
  fun onCall(info: WalletAccountBalanceInfo)
}

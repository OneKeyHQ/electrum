package org.haobtc.onekey.viewmodel

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.AssetsBalance
import org.haobtc.onekey.bean.AssetsBalanceFiat
import org.haobtc.onekey.bean.CoinAssets
import org.haobtc.onekey.bean.DEF_WALLET_FIAT_BALANCE
import org.haobtc.onekey.bean.ERC20Assets
import org.haobtc.onekey.bean.LocalImage
import org.haobtc.onekey.bean.LocalWalletInfo
import org.haobtc.onekey.bean.RemoteImage
import org.haobtc.onekey.bean.WalletAccountInfo
import org.haobtc.onekey.bean.WalletAccountInfo.Companion.convert
import org.haobtc.onekey.business.assetsLogo.AssetsLogo
import org.haobtc.onekey.business.wallet.AccountManager
import org.haobtc.onekey.business.wallet.BalanceManager
import org.haobtc.onekey.business.wallet.SystemConfigManager
import org.haobtc.onekey.business.wallet.TokenManager
import org.haobtc.onekey.constant.Vm.CoinType.ETH
import org.haobtc.onekey.constant.Vm.CoinType.convertByCallFlag
import org.haobtc.onekey.event.CreateSuccessEvent
import org.haobtc.onekey.event.LoadOtherWalletEvent
import org.haobtc.onekey.event.SecondEvent
import org.haobtc.onekey.manager.PyEnv
import java.math.BigDecimal
import java.util.concurrent.Executors

/**
 * 存放 App 当前余额，钱包类型，Application 生命周期的 ViewModel
 *
 * @author Onekey@QuincySx
 * @create 2021-01-06 11:09 AM
 */
class AppWalletViewModel : ViewModel() {
  private val mExecutorService = Executors.newFixedThreadPool(4)
  private val mMainHandler = Handler(Looper.getMainLooper())
  private val mBalanceManager = BalanceManager()
  private val mAssetsLogo = AssetsLogo()
  private val mAccountManager = AccountManager(MyApplication.getInstance())
  private val mTokenManager = TokenManager()
  private val mSystemConfigManager = SystemConfigManager(MyApplication.getInstance())

  private var mOldAccountName: String? = null

  @JvmField
  val existsWallet = MutableLiveData<Boolean>()

  @JvmField
  val currentWalletAccountInfo = MutableLiveData<WalletAccountInfo?>()

  @JvmField
  val currentWalletAssetsList = MutableLiveData(AssetsList())

  @JvmField
  val currentWalletTotalBalanceFiat = MutableLiveData(DEF_WALLET_FIAT_BALANCE)

  @JvmField
  @Deprecated("")
  val currentWalletBalance = MutableLiveData(AssetsBalance(BigDecimal("0"), "BTC"))

  fun submit(runnable: Runnable) {
    mExecutorService.submit(runnable)
  }

  @WorkerThread
  fun refresh() {
    refreshExistsWallet()
    refreshWalletInfo()
  }

  @WorkerThread
  fun refreshExistsWallet() {
    existsWallet.postValue(mAccountManager.existsWallets())
  }

  @WorkerThread
  fun refreshWalletInfo() {
    val currentWalletName = mAccountManager.currentWalletName
    var localWallet = mAccountManager.getLocalWalletByName(currentWalletName)
    if (localWallet == null) {
      // 容错处理：如果本地信息存储错误，则随机选择一下钱包账户。
      localWallet = mAccountManager.autoSelectNextWallet()
    }
    setCurrentWalletInfo(localWallet)
  }

  private fun refreshAssets(walletAccountInfo: WalletAccountInfo?): AssetsList? {
    if (walletAccountInfo == null) {
      return null
    }
    val isOriginalAccount = walletAccountInfo.id == mOldAccountName
    val currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit(
        walletAccountInfo.coinType)

    val walletAssets = mAccountManager.getWalletAssets(walletAccountInfo.id)
    val assets = AssetsList()
    val coinAssets = CoinAssets(
        walletAccountInfo.coinType,
        walletAccountInfo.coinType.coinName,
        walletAccountInfo.coinType.digits,
        "",
        LocalImage(
            mAssetsLogo.getLogoResources(walletAccountInfo.coinType)),
        AssetsBalance("0", currentBaseUnit)
    )
    if (isOriginalAccount) {
      currentWalletAssetsList.value?.getByUniqueId(coinAssets.uniqueId())?.let {
        coinAssets.balance = it.balance
        coinAssets.balanceFiat = it.balanceFiat
      }
    }
    assets.add(coinAssets)
    walletAssets?.wallets
        ?.filter { it.contractAddress.isNotEmpty() }
        ?.forEach {
          if (walletAccountInfo.coinType == ETH) {
            mTokenManager.getTokenByAddress(it.contractAddress)?.let { tokenByAddress ->
              val erC20Assets = ERC20Assets(
                  it.contractAddress,
                  tokenByAddress.symbol,
                  tokenByAddress.decimals,
                  tokenByAddress.name,
                  RemoteImage(tokenByAddress.logoURI),
                  AssetsBalance("0", tokenByAddress.symbol)
              )
              if (isOriginalAccount) {
                currentWalletAssetsList.value?.getByUniqueId(erC20Assets.uniqueId())?.let { assets ->
                  erC20Assets.balance = assets.balance
                  erC20Assets.balanceFiat = assets.balanceFiat
                }
              }
              assets.add(erC20Assets)
            }
          }
        }
    currentWalletAssetsList.postValue(assets)
    return assets
  }

  private fun refreshBalance(walletAccountInfo: WalletAccountInfo?, assets: AssetsList) {
    if (walletAccountInfo == null) {
      return
    }
    val currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit(
        walletAccountInfo.coinType)
    val currentFiatUnitSymbol = mSystemConfigManager.currentFiatUnitSymbol
    val balance = mBalanceManager.currentBalance

    balance?.allBalance?.let {
      currentWalletTotalBalanceFiat.postValue(AssetsBalanceFiat.fromAmountAndUnit(
          it,
          currentFiatUnitSymbol.symbol))
    }
    val triggerMark = TriggerMark()
    balance?.wallets?.filterNotNull()?.forEachIndexed { index, walletBalanceBean ->
      val generateUniqueId = if (index == 0) {
        CoinAssets.generateUniqueId(walletAccountInfo.coinType)
      } else {
        walletBalanceBean.contractAddress?.let { ERC20Assets.generateUniqueId(it, walletAccountInfo.coinType) }
      } ?: return@forEachIndexed

      setBalance(
          triggerMark,
          generateUniqueId,
          walletBalanceBean.balance,
          currentBaseUnit,
          walletBalanceBean.fiat,
          currentFiatUnitSymbol.symbol
      )
    }
    if (triggerMark.isTriggered()) {
      currentWalletAssetsList.postValue(assets)
    }
  }

  /**
   * 切换当前钱包
   *
   * @param name 钱包名称
   */
  @WorkerThread
  fun changeCurrentWallet(name: String) {
    val localWalletInfo = mAccountManager.selectWallet(name)
    if (localWalletInfo == null) {
      mSystemConfigManager.passWordType = SystemConfigManager.SoftHdPassType.SHORT
    }
    setCurrentWalletInfo(localWalletInfo)
    refreshExistsWallet()
  }

  /**
   * 自动选择并切换一个钱包
   */
  @WorkerThread
  fun autoSelectWallet() {
    val localWalletInfo = mAccountManager.autoSelectNextWallet()
    if (localWalletInfo == null) {
      mSystemConfigManager.passWordType = SystemConfigManager.SoftHdPassType.SHORT
    }
    mMainHandler.post {
      setCurrentWalletInfo(localWalletInfo)
      refreshExistsWallet()
    }
  }

  /**
   * 删除钱包后选择其他钱包
   */
  @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
  fun event(event: LoadOtherWalletEvent?) {
    submit(::autoSelectWallet)
  }

  @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
  fun event(event: SecondEvent) {
    mExecutorService.execute {
      val eventBean = mBalanceManager.decodePythonBalanceNotice(event.msg)
      if (currentWalletAccountInfo.value == null) {
        return@execute
      }

      val address = currentWalletAccountInfo.value?.address
      if (eventBean == null || address == null || !address.equals(eventBean.address, true)) {
        return@execute
      }
      val coinType = convertByCallFlag(eventBean.coin)
      val currentBaseUnit = mSystemConfigManager.getCurrentBaseUnit(coinType)
      val currentFiatUnitSymbol = mSystemConfigManager.currentFiatUnitSymbol

      eventBean.sumFiat?.let {
        currentWalletTotalBalanceFiat.postValue(AssetsBalanceFiat.fromAmountAndUnit(
            it,
            currentFiatUnitSymbol.symbol))
      }

      val triggerMark = TriggerMark()

      setBalance(
          triggerMark,
          CoinAssets.generateUniqueId(coinType),
          eventBean.balance,
          currentBaseUnit,
          eventBean.fiat,
          currentFiatUnitSymbol.symbol
      )

      eventBean.tokens?.forEach {
        setBalance(
            triggerMark,
            ERC20Assets.generateUniqueId(it.address, coinType),
            it.balance,
            currentBaseUnit,
            it.fiat,
            currentFiatUnitSymbol.symbol
        )
      }

      if (triggerMark.isTriggered()) {
        currentWalletAssetsList.postValue(currentWalletAssetsList.value)
      }
    }
  }

  private fun setBalance(triggerMark: TriggerMark, assetsId: Int, balance: String, balanceUnit: String, balanceFiat: String, fiatSymbol: String) {
    currentWalletAssetsList.value?.getByUniqueId(assetsId)?.let {
      val assetsBalance = if (it is CoinAssets) {
        AssetsBalance(balance, balanceUnit)
      } else {
        AssetsBalance(balance, it.balance.unit)
      }
      val assetsBalanceFiat = AssetsBalanceFiat.fromAmountAndUnit(balanceFiat, fiatSymbol)

      val setBalance = checkRepeatAssignment(it.balance, assetsBalance)
      val setBalanceFiat = checkRepeatAssignment(it.balanceFiat, assetsBalanceFiat)

      triggerMark.trigger(setBalance || setBalanceFiat)
      if (setBalance || setBalanceFiat) {
        val newInstance = it.newInstance()
        newInstance.balance = assetsBalance
        newInstance.balanceFiat = assetsBalanceFiat
        currentWalletAssetsList.value?.replace(newInstance)
      }
    }
  }

  private fun <T> checkRepeatAssignment(source: T, value: T): Boolean {
    if (source == null) {
      return true
    }
    if (value != source
        || (value is Number && value != source)) {
      return true
    }
    return false
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onCreateWalletSuccess(event: CreateSuccessEvent) {
    PyEnv.loadLocalWalletInfo(MyApplication.getInstance())
    if (TextUtils.isEmpty(event.name)) {
      // 容错处理：如果有人发送一个空的名字，则随机选择一下钱包账户。
      autoSelectWallet()
    } else {
      changeCurrentWallet(event.name)
    }
  }

  override fun onCleared() {
    mExecutorService.shutdown()
    EventBus.getDefault().unregister(this)
    super.onCleared()
  }

  private fun setCurrentWalletInfo(info: WalletAccountInfo?) {
    currentWalletAccountInfo.postValue(info)

    refreshAssets(info)?.let {
      if (info?.id != mOldAccountName) {
        // 切换账户清零总金额
        mOldAccountName = info?.id
        val currentFiatUnitSymbol = mSystemConfigManager.currentFiatUnitSymbol
        currentWalletTotalBalanceFiat.postValue(AssetsBalanceFiat(
            DEF_WALLET_FIAT_BALANCE.balance,
            currentFiatUnitSymbol.unit,
            currentFiatUnitSymbol.symbol))
      }
      refreshBalance(info, it)
    }
  }

  private fun setCurrentWalletInfo(info: LocalWalletInfo?) {
    setCurrentWalletInfo(convert(info))
  }

  init {
    EventBus.getDefault().register(this)
    refresh()
  }

  companion object {
    fun convert(info: LocalWalletInfo?): WalletAccountInfo? {
      return if (info == null) {
        null
      } else {
        convert(
            info.type ?: "",
            info.addr ?: "",
            info.name ?: "",
            info.label ?: "",
            info.deviceId ?: null
        )
      }
    }
  }
}

/**
 * 触发标志
 */
class TriggerMark {
  private var activation: Boolean = false
  fun isTriggered() = activation
  fun trigger(b: Boolean) {
    if (b) {
      activation = true
    }
  }
}

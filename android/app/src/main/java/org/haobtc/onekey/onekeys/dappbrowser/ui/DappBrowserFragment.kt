package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.logger.Logger
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.state.SuccessState
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.haobtc.onekey.BuildConfig
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.DAppBrowserBean
import org.haobtc.onekey.business.assetsLogo.AssetsLogo
import org.haobtc.onekey.business.wallet.DappWeb3Manager
import org.haobtc.onekey.business.wallet.DeviceException
import org.haobtc.onekey.constant.PyConstant
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.databinding.FragmentDappBrowserBinding
import org.haobtc.onekey.event.ButtonRequestEvent
import org.haobtc.onekey.event.ChangePinEvent
import org.haobtc.onekey.exception.PyEnvException
import org.haobtc.onekey.extensions.cutTheLast
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.onekeys.dappbrowser.URLLoadInterface
import org.haobtc.onekey.onekeys.dappbrowser.Web3View
import org.haobtc.onekey.onekeys.dappbrowser.bean.Address
import org.haobtc.onekey.onekeys.dappbrowser.bean.DAppFunction
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumMessage
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumTypedMessage
import org.haobtc.onekey.onekeys.dappbrowser.bean.SignMessageType
import org.haobtc.onekey.onekeys.dappbrowser.bean.Signable
import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction
import org.haobtc.onekey.onekeys.dappbrowser.callback.DappActionSheetCallback
import org.haobtc.onekey.onekeys.dappbrowser.callback.SignAuthenticationCallback
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignMessageListener
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignPersonalMessageListener
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTransactionListener
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTypedMessageListener
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappSettingSheetDialog.ClickType.Companion.CLICK_BROWSER
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappSettingSheetDialog.ClickType.Companion.CLICK_COPY_URL
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappSettingSheetDialog.ClickType.Companion.CLICK_REFRESH
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappSettingSheetDialog.ClickType.Companion.CLICK_SHARE
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappSettingSheetDialog.ClickType.Companion.CLICK_SWITCH_ACCOUNT
import org.haobtc.onekey.ui.activity.HardwarePinDialog
import org.haobtc.onekey.ui.activity.HardwarePinFragment
import org.haobtc.onekey.ui.activity.InputPinOnHardware
import org.haobtc.onekey.ui.activity.SoftPassDialog
import org.haobtc.onekey.ui.base.BaseFragment
import org.haobtc.onekey.ui.dialog.SelectAccountBottomSheetDialog
import org.haobtc.onekey.ui.status.BrowserLoadError
import org.haobtc.onekey.utils.ClipboardUtils
import org.haobtc.onekey.utils.HexUtils
import org.haobtc.onekey.utils.Utils
import org.haobtc.onekey.viewmodel.AppWalletViewModel
import org.web3j.utils.Numeric
import java.util.*

/**
 * Dapp 浏览器
 *
 * @author Onekey@QuincySx
 * @create 2021-03-02 2:23 PM
 */
class DappBrowserFragment : BaseFragment(),
    OnBackPressedCallback,
    URLLoadInterface,
    OnSignTransactionListener,
    OnSignPersonalMessageListener,
    OnSignTypedMessageListener,
    OnSignMessageListener,
    DappActionSheetCallback,
    SignAuthenticationCallback,
    CurrentCoinTypeProvider {
  companion object {
    val TAG: String = DappBrowserFragment::class.java.simpleName

    @JvmStatic
    val DAPP_DEFAULT_URL = "https://app.uniswap.org/#/swap"

    @JvmStatic
    val DAPP_PREFIX_TELEPHONE = "tel"

    @JvmStatic
    val DAPP_PREFIX_MAILTO = "mailto"

    @JvmStatic
    val DAPP_PREFIX_ONEKEYWALLET = "onekeywallet"

    @JvmStatic
    val DAPP_SUFFIX_RECEIVE = "receive"

    @JvmStatic
    val DAPP_PREFIX_MAPS = "maps.google.com/maps?daddr="

    @JvmStatic
    val DAPP_PREFIX_WALLETCONNECT = "wc"

    private const val GOOGLE_SEARCH_PREFIX = "https://www.google.com/search?q="
    private const val HTTPS_PREFIX = "https://"

    private const val UPLOAD_FILE = 1
    private const val REQUEST_FILE_ACCESS = 31
    private const val REQUEST_FINE_LOCATION = 110

    private const val EXT_CURRENT_URL = "current_url"
    private const val EXT_URL = "url"
    private const val EXT_DAPP_BEAN = "data"

    @JvmStatic
    fun start(url: String, dAppBrowserBean: DAppBrowserBean?): Fragment {
      val dappBrowserFragment = DappBrowserFragment()
      dappBrowserFragment.arguments = Bundle().apply {
        putString(EXT_URL, url)
        putParcelable(EXT_DAPP_BEAN, dAppBrowserBean)
      }
      return dappBrowserFragment
    }

    // utils fun
    private fun formatUrl(url: String?): String {
      return if (url != null && (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url) || URLUtil.isAssetUrl(url))) {
        url
      } else {
        if (url != null && isValidUrl(url)) {
          HTTPS_PREFIX + url
        } else {
          GOOGLE_SEARCH_PREFIX + url
        }
      }
    }

    private fun removeHTTPProtocol(url: String?): String {
      return if (url != null && URLUtil.isHttpsUrl(url)) {
        url.replace("https://", "")
      } else if (url != null && URLUtil.isHttpUrl(url)) {
        url.replace("http://", "")
      } else {
        url ?: ""
      }
    }

    private fun isValidUrl(url: String): Boolean {
      val p = Patterns.WEB_URL
      val m = p.matcher(url.toLowerCase(Locale.ROOT))
      return m.matches()
    }
  }

  // Web 首次加载的 Url
  private var mLoadOnInit: String? = null
  private var mHomePressed = false

  // 当前网页状态
  private var mCurrentWebpageTitle: String? = null

  // 签字授权中断数据临时存储
  private var messageTBS: Signable? = null
  private var dAppFunction: DAppFunction? = null

  // 弹窗
  private var resultDialog: DappResultAlertDialog? = null
  private var confirmationDialog: DappActionSheetDialog? = null
  private var connectDeviceDialog: ConnectDeviceDialog? = null
  private var hardwarePinDialog: HardwarePinDialog? = null

  // 选择上传图片相关
  private var mUploadMessage: ValueCallback<Array<Uri>>? = null
  private var mFileChooserParams: FileChooserParams? = null
  private var mPicker: Intent? = null

  // 地理位置相关
  private var mGeoCallback: GeolocationPermissions.Callback? = null
  private var mGeoOrigin: String? = null

  // Host CallBack
  private var mOnFinishOrBackCallback: OnFinishOrBackCallback? = null

  private lateinit var mBinding: FragmentDappBrowserBinding
  private val mMultiStateContainer by lazy {
    MultiStatePage.bindMultiState(mBinding.viewWeb3view)
  }

  private val mDappWeb3Manager = DappWeb3Manager()
  private val web3: Web3View
    get() = mBinding.viewWeb3view
  private val mAppWalletViewModel by lazy {
    ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel::class.java)
  }
  private val mDAppBean by lazy {
    arguments?.getParcelable(EXT_DAPP_BEAN) as DAppBrowserBean?
  }

  override fun needEvents() = true

  override fun enableViewBinding() = true

  override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    mBinding = FragmentDappBrowserBinding.inflate(inflater, container, false)
    return mBinding.root
  }

  override fun init(view: View) {}

  override fun getContentViewId(): Int {
    return 0
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is OnFinishOrBackCallback) {
      mOnFinishOrBackCallback = context
      context.setOnBackPressed(this)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    web3.setBackgroundColor(Color.parseColor("#F9F9FB"))

    initLoadUrl(savedInstanceState)

    setupViewModule()

    setupClickListener()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(EXT_CURRENT_URL, web3.url)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_FILE_ACCESS -> gotFileAccess(permissions, grantResults)
      REQUEST_FINE_LOCATION -> gotGeoAccess(permissions, grantResults)
    }
  }

  private fun initLoadUrl(savedInstanceState: Bundle?) {
    // Load url from a link within the app
    mLoadOnInit = if (arguments != null && arguments?.getString(EXT_URL) != null) {
      arguments?.getString(EXT_URL) ?: DAPP_DEFAULT_URL
    } else {
      if (savedInstanceState != null) {
        savedInstanceState.getString(EXT_CURRENT_URL, DAPP_DEFAULT_URL)
      } else {
        DAPP_DEFAULT_URL
      }
    }
  }

  private fun setupViewModule() {
    mAppWalletViewModel.currentWalletAccountInfo.observe(viewLifecycleOwner) {
      val drawable = ResourcesCompat.getDrawable(resources, AssetsLogo.getLogoResources(it?.coinType), null)
      mBinding.ivTokenLogo.setImageDrawable(drawable)
      mBinding.tvWalletName.text = it?.address?.cutTheLast(4)
          ?: getString(R.string.title_select_account)

      it?.let {
        refreshEvent()
      }
    }
  }

  private fun setupClickListener() {
    mBinding.ivBack.setOnClickListener { goToPreviousPage() }
    mBinding.ivClose.setOnClickListener { mOnFinishOrBackCallback?.finish() }
    mBinding.layoutChangeAccount.setOnClickListener { showChangeAccountDialog() }
    mBinding.ivShare.setOnClickListener {
      val dappName = mDAppBean?.name ?: getString(R.string.app_name)
      val content = mDAppBean?.subtitle ?: ""
      val imageLogo = mDAppBean?.getLogoImage() ?: ""
      DappSettingSheetDialog.newInstance(dappName, content, imageLogo)
          .setOnSettingHandleClickCallback {
            when (it) {
              CLICK_SWITCH_ACCOUNT -> {
                showChangeAccountDialog()
              }
              CLICK_BROWSER -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(web3.url)))
              }
              CLICK_REFRESH -> {
                refreshEvent()
              }
              CLICK_COPY_URL -> {
                // copy text
                ClipboardUtils.copyText(requireContext(), web3.url)
              }
              CLICK_SHARE -> {
                val shareIntent = Intent(Intent.ACTION_SEND).also { intent ->
                  intent.type = "text/*"
                  intent.putExtra(Intent.EXTRA_TEXT, web3.url)
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                  Intent.createChooser(intent, getString(R.string.share_to))
                }
                startActivity(shareIntent)
              }
            }
          }
          .show(childFragmentManager, "Setting")
    }
  }

  /**
   * 设置 Web3 如果切换账户记得重新刷新网页
   */
  private fun setupWeb3() {
    web3.setActivity(activity)
    web3.setWebLoadCallback(this)

    mDappWeb3Manager.getPRCInfo()?.apply {
      web3.setRpcUrl(rpc)
      web3.chainId = chainId
    }
    mAppWalletViewModel.currentWalletAccountInfo.value?.address?.let {
      web3.setWalletAddress(it)
    }

    web3.webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(webview: WebView, newProgress: Int) {
        if (newProgress == 100) {
          mBinding.progressBar.visibility = View.GONE
        } else {
          mBinding.progressBar.visibility = View.VISIBLE
          mBinding.progressBar.progress = newProgress
        }
      }

      override fun onReceivedTitle(view: WebView, title: String) {
        super.onReceivedTitle(view, title)
        mCurrentWebpageTitle = title
      }

      override fun onGeolocationPermissionsShowPrompt(
          origin: String,
          callback: GeolocationPermissions.Callback,
      ) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
        requestGeoPermission(origin, callback)
      }

      override fun onShowFileChooser(
          webView: WebView, filePathCallback: ValueCallback<Array<Uri>>?,
          fCParams: FileChooserParams,
      ): Boolean {
        if (filePathCallback == null) return true
        mUploadMessage = filePathCallback
        mFileChooserParams = fCParams
        mPicker = mFileChooserParams?.createIntent()
        return if (checkReadPermission()) requestUpload() else true
      }

      override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
        if (BuildConfig.DEBUG) {
          Log.d("WebView Log", "$message -- From line $lineNumber of $sourceID")
        }
      }
    }
    web3.setWebViewClient(object : WebViewClient() {
      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
      }

      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val prefixCheck = url.split(":").toTypedArray()
        if (prefixCheck.size > 1) {
          val intent: Intent
          when (prefixCheck[0]) {
            DAPP_PREFIX_TELEPHONE -> {
              intent = Intent(Intent.ACTION_DIAL)
              intent.data = Uri.parse(url)
              startActivity(Intent.createChooser(intent, "Call " + prefixCheck[1]))
              return true
            }
            DAPP_PREFIX_MAILTO -> {
              intent = Intent(Intent.ACTION_SENDTO)
              intent.data = Uri.parse(url)
              startActivity(Intent.createChooser(intent, "Email: " + prefixCheck[1]))
              return true
            }
            DAPP_PREFIX_ONEKEYWALLET -> if (prefixCheck[1] == DAPP_SUFFIX_RECEIVE) {
              // todo Show Collection Code
              return false
            }
            DAPP_PREFIX_WALLETCONNECT -> {
              // todo WalletConnect
              return false
            }
            else -> {
            }
          }
        }

        setDappTitle(url)
        return false
      }
    })
    web3.setOnSignMessageListener(this)
    web3.setOnSignPersonalMessageListener(this)
    web3.setOnSignTransactionListener(this)
    web3.setOnSignTypedMessageListener(this)
    if (mLoadOnInit != null) {
      web3.loadUrl(formatUrl(mLoadOnInit), getWeb3Headers())
      setDappTitle(formatUrl(mLoadOnInit))
    }
  }


  // region: 处理 Dapp Action 弹窗 SignAuthenticationCallback 回调操作

  /**
   * 处理 SignMessage 授权回调
   */
  override fun gotAuthorisation(pwd: String?, gotAuth: Boolean) {
    if (gotAuth && dAppFunction != null) {
      Logger.e("==signMessage==:${HexUtils.byteArrayToHexString(messageTBS?.prehash)}")
      // complete Authentication
      val walletInfo = mAppWalletViewModel.currentWalletAccountInfo.value
      val messageHexString = HexUtils.byteArrayToHexString(messageTBS?.prehash)
      val success: (String) -> Unit = {
        confirmationDialog?.hideProgress()
        try {
          Utils.finishActivity(InputPinOnHardware::class.java)
        } catch (e: Exception) {
        }
        dAppFunction?.DAppReturn(HexUtils.hexStringToByteArray(it), messageTBS)
      }
      val error: (Exception) -> Unit = {
        hardwarePinDialog?.onFinish()
        try {
          Utils.finishActivity(InputPinOnHardware::class.java)
        } catch (e: Exception) {
        }
        it.message?.let { it1 -> showErrorDialog(it1, getString(R.string.hint_message_signature_failed)) }
        confirmationDialog?.dismiss()
        dAppFunction?.DAppError(it, messageTBS)
      }

      if (messageHexString != null && walletInfo?.walletType == Vm.WalletType.HARDWARE) {
        confirmationDialog?.showHardwareProgress()
        mDappWeb3Manager.signMessageByHardware(walletInfo.address, messageHexString, null, success, error)
      } else if (messageHexString != null && walletInfo != null && walletInfo.walletType != Vm.WalletType.IMPORT_WATCH) {
        confirmationDialog?.showProgress()
        mDappWeb3Manager.signMessage(walletInfo.address, messageHexString, pwd, null, success, error)
      }
    } else if (confirmationDialog != null && confirmationDialog?.isShowing == true) {
      if (messageTBS != null) web3.onSignCancel(messageTBS!!.callbackId)
      confirmationDialog?.dismiss()
    }
  }

  /**
   * Endpoint from PIN/Swipe authorisation
   * @param gotAuth
   */
  fun pinAuthorisation(pwd: String?, gotAuth: Boolean) {
    if (confirmationDialog != null && confirmationDialog?.isShowing == true) {
      confirmationDialog?.completeSignRequest(pwd, gotAuth)
    }
  }

  override fun cancelAuthentication() {

  }
  // endregion


  // region 处理硬件链接
  private fun getHardwareAuthorisation(deviceId: String) {
    if (connectDeviceDialog == null || connectDeviceDialog?.isShowing() == false) {
      connectDeviceDialog = context?.let { ConnectDeviceDialog(it) }?.apply {
        setOnConnectListener({
          pinAuthorisation(null, true)
        }, { e ->
          when (e) {
            is DeviceException.OnCancelError -> {
              // ignore
            }
            is DeviceException.OnTimeoutError -> {
              showErrorDialog(
                  getString(R.string.hint_device_connect_timeout),
                  getString(R.string.hint_device_connect_error_title)
              )
            }
            is PyEnvException.ForcedHardwareUpgradeException -> {
              showErrorDialog(
                  getString(R.string.hint_device_connect_hardware_upgrade_error_title),
                  getString(R.string.hint_forced_hardware_upgrade),
                  DappResultAlertDialog.WARNING
              )
            }
            else -> {
              showErrorDialog(
                  getString(R.string.hint_device_connect_error),
                  getString(R.string.hint_device_connect_error_title)
              )
            }
          }
        })
        show()
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onChangePin(event: ChangePinEvent) {
    // 回写PIN码
    PyEnv.setPin(event.toString())
  }

  /**
   * 接收硬件的按键事件
   */
  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onButtonRequest(event: ButtonRequestEvent) {
    when (event.type) {
      PyConstant.PIN_CURRENT -> {
        hardwarePinDialog?.dismiss()
        hardwarePinDialog = HardwarePinDialog.newInstance(HardwarePinFragment.PinActionType.VERIFY_PIN)
        hardwarePinDialog?.setOnInputSuccessListener(object : HardwarePinDialog.OnInputSuccessListener {
          override fun onSuccess() {

          }

          override fun onCancel() {
            web3.onCallFunctionError(Web3View.JS_SING_ID, "User cancelled")
          }
        })
        hardwarePinDialog?.show(childFragmentManager, "HardwarePinDialog")
      }
      PyConstant.BUTTON_REQUEST_7 -> {
        // 确认键
      }
      PyConstant.BUTTON_REQUEST_8 -> {
        // 转账确认键
      }
      else -> {
      }
    }
  }

  // endregion

  // region 处理 Dapp 弹窗 DappActionSheetCallback 回调操作
  /**
   * 获取签字权限和密码
   * 弹窗 UI
   */
  override fun getAuthorisation(callback: SignAuthenticationCallback) {
    val walletInfo = mAppWalletViewModel.currentWalletAccountInfo.value
    // 观察钱包无法使用
    if (walletInfo?.walletType == Vm.WalletType.IMPORT_WATCH) {
      val dialog = BaseAlertBottomDialog(requireContext())
      dialog.show()
      dialog.setTitle(R.string.hint_please_switch_account_observe)
      dialog.setMessage(R.string.hint_account_observe_unavailable_content)
      return
    }
    // 硬件钱包授权
    if (walletInfo?.walletType == Vm.WalletType.HARDWARE) {
      getHardwareAuthorisation(walletInfo.deviceId!!)
      return
    }
    showPasswordDialog(success = {
      // todo 如果是硬件先连接硬件
      pinAuthorisation(it, true)
    }, cancel = {
      callback.cancelAuthentication()
    })
  }

  override fun signTransaction(pwd: String?, finalTx: Web3Transaction) {
    val callback: SignTransactionInterface = object : SignTransactionInterface {
      override fun transactionSuccess(web3Tx: Web3Transaction, rawTx: String) {
        // 处理 Dapp 交易签字成功
        confirmationDialog?.hideProgress()
        confirmationDialog?.transactionWritten(rawTx)
        try {
          Utils.finishActivity(InputPinOnHardware::class.java)
        } catch (e: Exception) {
        }
        web3.onSignTransactionSuccessful(web3Tx, rawTx)
      }

      override fun transactionError(callbackId: Long, error: Throwable) {
        // 处理 Dapp 消息签字取消
        error.printStackTrace()
        confirmationDialog?.hideProgress()
        confirmationDialog?.dismiss()
        try {
          Utils.finishActivity(InputPinOnHardware::class.java)
        } catch (e: Exception) {
        }
        hardwarePinDialog?.onFinish()
        txError(error)
        web3.onSignCancel(callbackId)
      }
    }
    val walletInfo = mAppWalletViewModel.currentWalletAccountInfo.value
    if (walletInfo?.walletType == Vm.WalletType.HARDWARE) {
      confirmationDialog?.showHardwareProgress()
      mDappWeb3Manager.signTxByHardware(walletInfo.address, finalTx, null, callback)
    } else if (walletInfo != null && walletInfo.walletType != Vm.WalletType.IMPORT_WATCH) {
      confirmationDialog?.showProgress()
      mDappWeb3Manager.signTx(walletInfo.address, finalTx, pwd, null, callback)
    }
  }

  override fun sendTransaction(pwd: String?, finalTx: Web3Transaction) {
    val callback: SendTransactionInterface = object : SendTransactionInterface {
      override fun transactionSuccess(web3Tx: Web3Transaction, hashData: String) {
        confirmationDialog?.hideProgress()
        confirmationDialog?.transactionWritten(hashData)
        web3.onSignTransactionSuccessful(web3Tx, hashData)
      }

      override fun transactionError(callbackId: Long, error: Throwable) {
        confirmationDialog?.hideProgress()
        confirmationDialog?.dismiss()
        hardwarePinDialog?.onFinish()
        txError(error)
        web3.onSignCancel(callbackId)
      }
    }
    // todo createTransactionWithSig
    // createTransactionWithSig(finalTx, networkInfo.chainId, callback);
  }

  override fun dismissed(txHash: String?, callbackId: Long, actionCompleted: Boolean) {
    // 如果没有完成签字，则取消签字
    if (!actionCompleted) {
      web3.onSignCancel(callbackId)
    }
  }

  override fun notifyConfirm(mode: String?) {
    // todo
  }
  // endregion


  // region : 监听 Dapp 调用 Web3 时被 Hook 的方法调用

  /**
   * 监听 Dapp 发起的 SignTransaction 操作
   * @param transaction 交易
   * @url Dapp 链接
   */
  override fun onSignTransaction(transaction: Web3Transaction, url: String) {
    try {
      val transactionSuccess = transaction.recipient.equals(Address.EMPTY) && transaction.payload != null
          || !transaction.recipient.equals(Address.EMPTY) && (transaction.payload != null || transaction.value != null)
      if ((confirmationDialog == null || confirmationDialog?.isShowing == false) &&
          transactionSuccess
      ) {
        val currentWallet = mAppWalletViewModel.currentWalletAccountInfo.value
        if (currentWallet == null) {
          web3.onSignCancel(transaction.leafPosition)
          return
        }
        confirmationDialog = activity?.let { DappActionSheetDialog(it, transaction, currentWallet, this,this) }
        confirmationDialog?.apply {
          setSignOnly()
          setURL(url)
          setCanceledOnTouchOutside(false)
          show()
        }
        // todo 计算手续费
        return
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    // 交易逻辑处理错误
    onInvalidTransaction(transaction)
    web3.onSignCancel(transaction.leafPosition)
  }

  /**
   * 监听 Dapp 发起的 onSignPersonalMessage 操作
   * @param message 信息
   */
  override fun onSignPersonalMessage(message: EthereumMessage) {
    handleSignMessage(message)
  }

  /**
   * 监听 Dapp 发起的 onSignTypedMessage 操作
   * @param message 信息
   */
  override fun onSignTypedMessage(message: EthereumTypedMessage) {
    if (message.prehash == null || message.messageType === SignMessageType.SIGN_ERROR) {
      web3.onSignCancel(message.callbackId)
    } else {
      handleSignMessage(message)
    }
  }

  /**
   * 监听 Dapp 发起的 onSignMessage 操作
   * @param message 信息
   */
  override fun onSignMessage(message: EthereumMessage) {
    handleSignMessage(message)
  }

  /**
   * 处理消息签字
   * @param message 消息
   */
  private fun handleSignMessage(message: Signable) {
    messageTBS = message
    dAppFunction = object : DAppFunction {
      override fun DAppError(error: Throwable?, message: Signable) {
        // 处理 Dapp 消息签字取消或失败
        web3.onSignCancel(message.callbackId)
        confirmationDialog?.dismiss()
      }

      override fun DAppReturn(data: ByteArray?, message: Signable) {
        // 处理 Dapp 消息签字正确
        val signHex: String = Numeric.toHexString(data)
        Log.d(TAG, "Initial Msg: " + message.getMessage())
        web3.onSignMessageSuccessful(message, signHex)
        if (BuildConfig.DEBUG && message.getMessageType() == SignMessageType.SIGN_PERSONAL_MESSAGE) {
          Log.e(TAG, HexUtils.hexToUtf8(message.getMessage()) + "    " + signHex);
        }
        confirmationDialog?.success()
      }
    }
    Log.d(TAG, "handle Sign Message: " + message.getMessage())
    if (confirmationDialog == null || confirmationDialog?.isShowing == false) {
      val currentWallet = mAppWalletViewModel.currentWalletAccountInfo.value
      if (currentWallet == null) {
        web3.onSignCancel(message.callbackId)
        return
      }
      confirmationDialog = activity?.let { DappActionSheetDialog(it, currentWallet, this, this, message) }?.apply {
        setCanceledOnTouchOutside(false)
        show()
      }
    }
  }
  // endregion


  override fun currentCoinType(): Vm.CoinType? {
    return mAppWalletViewModel.currentWalletAccountInfo.value?.coinType
  }

  /**
   * 展示账户切换弹窗
   */
  private fun showChangeAccountDialog() {
    if (mDAppBean == null) {
      val toList = Vm.CoinType.values().filter { it.chainType == Vm.CoinType.ETH.chainType }
          .toList()
      SelectAccountBottomSheetDialog.newInstance(toList)
          .show(childFragmentManager, "SelectAccount")
    } else {
      val convertByCallFlag = Vm.CoinType.convertByCallFlag(mDAppBean?.chain)
      SelectAccountBottomSheetDialog.newInstance(convertByCallFlag)
          .show(childFragmentManager, "SelectAccount")
    }
  }

  /**
   * 弹出密码弹窗
   */
  private fun showPasswordDialog(success: (String) -> Unit, cancel: (() -> Unit)? = null) {
    SoftPassDialog.newInstance()
        .setOnInputSuccessListener(object : SoftPassDialog.OnInputSuccessListener {
          override fun onSuccess(pwd: String) {
            success.invoke(pwd)
          }

          override fun onCancel() {
            cancel?.invoke()
          }
        })
        .show(childFragmentManager, "passwordDialog")
  }

  private fun showErrorDialog(message: String, title: String? = null, icon: Int? = null) {
    if (resultDialog?.isShowing == true) resultDialog?.dismiss()
    context?.let {
      resultDialog = DappResultAlertDialog(it).apply {
        setIcon(icon ?: DappResultAlertDialog.ERROR)
        if (title == null) {
          setTitle(R.string.reansaction_error)
        } else {
          setTitle(title)
        }
        setMessage(message)
        setButtonText(R.string.confirm)
        setButtonListener { v -> resultDialog?.dismiss() }
        show()
      }
    }
  }

  /**
   * 交易签字失败，展示提示。
   */
  private fun txError(throwable: Throwable) {
    if (resultDialog?.isShowing == true) resultDialog?.dismiss()
    context?.let {
      resultDialog = DappResultAlertDialog(it).apply {
        setIcon(DappResultAlertDialog.ERROR)
        setTitle(R.string.reansaction_error)
        setMessage(throwable.message)
        setButtonText(R.string.confirm)
        setButtonListener { v -> resultDialog?.dismiss() }
        show()
      }
      confirmationDialog?.dismiss()
    }
  }

  /**
   * 接收到 Dapp 传来的错误交易，展示提示。
   */
  private fun onInvalidTransaction(transaction: Web3Transaction) {
    activity?.let {
      resultDialog = DappResultAlertDialog(it).apply {
        setIcon(DappResultAlertDialog.ERROR)
        setTitle(getString(R.string.transaction_parse_error))
        if (transaction.recipient.equals(Address.EMPTY) && (transaction.payload == null || transaction.value != null)) {
          setMessage(R.string.hint_transaction_no_sender)
        } else if (transaction.payload == null && transaction.value == null) {
          setMessage(R.string.hint_transaction_no_send_amount)
        } else {
          setMessage(R.string.hint_transaction_no_payload)
        }
        setButtonText(R.string.confirm)
        setButtonListener { v -> resultDialog?.dismiss() }
        setCancelable(true)
        show()
      }
    }
  }
  // endregion


  // region 浏览器前进、刷新、后退、加载等操作方法

  fun setDappTitle(title: String?) {
    if (mDAppBean == null) {
      mBinding.tvTitle.text = removeHTTPProtocol(title ?: web3.url)
    } else {
      if (title != null) {
        mBinding.tvTitle.text = mDAppBean?.name
      } else {
        mBinding.tvTitle.text = removeHTTPProtocol(title ?: web3.url)
      }
    }
  }

  override fun onWebpageBeginLoad(url: String?, title: String?) {
    mMultiStateContainer.show(SuccessState::class.java)
  }

  override fun onWebpageLoaded(url: String?, title: String?) {
    if (context == null) return  //could be a late return from dead fragment
    onWebpageLoadComplete()
  }

  override fun onWebpageLoadComplete() {
    runOnUiThread { setBackForwardButtons() }
  }

  override fun onWebpageLoadError() {
    mMultiStateContainer.show(BrowserLoadError::class.java)
  }

  override fun onBackPressed(): Boolean {
    return if (web3.canGoBack()) {
      goToPreviousPage()
      false
    } else {
      true
    }
  }

  /**
   * 返回首页
   */
  fun homePressedEvent() {
    mHomePressed = true
    web3.clearHistory()
    web3.stopLoading()
    web3.loadUrl(mLoadOnInit, getWeb3Headers())
    setDappTitle(mLoadOnInit)

    //blank forward / backward arrows
    setBackForwardButtons()
  }

  /**
   * 刷新网页
   */
  fun refreshEvent() {
    setupWeb3()
    web3.reload()
  }

  /**
   * 下一页
   */
  private fun goToPreviousPage() {
    if (web3.canGoBack()) {
      checkBackClickArrowVisibility()
      web3.goBack()
      loadSessionUrl(-1)
    } else {
      //load homepage
      mHomePressed = true
      web3.loadUrl(mLoadOnInit, getWeb3Headers())
      setDappTitle(mLoadOnInit)
      web3.clearHistory()
    }
    setBackForwardButtons()
  }

  /**
   * 上一页
   */
  private fun goToNextPage() {
    if (web3.canGoForward()) {
      checkForwardClickArrowVisibility()
      web3.goForward()
      loadSessionUrl(1)
    }
    setBackForwardButtons()
  }

  private fun setBackForwardButtons() {
    var sessionHistory: WebBackForwardList? = null
    var canBrowseBack = false
    var canBrowseForward = false

    sessionHistory = web3.copyBackForwardList()
    val url: String = web3.getUrl()
    canBrowseBack = web3.canGoBack()
    canBrowseForward =
        web3.canGoForward() || sessionHistory != null && sessionHistory.currentIndex < sessionHistory.size - 1

    if (canBrowseBack) {
      mBinding.ivBack.visibility = View.VISIBLE
      mBinding.viewDivision.visibility = View.VISIBLE
    } else {
      mBinding.ivBack.visibility = View.GONE
      mBinding.viewDivision.visibility = View.GONE
    }

//    if (next != null) {
//      if (canBrowseForward) {
//        next.setAlpha(1.0f)
//      } else {
//        next.setAlpha(0.3f)
//      }
//    }
  }

  private fun loadUrl(urlText: String): Boolean {
    web3.loadUrl(formatUrl(urlText), getWeb3Headers())
    setDappTitle(formatUrl(urlText))
    web3.requestFocus()
    return true
  }

  /**
   * Check if this is the last web item and the last fragment item.
   */
  private fun checkBackClickArrowVisibility() {
    val sessionHistory: WebBackForwardList = web3.copyBackForwardList()
    val nextIndex = sessionHistory.currentIndex - 1
    if (nextIndex <= 0) {
      mBinding.ivBack.visibility = View.GONE
      mBinding.viewDivision.visibility = View.GONE
    } else {
      mBinding.ivBack.visibility = View.VISIBLE
      mBinding.viewDivision.visibility = View.VISIBLE
    }
  }

  /**
   * Browse to relative entry with sanity check on value
   * @param relative relative addition or subtraction of browsing index
   */
  private fun loadSessionUrl(relative: Int) {
    val sessionHistory: WebBackForwardList = web3.copyBackForwardList()
    val newIndex = sessionHistory.currentIndex + relative
    if (newIndex < sessionHistory.size) {
      val newItem = sessionHistory.getItemAtIndex(newIndex)
      if (newItem != null) {
        setDappTitle(newItem.url)
      }
    }
  }

  /**
   * After a forward click while web browser active, check if forward and back arrows should be updated.
   * Note that the web item only becomes history after the next page is loaded, so if the next item is new, then
   */
  private fun checkForwardClickArrowVisibility() {
    val sessionHistory: WebBackForwardList = web3.copyBackForwardList()
    val nextIndex = sessionHistory.currentIndex + 1
    if (nextIndex >= sessionHistory.size - 1) {
      // todo 隐藏前进按钮
    } else {
      // todo 展示前进按钮
    }
  }
// endregion

  /* Required for CORS requests */
  private fun getWeb3Headers(): Map<String, String> {
    // headers
    return object : HashMap<String, String>() {
      init {
        put("Connection", "close")
        put("Content-Type", "text/plain")
        put("Access-Control-Allow-Origin", "*")
        put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS")
        put("Access-Control-Max-Age", "600")
        put("Access-Control-Allow-Credentials", "true")
        put("Access-Control-Allow-Headers", "accept, authorization, Content-Type")
      }
    }
  }

  // region 权限检查、功能检查
  private fun checkReadPermission(): Boolean {
    return if (activity?.applicationContext?.let {
          ContextCompat.checkSelfPermission(
              it,
              Manifest.permission.READ_EXTERNAL_STORAGE
          )
        }
        == PackageManager.PERMISSION_GRANTED) {
      true
    } else {
      val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
      activity?.requestPermissions(permissions, REQUEST_FILE_ACCESS)
      false
    }
  }

  private fun requestGeoPermission(origin: String, callback: GeolocationPermissions.Callback) {

    if (activity?.applicationContext?.let {
          ContextCompat.checkSelfPermission(
              it,
              Manifest.permission.ACCESS_FINE_LOCATION
          )
        }
        != PackageManager.PERMISSION_GRANTED) {
      mGeoCallback = callback
      mGeoOrigin = origin
      val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
      activity?.requestPermissions(permissions, REQUEST_FINE_LOCATION)
    } else {
      callback.invoke(origin, true, false)
    }
  }

  private fun gotGeoAccess(permissions: Array<String>, grantResults: IntArray) {
    var geoAccess = false
    for (i in permissions.indices) {
      if (permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[i] != -1) geoAccess = true
    }
    if (!geoAccess) Toast.makeText(context, "Permission not given", Toast.LENGTH_SHORT).show()
    if (mGeoCallback != null && mGeoOrigin != null) mGeoCallback?.invoke(mGeoOrigin, geoAccess, false)
  }

  private fun gotFileAccess(permissions: Array<String>, grantResults: IntArray) {
    var fileAccess = false
    for (i in permissions.indices) {
      if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[i] != -1) fileAccess = true
    }
    if (fileAccess && mPicker != null) requestUpload()
  }

  protected fun requestUpload(): Boolean {
    try {
      startActivityForResult(mPicker, UPLOAD_FILE)
    } catch (e: ActivityNotFoundException) {
      mUploadMessage = null
      Toast.makeText(activity?.applicationContext, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
      return false
    }
    return true
  }
// endregion
}

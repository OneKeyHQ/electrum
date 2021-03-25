package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.IntDef
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.WalletAccountInfo
import org.haobtc.onekey.business.assetsLogo.AssetsLogo
import org.haobtc.onekey.databinding.DialogDappSettingSheetBinding
import org.haobtc.onekey.extensions.cutTheLast
import org.haobtc.onekey.repository.DataRepository
import org.haobtc.onekey.viewmodel.AppWalletViewModel

class DappSettingSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {
  @IntDef(
      ClickType.CLICK_SWITCH_ACCOUNT,
      ClickType.CLICK_REFRESH,
      ClickType.CLICK_SHARE,
      ClickType.CLICK_COPY_URL,
      ClickType.CLICK_COLLECTION,
      ClickType.CLICK_BROWSER)
  annotation class ClickType {
    companion object {
      const val CLICK_SWITCH_ACCOUNT = 0
      const val CLICK_REFRESH = 1
      const val CLICK_SHARE = 2
      const val CLICK_COPY_URL = 3
      const val CLICK_BROWSER = 4
      const val CLICK_COLLECTION = 5
    }
  }

  companion object {
    private const val EXT_DAPP_UUID = "dapp_uuid"
    private const val EXT_DAPP_NAME = "dapp_name"
    private const val EXT_DAPP_CONTENT = "dapp_content"
    private const val EXT_DAPP_LOGO_URL = "dapp_logo_url"

    @JvmStatic
    fun newInstance(
        dappUuid: String = "",
        dappName: String = "Onekey",
        dappContent: String = "",
        dappLogoUrl: String = ""
    ): DappSettingSheetDialog {
      return DappSettingSheetDialog().apply {
        Bundle().apply {
          putString(EXT_DAPP_UUID, dappUuid)
          putString(EXT_DAPP_CONTENT, dappContent)
          putString(EXT_DAPP_NAME, dappName)
          putString(EXT_DAPP_LOGO_URL, dappLogoUrl)
          arguments = this
        }
      }
    }
  }

  private lateinit var mBinding: DialogDappSettingSheetBinding
  private val mAppWalletViewModel: AppWalletViewModel by lazy {
    ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel::class.java)
  }
  private val mDappCollectionDao by lazy {
    DataRepository.getDappCollectionDao()
  }
  private var mOnSettingHandleClick: OnSettingHandleClick? = null

  override fun getTheme(): Int = R.style.BottomSheetDialogTheme

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    dialog.setContentView(DialogDappSettingSheetBinding.inflate(layoutInflater).also {
      mBinding = it
    }.root)
    dialog.delegate
        ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        ?.setBackgroundColor(Color.TRANSPARENT)
    initViewListener()
    initViewModel()
    return dialog
  }

  private fun initViewModel() {
    arguments?.getString(EXT_DAPP_UUID)?.let {
      mDappCollectionDao.observeExistsUuid(it)
          .observe(this) {
            val resId = if (it.isNotEmpty()) {
              R.drawable.vector_dapp_setting_button_collection
            } else {
              R.drawable.vector_dapp_setting_button_un_collection
            }
            mBinding.ivCollect.setImageDrawable(ResourcesCompat.getDrawable(resources, resId, null))
          }
    }
  }

  private fun initViewListener() {
    mBinding.layoutActionSwitchAccount.setOnClickListener(this)
    mBinding.layoutActionRefresh.setOnClickListener(this)
    mBinding.layoutActionCopyUrl.setOnClickListener(this)
    mBinding.layoutActionShare.setOnClickListener(this)
    mBinding.layoutActionBrowser.setOnClickListener(this)
    mBinding.layoutActionCollect.setOnClickListener(this)
    mAppWalletViewModel.currentWalletAccountInfo.observe(this) {
      setAccount(it)
    }
    if (mAppWalletViewModel.currentWalletAccountInfo.value == null) {
      setAccount(null)
    }
    arguments?.getString(EXT_DAPP_NAME)?.let {
      mBinding.tvDappName.text = it
    }
    arguments?.getString(EXT_DAPP_CONTENT)?.let {
      mBinding.tvDappContent.text = it
    }
    arguments?.getString(EXT_DAPP_LOGO_URL)?.let {
      Glide.with(requireContext()).load(it).placeholder(R.drawable.loco_round)
          .apply(RequestOptions.bitmapTransform(CircleCrop())).into(mBinding.ivDappLogo)
    }
    mBinding.tvButton.setOnClickListener { dismiss() }
  }

  fun setOnSettingHandleClickCallback(callback: OnSettingHandleClick): DappSettingSheetDialog {
    mOnSettingHandleClick = callback
    return this
  }

  private fun setAccount(walletInfo: WalletAccountInfo?) {
    mBinding.tvWalletName.text = walletInfo?.address?.cutTheLast(4)
        ?: getString(R.string.title_select_account)
    AssetsLogo.getLogoResources(walletInfo?.coinType).apply {
      mBinding.ivTokenLogo.setImageDrawable(ResourcesCompat.getDrawable(resources, this, null))
    }
  }

  fun interface OnSettingHandleClick {
    fun onSettingClick(@ClickType type: Int)
  }

  override fun onClick(v: View?) {
    val clickType = when (v?.id) {
      R.id.layout_action_switch_account -> {
        ClickType.CLICK_SWITCH_ACCOUNT
      }
      R.id.layout_action_refresh -> {
        ClickType.CLICK_REFRESH
      }
      R.id.layout_action_share -> {
        ClickType.CLICK_SHARE
      }
      R.id.layout_action_copy_url -> {
        ClickType.CLICK_COPY_URL
      }
      R.id.layout_action_browser -> {
        ClickType.CLICK_BROWSER
      }
      R.id.layout_action_collect -> {
        ClickType.CLICK_COLLECTION
      }
      else -> {
        null
      }
    }
    clickType?.let {
      mOnSettingHandleClick?.onSettingClick(clickType)
    }
    dismiss()
  }
}

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
import org.haobtc.onekey.viewmodel.AppWalletViewModel

class DappSettingSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {
  @IntDef(
      ClickType.CLICK_SWITCH_ACCOUNT,
      ClickType.CLICK_REFRESH,
      ClickType.CLICK_SHARE,
      ClickType.CLICK_COPY_URL,
      ClickType.CLICK_BROWSER)
  annotation class ClickType {
    companion object {
      const val CLICK_SWITCH_ACCOUNT = 0
      const val CLICK_REFRESH = 1
      const val CLICK_SHARE = 2
      const val CLICK_COPY_URL = 3
      const val CLICK_BROWSER = 4
    }
  }

  companion object {
    private const val EXT_DAPP_NAME = "dapp_name"
    private const val EXT_DAPP_CONTENT = "dapp_content"
    private const val EXT_DAPP_LOGO_URL = "dapp_logo_url"

    @JvmStatic
    fun newInstance(
        dappName: String = "Onekey",
        dappContent: String = "",
        dappLogoUrl: String = ""
    ): DappSettingSheetDialog {
      return DappSettingSheetDialog().apply {
        Bundle().apply {
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
  private var mOnSettingHandleClick: OnSettingHandleClick? = null
  private val mAssetsLogo by lazy {
    AssetsLogo()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    dialog.setContentView(DialogDappSettingSheetBinding.inflate(layoutInflater).also {
      mBinding = it
    }.root)
    dialog.delegate
        ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        ?.setBackgroundColor(Color.TRANSPARENT)
    initViewListener()
    return dialog
  }

  private fun initViewListener() {
    mBinding.layoutActionSwitchAccount.setOnClickListener(this)
    mBinding.layoutActionRefresh.setOnClickListener(this)
    mBinding.layoutActionCopyUrl.setOnClickListener(this)
    mBinding.layoutActionShare.setOnClickListener(this)
    mBinding.layoutActionBrowser.setOnClickListener(this)
    mAppWalletViewModel.currentWalletAccountInfo.observe(
        this, {
      if (it != null) {
        setAccount(it)
      }
    })
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

  private fun setAccount(walletInfo: WalletAccountInfo) {
    mBinding.tvWalletName.text = walletInfo.name
    mAssetsLogo.getLogoResources(walletInfo.coinType).apply {
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

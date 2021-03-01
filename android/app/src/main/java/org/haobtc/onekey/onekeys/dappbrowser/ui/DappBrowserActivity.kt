package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.content.Context
import android.content.Intent
import org.haobtc.onekey.R
import org.haobtc.onekey.ui.base.BaseActivity

/**
 * Dapp 浏览器
 *
 * @author Onekey@QuincySx
 * @create 2021-03-02 2:23 PM
 */
class DappBrowserActivity : BaseActivity(), OnFinishOrBackCallback {
  companion object {
    const val EXT_URL = "url"
//    @JvmField val DEFAULT_URL = "http://uniswap.defiplot.com/#/swap"
    @JvmField val DEFAULT_URL = "https://js-eth-sign.surge.sh/"

    @JvmStatic
    fun start(context: Context, url: String) {
      Intent(context, DappBrowserActivity::class.java).apply {
        putExtra(EXT_URL, url)
        context.startActivity(this)
      }
    }
  }

  private var mOnBackPressedCallback: OnBackPressedCallback? = null
  override fun getContentViewId() = R.layout.activity_dapp_browser

  override fun init() {
    supportFragmentManager.beginTransaction()
        .add(R.id.fragment_container_view,
            DappBrowserFragment.start(intent.getStringExtra(EXT_URL) ?: DEFAULT_URL))
        .commit()
  }

  override fun onBackPressed() {
    if (mOnBackPressedCallback?.onBackPressed() == true) {
      super.onBackPressed()
    }
  }

  override fun setOnBackPressed(onBackPressed: OnBackPressedCallback) {
    mOnBackPressedCallback = onBackPressed
  }
}

interface OnFinishOrBackCallback {
  fun setOnBackPressed(onBackPressed: OnBackPressedCallback)

  fun finish()
}

interface OnBackPressedCallback {
  fun onBackPressed(): Boolean
}

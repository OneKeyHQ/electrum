package org.haobtc.onekey.onekeys.homepage.process

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import com.scwang.smartrefresh.layout.util.SmartUtil
import com.yzq.zxinglibrary.encode.CodeCreator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.BaseActivity
import org.haobtc.onekey.databinding.ActivityExportKeystoreBinding


class ExportKeystoreActivity : BaseActivity() {
  companion object {
    private const val EXT_KEYSTORE = "1"

    @JvmStatic
    fun start(context: Context, keystore: String) {
      Intent(context, ExportKeystoreActivity::class.java).apply {
        putExtra(EXT_KEYSTORE, keystore)
        context.startActivity(this)
      }
    }
  }

  private lateinit var mBinding: ActivityExportKeystoreBinding
  private var show = false
  override fun getLayoutView() = ActivityExportKeystoreBinding.inflate(layoutInflater).also { mBinding = it }.root

  override fun initData() {
    val keystoreExtra = intent.getStringExtra(EXT_KEYSTORE)
    mBinding.textKeystore.text = keystoreExtra
    mBinding.textKeystore.movementMethod = ScrollingMovementMethod.getInstance()

    Single
        .create<Bitmap> {
          val bitmap = CodeCreator.createQRCode(keystoreExtra, SmartUtil.dp2px(180F), SmartUtil.dp2px(180F), null)
          it.onSuccess(bitmap)
        }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          mBinding.imgKeystore.setImageBitmap(it)
        }, {
          it.printStackTrace()
        })
  }

  override fun requireSecure() = true

  override fun enableViewBinding() = true

  override fun getLayoutId() = 0

  override fun initView() {
    mBinding.btnNext.setOnClickListener { finish() }
    mBinding.imgBack.setOnClickListener { finish() }
    mBinding.imgCopy.setOnClickListener {
      val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
      clipboardManager?.apply {
        setPrimaryClip(ClipData.newPlainText(null, mBinding.textKeystore.getText()))
        Toast.makeText(this@ExportKeystoreActivity, R.string.copysuccess, Toast.LENGTH_LONG).show()
      }
    }
    mBinding.textShowCode.setOnClickListener {
      if (!show) {
        mBinding.imgKeystore.setVisibility(View.VISIBLE)
        mBinding.textShowCode.setText(getString(R.string.hide_code))
        show = true
      } else {
        mBinding.imgKeystore.setVisibility(View.GONE)
        mBinding.textShowCode.setText(getString(R.string.show_code))
        show = false
      }
    }

    handleSlidingConflict()
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun handleSlidingConflict() {
    mBinding.nestedScrollView.setOnTouchListener { _, _ ->
      mBinding.textKeystore.getParent().requestDisallowInterceptTouchEvent(false)
      false
    }

    mBinding.textKeystore.setOnTouchListener { _, _ ->
      mBinding.textKeystore.getParent().requestDisallowInterceptTouchEvent(true)
      false
    }
  }
}

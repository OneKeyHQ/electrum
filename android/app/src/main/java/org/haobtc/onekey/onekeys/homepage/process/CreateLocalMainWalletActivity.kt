package org.haobtc.onekey.onekeys.homepage.process

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.scwang.smartrefresh.layout.util.SmartUtil
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.bean.BalanceInfoDTO
import org.haobtc.onekey.business.wallet.SystemConfigManager
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.databinding.ActivityCreateLocalMainWalletBinding
import org.haobtc.onekey.manager.PyEnv
import org.haobtc.onekey.ui.base.BaseActivity
import java.util.*
import kotlin.collections.ArrayList

class CreateLocalMainWalletActivity : BaseActivity(), AccountSelectAdapter.OnSelectorItem {
  companion object {
    private const val EXT_PWD = "ext_pwd"

    @JvmStatic
    fun start(context: Context, pwd: String) {
      Intent(context, CreateLocalMainWalletActivity::class.java).apply {
        putExtra(EXT_PWD, pwd)
        context.startActivity(this)
      }
    }
  }

  private val mBinding by lazy {
    ActivityCreateLocalMainWalletBinding.inflate(layoutInflater)
  }
  private val mAdapter by lazy {
    AccountSelectAdapter(mAccountList, this)
  }
  private val mAccountList = ArrayList<AccountSelectVo>()

  override fun showToolBar() = true

  override fun getContentViewId() = 0

  override fun enableViewBinding() = true

  override fun getLayoutView() = mBinding.root

  override fun init() {
    ResourcesCompat.getColor(resources, R.color.button_bk_light_grey, null).apply {
      setStatusBarColor(this)
      toolbar.background = ColorDrawable(this)
    }

    val build = HorizontalDividerItemDecoration.Builder(this)
        .color(ResourcesCompat.getColor(resources, R.color.color_select_wallet_divider, theme))
        .sizeResId(R.dimen.line_hight)
        .margin(SmartUtil.dp2px(12F), 0)
        .build()

    mBinding.apply {
      reclSelectCoin.addItemDecoration(build)
      reclSelectCoin.itemAnimator = null
      btnConfirm.setOnClickListener {
        createLocalMainWallet()
      }
      reclSelectCoin.adapter = mAdapter
    }

    initData()
  }

  private fun initData() {
    Single
        .create<List<AccountSelectVo>> { emitter ->
          val accounts = ArrayList<AccountSelectVo>()
          Vm.CoinType.values().filter { it.enable }.forEach {
            val resId = when (it) {
              Vm.CoinType.BTC -> R.drawable.token_btc
              Vm.CoinType.ETH -> R.drawable.token_eth
            }

            val describe = when (it) {
              Vm.CoinType.BTC -> getString(R.string.coin_btc)
              Vm.CoinType.ETH -> getString(R.string.coin_eth)
            }

            accounts.add(AccountSelectVo(
                it,
                it.coinName.toUpperCase(Locale.ROOT),
                describe,
                LocalImage(resId),
                it == Vm.CoinType.BTC
            ))
          }
          emitter.onSuccess(accounts)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { list ->
          mAccountList.clear()
          mAccountList.addAll(list)
          mAdapter.notifyDataSetChanged()
          onSelect(null)
        }
  }

  override fun onSelect(item: AccountSelectVo?) {
    mBinding.btnConfirm.isEnabled = mAdapter.getSelectorItems().isNotEmpty()
  }

  private fun createLocalMainWallet() {
    Single
        .create<List<BalanceInfoDTO>> { emitter ->
          val coinList = mAdapter.getSelectorItems().map { it.coinType }.toList()
          val createLocalHDWallet = PyEnv.createLocalHDWallet(intent.getStringExtra(EXT_PWD), coinList)
          emitter.onSuccess(createLocalHDWallet)
        }
        .doOnSubscribe { showProgress() }
        .doFinally { dismissProgress() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ list ->
          SystemConfigManager(mContext).needPopBackUpDialog = true
          finish()
        }, {
          if (it is Exception) {
            MyApplication.getInstance().toastErr(it)
          }
          it.printStackTrace()
        })
  }
}

class AccountSelectVo(
    val coinType: Vm.CoinType,
    val name: String,
    val describe: String,
    val logo: ImageResources,
    var checked: Boolean = false,
    val isToken: Boolean = false,
) : MultiItemEntity {
  override fun getItemType(): Int {
    return if (isToken) {
      AccountSelectAdapter.TOKEN_ITEM
    } else {
      AccountSelectAdapter.COIN_ITEM
    }
  }
}

class AccountSelectAdapter(private val accountList: List<AccountSelectVo>, private val mOnSelectorItem: OnSelectorItem? = null) : BaseMultiItemQuickAdapter<AccountSelectVo, BaseViewHolder>(accountList) {
  companion object {
    const val COIN_ITEM = 0
    const val TOKEN_ITEM = 1
  }

  init {
    addItemType(COIN_ITEM, R.layout.item_create_wallet_select_coin)
    addItemType(TOKEN_ITEM, R.layout.item_create_wallet_select_token)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
    val viewHolder = super.onCreateViewHolder(parent, viewType)
    viewHolder.getView<View>(R.id.check_wallet).setOnClickListener {
      val accountSelectVo = accountList[viewHolder.adapterPosition]
      accountSelectVo.checked = !accountSelectVo.checked
      notifyItemChanged(viewHolder.adapterPosition)
      mOnSelectorItem?.onSelect(accountSelectVo)
    }
    viewHolder.itemView.setOnClickListener {
      val accountSelectVo = accountList[viewHolder.adapterPosition]
      accountSelectVo.checked = !accountSelectVo.checked
      notifyItemChanged(viewHolder.adapterPosition)
      mOnSelectorItem?.onSelect(accountSelectVo)
    }
    return viewHolder
  }

  override fun convert(helper: BaseViewHolder, item: AccountSelectVo?) {
    item?.apply {
      helper.setText(R.id.text_wallet_name, name)
      helper.setText(R.id.text_wallet_balance, describe)
      helper.setChecked(R.id.check_wallet, checked)
      helper.getView<ImageView>(R.id.token_logo)?.let {
        logo.intoTarget(it)
      }
    }
  }

  interface OnSelectorItem {
    fun onSelect(item: AccountSelectVo?)
  }

  fun getSelectorItems() = accountList.filter { it.checked }.toList()
}

// 测试一下用法，成熟后拿出去。
interface ImageResources {
  fun intoTarget(imageView: ImageView)
}

class LocalImage(@DrawableRes val res: Int) : ImageResources {
  override fun intoTarget(imageView: ImageView) {
    imageView.setImageDrawable(ResourcesCompat.getDrawable(imageView.resources, res, null))
  }
}

class RemoteImage(val url: String) : ImageResources {
  override fun intoTarget(imageView: ImageView) {
    Glide.with(imageView.context).load(url).into(imageView)
  }
}

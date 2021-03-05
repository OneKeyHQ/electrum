package org.haobtc.onekey.adapter

import android.content.Context
import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.bean.BalanceCoinInfo
import org.haobtc.onekey.bean.BalanceInfoDTO
import org.haobtc.onekey.bean.CoinAssets
import org.haobtc.onekey.bean.ERC20Assets
import org.haobtc.onekey.constant.Vm
import org.haobtc.onekey.onekeys.homepage.process.TransactionDetailWalletActivity
import org.haobtc.onekey.viewmodel.AppWalletViewModel

class WalletAssetAccountAdapter(context: Context, appViewModel: AppWalletViewModel, decoration: HorizontalDividerItemDecoration, data: List<BalanceInfoDTO?>?) : BaseQuickAdapter<BalanceInfoDTO, BaseViewHolder>(R.layout.item_all_asset_account, data) {

  private var mDeprecated: Disposable? = null
  val model = appViewModel
  val itemDerection = decoration

  override fun convert(helper: BaseViewHolder, item: BalanceInfoDTO) {
    helper.setText(R.id.account_name, item.name)
    val recyclerView = helper.getView<RecyclerView>(R.id.account_recyclerview)
    val adapter = HdWalletAssetAdapter(mContext, item.wallets)
    recyclerView.adapter = adapter
    recyclerView.addItemDecoration(itemDerection)
    adapter.setOnItemClickListener { adapter, view, position ->
      if (mDeprecated?.isDisposed == false) {
        mDeprecated?.dispose()
      }
      mDeprecated = Single
          .fromCallable {
            val childItem = adapter.data[position] as BalanceCoinInfo
            val coinType = Vm.CoinType.convertByCallFlag((adapter.data[0] as BalanceCoinInfo).coin)
            val uniqueId = if (TextUtils.isEmpty(childItem.address)) {
              CoinAssets.generateUniqueId(coinType)
            } else {
              ERC20Assets.generateUniqueId(childItem.address, coinType)
            }
            model.changeCurrentWallet(item.label)
            uniqueId
          }
          .observeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .subscribe { uniqueId ->
            TransactionDetailWalletActivity.start(mContext, item.label, uniqueId)
          }

    }
  }

}

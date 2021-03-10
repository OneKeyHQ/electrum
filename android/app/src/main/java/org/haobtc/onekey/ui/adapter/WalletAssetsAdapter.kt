package org.haobtc.onekey.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.haobtc.onekey.bean.Assets
import org.haobtc.onekey.databinding.HomeItemBinding
import org.haobtc.onekey.utils.CoinDisplayUtils


class WalletAssetsAdapter : ListAdapter<Assets, WalletAssetsAdapter.AssetsViewHolder>(AssetsDiff()) {
  companion object {
    const val EXT_PAYLOAD_AMOUNT = "amount"
    const val EXT_PAYLOAD_AMOUNT_FIAT = "amount_fiat"
    const val EXT_PAYLOAD_LOGO = "logo"
    const val PAYLOAD_FLAG = 1
  }

  private var mOnItemClickListener: OnItemClickListener? = null
  private var mPrivacyMode: Boolean = false

  fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
    mOnItemClickListener = onItemClickListener
  }

  fun setPrivacyMode(enable: Boolean) {
    mPrivacyMode = enable
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetsViewHolder {
    val itemBinding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    val assetsViewHolder = AssetsViewHolder(itemBinding)
    assetsViewHolder.itemView.setOnClickListener {
      mOnItemClickListener?.onItemClick(assetsViewHolder.adapterPosition, getItem(assetsViewHolder.adapterPosition))
    }
    return assetsViewHolder
  }

  override fun onBindViewHolder(holder: AssetsViewHolder, position: Int) {
    val item = getItem(position)
    item.logo.intoTarget(holder.bind.ivAssetsLogo)
    holder.bind.tvAssetsName.text = item.name
    holder.bind.textAssetsAmount.text = CoinDisplayUtils.getCoinBalanceDisplay(item)

    holder.bind.textDollar.setText(
      String.format(
        "%s %s", item.balanceFiat.symbol, item.balanceFiat.balanceFormat
      )
    )

    if (mPrivacyMode) {
      holder.bind.textAssetsAmountStars.visibility = View.VISIBLE
      holder.bind.textAssetsDollarStars.visibility = View.VISIBLE
      holder.bind.textAssetsAmount.visibility = View.GONE
      holder.bind.textDollar.visibility = View.GONE
    } else {
      holder.bind.textAssetsAmountStars.visibility = View.GONE
      holder.bind.textAssetsDollarStars.visibility = View.GONE
      holder.bind.textAssetsAmount.visibility = View.VISIBLE
      holder.bind.textDollar.visibility = View.VISIBLE
    }
  }

  /**
   * 处理局部刷新
   */
  override fun onBindViewHolder(holder: AssetsViewHolder, position: Int, payloads: MutableList<Any>) {
    val item = getItem(position)
    if (payloads.isEmpty()) {
      // Item 整条刷新
      super.onBindViewHolder(holder, position, payloads)
    } else {
      val bundle = payloads[0] as Bundle
      if (bundle.getInt(EXT_PAYLOAD_AMOUNT, 0) == PAYLOAD_FLAG) {
        holder.bind.textAssetsAmount.text = CoinDisplayUtils.getCoinBalanceDisplay(item)
      }
      if (bundle.getInt(EXT_PAYLOAD_AMOUNT_FIAT, 0) == PAYLOAD_FLAG) {
        holder.bind.textDollar.setText(
            String.format(
                "%s %s", item.balanceFiat.symbol, item.balanceFiat.balanceFormat))
      }
      if (bundle.getInt(EXT_PAYLOAD_LOGO, 0) == PAYLOAD_FLAG) {
        item.logo.intoTarget(holder.bind.ivAssetsLogo)
      }
    }
  }

  class AssetsViewHolder(val bind: HomeItemBinding) : RecyclerView.ViewHolder(bind.root)

  fun interface OnItemClickListener {
    fun onItemClick(position: Int, assets: Assets)
  }
}

/**
 * 资产差异对比
 */
class AssetsDiff : DiffUtil.ItemCallback<Assets>() {
  /**
   *  判断新旧两个数据是不是同一个资产
   */
  override fun areItemsTheSame(oldItem: Assets, newItem: Assets): Boolean {
    return oldItem.uniqueId() == newItem.uniqueId()
  }

  /**
   *  判断两个资产数据内容是否一致
   */
  override fun areContentsTheSame(oldItem: Assets, newItem: Assets): Boolean {
    if (oldItem.balance != newItem.balance) {
      return false
    }
    if (oldItem.balanceFiat != newItem.balanceFiat) {
      return false
    }
    if (oldItem.logo != newItem.logo) {
      return false
    }
    return true
  }

  /**
   *  判断资产数据哪一项不一致，返回局部刷新标志
   */
  override fun getChangePayload(oldItem: Assets, newItem: Assets): Any? {
    val diffBundle = Bundle()
    if (oldItem.balance != newItem.balance) {
      diffBundle.putInt(WalletAssetsAdapter.EXT_PAYLOAD_AMOUNT, WalletAssetsAdapter.PAYLOAD_FLAG)
    }
    if (oldItem.balanceFiat != newItem.balanceFiat) {
      diffBundle.putInt(WalletAssetsAdapter.EXT_PAYLOAD_AMOUNT_FIAT, WalletAssetsAdapter.PAYLOAD_FLAG)
    }
    if (oldItem.logo != newItem.logo) {
      diffBundle.putInt(WalletAssetsAdapter.EXT_PAYLOAD_LOGO, WalletAssetsAdapter.PAYLOAD_FLAG)
    }
    if (diffBundle.size() > 0) {
      return diffBundle
    }
    return null
  }
}

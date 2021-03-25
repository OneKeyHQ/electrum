package org.haobtc.onekey.onekeys.dappbrowser.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scwang.smartrefresh.layout.util.SmartUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.bean.DAppBrowserBean
import org.haobtc.onekey.databinding.DialogDappCollectionSheetBinding
import org.haobtc.onekey.databinding.ItemDappCollectionBinding
import org.haobtc.onekey.repository.DataRepository
import org.haobtc.onekey.repository.database.entity.DappCollectionDO
import org.haobtc.onekey.repository.database.entity.DappCollectionType
import org.haobtc.onekey.ui.widget.SlideItemView
import org.haobtc.onekey.utils.ToastUtils
import org.haobtc.onekey.utils.URLUtils


class DappCollectionSheetDialog : BottomSheetDialogFragment() {

  private lateinit var mBinding: DialogDappCollectionSheetBinding
  private val mDappCollectionDao by lazy {
    DataRepository.getDappCollectionDao()
  }
  private val mAdapter by lazy {
    CollectionAdapter()
  }

  override fun getTheme(): Int = R.style.BottomSheetDialogTheme

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    dialog.setContentView(DialogDappCollectionSheetBinding.inflate(layoutInflater).also {
      mBinding = it
    }.root)
    dialog.delegate
        ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        ?.setBackgroundColor(Color.TRANSPARENT)

    initViewListener()
    return dialog
  }

  private fun initViewListener() {
    mBinding.imgCancel.setOnClickListener { dismiss() }
    mBinding.recyclerView.adapter = mAdapter
    mDappCollectionDao.observe().observe(this, {
      mAdapter.submitList(it)
    })
    mAdapter.setOnItemClickListener(object : CollectionAdapter.OnItemClickListener {
      override fun onItemClick(position: Int, item: DappCollectionDO) {
        if (item.type == DappCollectionType.DAPP) {
          val dAppBrowserBean = DAppBrowserBean(item.chain, item.description, "", item.img, item.uuid, item.name
              ?: "", item.subtitle, item.url)
          DappBrowserActivity.start(requireContext(), dAppBrowserBean)
        } else {
          DappBrowserActivity.start(requireContext(), item.url)
        }
        dismiss()
      }

      override fun onItemDeleteClick(position: Int, item: DappCollectionDO) {
        Single
            .fromCallable {
              mDappCollectionDao.deleteAtUuid(item.uuid)
              "success"
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              ToastUtils.toast(getString(R.string.hint_content_cancel_collection_success))
            }, {

            })
      }
    })
  }
}

class CollectionAdapter : ListAdapter<DappCollectionDO, CollectionAdapter.CollectionViewHolder>(CollectionDiff()) {
  private var mOnItemClickListener: OnItemClickListener? = null

  fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
    mOnItemClickListener = onItemClickListener
  }

  class CollectionViewHolder(val bind: ItemDappCollectionBinding) : RecyclerView.ViewHolder(bind.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
    val itemBinding = ItemDappCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    val viewHolder = CollectionViewHolder(itemBinding)
    viewHolder.bind.layoutItem.setOnClickListener {
      if (viewHolder.bind.slideItem.isExpansion()) {
        viewHolder.bind.slideItem.reset()
        return@setOnClickListener
      }
      mOnItemClickListener?.onItemClick(viewHolder.adapterPosition, getItem(viewHolder.adapterPosition))
    }
    viewHolder.bind.tvDelete.setOnClickListener {
      viewHolder.bind.tvConfirmDelete.visibility = View.VISIBLE
      viewHolder.bind.tvDelete.visibility = View.GONE
      viewHolder.bind.slideItem.setSlideWidth(SmartUtil.dp2px(160f))
      viewHolder.bind.slideItem.post {
        viewHolder.bind.slideItem.smoothScrollTo(SmartUtil.dp2px(160f), 0)
      }
    }
    viewHolder.bind.tvConfirmDelete.setOnClickListener {
      mOnItemClickListener?.onItemDeleteClick(viewHolder.adapterPosition, getItem(viewHolder.adapterPosition))
    }
    viewHolder.bind.slideItem.setOnSlideListener(object : SlideItemView.OnSlideListener {
      override fun onSlideOpen() {

      }

      override fun onSlideClose() {
        viewHolder.bind.tvDelete.visibility = View.VISIBLE
        viewHolder.bind.tvConfirmDelete.visibility = View.GONE
        viewHolder.bind.slideItem.setSlideWidth(SmartUtil.dp2px(80f))
        viewHolder.bind.slideItem.post {
          viewHolder.bind.slideItem.smoothScrollTo(0, 0)
        }
      }
    })
    return viewHolder
  }

  override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
    val item = getItem(position)
    holder.bind.tvDappName.text = item.name ?: item.url
    holder.bind.tvDappContent.text = item.description ?: item.subtitle ?: item.url

    Glide.with(holder.itemView).load(item.img
        ?: URLUtils.getWebFavicon(item.url)).placeholder(R.drawable.vector_dapp_default_ic)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(SmartUtil.dp2px(20f)))).into(holder.bind.ivDappLogo)
  }

  interface OnItemClickListener {
    fun onItemClick(position: Int, item: DappCollectionDO)
    fun onItemDeleteClick(position: Int, item: DappCollectionDO)
  }
}

class CollectionDiff : DiffUtil.ItemCallback<DappCollectionDO>() {

  override fun areItemsTheSame(oldItem: DappCollectionDO, newItem: DappCollectionDO): Boolean {
    return oldItem.uuid == newItem.uuid && oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: DappCollectionDO, newItem: DappCollectionDO): Boolean {
    if (oldItem.uuid != newItem.uuid) {
      return false
    }
    if (oldItem.id != newItem.id) {
      return false
    }
    return true
  }
}

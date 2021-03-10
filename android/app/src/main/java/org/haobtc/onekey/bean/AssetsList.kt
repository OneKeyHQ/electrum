package org.haobtc.onekey.viewmodel

import androidx.collection.SparseArrayCompat
import org.haobtc.onekey.bean.Assets
import org.haobtc.onekey.bean.CoinAssets
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList
import kotlin.concurrent.read
import kotlin.concurrent.write

typealias AliasT = Assets

class AssetsList(initialCapacity: Int = 15) : Collection<AliasT> {

  // 读写锁
  private val mReadWriteLock = ReentrantReadWriteLock()

  /**
   * 存放顺序的索引，值为 mAssetStorage 的 key
   */
  private val mIndexList = LinkedList<Int>()

  /**
   * 存放账户资产的仓库
   */
  private val mAssetStorage = SparseArrayCompat<AliasT>(initialCapacity)

  override val size: Int
    get() = mReadWriteLock.read {
      mAssetStorage.size()
    }

  fun get(index: Int): AliasT? {
    mReadWriteLock.read {
      return mIndexList.getOrNull(index)
        ?.let { mAssetStorage.get(it) }
    }
  }

  /**
   * 获取当前钱包的平台币，默认索引0为平台币
   */
  fun getCoinAsset(): CoinAssets {
    mReadWriteLock.read {
      return mIndexList.getOrNull(0).let {
        it?.let { it1 -> mAssetStorage.get(it1) } as CoinAssets
      }
    }
  }

  fun getByUniqueId(uniqueId: Int): AliasT? {
    mReadWriteLock.read {
      return mAssetStorage.get(uniqueId)
    }
  }

  /**
   * 根据唯一 ID 获取，获取不到返回第 0 个资产
   */
  fun getByUniqueIdOrZero(uniqueId: Int): AliasT? {
    mReadWriteLock.read {
      return mAssetStorage.get(uniqueId)
          ?: return mIndexList.getOrNull(0)?.let { mAssetStorage.get(it) }
    }
  }

  fun indexOfUniqueId(uniqueId: Int): Int {
    mReadWriteLock.read {
      return mIndexList.indexOf(uniqueId)
    }
  }

  fun indexOf(element: AliasT): Int {
    mReadWriteLock.read {
      val uniqueId = element.uniqueId()
      return mIndexList.indexOf(uniqueId)
    }
  }

  fun replace(element: AliasT) {
    mReadWriteLock.write {
      val uniqueId = element.uniqueId()
      if (!mIndexList.contains(uniqueId)) {
        mIndexList.add(uniqueId)
      }
      if (mAssetStorage.containsKey(uniqueId)) {
        mAssetStorage.replace(uniqueId, element)
      }
    }
  }

  fun add(element: AliasT) {
    mReadWriteLock.write {
      val uniqueId = element.uniqueId()
      if (mIndexList.contains(uniqueId)) {
        mIndexList.remove(uniqueId)
      }
      mIndexList.add(uniqueId)
      if (mAssetStorage.containsKey(uniqueId)) {
        mAssetStorage.replace(uniqueId, element)
      } else {
        mAssetStorage.append(uniqueId, element)
      }
    }
  }

  fun add(index: Int, element: AliasT) {
    mReadWriteLock.write {
      val uniqueId = element.uniqueId()
      if (mIndexList.contains(uniqueId)) {
        mIndexList.removeAt(index)
      }
      mIndexList.add(index, uniqueId)
      if (mAssetStorage.containsKey(uniqueId)) {
        mAssetStorage.replace(uniqueId, element)
      } else {
        mAssetStorage.append(uniqueId, element)
      }
    }
  }

  fun removeAt(index: Int) {
    if (index == -1) {
      return
    }
    mReadWriteLock.write {
      val removeAt = mIndexList.removeAt(index)
      mAssetStorage.remove(removeAt)
    }
  }

  fun remove(element: AliasT) {
    mReadWriteLock.write {
      val uniqueId = element.uniqueId()
      removeAt(mIndexList.indexOf(uniqueId))
    }
  }

  fun clear() {
    mReadWriteLock.write {
      mIndexList.clear()
      mAssetStorage.clear()
    }
  }

  fun sort(c: Comparator<AliasT>) {
    val comparator = Comparator<Int> { o1, o2 -> c.compare(mAssetStorage.get(o1), mAssetStorage.get(o2)) }
    mReadWriteLock.write {
      mIndexList.sortWith(comparator)
    }
  }

  fun sortFiatBalance() {
    mReadWriteLock.write {
      mIndexList.sortWith { o1, o2 ->
        val assets1 = mAssetStorage[o1]
        val assets2 = mAssetStorage[o2]
        // 链上币排在最前面
        when {
          assets1 is CoinAssets -> {
            1
          }
          assets2 is CoinAssets -> {
            -1
          }
          else -> {
            BigDecimal(assets1?.balanceFiat?.balance ?: "0")
                .compareTo(BigDecimal(assets2?.balanceFiat?.balance ?: "0"))
          }
        }
      }
    }
  }

  override fun contains(element: AliasT) = mReadWriteLock.read {
    mAssetStorage.containsValue(element)
  }

  override fun containsAll(elements: Collection<AliasT>): Boolean {
    mReadWriteLock.read {
      for (element in elements) {
        if (!mAssetStorage.containsValue(element)) {
          return false
        }
      }
      return true
    }
  }

  override fun isEmpty() = mReadWriteLock.read { mAssetStorage.isEmpty }

  @Deprecated("Thread insecurity")
  override fun iterator(): Iterator<AliasT> {
    return object : Iterator<AliasT> {
      private var currentIndex = 0
      override fun hasNext(): Boolean {
        return mAssetStorage.size() > currentIndex
      }

      override fun next(): AliasT {
        val key = mIndexList[currentIndex]
        return mAssetStorage[key]?.also {
          currentIndex += 1
        } ?: throw RuntimeException("key:${key} store error")
      }
    }
  }

  fun lastIndexOf(element: AliasT): Int {
    mReadWriteLock.read {
      return mIndexList.lastIndexOf(element.uniqueId())
    }
  }

  @Deprecated("Thread insecurity")
  fun listIterator(): ListIterator<AliasT> {
    return SubListIterator(mAssetStorage, mIndexList)
  }

  fun toList(): List<AliasT> {
    return subList(0, mAssetStorage.size())
  }

  fun subList(fromIndex: Int, toIndex: Int): List<AliasT> {
    val list = ArrayList<AliasT>(Math.min(toIndex - fromIndex, 0))
    mReadWriteLock.read {
      mIndexList.subList(fromIndex, toIndex).forEach {
        mAssetStorage[it]?.let { it1 -> list.add(it1) }
      }
      return list
    }
  }

  override fun toString(): String {
    return "AssetsList(mIndexList=$mIndexList, mAssetStorage=$mAssetStorage)"
  }

  class SubListIterator(
      private val assetStorage: SparseArrayCompat<AliasT>,
      private val indexList: List<Int>,
      private var currentIndex: Int = 0,
  ) : ListIterator<AliasT> {
    override fun hasNext(): Boolean {
      return assetStorage.size() > currentIndex
    }

    override fun next(): AliasT {
      val key = indexList[currentIndex]
      return assetStorage[key]?.also {
        currentIndex += 1
      } ?: throw RuntimeException("key:${key} store error")
    }

    override fun hasPrevious(): Boolean {
      return currentIndex - 1 >= 0
    }

    override fun nextIndex(): Int {
      return currentIndex + 1
    }

    override fun previous(): AliasT {
      val key = indexList[currentIndex - 1]
      return assetStorage[key]?.also {
        currentIndex -= 1
      } ?: throw RuntimeException("key:${key} store error")
    }

    override fun previousIndex(): Int {
      return currentIndex - 1
    }
  }
}

package org.haobtc.onekey.repository.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.haobtc.onekey.repository.database.entity.DappCollectionDO

@Dao
abstract class DappCollectionDao : BaseDao<DappCollectionDO> {
  @Query("DELETE FROM dapp_collect")
  abstract fun deleteAll()

  @Query("SELECT count(id) FROM dapp_collect")
  abstract fun count(): Long

  @Query("SELECT * FROM dapp_collect ORDER BY createTime DESC")
  abstract fun queryAll(): List<DappCollectionDO>

  @Query("DELETE FROM dapp_collect WHERE uuid = :uuid")
  abstract fun deleteAtUuid(uuid: String)

  @Query("SELECT * FROM dapp_collect ORDER BY createTime DESC")
  abstract fun observe(): LiveData<List<DappCollectionDO>>

  @Query("SELECT * FROM dapp_collect WHERE url = :url LIMIT 1")
  abstract fun existsUrl(url: String): List<DappCollectionDO>

  @Query("SELECT * FROM dapp_collect WHERE url = :url LIMIT 1")
  abstract fun observeExistsUrl(url: String): LiveData<List<DappCollectionDO>>

  @Query("SELECT * FROM dapp_collect WHERE uuid = :uuid LIMIT 1")
  abstract fun existsUuid(uuid: String): List<DappCollectionDO>

  @Query("SELECT * FROM dapp_collect WHERE uuid = :uuid LIMIT 1")
  abstract fun observeExistsUuid(uuid: String): LiveData<List<DappCollectionDO>>

  @Transaction
  open fun insertOnes(dapp: DappCollectionDO, max: Int = 30): Boolean {
    if (count() >= max) {
      return false
    }
    insert(dapp)
    return true
  }
}

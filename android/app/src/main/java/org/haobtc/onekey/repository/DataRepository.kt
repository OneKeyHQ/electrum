package org.haobtc.onekey.repository

import org.haobtc.onekey.activities.base.MyApplication
import org.haobtc.onekey.repository.database.AppDatabase

class DataRepository(private val database: AppDatabase) {
  private val mDatabase: AppDatabase = database

  companion object {
    @JvmStatic
    private var sInstance: DataRepository? = null

    @JvmStatic
    fun getInstance(database: AppDatabase): DataRepository {
      return sInstance ?: synchronized(DataRepository::class.java) {
        sInstance ?: DataRepository(database).also {
          sInstance = it
        }
      }
    }

    public fun getDappCollectionDao() = getInstance(AppDatabase.getInstance(MyApplication.getInstance())).mDatabase.dappCollectionDao()
  }
}

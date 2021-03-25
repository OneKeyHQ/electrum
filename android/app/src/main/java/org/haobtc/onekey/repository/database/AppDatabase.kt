package org.haobtc.onekey.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.haobtc.onekey.repository.database.converter.DateConverter
import org.haobtc.onekey.repository.database.dao.DappCollectionDao
import org.haobtc.onekey.repository.database.entity.DappCollectionDO

@Database(entities = [DappCollectionDO::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

  abstract fun dappCollectionDao(): DappCollectionDao

  companion object {
    private const val DATABASE_NAME = "onekey_db_v1"

    @Volatile
    private var sInstance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return sInstance ?: synchronized(this) {
        sInstance ?: buildDatabase(context).also { sInstance = it }
      }
    }

    private fun buildDatabase(context: Context): AppDatabase {
      return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
          .build()
    }
  }
}

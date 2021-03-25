package org.haobtc.onekey.repository.database.dao

import androidx.room.Delete
import androidx.room.Insert

interface BaseDao<T> {
  @Insert
  fun insert(vararg obj: T)

  @Delete
  fun delete(vararg obj: T)
}

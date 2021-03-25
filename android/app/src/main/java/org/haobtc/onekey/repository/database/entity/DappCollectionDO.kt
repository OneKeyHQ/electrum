package org.haobtc.onekey.repository.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

annotation class DappCollectionType {
  companion object {
    const val URL = 0
    const val DAPP = 1
  }
}

@Entity(tableName = DappCollectionDO.TABLE_NAME,
    indices = [
      Index(value = ["uuid"], unique = true)
    ]
)
data class DappCollectionDO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "uuid")
    var uuid: String,

    @DappCollectionType
    @ColumnInfo(name = "type")
    var type: Int,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "chain")
    var chain: String?,

    @ColumnInfo(name = "subtitle")
    var subtitle: String?,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "img")
    var img: String?,

    @ColumnInfo(name = "createTime")
    var createTime: Date = Date(),
) {
  companion object {
    const val TABLE_NAME = "dapp_collect"
  }
}

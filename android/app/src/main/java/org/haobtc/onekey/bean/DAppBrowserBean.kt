package org.haobtc.onekey.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class JsBridgeRequestBean(
    @SerializedName("method")
    val method: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("params")
    val params: String,
)

data class JsBridgeResponseBean(
    @SerializedName("id")
    val id: String,
    @SerializedName("result")
    val result: String,
)

data class DAppBrowserBean(
    @SerializedName("chain")
    val chain: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("favicon")
    val favicon: String?,
    @SerializedName("img")
    val img: String?,
    @SerializedName("code")
    val uuid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("url")
    val url: String?,
    var firstUse: Boolean = false,
) : Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readString() ?: "",
      parcel.readByte().toInt() == 1) {
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(chain)
    parcel.writeString(description)
    parcel.writeString(favicon)
    parcel.writeString(img)
    parcel.writeString(uuid)
    parcel.writeString(name)
    parcel.writeString(subtitle)
    parcel.writeString(url)
    parcel.writeByte(if (firstUse) 1 else 0)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DAppBrowserBean> {
    override fun createFromParcel(parcel: Parcel): DAppBrowserBean {
      return DAppBrowserBean(parcel)
    }

    override fun newArray(size: Int): Array<DAppBrowserBean?> {
      return arrayOfNulls(size)
    }
  }

}

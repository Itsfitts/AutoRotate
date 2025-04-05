package com.eiyooooo.autorotate.data

import android.content.pm.ActivityInfo
import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class ScreenConfig(
    val displayAddress: String,
    val displayName: String,
    val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(displayAddress)
        parcel.writeString(displayName)
        parcel.writeInt(orientation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScreenConfig> {
        override fun createFromParcel(parcel: Parcel): ScreenConfig {
            return ScreenConfig(parcel)
        }

        override fun newArray(size: Int): Array<ScreenConfig?> {
            return arrayOfNulls(size)
        }
    }
}

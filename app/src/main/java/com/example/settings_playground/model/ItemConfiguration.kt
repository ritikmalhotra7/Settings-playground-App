package com.example.settings_playground.model

import android.os.Parcel
import android.os.Parcelable

data class ItemConfiguration(
    val id: String,
    val string1: String,
    var string2: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(string1)
        parcel.writeString(string2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemConfiguration> {
        override fun createFromParcel(parcel: Parcel): ItemConfiguration {
            return ItemConfiguration(parcel)
        }

        override fun newArray(size: Int): Array<ItemConfiguration?> {
            return arrayOfNulls(size)
        }
    }
}

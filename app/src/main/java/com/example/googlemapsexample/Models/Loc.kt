package com.example.googlemapsexample.Models

import android.os.Parcel
import android.os.Parcelable

class Loc constructor(var Longitude:String,var Latitude:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Longitude)
        parcel.writeString(Latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Loc> {
        override fun createFromParcel(parcel: Parcel): Loc {
            return Loc(parcel)
        }

        override fun newArray(size: Int): Array<Loc?> {
            return arrayOfNulls(size)
        }
    }

}
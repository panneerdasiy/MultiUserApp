package com.iydas.multiuserloginapp.pojo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "contact")
data class Contact(
    var email: String = "",
    var name: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    @ColumnInfo(name = "mobile_number")
    var mobileNumber: String = ""
) : Parcelable
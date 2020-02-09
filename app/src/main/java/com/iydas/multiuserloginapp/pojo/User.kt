package com.iydas.multiuserloginapp.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    var name: String = "",
    @PrimaryKey(autoGenerate = false)
    var email: String = "",
    var password: String = "",
    @Ignore
    var passwordConfirm: String = ""
)
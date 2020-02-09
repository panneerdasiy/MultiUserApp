package com.iydas.multiuserloginapp.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_user")
data class LoggedUser(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,
    var email: String = ""
)
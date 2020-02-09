package com.iydas.multiuserloginapp.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.iydas.multiuserloginapp.pojo.Contact
import com.iydas.multiuserloginapp.pojo.LoggedUser
import com.iydas.multiuserloginapp.pojo.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun saveUser(user: User)

    @Query("SELECT * from user WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?
}

@Dao
interface ContactDao{
    @Query("SELECT * from contact user WHERE :email = email")
    fun getContacts(email: String): LiveData<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveContact(contact: Contact?)

    @Delete
    fun deleteContact(contact: Contact?)
}

@Dao
interface LoggedUserDao{
    @Query("SELECT * FROM logged_user")
    fun getLoggedUser(): LoggedUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setLoggedUser(loggedUser: LoggedUser)
}

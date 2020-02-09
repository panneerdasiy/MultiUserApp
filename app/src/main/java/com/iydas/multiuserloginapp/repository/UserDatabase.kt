package com.iydas.multiuserloginapp.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iydas.multiuserloginapp.pojo.Contact
import com.iydas.multiuserloginapp.pojo.LoggedUser
import com.iydas.multiuserloginapp.pojo.User

@Database(entities = [User::class, Contact::class, LoggedUser::class], version = 1,  exportSchema = false)
abstract class UserDatabase: RoomDatabase(){
    abstract val userDao: UserDao
    abstract val contactDao: ContactDao
    abstract val loggedUserDao: LoggedUserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null


        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_contacts_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}
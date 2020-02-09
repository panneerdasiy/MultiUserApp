package com.iydas.multiuserloginapp

import android.app.Application
import android.util.Log
import com.iydas.multiuserloginapp.repository.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyApplication : Application() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object{
        lateinit var database: UserDatabase
    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            database = getDatabase()
            Log.d("MyApp", "$database")
        }
    }

    private fun getDatabase(): UserDatabase {
        return UserDatabase.getInstance(this)
    }
}
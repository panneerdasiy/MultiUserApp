package com.iydas.multiuserloginapp.utils

import android.content.Context
import android.content.SharedPreferences

class Preference private constructor() {

    companion object {
        private lateinit var sharedPref: SharedPreferences

        fun getSharedPref(context: Context): SharedPreferences {
            if (::sharedPref.isInitialized) {
                sharedPref = context.getSharedPreferences(
                    context.applicationInfo.packageName,
                    Context.MODE_PRIVATE
                )
            }
            return sharedPref
        }
    }


}
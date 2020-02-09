package com.iydas.multiuserloginapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.dismissKeyboard(){
    val ipMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    ipMethodManager.hideSoftInputFromWindow(windowToken, 0)
}
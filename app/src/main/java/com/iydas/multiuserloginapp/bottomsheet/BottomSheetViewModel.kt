package com.iydas.multiuserloginapp.bottomsheet

import android.database.sqlite.SQLiteConstraintException
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iydas.multiuserloginapp.MyApplication
import com.iydas.multiuserloginapp.pojo.Contact
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*

@Parcelize
enum class Mode : Parcelable {
    EDIT,
    VIEW,
    SAVE
}

class BottomSheetViewModel(
    contact: Contact?,
    mode: Mode?
) : ViewModel() {

    private val _contact: MutableLiveData<Contact> = MutableLiveData()
    val contact: LiveData<Contact>
        get() = _contact
    private lateinit var loggedEmail: String
    private val TAG: String = this.javaClass.name
    private val _mode: MutableLiveData<Mode> = MutableLiveData()
    val mode: LiveData<Mode>
        get() = _mode
    private val _dismiss: MutableLiveData<Boolean> = MutableLiveData(false)
    val dismiss: LiveData<Boolean>
        get() = _dismiss

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _mode.value = mode ?: Mode.SAVE
        _contact.value = contact
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val loggedUser = MyApplication.database.loggedUserDao.getLoggedUser()
                if (loggedUser != null) {
                    loggedEmail = loggedUser.email
                }
            }
        }
    }

    fun saveClicked(name: String, mobile: String) {
        Log.d(TAG, "save = $name $mobile ${mode.value} ${contact.toString()}")
        if (name.isNotBlank() && mobile.isNotBlank()) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        MyApplication.database.contactDao.saveContact(
                            Contact(
                                id = if (contact.value != null) contact.value!!.id else 0L,
                                email = loggedEmail,
                                name = name, mobileNumber = mobile
                            )
                        )
                        _dismiss.postValue(true)
                    } catch (e: SQLiteConstraintException) {
                        Log.e(TAG, "error = ", e)
                    }
                }
            }
        }
    }

    fun editClicked() {
        _mode.value = Mode.EDIT
    }

    fun deleteClicked() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                MyApplication.database.contactDao.deleteContact(contact.value)
                _dismiss.postValue(true)
            }
        }
    }

    fun stopDismissing() {
        _dismiss.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class BottomSheetModelFactory(
    private val contact: Contact?,
    private val mode: Mode?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetViewModel::class.java)) {
            return BottomSheetViewModel(contact, mode) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
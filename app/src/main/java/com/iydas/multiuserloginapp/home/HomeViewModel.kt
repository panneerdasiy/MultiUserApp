package com.iydas.multiuserloginapp.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iydas.multiuserloginapp.MyApplication
import com.iydas.multiuserloginapp.pojo.Contact
import com.iydas.multiuserloginapp.pojo.LoggedUser
import kotlinx.coroutines.*

class HomeViewModel : ViewModel() {

    private val TAG: String = this.javaClass.name
    private val _contactsLoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val contactsLoaded: LiveData<Boolean>
        get() = _contactsLoaded
    private val _showBottomSheet: MutableLiveData<Contact> = MutableLiveData()
    val showBottomSheet: LiveData<Contact>
        get() = _showBottomSheet
    private val _moveToLogin: MutableLiveData<Boolean> = MutableLiveData()
    val moveToLogin: LiveData<Boolean>
        get() = _moveToLogin
    lateinit var contacts: LiveData<List<Contact>>

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val loggedUser = MyApplication.database.loggedUserDao.getLoggedUser()
                if (loggedUser != null) {
                    contacts = getContacts(loggedUser.email)
                    _contactsLoaded.postValue(true)
                }
            }
        }
    }


    fun getContacts(email: String): LiveData<List<Contact>> {
        return MyApplication.database.contactDao.getContacts(email)
    }

    fun logOut() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                MyApplication.database.loggedUserDao.setLoggedUser(LoggedUser())
                _moveToLogin.postValue(true)
            }
        }
    }

    fun finishMovingToLogin() {
        Log.d(TAG, "finish logout")
        _moveToLogin.value = null
    }

    fun fabAddClicked() {
        _showBottomSheet.value = Contact()
    }

    fun dismissBottomSheet() {
        _showBottomSheet.value = null
    }

    fun showContactBottomSheet(contact: Contact) {
        _showBottomSheet.value = contact
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

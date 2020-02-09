package com.iydas.multiuserloginapp.login

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iydas.multiuserloginapp.MyApplication
import com.iydas.multiuserloginapp.R
import com.iydas.multiuserloginapp.pojo.LoggedUser
import com.iydas.multiuserloginapp.pojo.User
import com.iydas.multiuserloginapp.utils.EmailValidator
import com.iydas.multiuserloginapp.utils.PasswordValidator
import com.iydas.multiuserloginapp.utils.Result
import com.iydas.multiuserloginapp.utils.Validator
import kotlinx.coroutines.*

class LoginViewModel : ViewModel() {
    private val TAG: String = this.javaClass.name

    private val _loggedEmail: MutableLiveData<String> = MutableLiveData()
    val loggedEmail: LiveData<String>
        get() {
            return _loggedEmail
        }
    private val _signUpUserName: MutableLiveData<String> = MutableLiveData()
    val signUpUserName: LiveData<String>
        get() {
            return _signUpUserName
        }
    private val _signUpEmailError: MutableLiveData<String> = MutableLiveData()
    val signUpEmailError: LiveData<String>
        get() {
            return _signUpEmailError
        }
    private val _signUpPasswordError: MutableLiveData<String> = MutableLiveData()
    val signUpPasswordError: LiveData<String>
        get() {
            return _signUpPasswordError
        }
    private val _showSignUpAlert: MutableLiveData<Boolean> = MutableLiveData()
    val showSignUpAlert: LiveData<Boolean>
        get() {
            return _showSignUpAlert
        }
    private val _moveToHome: MutableLiveData<String> = MutableLiveData()
    val moveToHome: LiveData<String>
        get() {
            return _moveToHome
        }
    private val _errorEtEmail: MutableLiveData<String> = MutableLiveData()
    val errorEtEmail: LiveData<String>
        get() {
            return _errorEtEmail
        }
    private val _errorEtPassword: MutableLiveData<String> = MutableLiveData()
    val errorEtPassword: LiveData<String>
        get() {
            return _errorEtPassword
        }
    private val _showToast: MutableLiveData<Int> = MutableLiveData()
    val showToast: LiveData<Int>
        get() {
            return _showToast
        }

    private val viewModelJob: Job = Job()
    private val uiScope: CoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val logged = MyApplication.database.loggedUserDao.getLoggedUser()
                Log.d(TAG, "logged = $logged")
                _loggedEmail.postValue(logged?.email)
            }
        }
    }

    fun login(email: String, password: String) {
        //check database in co-routines
        val passwordValidator = getPasswordValidator()
        if (validateEmailPasswordSetError(
                passwordValidator = passwordValidator,
                email = email,
                password = password
            )
        ) {
            //set loggedEmail if success
            uiScope.launch {
                val user = withContext(Dispatchers.IO) {
                    MyApplication.database.userDao.getUser(email, password)
                }
                Log.d(TAG, "after  $user ")
                if (user != null && email == user.email && password == user.password) {
                    withContext(Dispatchers.IO){
                        MyApplication.database.loggedUserDao.setLoggedUser(LoggedUser(email = user.email))
                    }
                    _moveToHome.value = user.email
                }else
                    _showToast.value = R.string.invalid_credentials
            }
        }
    }

    private fun validateEmailPasswordSetError(
        emailValidator: Validator = EmailValidator(),
        passwordValidator: Validator = PasswordValidator(),
        email: String,
        password: String
    ): Boolean {
        val emailRes: Result = emailValidator.validate(email)
        val passwordRes: Result = passwordValidator.validate(password)

        _errorEtEmail.value = if (emailRes.success) null else emailRes.message
        _errorEtPassword.value = if (passwordRes.success) null else passwordRes.message

        return emailRes.success && passwordRes.success
    }


    fun showSignUp() {
        _showSignUpAlert.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun finishShowingToast() {
        _showToast.value = null
    }

    fun completeMovingToHome() {
        _moveToHome.value = null
    }

    fun signUpValidation(user: User) {
        Log.d(TAG, user.toString())
        var hasError = false
        if (!validateEmail(user.email)) {
            hasError = true
        }
        if (!validatePassword(user.password)) {
            hasError = true
        }
        if (!validateUsername(user.name)) {
            hasError = true
        }
        if (!validateMatchPasswords(user.password, user.passwordConfirm)) {
            hasError = true
        }
        if (!hasError) {
            signUp(user)
        }
    }

    private fun signUp(user: User) {
        Log.d(TAG, "signUp save to db")
        uiScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    MyApplication.database.userDao.saveUser(user)
                    MyApplication.database.loggedUserDao.setLoggedUser(LoggedUser(email = user.email))
                    _moveToHome.postValue(user.email)
                } catch (e: SQLiteConstraintException) {
                    _showToast.postValue(R.string.sign_up_failed)
                }
            }
        }
    }

    private fun validateMatchPasswords(password: String, passwordConfirm: String): Boolean {
        if (password.isNotBlank() || passwordConfirm.isNotBlank())
            _showToast.value = if (password == passwordConfirm) null else R.string.password_mismatch
        return password == passwordConfirm
    }

    private fun validateUsername(userName: String): Boolean {
        _signUpUserName.value = if (userName.trim().isEmpty()) "Username cannot be empty" else null
        return userName.trim().isNotEmpty()
    }

    private fun validatePassword(password: String): Boolean {
        val validator = getPasswordValidator()
        val result = validator.validate(password)
        _signUpPasswordError.value = if (result.success) null else result.message
        return result.success
    }

    private fun validateEmail(email: String): Boolean {
        val validator = EmailValidator()
        val result = validator.validate(email)
        _signUpEmailError.value = if (result.success) null else result.message
        return result.success
    }

    fun dismissSignUpAlert() {
        _showSignUpAlert.value = false
    }

    private fun getPasswordValidator(): PasswordValidator {
        val validator = PasswordValidator()
        return validator.apply {
            minCount = 6
            upperCaseCheck = true
            lowerCaseCheck = true
            numericalCheck = true
        }
    }

    fun alreadyLoggedIn(email: String) {
        _moveToHome.value = email
    }
}

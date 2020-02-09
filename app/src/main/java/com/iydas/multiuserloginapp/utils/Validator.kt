package com.iydas.multiuserloginapp.utils

import android.util.Log

interface Validator {
    fun validate(data: String): Result
}

class EmailValidator: Validator {
    override fun validate(data: String): Result {
        var success: Boolean = false
        val message: String = "Please enter a valid E-mail address"

        val regEx: String =
        "^[a-zA-Z0-9.a-zA-Z0-9.!#\$%&'*+-/=?^_`{|}~]+@[a-zA-Z0-9]+\\.[a-zA-Z]+"

        success = Regex(regEx).matches(data)

        return Result(
            success,
            if (success) "" else message
        )
    }
}

class PasswordValidator(var upperCaseCheck: Boolean = false,
                        var lowerCaseCheck: Boolean = false,
                        var numericalCheck: Boolean = false,
                        var specialCharCheck: Boolean = false) :
    Validator {

    private val TAG: String = this.javaClass.name
    private var _DEFAULT_MIN: Int = 1
    var minCount: Int = _DEFAULT_MIN
        ///count has to greater than 0
        set(count) {
            if (count > 1) {
                Log.d(TAG, "minCount = ${count}")
                field = count
                _MIN_COUNT_FAILED =
                    "Password has to contain minimum $minCount ${if (minCount == 1) "character" else "characters"}"
            }
        }


    private var _MIN_COUNT_FAILED: String =
        "Password has to contain minimum $minCount ${if (minCount == 1) "character" else "characters"}"
    private val _UPPER_CASE_CHECK_FAILED : String =
        "Password has to contain atleast one upper case letter"
    private val _LOWER_CASE_CHECK_FAILED : String =
        "Password has to contain atleast one lower case letter"
    private val _NUMERICAL_CHECK_FAILED : String =
        "Password has to contain atleast one number"
    private var _SPECIAL_CHARS_CHECK_FAILED : String

    /// specify all the special characters that you want to validate with out space as a single string with necessary escape characters eg., \\|@%\+\\\\/'!#\$\^?:;,()\\]\\[\{\}~\.=\-
//    var _specialChars: String = "\\|@%\\+\\\\/'!#\$\\^?:;,()\\]\\[\\{\\}~\\.=\\-"
    var specialChars: String = "|@%+\\\\/'!#\$^?:;,()\\]\\[{}~.=\\-"
        set(characters) {
            field = characters
            _specialCharsRegEx = Regex("[$characters]+")
            _SPECIAL_CHARS_CHECK_FAILED =
                "Password has to contain atleast one special characters from $specialChars";
        }

    private var _specialCharsRegEx: Regex
    init {
        _specialCharsRegEx = Regex("[$specialChars]+")
        _SPECIAL_CHARS_CHECK_FAILED =
            "Password has to contain atleast one special characters from $specialChars"
    }


    override fun validate(data: String): Result {
        var success: Boolean = false
        var message: String = ""
        val upperCaseRegExp: Regex = Regex("[A-Z]+")
        val lowerCaseRegExp: Regex = Regex("[a-z]+")
        val numericalRegExp: Regex  = Regex("[0-9]+")

        if (minCount >= _DEFAULT_MIN && data.trim().length < minCount) {
            //check if the password has the min number (default min = 1) of characters
            Log.d(TAG, "$minCount < ${data.trim().length}")
            message = _MIN_COUNT_FAILED;
        } else if (upperCaseCheck && !upperCaseRegExp.containsMatchIn(data)) {
            //check if at least one upper case is there
            Log.d(TAG, "upperCaseCheck $data")
            message = _UPPER_CASE_CHECK_FAILED;
        } else if (lowerCaseCheck && !lowerCaseRegExp.containsMatchIn(data)) {
            //check if at least one lower case is there
            Log.d(TAG, "lowerCaseCheck")
            message = _LOWER_CASE_CHECK_FAILED;
        } else if (numericalCheck && !numericalRegExp.containsMatchIn(data)) {
            //check if at least one numerical character is there
            Log.d(TAG, "numericalCheck")
            message = _NUMERICAL_CHECK_FAILED;
        } else if (specialCharCheck && !_specialCharsRegEx.containsMatchIn(data)) {
            //check if at least one special character is there
            Log.d(TAG, "specialCharCheck")
            message = _SPECIAL_CHARS_CHECK_FAILED;
        } else {
            Log.d(TAG, "else")
            success = true;
        }

        return Result(success, message);
    }
}

data class Result(var success: Boolean, var message: String)


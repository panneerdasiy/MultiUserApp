package com.iydas.multiuserloginapp.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.iydas.multiuserloginapp.R
import com.iydas.multiuserloginapp.databinding.LoginFragmentBinding
import com.iydas.multiuserloginapp.databinding.SignUpFragmentBinding
import com.iydas.multiuserloginapp.pojo.User
import com.iydas.multiuserloginapp.utils.dismissKeyboard

class LoginFragment : Fragment() {
    private val TAG: String = this.javaClass.name
    private lateinit var signAlert: AlertDialog
    private lateinit var alertBinding: SignUpFragmentBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        binding.btnLogin.setOnClickListener {
            it?.dismissKeyboard()
            viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
        binding.btnSignUp.setOnClickListener {
            it?.dismissKeyboard()
            viewModel.showSignUp()
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.errorEtPassword.observe(viewLifecycleOwner, Observer {
            binding.etPassword.error = it
        })
        viewModel.errorEtEmail.observe(viewLifecycleOwner, Observer {
            binding.etEmail.error = it
        })
        viewModel.loggedEmail.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "already logged $it")
            if (it != null && it.isNotBlank()) {
                viewModel.alreadyLoggedIn(it)
            }
        })
        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "Toast $it")
            if (it != null) {
                Toast.makeText(activity, getString(it), Toast.LENGTH_LONG).show()
                viewModel.finishShowingToast()
            }
        })
        viewModel.moveToHome.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Navigation.findNavController(binding.btnLogin)
                    .navigate(R.id.action_loginFragment_to_homeFragment)
                viewModel.completeMovingToHome()
            }
        })
        viewModel.showSignUpAlert.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "show signup $it")
            if (it)
                showSignUpAlert(activity as Context)
            else
                if (::signAlert.isInitialized) dismissSignUpAlert(signAlert)
        })
        viewModel.signUpPasswordError.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "signUpError = $it")
            if (::alertBinding.isInitialized)
                alertBinding.etPassword.error = it
        })
        viewModel.signUpUserName.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "signUpUserName = $it")
            if (::alertBinding.isInitialized)
                alertBinding.etName.error = it
        })
        viewModel.signUpEmailError.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "signUpEmailError = $it")
            if (::alertBinding.isInitialized)
                alertBinding.etEmail.error = it
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.dismissSignUpAlert()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OnDestroy")
    }

    private fun showSignUpAlert(
        context: Context
    ) {
        Log.d(TAG, "show signup")
        if (!::alertBinding.isInitialized)
            alertBinding = initSignUpAlert()
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle(R.string.sign_up)
            .setView(alertBinding.root)
        signAlert = builder.show()
        signAlert.setOnDismissListener {
            viewModel.dismissSignUpAlert()
        }
    }


    private fun initSignUpAlert(): SignUpFragmentBinding {
        alertBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.sign_up_fragment,
            null,
            false
        )
        alertBinding.btnSignUp.setOnClickListener {
            Log.d(TAG, "sign up $it")
            it?.dismissKeyboard()
            viewModel.signUpValidation(
                User(
                    name = alertBinding.etName.text.toString(),
                    email = alertBinding.etEmail.text.toString(),
                    password = alertBinding.etPassword.text.toString(),
                    passwordConfirm = alertBinding.etPasswordConfirm.text.toString()
                )
            )
        }
        return alertBinding
    }

    private fun dismissSignUpAlert(alert: AlertDialog) {
        alert.dismiss()
    }
}

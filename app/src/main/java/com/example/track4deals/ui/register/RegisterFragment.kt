package com.example.track4deals.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var registerViewModel: RegisterViewModel
    private val registerViewModelFactory: RegisterViewModelFactory by instance()

    companion object {
        const val TAG = "RegistrationFragment"
        fun newInstance() = RegisterFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerViewModel =
            ViewModelProvider(this, registerViewModelFactory).get(RegisterViewModel::class.java)
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel.registerFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                registrationButton.isEnabled = loginFormState.isDataValid

                loginFormState.usernameError?.let {
                    usernameReg.error = getString(it)
                }
                loginFormState.emailError?.let {
                    emailReg.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordReg.error = getString(it)
                }
                loginFormState.password2Error?.let {
                    repeat_password.error = getString(it)
                }
            }
        )

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                registerViewModel.registerDataChanged(
                    usernameReg.text.toString(),
                    emailReg.text.toString(),
                    passwordReg.text.toString(),
                    repeat_password.text.toString()
                )
            }
        }
        usernameReg.addTextChangedListener(afterTextChangedListener)
        emailReg.addTextChangedListener(afterTextChangedListener)
        passwordReg.addTextChangedListener(afterTextChangedListener)
        repeat_password.addTextChangedListener(afterTextChangedListener)

        goToSignInButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        registrationButton.setOnClickListener {
            loadingR.visibility = View.VISIBLE
            registerViewModel.registerUserServer(usernameReg.text.toString(), emailReg.text.toString(), passwordReg.text.toString())
        }

        registerViewModel.registerResult.observe(viewLifecycleOwner,
            Observer { regResult ->
                regResult ?: return@Observer
                loadingR.visibility = View.GONE
                regResult.error?.let {
                    showLoginFailed(it)
                    Log.d(TAG, "onViewCreated: FAILED")
                }
                regResult.success?.let {
                    updateUiWithUser()
                    Log.d(TAG, "onViewCreated: success: ${regResult.success}")
                }
            })

    }

    private fun updateUiWithUser() {
        Toast.makeText(context, getString(R.string.registrationSucceded), Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.navigation_login)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(context, getString(errorString), Toast.LENGTH_LONG).show()
    }


}
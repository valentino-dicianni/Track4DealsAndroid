package com.example.track4deals.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import com.example.track4deals.data.models.LoggedInUserView
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.ui.register.RegisterFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val loginViewModelFactory: LoginViewModelFactory by instance()
    private lateinit var loginViewModel: LoginViewModel

    companion object {
        const val TAG = "LoginFragment"
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginViewModel = ViewModelProvider(this, loginViewModelFactory)
            .get(LoginViewModel::class.java)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    username.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    password.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loading.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })


        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )

            }
        }
        username.addTextChangedListener(afterTextChangedListener)
        password.addTextChangedListener(afterTextChangedListener)
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(
                username.text.toString(),
                password.text.toString()
            )
        }

        toRegistrationBtn.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.nav_host_fragment, RegisterFragment.newInstance())
                    .addToBackStack(RegisterFragment.TAG)
                    .commit()
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()


        findNavController().navigate(R.id.navigation_profile)



    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}
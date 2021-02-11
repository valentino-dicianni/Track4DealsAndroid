package com.example.track4deals.ui.login

import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.RC_SIGN_IN
import com.example.track4deals.data.models.LoggedInUserView
import com.example.track4deals.ui.register.RegisterFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class LoginFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val loginViewModelFactory: LoginViewModelFactory by instance()
    private lateinit var loginViewModel: LoginViewModel

    companion object {
        const val TAG = "LoginFragment"
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

        loginViewModel.changePswState.observe(viewLifecycleOwner,
            Observer { forgotFormStete ->
                if (forgotFormStete == null) {
                    return@Observer
                }

                if (forgotFormStete.isDataValid) {
                    forgot_password.isEnabled = true
                    forgot_password.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primaryDarkColorBlue
                        )
                    )
                } else {
                    forgot_password.isEnabled = false
                    forgot_password.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primaryDarkColorGray
                        )
                    )
                }

            })

        loginViewModel.changePswResponse.observe(viewLifecycleOwner, Observer {
            if (it.status) {
                Toast.makeText(context, getString(R.string.checkEmail), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, getString(R.string.error_reset_Psw), Toast.LENGTH_LONG)
                    .show()

            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loading.visibility = View.GONE
                containerLogin.visibility = View.VISIBLE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
                loginViewModel.forgotPasswordDataChanged(username.text.toString())
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
            containerLogin.visibility = View.GONE
            loginViewModel.login(
                username.text.toString(),
                password.text.toString()
            )
        }

        forgot_password.setOnClickListener {
            loginViewModel.forgotPassword(
                username.text.toString()
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

        google_button.setOnClickListener {
            loading.visibility = View.VISIBLE
            signInGoogle()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcomeMessage) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.navigation_profile)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        loading.visibility = View.GONE
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(errorString), Toast.LENGTH_LONG).show()
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
            loginViewModel.loginWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            showLoginFailed(R.string.errorGLogin)
        }
    }
}
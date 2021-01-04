package com.example.track4deals.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.example.track4deals.R

class RegisterFragment : Fragment() {

    companion object {
        const val TAG = "RegistrationFragment"
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInButton = view.findViewById<Button>(R.id.goToSignInButton)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingR)
        val registerButton = view.findViewById<Button>(R.id.registrationButton)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameReg)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordReg)

        signInButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        registerButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.addUser(
                "testUSER",
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }


}
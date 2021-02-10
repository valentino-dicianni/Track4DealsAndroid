package com.example.track4deals.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import kotlinx.android.synthetic.main.change_password_fragment.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ChangePasswordDialogFragment() : DialogFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()
    private lateinit var viewModel : ProfileViewModel

    companion object {
        const val TAG = "ChangePasswordFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.change_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.changeFormState.observe(viewLifecycleOwner,
            Observer { changePswdState ->

                if (changePswdState == null) {
                    return@Observer
                }
                changePassButton.isEnabled = changePswdState.isDataValid

                changePswdState.oldPasswordError?.let {
                    old_password.error = getString(it)
                }

                changePswdState.newPasswordError?.let {
                    new_password.error = getString(it)
                }

                changePswdState.repeatPasswordError?.let {
                    repeat_password.error = getString(it)
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
                viewModel.passwdDataChanged(
                    old_password.text.toString(),
                    new_password.text.toString(),
                    repeat_password.text.toString()
                )
            }
        }
        old_password.addTextChangedListener(afterTextChangedListener)
        new_password.addTextChangedListener(afterTextChangedListener)
        repeat_password.addTextChangedListener(afterTextChangedListener)


        changePassButton.setOnClickListener {
            viewModel.changePassword(old_password.text.toString(), new_password.text.toString())
            dismiss()
        }

    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
package com.example.track4deals.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.track4deals.R
import kotlinx.android.synthetic.main.fragment_profile_edit_dialog.*
import kotlinx.android.synthetic.main.fragment_profile_edit_dialog.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class PasswordConfirmationDialogFragment : DialogFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()
    private lateinit var viewModel: ProfileViewModel

    companion object {
        const val TAG = "DialogWithData"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile_edit_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)

        if (viewModel.isDeleteDialogFragment) {
            textView.text = getString(R.string.dialog_message_delete)
            btnSubmit.text = getString(R.string.caps_delete)
        } else {
            textView.text = getString(R.string.dialog_message_email)
            btnSubmit.text = getString(R.string.caps_edit)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupClickListeners(view: View) {
        view.btnSubmit.setOnClickListener {
            viewModel.sendPassword(view.txt_password.text.toString())
            dismiss()
        }
    }

}
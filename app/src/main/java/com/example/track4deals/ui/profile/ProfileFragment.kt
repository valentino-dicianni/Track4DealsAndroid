package com.example.track4deals.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import com.example.track4deals.internal.ScopedFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_profile.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()

    companion object {
        fun newInstance() = ProfileFragment()
        const val TAG = "ProfileFragment"

    }

    private lateinit var viewModel: ProfileViewModel
    private var firebaseUser: FirebaseUser? = null

    private fun makeVisible(textInput: TextInputEditText){
        textInput.focusable = 1
        textInput.isClickable = true
        textInput.isCursorVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (firebaseUser != null) {
            fullname_field.text = firebaseUser!!.displayName
            email_field.text = firebaseUser!!.email
            name_profile.setText(firebaseUser!!.displayName)
            email_profile.setText(firebaseUser!!.email)
            phone_profile.setText(firebaseUser!!.phoneNumber)


        } else {
            navController.navigate(R.id.navigation_login)
        }

        modify_profile_btn.setOnClickListener{
            makeVisible(name_profile)
            makeVisible(email_profile)
            makeVisible(phone_profile)

            //TODO API communication
            modify_profile_btn.text = getString(R.string.Save)
        }

        change_password_btn.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                        .replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance())
                        .addToBackStack(ChangePasswordFragment.TAG)
                        .commit()
            }
        }

    }






}
package com.example.track4deals.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()
    private val userProvider : UserProvider by instance()

    companion object {
        fun newInstance() = ProfileFragment()
        const val TAG = "ProfileFragment"

    }

    private lateinit var viewModel: ProfileViewModel

    private fun makeVisible(textInput: TextInputEditText){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            textInput.focusable = 1
        }
        textInput.isClickable = true
        textInput.isCursorVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (userProvider.isLoggedIn()) {
            fullname_field.text = userProvider.getUserName()
            email_field.text = userProvider.getEmail()
            email_profile.setText(userProvider.getEmail())
            phone_profile.setText(userProvider.getPhoneNumber())
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
                        //.addToBackStack(ChangePasswordFragment.TAG)
                        .commit()
            }
        }

    }






}
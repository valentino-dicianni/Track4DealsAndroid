package com.example.track4deals.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.track4deals.R
import com.example.track4deals.ui.login.LoginFragment
import com.example.track4deals.ui.profile.ProfileFragment.Companion.newInstance
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel
    private var firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        if (firebaseUser == null) {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.nav_host_fragment, LoginFragment.newInstance())
                    .addToBackStack(LoginFragment.TAG)
                    .commit()
            }
        } else {
            fullname_field.text = firebaseUser!!.displayName
            email_field.text = firebaseUser!!.email

        }

    }

}